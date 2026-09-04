package ruiseki.integrateddynamics.inventory.container;

import java.util.function.Supplier;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.core.inventory.container.ContainerMechanicalMachine;
import ruiseki.integrateddynamics.tileentity.TileMechanicalDryingBasin;
import ruiseki.okcore.inventory.slot.SlotRemoveOnly;

/**
 * Container for the mechanical drying basin.
 * 
 * @author rubensworks
 */
public class ContainerMechanicalDryingBasin extends ContainerMechanicalMachine<TileMechanicalDryingBasin> {

    private final Supplier<FluidStack> variableInputFluidStack;
    private final Supplier<Integer> variableInputFluidCapacity;
    private final Supplier<FluidStack> variableOutputFluidStack;
    private final Supplier<Integer> variableOutputFluidCapacity;

    /**
     * Make a new instance.
     * 
     * @param inventory The player inventory.
     * @param tile      The part.
     */
    public ContainerMechanicalDryingBasin(InventoryPlayer inventory, TileMechanicalDryingBasin tile) {
        super(inventory, tile);

        this.variableInputFluidStack = registerSyncedVariable(
            FluidStack.class,
            () -> getTile().getTankInput()
                .getFluid());
        this.variableInputFluidCapacity = registerSyncedVariable(
            Integer.class,
            () -> getTile().getTankInput()
                .getCapacity());
        this.variableOutputFluidStack = registerSyncedVariable(
            FluidStack.class,
            () -> getTile().getTankOutput()
                .getFluid());
        this.variableOutputFluidCapacity = registerSyncedVariable(
            Integer.class,
            () -> getTile().getTankOutput()
                .getCapacity());

        addSlotToContainer(new Slot(tile, 0, 54, 37));

        addSlotToContainer(new SlotRemoveOnly(tile, 1, 108, 29));
        addSlotToContainer(new SlotRemoveOnly(tile, 2, 126, 29));
        addSlotToContainer(new SlotRemoveOnly(tile, 3, 108, 47));
        addSlotToContainer(new SlotRemoveOnly(tile, 4, 126, 47));

        addPlayerInventory(inventory, offsetX + 8, offsetY + 86);
    }

    @Nullable
    public FluidStack getInputFluidStack() {
        return variableInputFluidStack.get();
    }

    public int getInputFluidCapacity() {
        return variableInputFluidCapacity.get();
    }

    @Nullable
    public FluidStack getOutputFluidStack() {
        return variableOutputFluidStack.get();
    }

    public int getOutputFluidCapacity() {
        return variableOutputFluidCapacity.get();
    }
}
