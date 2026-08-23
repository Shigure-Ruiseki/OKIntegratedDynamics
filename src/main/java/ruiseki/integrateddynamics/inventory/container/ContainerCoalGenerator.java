package ruiseki.integrateddynamics.inventory.container;

import java.util.function.Supplier;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import ruiseki.integrateddynamics.tileentity.TileCoalGenerator;
import ruiseki.okcore.inventory.container.TileInventoryContainerConfigurable;
import ruiseki.okcore.inventory.slot.SlotFurnaceFuel;

/**
 * Container for the coal generator.
 * 
 * @author rubensworks
 */
public class ContainerCoalGenerator extends TileInventoryContainerConfigurable<TileCoalGenerator> {

    private final Supplier<Integer> variableProgress;

    /**
     * Make a new instance.
     * 
     * @param inventory The player inventory.
     * @param tile      The part.
     */
    public ContainerCoalGenerator(InventoryPlayer inventory, TileCoalGenerator tile) {
        super(inventory, tile);

        this.variableProgress = registerSyncedVariable(Integer.class, () -> getTile().getProgress());

        addInventory(tile, 0, offsetX + 80, offsetY + 11, 1, 1);
        addPlayerInventory(inventory, offsetX + 8, offsetY + 46);
    }

    @Override
    public Slot createNewSlot(IInventory inventory, int index, int row, int column) {
        if (inventory instanceof InventoryPlayer) {
            return super.createNewSlot(inventory, index, row, column);
        }
        return new SlotFurnaceFuel(inventory, index, row, column);
    }

    public int getProgress() {
        return this.variableProgress.get();
    }

}
