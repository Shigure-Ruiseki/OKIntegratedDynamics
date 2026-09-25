package ruiseki.integrateddynamics.inventory.container;

import java.util.function.Supplier;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import ruiseki.integrateddynamics.tileentity.TileCoalGenerator;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.inventory.container.TileInventoryContainer;
import ruiseki.okcore.inventory.slot.SlotFurnaceFuel;

/**
 * Container for the coal generator.
 *
 * @author rubensworks
 */
public class ContainerCoalGenerator extends TileInventoryContainer<TileCoalGenerator> {

    private final Supplier<Integer> variableProgress;

    public ContainerCoalGenerator(InventoryPlayer playerInventory) {
        this(playerInventory, new SimpleInventory(TileCoalGenerator.INVENTORY_SIZE), null);
    }

    /**
     * Make a new instance.
     *
     * @param inventory The player inventory.
     */
    public ContainerCoalGenerator(InventoryPlayer playerInventory, IInventory inventory, TileCoalGenerator tile) {
        super(ContainerCoalGeneratorConfig._instance.getInstance(), playerInventory, inventory, tile);

        this.variableProgress = registerSyncedVariable(
            Integer.class,
            () -> getTile().map(TileCoalGenerator::getProgress)
                .orElse(0));

        addInventory(inventory, 0, offsetX + 80, offsetY + 11, 1, 1);
        addPlayerInventory(playerInventory, offsetX + 8, offsetY + 46);
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
