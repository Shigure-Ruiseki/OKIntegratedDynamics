package ruiseki.integrateddynamics.inventory.container;

import java.util.function.Supplier;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.core.inventory.container.ContainerMechanicalMachine;
import ruiseki.integrateddynamics.tileentity.TileMechanicalSqueezer;
import ruiseki.okcore.helper.FluidHelpers;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.inventory.slot.SlotRemoveOnly;

/**
 * Container for the mechanical squeezer.
 *
 * @author rubensworks
 */
public class ContainerMechanicalSqueezer extends ContainerMechanicalMachine<TileMechanicalSqueezer> {

    public static final String BUTTON_TOGGLE_FLUID_EJECT = "button_eject";

    private final Supplier<FluidStack> variableFluidStack;
    private final Supplier<Integer> variableFluidCapacity;
    private final Supplier<Boolean> variableAutoEject;

    public ContainerMechanicalSqueezer(InventoryPlayer playerInventory) {
        this(playerInventory, new SimpleInventory(TileMechanicalSqueezer.INVENTORY_SIZE), null);
    }

    public ContainerMechanicalSqueezer(InventoryPlayer inventoryPlayer, IInventory inventory,
        TileMechanicalSqueezer tile) {
        super(ContainerMechanicalSqueezerConfig._instance.getInstance(), inventoryPlayer, inventory, tile);

        this.variableFluidStack = registerSyncedVariable(
            FluidStack.class,
            () -> getTile().map(
                t -> t.getTank()
                    .getFluid())
                .orElse(FluidHelpers.EMPTY));
        this.variableFluidCapacity = registerSyncedVariable(
            Integer.class,
            () -> getTile().map(
                t -> t.getTank()
                    .getCapacity())
                .orElse(0));
        this.variableAutoEject = registerSyncedVariable(
            Boolean.class,
            () -> getTile().map(TileMechanicalSqueezer::isAutoEjectFluids)
                .orElse(false));

        addSlotToContainer(new Slot(inventory, 0, 44, 37));

        addSlotToContainer(new SlotRemoveOnly(inventory, 1, 98, 29));
        addSlotToContainer(new SlotRemoveOnly(inventory, 2, 116, 29));
        addSlotToContainer(new SlotRemoveOnly(inventory, 3, 98, 47));
        addSlotToContainer(new SlotRemoveOnly(inventory, 4, 116, 47));

        addPlayerInventory(inventoryPlayer, offsetX + 8, offsetY + 86);

        putButtonAction(
            BUTTON_TOGGLE_FLUID_EJECT,
            (buttonId, container) -> getTile().ifPresent(
                t -> t.setAutoEjectFluids(
                    !getTile().get()
                        .isAutoEjectFluids())));
    }

    @Nullable
    public FluidStack getFluidStack() {
        return variableFluidStack.get();
    }

    public int getFluidCapacity() {
        return variableFluidCapacity.get();
    }

    public boolean isAutoEjectFluids() {
        return variableAutoEject.get();
    }
}
