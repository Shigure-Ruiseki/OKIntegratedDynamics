package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import org.apache.commons.lang3.tuple.Pair;

import ruiseki.integrateddynamics.api.PartStateException;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.IAspectWrite;
import ruiseki.integrateddynamics.api.part.write.IPartStateWriter;
import ruiseki.integrateddynamics.api.part.write.IPartTypeWriter;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.inventory.container.ContainerMultipartAspects;
import ruiseki.integrateddynamics.core.inventory.container.slot.SlotVariable;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.helper.ValueNotifierHelpers;
import ruiseki.okcore.inventory.IGuiContainerProvider;
import ruiseki.okcore.inventory.SimpleInventory;

/**
 * Container for writer parts.
 *
 * @author rubensworks
 */
public class ContainerPartWriter<P extends IPartTypeWriter<P, S> & IGuiContainerProvider, S extends IPartStateWriter<P>>
    extends ContainerMultipartAspects<P, S, IAspectWrite> {

    public static final int ASPECT_BOX_HEIGHT = 18;
    private static final int PAGE_SIZE = 6;
    private static final int SLOT_X = 131;
    private static final int SLOT_Y = 18;

    private final int valueId, colorId;

    /**
     * Make a new instance.
     *
     * @param partTarget    The target.
     * @param player        The player.
     * @param partContainer The part container.
     * @param partType      The part type.
     */
    public ContainerPartWriter(EntityPlayer player, PartTarget partTarget, IPartContainer partContainer, P partType) {
        super(player, partTarget, partContainer, partType, partType.getWriteAspects());
        for (int i = 0; i < getUnfilteredItemCount(); i++) {
            addSlotToContainer(new SlotVariable(inputSlots, i, SLOT_X, SLOT_Y + getAspectBoxHeight() * i));
            disableSlot(i);
        }

        addPlayerInventory(player.inventory, 9, 140);

        this.valueId = getNextValueId();
        this.colorId = getNextValueId();
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
            getPartType().updateActivation(getTarget(), getPartState(), getPlayer());
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        try {
            if (!MinecraftHelpers.isClientSide()) {
                Pair<String, Integer> readValue;
                if (!getPartState().isEnabled()) {
                    readValue = Pair.of("NO POWER", 0);
                } else if (getPartState().hasVariable()) {
                    INetwork network = NetworkHelpers.getNetwork(
                        getPartContainer().getPosition()
                            .getWorld(),
                        getPartContainer().getPosition()
                            .getBlockPos(),
                        getTarget().getCenter()
                            .getSide());
                    IPartNetwork partNetwork = NetworkHelpers.getPartNetwork(network);
                    if (partNetwork != null) {
                        IVariable variable = getPartState().getVariable(network, partNetwork);
                        readValue = ValueHelpers.getSafeReadableValue(variable);
                    } else {
                        readValue = Pair.of("NETWORK CORRUPTED!", Helpers.RGBToInt(255, 100, 0));
                    }
                } else {
                    readValue = Pair.of("", 0);
                }
                setWriteValue(readValue.getLeft(), readValue.getRight());
            }
        } catch (PartStateException e) {
            getPlayer().closeScreen();
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

}
