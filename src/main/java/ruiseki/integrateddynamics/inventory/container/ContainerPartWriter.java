package ruiseki.integrateddynamics.inventory.container;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Maps;

import ruiseki.integrateddynamics.api.PartStateException;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.api.part.aspect.IAspectWrite;
import ruiseki.integrateddynamics.api.part.write.IPartStateWriter;
import ruiseki.integrateddynamics.api.part.write.IPartTypeWriter;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.core.inventory.container.ContainerMultipartAspects;
import ruiseki.integrateddynamics.core.inventory.container.slot.SlotVariable;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.helper.ValueNotifierHelpers;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.network.ExtendedBuffer;

/**
 * Container for writer parts.
 *
 * @author rubensworks
 */
public class ContainerPartWriter<P extends IPartTypeWriter<P, S>, S extends IPartStateWriter<P>>
    extends ContainerMultipartAspects<P, S, IAspectWrite> {

    public static final int ASPECT_BOX_HEIGHT = 18;
    private static final int PAGE_SIZE = 6;
    private static final int SLOT_X = 131;
    private static final int SLOT_Y = 18;

    private final int valueId, colorId, enabledId, activeAspectId;
    private final Map<IAspectWrite, Integer> aspectErrorIds;

    public ContainerPartWriter(InventoryPlayer playerInventory, ExtendedBuffer packetBuffer) {
        this(
            playerInventory,
            new SimpleInventory(packetBuffer.readInt(), 1),
            PartHelpers.readPartTarget(packetBuffer),
            Optional.empty(),
            PartHelpers.readPart(packetBuffer));
    }

    public ContainerPartWriter(InventoryPlayer playerInventory, IInventory inventory, PartTarget target,
        Optional<IPartContainer> partContainer, P partType) {
        super(
            ContainerPartWriterConfig._instance.getInstance(),
            playerInventory,
            inventory,
            target,
            partContainer,
            partType,
            partType.getWriteAspects());
        for (int i = 0; i < getUnfilteredItemCount(); i++) {
            addSlotToContainer(new SlotVariable(inputSlots, i, SLOT_X, SLOT_Y + getAspectBoxHeight() * i));
            disableSlot(i);
        }

        addPlayerInventory(player.inventory, 9, 140);

        this.valueId = getNextValueId();
        this.colorId = getNextValueId();
        this.enabledId = getNextValueId();
        this.activeAspectId = getNextValueId();
        this.aspectErrorIds = Maps.newIdentityHashMap();
        for (IAspectWrite aspect : partType.getWriteAspects()) {
            this.aspectErrorIds.put(aspect, getNextValueId());
        }
    }

    @Override
    public int getAspectBoxHeight() {
        return ASPECT_BOX_HEIGHT;
    }

    @Override
    public int getPageSize() {
        return PAGE_SIZE;
    }

    @Override
    protected void enableSlot(int slotIndex, int row) {
        Slot slot = getSlot(slotIndex);
        slot.xDisplayPosition = SLOT_X;
        slot.yDisplayPosition = SLOT_Y + ASPECT_BOX_HEIGHT * row;
    }

    @Override
    protected IInventory constructInputSlotsInventory() {
        SimpleInventory inventory = getPartState().getInventory();
        inventory.addDirtyMarkListener(this);
        return inventory;
    }

    @Override
    public void onDirty() {
        if (!MinecraftHelpers.isClientSide()) {
            getPartType().updateActivation(getTarget(), getPartState(), player);
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        try {
            if (!player.worldObj.isRemote) {
                // Update write value
                Pair<String, Integer> readValue;
                S partState = getPartState();
                if (!partState.isEnabled()) {
                    readValue = Pair.of("NO POWER", 0);
                } else if (partState.hasVariable()) {
                    IPartContainer partContainer = getPartContainer();
                    LazyOptional<INetwork> optionalNetwork = NetworkHelpers.getNetwork(
                        partContainer.getPosition()
                            .getWorld(),
                        partContainer.getPosition()
                            .getBlockPos(),
                        getTarget().getCenter()
                            .getSide());
                    IPartNetwork partNetwork = optionalNetwork.map(NetworkHelpers::getPartNetworkChecked)
                        .orElse(null);
                    if (partNetwork != null) {
                        IVariable variable = partState.getVariable(optionalNetwork.getOrNull(), partNetwork);
                        readValue = ValueHelpers.getSafeReadableValue(variable);
                    } else {
                        readValue = Pair.of("NETWORK CORRUPTED!", Helpers.RGBToInt(255, 100, 0));
                    }
                } else {
                    readValue = Pair.of("", 0);
                }
                setWriteValue(readValue.getLeft(), readValue.getRight());

                // Update error values
                for (IAspectWrite aspectWrite : getPartType().getWriteAspects()) {
                    ValueNotifierHelpers.setValueUnlocalizedStringList(
                        this,
                        aspectErrorIds.get(aspectWrite),
                        getPartState().getErrors(aspectWrite));
                }

                // Update state
                ValueNotifierHelpers.setValue(this, enabledId, partState.isEnabled());
                ValueNotifierHelpers.setValue(
                    this,
                    activeAspectId,
                    partState.getActiveAspect() != null ? partState.getActiveAspect()
                        .getUniqueName()
                        .toString() : "");
            }
        } catch (PartStateException e) {
            player.closeScreen();
        }
    }

    public void setWriteValue(String writeValue, int writeColor) {
        ValueNotifierHelpers.setValue(this, valueId, writeValue);
        ValueNotifierHelpers.setValue(this, colorId, writeColor);
    }

    public String getWriteValue() {
        String value = ValueNotifierHelpers.getValueString(this, valueId);
        if (value == null) {
            value = "";
        }
        return value;
    }

    public int getWriteValueColor() {
        return ValueNotifierHelpers.getValueInt(this, colorId);
    }

    public List<LangHelpers.UnlocalizedString> getAspectErrors(IAspectWrite aspectWrite) {
        return ValueNotifierHelpers.getValueUnlocalizedStringList(this, aspectErrorIds.get(aspectWrite));
    }

    public boolean isPartStateEnabled() {
        return ValueNotifierHelpers.getValueBoolean(this, enabledId);
    }

    @Nullable
    public IAspect getPartStateActiveAspect() {
        String aspectName = ValueNotifierHelpers.getValueString(this, activeAspectId);
        if (aspectName == null) {
            return null;
        }
        return AspectRegistry.getInstance()
            .getAspect(new ResourceLocation(aspectName));
    }

}
