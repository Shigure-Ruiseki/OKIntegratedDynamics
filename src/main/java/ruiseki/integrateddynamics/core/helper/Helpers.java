package ruiseki.integrateddynamics.core.helper;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.fluid.FluidHelpers;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidTankProperties;
import ruiseki.okcore.helper.TileHelpers;

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

    /**
     * Create a string of 'length' times '%s' seperated by ','.
     *
     * @param length The length for the series of '%s'.
     * @return The string.
     */
    public static String createPatternOfLength(int length) {
        StringBuilder pattern = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < length; i++) {
            if (first) {
                first = false;
            } else {
                pattern.append(",");
            }
            pattern.append("%s");
        }
        return pattern.toString();
    }

    private static final List<IInterfaceRetriever> INTERFACE_RETRIEVERS = Lists.newArrayList();
    static {
        addInterfaceRetriever(new IInterfaceRetriever() {

            @Override
            public <C> C getInterface(IBlockAccess world, BlockPos pos, Class<C> clazz) {
                return TileHelpers.getSafeTile(world, pos, clazz);
            }
        });
    }

    /**
     * Check for the given interface at the given position.
     *
     * @param world The world.
     * @param pos   The position.
     * @param clazz The class to find.
     * @param <C>   The class type.
     * @return The instance or null.
     */
    private static <C> C getInterface(IBlockAccess world, BlockPos pos, Class<C> clazz) {
        C instance;
        for (IInterfaceRetriever interfaceRetriever : INTERFACE_RETRIEVERS) {
            instance = interfaceRetriever.getInterface(world, pos, clazz);
            if (instance != null) {
                return instance;
            }
        }
        return null;
    }

    /**
     * Check for the given interface at the given position.
     *
     * @param dimPos The dimensional position.
     * @param clazz  The class to find.
     * @param <C>    The class type.
     * @return The instance or null.
     */
    public static <C> C getInterface(DimPos dimPos, Class<C> clazz) {
        return getInterface(dimPos.getWorld(), dimPos.getBlockPos(), clazz);
    }

    public static void addInterfaceRetriever(IInterfaceRetriever interfaceRetriever) {
        INTERFACE_RETRIEVERS.add(interfaceRetriever);
    }

    public static interface IInterfaceRetriever {

        /**
         * Attempt to get a given interface instance.
         *
         * @param world The world.
         * @param pos   The position.
         * @param clazz The class to find.
         * @param <C>   The class type.
         * @return The instance or null.
         */
        public <C> C getInterface(IBlockAccess world, BlockPos pos, Class<C> clazz);

    }

}
