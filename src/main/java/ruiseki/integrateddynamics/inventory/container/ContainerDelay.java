package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;

import ruiseki.integrateddynamics.core.inventory.container.ContainerActiveVariableBase;
import ruiseki.integrateddynamics.core.inventory.container.slot.SlotVariable;
import ruiseki.integrateddynamics.tileentity.TileDelay;
import ruiseki.integrateddynamics.tileentity.TileProxy;
import ruiseki.okcore.helper.ValueNotifierHelpers;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.inventory.slot.SlotRemoveOnly;

/**
 * Container for the delay.
 *
 * @author rubensworks
 */
public class ContainerDelay extends ContainerActiveVariableBase<TileDelay> {

    private final int lastUpdateValueId;
    private final int lastCapacityValueId;

    public ContainerDelay(InventoryPlayer playerInventory) {
        this(playerInventory, new SimpleInventory(TileDelay.INVENTORY_SIZE), null);
    }

    public ContainerDelay(InventoryPlayer playerInventory, IInventory inventory, TileDelay tile) {
        super(ContainerDelayConfig._instance.getInstance(), playerInventory, inventory, tile);
        addSlotToContainer(new SlotVariable(inventory, TileProxy.SLOT_READ, 81, 25));
        addSlotToContainer(new SlotVariable(inventory, TileProxy.SLOT_WRITE_IN, 56, 78));
        addSlotToContainer(new SlotRemoveOnly(inventory, TileProxy.SLOT_WRITE_OUT, 104, 78));
        addPlayerInventory(playerInventory, offsetX + 9, offsetY + 145);

        lastUpdateValueId = getNextValueId();
        lastCapacityValueId = getNextValueId();
        getTile().ifPresent(t -> t.setLastPlayer(playerInventory.player));
    }

    @Override
    protected void initializeValues() {
        ValueNotifierHelpers.setValue(
            this,
            lastUpdateValueId,
            getTile().map(TileDelay::getUpdateInterval)
                .orElse(0));
        ValueNotifierHelpers.setValue(
            this,
            lastCapacityValueId,
            getTile().map(TileDelay::getCapacity)
                .orElse(0));
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
        getTile().ifPresent(tile -> {
            if (valueId == getLastUpdateValueId()) {
                tile.setUpdateInterval(getLastUpdateValue());
            } else if (valueId == getLastCapacityValueId()) {
                tile.setCapacity(getLastCapacityValue());
            }
        });
    }
}
