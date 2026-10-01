package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import ruiseki.integrateddynamics.item.ItemVariableConfig;
import ruiseki.integrateddynamics.tileentity.TileVariablestore;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.inventory.container.InventoryContainer;
import ruiseki.okcore.inventory.slot.SlotSingleItem;

/**
 * Container for the variablestore.
 *
 * @author rubensworks
 */
public class ContainerVariablestore extends InventoryContainer {

    public ContainerVariablestore(int id, InventoryPlayer playerInventory) {
        this(id, playerInventory, new SimpleInventory(TileVariablestore.INVENTORY_SIZE));
    }

    public ContainerVariablestore(int id, InventoryPlayer inventoryPlayer, IInventory inventory) {
        super(ContainerVariablestoreConfig._instance.getInstance(), id, inventoryPlayer, inventory);
        addInventory(inventory, 0, offsetX + 8, offsetY + 18, TileVariablestore.ROWS, TileVariablestore.COLS);
        addPlayerInventory(inventoryPlayer, offsetX + 8, offsetY + 14 + TileVariablestore.ROWS * 18 + 17);
    }

    @Override
    public Slot createNewSlot(IInventory inventory, int index, int row, int column) {
        if (inventory instanceof InventoryPlayer) {
            return super.createNewSlot(inventory, index, row, column);
        }
        return new SlotSingleItem(inventory, index, row, column, ItemVariableConfig._instance.getInstance());
    }

}
