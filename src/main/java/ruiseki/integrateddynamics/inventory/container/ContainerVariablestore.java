package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import ruiseki.integrateddynamics.item.ItemVariable;
import ruiseki.integrateddynamics.tileentity.TileVariablestore;
import ruiseki.okcore.inventory.container.TileInventoryContainerConfigurable;
import ruiseki.okcore.inventory.slot.SlotSingleItem;

/**
 * Container for the variablestore.
 *
 * @author rubensworks
 */
public class ContainerVariablestore extends TileInventoryContainerConfigurable<TileVariablestore> {

    /**
     * Make a new instance.
     *
     * @param inventory The player inventory.
     * @param tile      The part.
     */
    public ContainerVariablestore(InventoryPlayer inventory, TileVariablestore tile) {
        super(inventory, tile);
        addInventory(tile, 0, offsetX + 8, offsetY + 18, TileVariablestore.ROWS, TileVariablestore.COLS);
        addPlayerInventory(inventory, offsetX + 8, offsetY + 14 + TileVariablestore.ROWS * 18 + 17);
    }

    @Override
    public Slot createNewSlot(IInventory inventory, int index, int row, int column) {
        if (inventory instanceof InventoryPlayer) {
            return super.createNewSlot(inventory, index, row, column);
        }
        return new SlotSingleItem(inventory, index, row, column, ItemVariable.getInstance());
    }

}
