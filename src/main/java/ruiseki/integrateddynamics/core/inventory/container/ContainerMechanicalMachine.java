package ruiseki.integrateddynamics.core.inventory.container;

import java.util.function.Supplier;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

import ruiseki.integrateddynamics.core.tileentity.TileMechanicalMachine;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.inventory.container.TileInventoryContainer;

/**
 * A base container for {@link TileMechanicalMachine}.
 *
 * @author rubensworks
 */
public class ContainerMechanicalMachine<T extends TileMechanicalMachine<?, ?>> extends TileInventoryContainer<T> {

    private final Supplier<Integer> variableMaxProgress;
    private final Supplier<Integer> variableProgress;
    private final Supplier<Integer> variableMaxEnergy;
    private final Supplier<Integer> variableEnergy;

    public ContainerMechanicalMachine(ContainerType<?> containerType, InventoryPlayer inventoryPlayer,
        IInventory inventory, T tile) {
        super(containerType, inventoryPlayer, inventory, tile);
        this.variableMaxProgress = registerSyncedVariable(
            Integer.class,
            () -> getTile().map(t -> t.getMaxProgress())
                .orElse(0));
        this.variableProgress = registerSyncedVariable(
            Integer.class,
            () -> getTile().map(t -> t.getProgress())
                .orElse(0));
        this.variableMaxEnergy = registerSyncedVariable(
            Integer.class,
            () -> getTile().map(t -> t.getMaxEnergyStored())
                .orElse(0));
        this.variableEnergy = registerSyncedVariable(
            Integer.class,
            () -> getTile().map(t -> t.getEnergyStored())
                .orElse(0));
    }

    public int getMaxProgress() {
        return variableMaxProgress.get();
    }

    public int getProgress() {
        return variableProgress.get();
    }

    public int getMaxEnergy() {
        return variableMaxEnergy.get();
    }

    public int getEnergy() {
        return variableEnergy.get();
    }
}
