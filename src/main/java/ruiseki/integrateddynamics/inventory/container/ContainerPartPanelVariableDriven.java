package ruiseki.integrateddynamics.inventory.container;

import java.util.List;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraftforge.common.MinecraftForge;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.core.inventory.container.ContainerMultipart;
import ruiseki.integrateddynamics.core.inventory.container.slot.SlotVariable;
import ruiseki.integrateddynamics.core.network.event.VariableContentsUpdatedEvent;
import ruiseki.integrateddynamics.core.part.event.PartVariableDrivenVariableContentsUpdatedEvent;
import ruiseki.integrateddynamics.core.part.panel.PartTypePanelVariableDriven;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.helper.ValueNotifierHelpers;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.network.ExtendedBuffer;

/**
 * Container for display parts.
 *
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class ContainerPartPanelVariableDriven<P extends PartTypePanelVariableDriven<P, S>, S extends PartTypePanelVariableDriven.State<P, S>>
    extends ContainerMultipart<P, S> {

    private static final int SLOT_X = 79;
    private static final int SLOT_Y = 8;

    private final int readValueId;
    private final int readColorId;
    private final int readErrorsId;

    public ContainerPartPanelVariableDriven(InventoryPlayer playerInventory, ExtendedBuffer packetBuffer) {
        this(
            playerInventory,
            new SimpleInventory(packetBuffer.readInt()),
            Optional.empty(),
            Optional.empty(),
            PartHelpers.readPart(packetBuffer));
    }

    public ContainerPartPanelVariableDriven(InventoryPlayer playerInventory, IInventory inventory,
        Optional<PartTarget> target, Optional<IPartContainer> partContainer, P partType) {
        super(
            ContainerPartDisplayConfig._instance.getInstance(),
            playerInventory,
            inventory,
            target,
            partContainer,
            partType);

        readValueId = getNextValueId();
        readColorId = getNextValueId();
        readErrorsId = getNextValueId();

        if (inventory instanceof SimpleInventory) {
            ((SimpleInventory) inventory).addDirtyMarkListener(this);
        }

        addInventory(inventory, 0, 80, 14, 1, 1);
        addPlayerInventory(player.inventory, 8, 46);
    }

    @Override
    protected Slot createNewSlot(IInventory inventory, int index, int x, int y) {
        if (inventory instanceof SimpleInventory) {
            return new SlotVariable(inventory, index, x, y);
        }
        return super.createNewSlot(inventory, index, x, y);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        if (!MinecraftHelpers.isClientSide()) {
            String readValue = "";
            int readValueColor = 0;
            if (!NetworkHelpers.shouldWork()) {
                readValue = "SAFE-MODE";
            } else {
                IValue value = getPartState().get()
                    .getDisplayValue();
                if (value != null) {
                    readValue = value.getType()
                        .toCompactString(value);
                    readValueColor = value.getType()
                        .getDisplayColor();
                }
            }
            ValueNotifierHelpers.setValue(this, readValueId, readValue);
            ValueNotifierHelpers.setValue(this, readColorId, readValueColor);
            ValueNotifierHelpers.setValueUnlocalizedStringList(
                this,
                readErrorsId,
                getPartState().get()
                    .getGlobalErrors());
        }
    }

    @Override
    public void onDirty() {
        if (!MinecraftHelpers.isClientSide()) {
            S partState = getPartState().get();
            partState.onVariableContentsUpdated(getPartType(), getTarget().get());
            LazyOptional<INetwork> optionalNetwork = NetworkHelpers.getNetwork(
                getTarget().get()
                    .getCenter());
            if (getContainerInventory() != null) {
                NetworkHelpers.getPartNetwork(optionalNetwork)
                    .ifPresent(partNetwork -> {
                        try {
                            INetwork network = optionalNetwork.orElse(null);
                            IVariable variable = partState.getVariable(network, partNetwork);
                            MinecraftForge.EVENT_BUS.post(
                                new PartVariableDrivenVariableContentsUpdatedEvent<>(
                                    network,
                                    partNetwork,
                                    getTarget().get(),
                                    getPartType(),
                                    partState,
                                    player,
                                    variable,
                                    variable != null ? variable.getValue() : null));
                        } catch (EvaluationException e) {

                        }
                    });

            }
            optionalNetwork.ifPresent(
                network -> network.getEventBus()
                    .post(new VariableContentsUpdatedEvent(network)));
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        if (inventory instanceof SimpleInventory) {
            ((SimpleInventory) inventory).removeDirtyMarkListener(this);
        }
    }

    public String getReadValue() {
        return ValueNotifierHelpers.getValueString(this, readValueId);
    }

    public int getReadValueColor() {
        return ValueNotifierHelpers.getValueInt(this, readColorId);
    }

    public List<LangHelpers.UnlocalizedString> getReadErrors() {
        return ValueNotifierHelpers.getValueUnlocalizedStringList(this, readErrorsId);
    }
}
