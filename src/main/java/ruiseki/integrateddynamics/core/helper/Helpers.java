package ruiseki.integrateddynamics.core.helper;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.ImmutableList;

import ruiseki.okcore.fluid.FluidHelpers;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidTankProperties;

/**
 * Helper methods.
 *
 * @author rubensworks
 */
public final class Helpers {

    /**
     * Get the fluidstack from the given itemstack.
     *
     * @param itemStack The itemstack.
     * @return The fluidstack or null.
     */
    public static FluidStack getFluidStack(ItemStack itemStack) {
        return FluidHelpers.getFluidContained(itemStack);
    }

    /**
     * Get the fluidstack capacity from the given itemstack.
     *
     * @param itemStack The itemstack.
     * @return The capacity
     */
    public static int getFluidStackCapacity(ItemStack itemStack) {
        IFluidHandler fluidHandler = FluidHelpers.getFluidHandler(itemStack)
            .getOrNull();
        if (fluidHandler != null) {
            for (IFluidTankProperties properties : fluidHandler.getTankProperties()) {
                return properties.getCapacity();
            }
        }
        return 0;
    }

    /**
     * Add the given element to a copy of the given list/
     *
     * @param list       The list.
     * @param newElement The element.
     * @param <T>        The type.
     * @return The new joined list.
     */
    public static <T> List<T> joinList(List<T> list, T newElement) {
        ImmutableList.Builder<T> builder = ImmutableList.<T>builder()
            .addAll(list);
        if (newElement != null) {
            builder.add(newElement);
        }
        return builder.build();
    }

}
