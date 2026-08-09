package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;

import ruiseki.integrateddynamics.core.inventory.container.ContainerActiveVariableBase;
import ruiseki.integrateddynamics.core.inventory.container.slot.SlotVariable;
import ruiseki.integrateddynamics.tileentity.TileDelay;
import ruiseki.integrateddynamics.tileentity.TileProxy;
import ruiseki.okcore.helper.ValueNotifierHelpers;
import ruiseki.okcore.inventory.slot.SlotRemoveOnly;

/**
 * Container for the delay.
 * 
 * @author rubensworks
 */
public class ContainerDelay extends ContainerActiveVariableBase<TileDelay> {

    private final int lastUpdateValueId;
    private final int lastCapacityValueId;

    /**
     * Make a new instance.
     * 
     * @param inventory The player inventory.
     * @param tile      The part.
     */
    public ContainerDelay(InventoryPlayer inventory, TileDelay tile) {
        super(inventory, tile);
        addSlotToContainer(new SlotVariable(tile, TileProxy.SLOT_READ, 81, 25));
        addSlotToContainer(new SlotVariable(tile, TileProxy.SLOT_WRITE_IN, 56, 78));
        addSlotToContainer(new SlotRemoveOnly(tile, TileProxy.SLOT_WRITE_OUT, 104, 78));
        addPlayerInventory(inventory, offsetX + 9, offsetY + 145);

        lastUpdateValueId = getNextValueId();
        lastCapacityValueId = getNextValueId();
        tile.setLastPlayer(inventory.player);
    }

    @Override
    protected void initializeValues() {
        ValueNotifierHelpers.setValue(this, lastUpdateValueId, getTile().getUpdateInterval());
        ValueNotifierHelpers.setValue(this, lastCapacityValueId, getTile().getCapacity());
    }

    public int getLastUpdateValueId() {
        return lastUpdateValueId;
    }

    public int getLastCapacityValueId() {
        return lastCapacityValueId;
    }

    public int getLastUpdateValue() {
        return ValueNotifierHelpers.getValueInt(this, getLastUpdateValueId());
    }

    public int getLastCapacityValue() {
        return ValueNotifierHelpers.getValueInt(this, getLastCapacityValueId());
    }

    @Override
    public void onUpdate(int valueId, NBTTagCompound value) {
        super.onUpdate(valueId, value);
        if (!getTile().getWorldObj().isRemote) {
            if (valueId == getLastUpdateValueId()) {
                getTile().setUpdateInterval(getLastUpdateValue());
            } else if (valueId == getLastCapacityValueId()) {
                getTile().setCapacity(getLastCapacityValue());
            }
        }
    }
}
