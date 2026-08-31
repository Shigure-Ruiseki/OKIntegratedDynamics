package ruiseki.integratedtunnels.core;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import ruiseki.commoncapabilities.api.capability.itemhandler.ItemMatch;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.operator.IOperator;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeListProxy;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeBlock;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeItemStack;
import ruiseki.integratedtunnels.GeneralConfig;
import ruiseki.integratedtunnels.core.predicate.IngredientPredicate;
import ruiseki.integratedtunnels.core.predicate.IngredientPredicateBlockList;
import ruiseki.integratedtunnels.core.predicate.IngredientPredicateBlockOperator;
import ruiseki.integratedtunnels.core.predicate.IngredientPredicateItemStackList;
import ruiseki.integratedtunnels.core.predicate.IngredientPredicateItemStackNbt;
import ruiseki.integratedtunnels.core.predicate.IngredientPredicateItemStackOperator;
import ruiseki.integratedtunnels.part.aspect.ITunnelConnection;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.ItemHelpers;

/**
 * @author rubensworks
 */
public class TunnelItemHelpers {

    public static final IngredientPredicate<ItemStack, Integer> MATCH_NONE = new IngredientPredicate<ItemStack, Integer>(
        IngredientComponent.ITEMSTACK,
        null,
        ItemMatch.EXACT,
        false,
        true,
        0,
        false) {

        @Override
        public boolean test(@Nullable ItemStack input) {
            return false;
        }

        @Override
        public boolean equals(Object obj) {
            return obj == TunnelItemHelpers.MATCH_NONE;
        }

        @Override
        public int hashCode() {
            return 9991029;
        }
    };

    public static IngredientPredicate<ItemStack, Integer> matchAll(final int amount, final boolean exactAmount) {
        return new IngredientPredicate<ItemStack, Integer>(
            IngredientComponent.ITEMSTACK,
            new ItemStack(Items.apple, amount),
            exactAmount ? ItemMatch.STACKSIZE : ItemMatch.ANY,
            false,
            false,
            amount,
            exactAmount) {

            @Override
            public boolean test(ItemStack input) {
                return true;
            }
        };
    }

    public static IngredientPredicate<ItemStack, Integer> matchItemStack(final ItemStack itemStack,
        final boolean checkItem, final boolean checkStackSize, final boolean checkDamage, final boolean checkNbt,
        final boolean blacklist, final boolean exactAmount) {
        int matchFlags = ItemMatch.ANY;
        if (checkItem) matchFlags = matchFlags | ItemMatch.ITEM;
        if (checkDamage) matchFlags = matchFlags | ItemMatch.DAMAGE;
        if (checkNbt) matchFlags = matchFlags | ItemMatch.NBT;
        if (checkStackSize) matchFlags = matchFlags | ItemMatch.STACKSIZE;
        return new IngredientPredicate<ItemStack, Integer>(
            IngredientComponent.ITEMSTACK,
            itemStack.copy(),
            matchFlags,
            blacklist,
            itemStack == null && !blacklist,
            itemStack.stackSize,
            exactAmount) {

            @Override
            public boolean test(@Nullable ItemStack input) {
                boolean result = areItemStackEqual(input, itemStack, checkStackSize, true, checkDamage, checkNbt);
                if (blacklist) {
                    result = !result;
                }
                return result;
            }
        };
    }

    public static IngredientPredicate<ItemStack, Integer> matchItemStacks(
        final IValueTypeListProxy<ValueObjectTypeItemStack, ValueObjectTypeItemStack.ValueItemStack> itemStacks,
        final boolean checkItem, final boolean checkStackSize, final boolean checkDamage, final boolean checkNbt,
        final boolean blacklist, final int amount, final boolean exactAmount) {
        return new IngredientPredicateItemStackList(
            blacklist,
            amount,
            exactAmount,
            itemStacks,
            checkStackSize,
            checkItem,
            checkDamage,
            checkNbt);
    }

    public static IngredientPredicate<ItemStack, Integer> matchPredicateItem(final PartTarget partTarget,
        final IOperator predicate, final int amount, final boolean exactAmount) {
        return new IngredientPredicateItemStackOperator(amount, exactAmount, predicate, partTarget);
    }

    public static IngredientPredicate<ItemStack, Integer> matchBlocks(
        final IValueTypeListProxy<ValueObjectTypeBlock, ValueObjectTypeBlock.ValueBlock> blocks,
        final boolean checkItem, final boolean checkStackSize, final boolean checkDamage, final boolean checkNbt,
        final boolean blacklist, final int amount, final boolean exactAmount) {
        return new IngredientPredicateBlockList(
            blacklist,
            amount,
            exactAmount,
            blocks,
            checkStackSize,
            checkItem,
            checkDamage,
            checkNbt);
    }

    public static IngredientPredicate<ItemStack, Integer> matchPredicateBlock(final PartTarget partTarget,
        final IOperator predicate, final int amount, final boolean exactAmount) {
        return new IngredientPredicateBlockOperator(amount, exactAmount, predicate, partTarget);
    }

    public static IngredientPredicate<ItemStack, Integer> matchNbt(final NBTTagCompound tag, final boolean subset,
        final boolean superset, final boolean requireNbt, final boolean recursive, final boolean blacklist,
        final int amount, final boolean exactAmount) {
        return new IngredientPredicateItemStackNbt(
            blacklist,
            amount,
            exactAmount,
            requireNbt,
            subset,
            tag,
            recursive,
            superset);
    }

    public static boolean areItemStackEqual(ItemStack stackA, ItemStack stackB, boolean checkStackSize,
        boolean checkItem, boolean checkDamage, boolean checkNbt) {
        if (stackA == null && stackB == null) return true;
        if (stackA != null && stackB != null) {
            if (checkStackSize && stackA.stackSize != stackB.stackSize) return false;
            if (checkItem && stackA.getItem() != stackB.getItem()) return false;
            if (checkDamage && stackA.getItemDamage() != stackB.getItemDamage()) return false;
            if (checkNbt && !ItemStack.areItemStackTagsEqual(stackA, stackB)) return false;
            return true;
        }
        return false;
    }

    /**
     * Place item blocks from the given source in the world.
     *
     * @param network            The network in which the movement is happening.
     * @param ingredientsNetwork The ingredients network in which the movement is happening.
     * @param channel            The channel.
     * @param connection         The connection object.
     * @param source             The source item storage.
     * @param world              The destination world.
     * @param pos                The destination position.
     * @param side               The destination side.
     * @param itemStackMatcher   The itemstack match predicate.
     * @param blockUpdate        If a block update should occur after placement.
     * @param ignoreReplacable   If replacable blocks should be overriden when placing blocks.
     * @param craftIfFailed      If the exact ingredient from ingredientPredicate should be crafted if transfer failed.
     * @return The placed item.
     * @throws EvaluationException If illegal movement occured and further movement should stop.
     */
    public static ItemStack placeItems(INetwork network,
        IPositionedAddonsNetworkIngredients<ItemStack, Integer> ingredientsNetwork, int channel,
        ITunnelConnection connection, IIngredientComponentStorage<ItemStack, Integer> source, World world, BlockPos pos,
        ForgeDirection side, IngredientPredicate<ItemStack, Integer> itemStackMatcher, boolean blockUpdate,
        boolean ignoreReplacable, boolean craftIfFailed) throws EvaluationException {
        Block block = pos.getBlock(world);
        final Material destMaterial = block.getMaterial();
        final boolean isDestNonSolid = !destMaterial.isSolid();
        final boolean isDestReplaceable = block.isReplaceable(world, pos.getX(), pos.getY(), pos.getZ());
        if (!world.isAirBlock(pos.getX(), pos.getY(), pos.getZ())
            && (!isDestNonSolid || !(ignoreReplacable && isDestReplaceable))) {
            return null;
        }

        IIngredientComponentStorage<ItemStack, Integer> destinationBlock = new ItemStorageBlockWrapper(
            true,
            (WorldServer) world,
            pos,
            side,
            blockUpdate,
            0,
            false,
            ignoreReplacable,
            true);
        return TunnelHelpers.moveSingleStateOptimized(
            network,
            ingredientsNetwork,
            channel,
            connection,
            source,
            -1,
            destinationBlock,
            -1,
            itemStackMatcher,
            PartPos.of(world, pos, side),
            craftIfFailed);
    }

    /**
     * Pick up item blocks from the given source in the world.
     *
     * @param network            The network in which the movement is happening.
     * @param ingredientsNetwork The ingredients network in which the movement is happening.
     * @param channel            The channel.
     * @param connection         The connection object.
     * @param world              The destination world.
     * @param pos                The destination position.
     * @param side               The destination side.
     * @param destination        The destination item storage.
     * @param itemStackMatcher   The itemstack match predicate.
     * @param blockUpdate        If a block update should occur after placement.
     * @param ignoreReplacable   If replacable blocks should be ignored from picking up.
     * @param fortune            The fortune level.
     * @param silkTouch          If the block should be broken with silk touch.
     * @param breakOnNoDrops     If the block should be broken if it produced no drops.
     * @return The picked-up items.
     * @throws EvaluationException If illegal movement occured and further movement should stop.
     */
    public static List<ItemStack> pickUpItems(INetwork network,
        IPositionedAddonsNetworkIngredients<ItemStack, Integer> ingredientsNetwork, int channel,
        ITunnelConnection connection, World world, BlockPos pos, ForgeDirection side,
        IIngredientComponentStorage<ItemStack, Integer> destination,
        IngredientPredicate<ItemStack, Integer> itemStackMatcher, boolean blockUpdate, boolean ignoreReplacable,
        int fortune, boolean silkTouch, boolean breakOnNoDrops) throws EvaluationException {
        Block block = pos.getBlock(world);
        final Material destMaterial = block.getMaterial();
        final boolean isDestReplaceable = block.isReplaceable(world, pos.getX(), pos.getY(), pos.getZ());
        if (world.isAirBlock(pos.getX(), pos.getY(), pos.getZ())
            || ((ignoreReplacable && isDestReplaceable) || destMaterial.isLiquid())) {
            return null;
        }

        ItemStorageBlockWrapper sourceBlock = new ItemStorageBlockWrapper(
            false,
            (WorldServer) world,
            pos,
            side,
            blockUpdate,
            fortune,
            silkTouch,
            ignoreReplacable,
            breakOnNoDrops);
        List<ItemStack> itemStacks = Lists.newArrayList();
        while (true) {
            ItemStack itemStack = TunnelHelpers.moveSingleStateOptimized(
                network,
                ingredientsNetwork,
                channel,
                connection,
                sourceBlock,
                -1,
                destination,
                -1,
                itemStackMatcher,
                PartPos.of(world, pos, side),
                false);

            if (itemStack == null || itemStack.stackSize <= 0 || itemStack.getItem() == null) {
                break;
            }
            itemStacks.add(itemStack);
        }

        // In some cases, the storage may still have cached drop.
        // In that case, make sure we insert or drop them, and DESTROY the block.
        List<ItemStack> cachedDrops = sourceBlock.getCachedDrops();
        if (sourceBlock.isExtracted() && cachedDrops != null) {
            Iterator<ItemStack> it = cachedDrops.iterator();
            while (it.hasNext()) {
                ItemStack cachedStack = it.next();
                if (cachedStack != null) {
                    ItemStack remaining = destination.insert(cachedStack, false);
                    if (GeneralConfig.ejectItemsOnBlockDropOverflow) {
                        ItemHelpers.spawnItemStack(world, pos, remaining);
                    }
                    it.remove();
                }
            }
            sourceBlock.postExtract();
        }

        return itemStacks;
    }

    /**
     * Helper function to get a copy of the given stack with the given stacksize.
     *
     * @param prototype A prototype stack.
     * @param count     A new stacksize.
     * @return A copy of the given stack with the given count.
     */
    public static ItemStack prototypeWithCount(ItemStack prototype, int count) {
        if (prototype.stackSize != count) {
            if (prototype == null) {
                return new ItemStack(Items.apple, count);
            } else {
                prototype = prototype.copy();
                prototype.stackSize = count;
            }
        }
        return prototype;
    }

}
