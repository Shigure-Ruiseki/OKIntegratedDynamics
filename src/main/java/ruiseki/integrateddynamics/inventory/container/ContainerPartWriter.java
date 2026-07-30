package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.IAspectWrite;
import ruiseki.integrateddynamics.api.part.write.IPartStateWriter;
import ruiseki.integrateddynamics.api.part.write.IPartTypeWriter;
import ruiseki.integrateddynamics.api.tileentity.ITileCableNetwork;
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
            getPartType().updateActivation(getTarget(), getPartState());
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (!MinecraftHelpers.isClientSide()) {
            String writeValue = "";
            int writeValueColor = 0;
            if (getPartContainer() instanceof ITileCableNetwork && getPartState().hasVariable()) {
                IVariable variable = getPartState().getVariable(((ITileCableNetwork) getPartContainer()).getNetwork());
                if (variable != null) {
                    try {
                        IValue value = variable.getValue();
                        writeValue = value.getType()
                            .toCompactString(value);
                        writeValueColor = variable.getType()
                            .getDisplayColor();
                    } catch (EvaluationException e) {
                        writeValue = "ERROR";
                        writeValueColor = Helpers.RGBToInt(255, 0, 0);
                    }
                }
            } else {
                writeValue = "";
            }
            setWriteValue(writeValue, writeValueColor);
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
