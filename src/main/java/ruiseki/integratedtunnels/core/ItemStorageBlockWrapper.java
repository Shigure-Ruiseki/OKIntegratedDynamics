package ruiseki.integratedtunnels.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import ruiseki.commoncapabilities.api.ingredient.IIngredientMatcher;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.integratedtunnels.GeneralConfig;
import ruiseki.integratedtunnels.IntegratedTunnels;
import ruiseki.integratedtunnels.api.world.IBlockBreakHandler;
import ruiseki.integratedtunnels.api.world.IBlockBreakHandlerRegistry;
import ruiseki.integratedtunnels.api.world.IBlockPlaceHandler;
import ruiseki.integratedtunnels.api.world.IBlockPlaceHandlerRegistry;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.BlockHelpers;
import ruiseki.okcore.helper.BlockStateHelpers;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.ingredient.collection.FilteredIngredientCollectionIterator;

/**
 * An item storage for world block placement (GTNH IBlockState Compatible).
 *
 * @author rubensworks
 */
public class ItemStorageBlockWrapper implements IIngredientComponentStorage<ItemStack, Integer> {

    private final boolean writeOnly;
    private final WorldServer world;
    private final BlockPos pos;
    private final ForgeDirection side;
    private final boolean blockUpdate;
    private final int fortune;
    private final boolean silkTouch;
    private final boolean ignoreReplacable;
    private final boolean breakOnNoDrops;

    private IBlockBreakHandler blockBreakHandler = null;
    private List<ItemStack> cachedDrops = null;
    private boolean extracted = false;

    public ItemStorageBlockWrapper(boolean writeOnly, WorldServer world, BlockPos pos, ForgeDirection side,
        boolean blockUpdate, int fortune, boolean silkTouch, boolean ignoreReplacable, boolean breakOnNoDrops) {
        this.writeOnly = writeOnly;
        this.world = world;
        this.pos = pos;
        this.side = side;
        this.blockUpdate = blockUpdate;
        this.fortune = fortune;
        this.silkTouch = silkTouch;
        this.ignoreReplacable = ignoreReplacable;
        this.breakOnNoDrops = breakOnNoDrops;
    }

    protected void sendBlockUpdate() {
        world.notifyBlocksOfNeighborChange(pos.getX(), pos.getY(), pos.getZ(), Blocks.air);
    }

    protected IBlockBreakHandler getBlockBreakHandler(BlockState state, World world, BlockPos pos,
        EntityPlayer player) {
        return IntegratedTunnels._instance.getRegistryManager()
            .getRegistry(IBlockBreakHandlerRegistry.class)
            .getHandler(state, world, pos, player);
    }

    protected void removeBlock(BlockState state, EntityPlayer player) {
        Block block = state.getBlock();
        int meta = state.getBlockMeta(0);

        if (blockBreakHandler != null) {
            blockBreakHandler.breakBlock(state, world, pos, player);
        } else {
            block.removedByPlayer(world, player, pos.getX(), pos.getY(), pos.getZ(), false);
        }
        if (GeneralConfig.worldInteractionEvents) {
            world.playAuxSFX(2001, pos.getX(), pos.getY(), pos.getZ(), Block.getIdFromBlock(block) + (meta << 12));
        }
        if (blockUpdate) {
            sendBlockUpdate();
        }
    }

    public boolean isExtracted() {
        return extracted;
    }

    @Nullable
    public List<ItemStack> getCachedDrops() {
        return cachedDrops;
    }

    protected List<ItemStack> getItemStacks() {
        if (writeOnly) {
            if (!world.isAirBlock(pos.getX(), pos.getY(), pos.getZ())) {
                BlockState state = BlockStateHelpers.getState(world, pos);
                Block block = state.getBlock();
                boolean isDestReplaceable = block.isReplaceable(world, pos.getX(), pos.getY(), pos.getZ());
                if (!isDestReplaceable || !ignoreReplacable) {
                    return Lists.newArrayList(BlockHelpers.getItemStackFromBlockState(state));
                }
            }
        } else {
            if (cachedDrops != null) {
                return cachedDrops;
            }
            if (!world.isAirBlock(pos.getX(), pos.getY(), pos.getZ())) {
                BlockState state = BlockStateHelpers.getState(world, pos);
                Block block = state.getBlock();
                int meta = state.getBlockMeta(0);

                EntityPlayer player = PlayerHelpers.getFakePlayer(world);

                blockBreakHandler = getBlockBreakHandler(state, world, pos, player);
                if (blockBreakHandler != null) {
                    cachedDrops = blockBreakHandler.getDrops(state, world, pos, player);
                } else {
                    BlockEvent.BreakEvent blockBreakEvent = new BlockEvent.BreakEvent(
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        world,
                        block,
                        meta,
                        player);
                    if (!MinecraftForge.EVENT_BUS.post(blockBreakEvent)) {
                        boolean doSilkTouch = silkTouch
                            && block.canSilkHarvest(world, player, pos.getX(), pos.getY(), pos.getZ(), meta);
                        ArrayList<ItemStack> drops;
                        if (doSilkTouch) {
                            Item item = Item.getItemFromBlock(block);
                            if (item != null) {
                                int damage = block.damageDropped(meta);
                                drops = Lists.newArrayList(new ItemStack(item, 1, damage));
                            } else {
                                drops = new ArrayList<>();
                            }
                        } else {
                            drops = Lists
                                .newArrayList(block.getDrops(world, pos.getX(), pos.getY(), pos.getZ(), meta, fortune));
                        }
                        float dropChance = ForgeEventFactory.fireBlockHarvesting(
                            drops,
                            world,
                            block,
                            pos.getX(),
                            pos.getY(),
                            pos.getZ(),
                            meta,
                            fortune,
                            1.0F,
                            doSilkTouch,
                            player);
                        if (drops.isEmpty()) {
                            if (breakOnNoDrops) {
                                removeBlock(state, player);
                            }
                            drops = new ArrayList<>();
                        } else {
                            drops.removeIf(ItemHelpers::isEmpty);
                        }
                        if (world.rand.nextFloat() <= dropChance) {
                            return cachedDrops = drops;
                        }
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    protected IBlockPlaceHandler getBlockPlaceHandler(ItemStack itemStack, World world, BlockPos pos,
        ForgeDirection side, float hitX, float hitY, float hitZ, EntityPlayer player) {
        return IntegratedTunnels._instance.getRegistryManager()
            .getRegistry(IBlockPlaceHandlerRegistry.class)
            .getHandler(itemStack, world, pos, side, hitX, hitY, hitZ, player);
    }

    protected ItemStack setItemStack(ItemStack itemStack, boolean simulate) {
        if (!ItemHelpers.isEmpty(itemStack) && itemStack.stackSize == 1) {
            Item item = itemStack.getItem();
            if (item instanceof ItemBlock itemBlock) {

                EntityPlayer player = PlayerHelpers.getFakePlayer(world);

                ForgeDirection opposite = side.getOpposite();
                IBlockPlaceHandler blockPlaceHandler = getBlockPlaceHandler(
                    itemStack,
                    world,
                    pos,
                    opposite,
                    0.5F,
                    0.5F,
                    0.5F,
                    player);

                if (blockPlaceHandler != null) {
                    if (!simulate) {
                        blockPlaceHandler.placeBlock(itemStack, world, pos, opposite, 0.5F, 0.5F, 0.5F, player);
                    }
                    return ItemHelpers.EMPTY;
                } else {
                    Block block = itemBlock.field_150939_a;

                    // Check if placement on the targeted face is valid
                    boolean canPlace = world.canPlaceEntityOnSide(
                        block,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        false,
                        opposite.ordinal(),
                        player,
                        itemStack);

                    if (canPlace) {
                        if (simulate) {
                            // Successfully validated simulation step without altering the world
                            return ItemHelpers.EMPTY;
                        }

                        // Pass target pos directly: onItemUse handles side offsets internally
                        if (itemBlock.onItemUse(
                            itemStack,
                            player,
                            world,
                            pos.getX(),
                            pos.getY(),
                            pos.getZ(),
                            opposite.ordinal(),
                            0.5F,
                            0.5F,
                            0.5F)) {

                            if (GeneralConfig.worldInteractionEvents) {
                                BlockState placedState = BlockStateHelpers.getState(world, pos);
                                Block placedBlock = placedState.getBlock();
                                world.playSoundEffect(
                                    pos.getX() + 0.5D,
                                    pos.getY() + 0.5D,
                                    pos.getZ() + 0.5D,
                                    placedBlock.stepSound.soundName,
                                    (placedBlock.stepSound.getVolume() + 1.0F) / 2.0F,
                                    placedBlock.stepSound.getPitch() * 0.8F);
                            }

                            if (blockUpdate) {
                                sendBlockUpdate();
                            }

                            return ItemHelpers.EMPTY;
                        }
                    }
                }
            }
        }
        return itemStack;
    }

    @Override
    public IngredientComponent<ItemStack, Integer> getComponent() {
        return IngredientComponent.ITEMSTACK;
    }

    @Override
    public Iterator<ItemStack> iterator() {
        return getItemStacks().iterator();
    }

    @Override
    public Iterator<ItemStack> iterator(@Nonnull ItemStack prototype, Integer matchCondition) {
        return new FilteredIngredientCollectionIterator<>(this, getComponent().getMatcher(), prototype, matchCondition);
    }

    @Override
    public long getMaxQuantity() {
        return 1;
    }

    @Override
    public ItemStack insert(@Nonnull ItemStack stack, boolean simulate) {
        if (ItemHelpers.isEmpty(stack)) {
            return ItemHelpers.EMPTY;
        }

        List<ItemStack> itemStacks = getItemStacks();
        if (!itemStacks.isEmpty()) {
            ItemStack itemStack = itemStacks.get(0);
            if (!ItemHelpers.isEmpty(itemStack)) {
                return stack;
            }
        }

        ItemStack remaining = ItemHelpers.copy(stack);
        ItemStack single = ItemHelpers.split(remaining, 1);

        if (ItemHelpers.isEmpty(setItemStack(single, simulate))) {
            return ItemHelpers.isEmpty(remaining) ? ItemHelpers.EMPTY : remaining;
        }

        return stack;
    }

    public void postExtract() {
        boolean allEmpty = true;
        for (ItemStack stack : getItemStacks()) {
            if (!ItemHelpers.isEmpty(stack)) {
                allEmpty = false;
                break;
            }
        }
        if (allEmpty) {
            BlockState state = BlockStateHelpers.getState(world, pos);
            EntityPlayer player = PlayerHelpers.getFakePlayer(world);
            removeBlock(state, player);
        }
    }

    @Override
    public ItemStack extract(@Nonnull ItemStack prototype, Integer matchCondition, boolean simulate) {
        if (ItemHelpers.isEmpty(prototype)) {
            return ItemHelpers.EMPTY;
        }

        IIngredientMatcher<ItemStack, Integer> matcher = getComponent().getMatcher();
        Integer quantityFlag = getComponent().getPrimaryQuantifier()
            .getMatchCondition();
        Integer subMatchCondition = matcher.withoutCondition(matchCondition, quantityFlag);
        List<ItemStack> itemStacks = getItemStacks();

        if (itemStacks.isEmpty()) {
            return ItemHelpers.EMPTY;
        }

        ListIterator<ItemStack> it = itemStacks.listIterator();
        while (it.hasNext()) {
            ItemStack itemStack = it.next();
            if (!ItemHelpers.isEmpty(itemStack) && matcher.matches(prototype, itemStack, subMatchCondition)
                && (!matcher.hasCondition(matchCondition, quantityFlag)
                    || itemStack.stackSize >= prototype.stackSize)) {

                itemStack = ItemHelpers.copy(itemStack);
                int extractCount = Math.min(itemStack.stackSize, Helpers.castSafe(prototype.stackSize));
                ItemStack ret = ItemHelpers.split(itemStack, extractCount);

                if (!simulate) {
                    if (ItemHelpers.isEmpty(itemStack)) {
                        it.remove();
                    } else {
                        it.set(itemStack);
                    }
                    this.extracted = true;
                    postExtract();
                }

                return ret;
            }
        }

        return ItemHelpers.EMPTY;
    }

    @Override
    public ItemStack extract(long maxQuantity, boolean simulate) {
        List<ItemStack> itemStacks = getItemStacks();
        if (itemStacks.isEmpty()) {
            return ItemHelpers.EMPTY;
        }

        ItemStack itemStack = itemStacks.get(0);
        if (ItemHelpers.isEmpty(itemStack)) {
            return ItemHelpers.EMPTY;
        }

        itemStack = ItemHelpers.copy(itemStack);
        int extractCount = Math.min(itemStack.stackSize, Helpers.castSafe(maxQuantity));
        ItemStack ret = ItemHelpers.split(itemStack, extractCount);

        if (!simulate) {
            if (ItemHelpers.isEmpty(itemStack)) {
                itemStacks.remove(0);
            } else {
                itemStacks.set(0, itemStack);
            }
            this.extracted = true;
            postExtract();
        }

        return ret;
    }
}
