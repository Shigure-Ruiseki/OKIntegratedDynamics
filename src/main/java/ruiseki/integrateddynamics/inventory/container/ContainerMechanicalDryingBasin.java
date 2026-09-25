package ruiseki.integrateddynamics.inventory.container;

import java.util.function.Supplier;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.core.inventory.container.ContainerMechanicalMachine;
import ruiseki.integrateddynamics.tileentity.TileMechanicalDryingBasin;
import ruiseki.okcore.helper.FluidHelpers;
import ruiseki.okcore.inventory.SimpleInventory;
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

    public ContainerMechanicalDryingBasin(InventoryPlayer playerInventory) {
        this(playerInventory, new SimpleInventory(TileMechanicalDryingBasin.INVENTORY_SIZE), null);
    }

    public ContainerMechanicalDryingBasin(InventoryPlayer inventoryPlayer, IInventory inventory,
        TileMechanicalDryingBasin tile) {
        super(ContainerMechanicalDryingBasinConfig._instance.getInstance(), inventoryPlayer, inventory, tile);

        this.variableInputFluidStack = registerSyncedVariable(
            FluidStack.class,
            () -> getTile().map(
                t -> t.getTankInput()
                    .getFluid())
                .orElse(FluidHelpers.EMPTY));
        this.variableInputFluidCapacity = registerSyncedVariable(
            Integer.class,
            () -> getTile().map(
                t -> t.getTankInput()
                    .getCapacity())
                .orElse(0));
        this.variableOutputFluidStack = registerSyncedVariable(
            FluidStack.class,
            () -> getTile().map(
                t -> t.getTankOutput()
                    .getFluid())
                .orElse(FluidHelpers.EMPTY));
        this.variableOutputFluidCapacity = registerSyncedVariable(
            Integer.class,
            () -> getTile().map(
                t -> t.getTankOutput()
                    .getCapacity())
                .orElse(0));

        addSlotToContainer(new Slot(inventory, 0, 54, 37));

        addSlotToContainer(new SlotRemoveOnly(inventory, 1, 108, 29));
        addSlotToContainer(new SlotRemoveOnly(inventory, 2, 126, 29));
        addSlotToContainer(new SlotRemoveOnly(inventory, 3, 108, 47));
        addSlotToContainer(new SlotRemoveOnly(inventory, 4, 126, 47));

        addPlayerInventory(inventoryPlayer, offsetX + 8, offsetY + 86);
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
