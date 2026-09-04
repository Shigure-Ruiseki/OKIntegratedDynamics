package ruiseki.integratedtunnels.core;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.google.common.collect.Iterators;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.inventory.PlayerInventoryIterator;

/**
 * An item storage for player interaction simulation (1.7.10 Backport).
 *
 * @author rubensworks
 */
public class ItemStoragePlayerWrapper implements IIngredientComponentStorage<ItemStack, Integer> {

    private final ExtendedFakePlayer player;
    private final WorldServer world;
    private final BlockPos pos;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final ForgeDirection side;
    private final boolean rightClick;
    private final boolean sneaking;
    private final boolean continuousClick;
    private final int entityIndex;
    private final IIngredientComponentStorage<ItemStack, Integer> playerReturnHandler;

    public ItemStoragePlayerWrapper(@Nullable ExtendedFakePlayer player, WorldServer world, BlockPos pos,
        double offsetX, double offsetY, double offsetZ, ForgeDirection side, boolean rightClick, boolean sneaking,
        boolean continuousClick, int entityIndex, IIngredientComponentStorage<ItemStack, Integer> playerReturnHandler) {
        this.player = player;
        this.world = world;
        this.pos = pos;
        this.continuousClick = continuousClick;
        this.entityIndex = entityIndex;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.side = side;
        this.rightClick = rightClick;
        this.sneaking = sneaking;
        this.playerReturnHandler = playerReturnHandler;
    }

    public static void cancelDestroyingBlock(EntityPlayerMP player) {
        player.theItemInWorldManager.cancelDestroyingBlock(0, 0, 0);
        player.theItemInWorldManager.durabilityRemainingOnBlock = -1;
    }

    protected Entity getEntity(List<Entity> entities) {
        if (this.entityIndex < 0) {
            return entities.get(world.rand.nextInt(entities.size()));
        }
        return entities.get(Math.min(this.entityIndex, entities.size() - 1));
    }

    private void returnPlayerInventory(EntityPlayer player) {
        PlayerInventoryIterator it = new PlayerInventoryIterator(player);
        while (it.hasNext()) {
            ItemStack itemStack = it.next();
            if (!ItemHelpers.isEmpty(itemStack)) {
                ItemStack remaining = this.playerReturnHandler.insert(itemStack, false);
                ItemHelpers.spawnItemStackToPlayer(world, pos, remaining, player);
                it.remove();
            }
        }
    }

    @Override
    public IngredientComponent<ItemStack, Integer> getComponent() {
        return IngredientComponent.ITEMSTACK;
    }

    @Override
    public Iterator<ItemStack> iterator() {
        return Iterators.emptyIterator();
    }

    @Override
    public Iterator<ItemStack> iterator(@Nonnull ItemStack prototype, Integer matchCondition) {
        return iterator();
    }

    @Override
    public long getMaxQuantity() {
        return 1;
    }

    @Override
    public ItemStack insert(@Nonnull ItemStack stack, boolean simulate) {
        if (simulate) {
            return ItemHelpers.EMPTY;
        }
        if (player == null) {
            return stack;
        }

        PlayerHelpers.setPlayerState(player, pos, (float) offsetX, (float) offsetY, (float) offsetZ, side, sneaking);
        PlayerHelpers.setHeldItemSilent(player, stack.copy());

        if (!continuousClick) {
            cancelDestroyingBlock(player);
        }

        if (rightClick) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            int sideOrdinal = side.ordinal();

            @SuppressWarnings("unchecked")
            List<Entity> targetEntities = world
                .getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1));
            if (!targetEntities.isEmpty()) {
                Entity targetEntity = getEntity(targetEntities);

                // Chặn tương tác với Dân làng
                if (targetEntity instanceof EntityVillager) {
                    return stack;
                }

                if (targetEntity instanceof EntityTameable) {
                    EntityTameable tameable = (EntityTameable) targetEntity;
                    if (!tameable.isTamed()) {
                        return stack;
                    }
                }

                if (targetEntity.interactFirst(player)) {
                    returnPlayerInventory(player);
                    return ItemHelpers.EMPTY;
                }
            }

            // 1. Fire Event Forge
            PlayerInteractEvent.Action action = pos.isAirBlock(world) ? PlayerInteractEvent.Action.RIGHT_CLICK_AIR
                : PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK;
            PlayerInteractEvent event = ForgeEventFactory.onPlayerInteract(player, action, x, y, z, sideOrdinal, world);
            if (event.isCanceled()) {
                return stack;
            }

            // 2. Item Use First
            if (!ItemHelpers.isEmpty(stack) && stack.getItem()
                .onItemUseFirst(
                    stack,
                    player,
                    world,
                    x,
                    y,
                    z,
                    sideOrdinal,
                    (float) offsetX,
                    (float) offsetY,
                    (float) offsetZ)) {
                returnPlayerInventory(player);
                return ItemHelpers.EMPTY;
            }

            // 3. Block Activation
            Block block = world.getBlock(x, y, z);
            if (!player.isSneaking() || ItemHelpers.isEmpty(stack)) {
                if (block.onBlockActivated(
                    world,
                    x,
                    y,
                    z,
                    player,
                    sideOrdinal,
                    (float) offsetX,
                    (float) offsetY,
                    (float) offsetZ)) {
                    returnPlayerInventory(player);
                    return ItemHelpers.EMPTY;
                }
            }

            // 4. ON ITEM USE (Bonemeal)
            if (!ItemHelpers.isEmpty(stack)) {
                if (stack.getItem()
                    .onItemUse(
                        stack,
                        player,
                        world,
                        x,
                        y,
                        z,
                        sideOrdinal,
                        (float) offsetX,
                        (float) offsetY,
                        (float) offsetZ)) {

                    if (stack.stackSize <= 0) {
                        PlayerHelpers.setHeldItemSilent(player, ItemHelpers.EMPTY);
                    }

                    returnPlayerInventory(player);
                    return ItemHelpers.EMPTY;
                }
            }

            // 5. Food, Bow, Potion
            if (!ItemHelpers.isEmpty(stack)) {
                ItemStack result = stack.useItemRightClick(world, player);
                PlayerHelpers.setHeldItemSilent(player, result);
                returnPlayerInventory(player);
                return ItemHelpers.EMPTY;
            }
        } else {
            // Left-click / Attack sequence
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();

            PlayerInteractEvent event = ForgeEventFactory
                .onPlayerInteract(player, PlayerInteractEvent.Action.LEFT_CLICK_BLOCK, x, y, z, side.ordinal(), world);
            if (event.isCanceled()) {
                world.markBlockForUpdate(x, y, z);
                return stack;
            }

            if (!world.isAirBlock(x, y, z)) {
                Block block = world.getBlock(x, y, z);
                int destroyProgress = player.theItemInWorldManager.durabilityRemainingOnBlock;

                if (destroyProgress < 0) {
                    block.onBlockClicked(world, x, y, z, player);
                    float hardness = block.getPlayerRelativeBlockHardness(player, world, x, y, z);
                    if (hardness >= 1.0F) {
                        player.theItemInWorldManager.tryHarvestBlock(x, y, z);
                    } else {
                        player.theItemInWorldManager.initialDamage = player.theItemInWorldManager.curblockDamage;
                        player.theItemInWorldManager.isDestroyingBlock = true;
                        player.theItemInWorldManager.posX = x;
                        player.theItemInWorldManager.posY = y;
                        player.theItemInWorldManager.posZ = z;
                        player.theItemInWorldManager.durabilityRemainingOnBlock = (int) (hardness * 10.0F);
                    }
                } else if (destroyProgress >= 9) {
                    player.theItemInWorldManager.tryHarvestBlock(x, y, z);
                    cancelDestroyingBlock(player);
                } else {
                    player.theItemInWorldManager.updateBlockRemoving();
                }
                returnPlayerInventory(player);
                return ItemHelpers.EMPTY;
            } else {
                cancelDestroyingBlock(player);

                @SuppressWarnings("unchecked")
                List<Entity> entities = world
                    .getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1));
                if (!entities.isEmpty()) {
                    Entity entity = getEntity(entities);
                    if (entity.canAttackWithItem()) {
                        player.attackTargetEntityWithCurrentItem(entity);
                        returnPlayerInventory(player);
                        return ItemHelpers.EMPTY;
                    }
                }
            }
        }

        return stack;
    }

    @Override
    public ItemStack extract(@Nonnull ItemStack prototype, Integer matchCondition, boolean simulate) {
        return ItemHelpers.EMPTY;
    }

    @Override
    public ItemStack extract(long maxQuantity, boolean simulate) {
        return ItemHelpers.EMPTY;
    }
}
