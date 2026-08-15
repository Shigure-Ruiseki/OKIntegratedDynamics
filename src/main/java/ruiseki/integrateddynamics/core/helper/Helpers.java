package ruiseki.integrateddynamics.core.helper;

import java.util.List;
import java.util.stream.Stream;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.fluid.FluidHelpers;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidTankProperties;
import ruiseki.okcore.helper.LangHelpers;
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
        if (itemStack == null) return null;
        FluidStack fluidStack = FluidHelpers.getFluidContained(itemStack);
        if (fluidStack == null && itemStack.getItem() instanceof ItemBlock
            && ((ItemBlock) itemStack.getItem()).field_150939_a instanceof IFluidBlock) {
            fluidStack = new FluidStack(
                ((IFluidBlock) ((ItemBlock) itemStack.getItem()).field_150939_a).getFluid(),
                FluidHelpers.BUCKET_VOLUME);
        }
        return fluidStack;
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
     * Retrieves a Stream of items that are registered to this ore type
     * with wildcard meta values expanded out into sub items
     *
     * @param name The ore name, directly calls OreDictionary.getOres
     * @return A Stream containing ItemStacks registered for this ore
     */
    public static Stream<ItemStack> getOresWildcard(String name) {
        Stream.Builder<ItemStack> builder = Stream.builder();
        for (ItemStack itemStack : OreDictionary.getOres(name)) {
            if (itemStack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                NonNullList<ItemStack> subItems = NonNullList.create();
                itemStack.getItem()
                    .getSubItems(itemStack.getItem(), null, subItems);
                for (ItemStack subItem : subItems) {
                    builder.accept(subItem);
                }
            } else {
                builder.accept(itemStack);
            }
        }
        return builder.build();
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
        World world = dimPos.getWorld();
        return world != null ? getInterface(world, dimPos.getBlockPos(), clazz) : null;
    }

    /**
     * Get a localized string showing the ratio of stored energy vs the capacity.
     *
     * @param stored   The stored amount of energy.
     * @param capacity The capacity of the energy container.
     * @return The localized string.
     */
    public static String getLocalizedEnergyLevel(int stored, int capacity) {
        return String.format("%,d", stored) + " / "
            + String.format("%,d", capacity)
            + " "
            + LangHelpers.localize(L10NValues.GENERAL_ENERGY_UNIT);
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
