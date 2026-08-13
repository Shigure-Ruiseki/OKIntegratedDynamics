package ruiseki.integratedtunnels.core;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Iterators;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.ItemStackHelpers;
import ruiseki.okcore.inventory.PlayerInventoryIterator;

/**
 * An item storage for player interaction simulation (1.7.10 GTNH Compatible).
 *
 * @author rubensworks
 */
public class ItemStoragePlayerWrapper implements IIngredientComponentStorage<ItemStack, Integer> {

    private static final IEntitySelector CAN_BE_ATTACKED = new IEntitySelector() {

        @Override
        public boolean isEntityApplicable(Entity entity) {
            return entity.canAttackWithItem();
        }
    };

    private final FakePlayer player;
    private final WorldServer world;
    private final BlockPos pos;
    private final float offsetX;
    private final float offsetY;
    private final float offsetZ;
    private final ForgeDirection side;
    private final boolean rightClick;
    private final boolean sneaking;
    private final boolean continuousClick;
    private final int entityIndex;
    private final IIngredientComponentStorage<ItemStack, Integer> playerReturnHandler;

    public ItemStoragePlayerWrapper(@Nullable FakePlayer player, WorldServer world, BlockPos pos, double offsetX,
        double offsetY, double offsetZ, ForgeDirection side, boolean rightClick, boolean sneaking,
        boolean continuousClick, int entityIndex, IIngredientComponentStorage<ItemStack, Integer> playerReturnHandler) {
        this.player = player;
        this.world = world;
        this.pos = pos;
        this.continuousClick = continuousClick;
        this.entityIndex = entityIndex;
        this.offsetX = (float) offsetX;
        this.offsetY = (float) offsetY;
        this.offsetZ = (float) offsetZ;
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
            if (itemStack != null && itemStack.stackSize > 0) {
                ItemStack remaining = this.playerReturnHandler.insert(itemStack, false);
                ItemStackHelpers.spawnItemStackToPlayer(world, pos, remaining, player);
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
            // We can ALWAYS click with items, so consume the whole item when simulating.
            return null;
        }
        if (player == null) {
            return stack;
        }

        PlayerHelpers.setPlayerState(player, pos, offsetX, offsetY, offsetZ, side, sneaking);
        PlayerHelpers.setHeldItemSilent(player, stack);

        if (!continuousClick) {
            cancelDestroyingBlock(player);
        }

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        int sideOrdinal = side.ordinal();

        if (rightClick) {
            // 1. Use item first (onItemUseFirst)
            if (stack != null && stack.stackSize > 0) {
                if (stack.getItem()
                    .onItemUseFirst(stack, player, world, x, y, z, sideOrdinal, offsetX, offsetY, offsetZ)) {
                    returnPlayerInventory(player);
                    return null;
                }
            }

            // 2. Activate block
            Block block = world.getBlock(x, y, z);
            boolean doesSneakBypass = stack != null && stack.getItem()
                .doesSneakBypassUse(world, x, y, z, player);
            if (!player.isSneaking() || stack == null || doesSneakBypass) {
                if (block.onBlockActivated(world, x, y, z, player, sideOrdinal, offsetX, offsetY, offsetZ)) {
                    returnPlayerInventory(player);
                    return null;
                }
            }

            // 3. Right Click Event via Forge
            if (stack != null && stack.stackSize > 0) {
                PlayerInteractEvent event = ForgeEventFactory
                    .onPlayerInteract(player, PlayerInteractEvent.Action.RIGHT_CLICK_AIR, x, y, z, sideOrdinal, world);
                if (event.isCanceled()) {
                    return stack;
                }

                ItemStack copyBeforeUse = stack.copy();
                ItemStack result = stack.getItem()
                    .onItemRightClick(stack, world, player);
                if (result != copyBeforeUse || (result != null && result.stackSize != copyBeforeUse.stackSize)) {
                    PlayerHelpers.setHeldItemSilent(player, result);
                    if (result != null && result.stackSize <= 0) {
                        PlayerHelpers.setHeldItemSilent(player, null);
                        ForgeEventFactory.onPlayerDestroyItem(player, copyBeforeUse);
                    }
                    returnPlayerInventory(player);
                    return null;
                }
            }

            // 4. Use item on block (onItemUse)
            if (stack != null && stack.stackSize > 0) {
                BlockPos targetPos = pos;
                int reachDistance = MathHelper
                    .clamp_int((int) player.theItemInWorldManager.getBlockReachDistance(), 0, 10);
                int i = 0;
                while (i++ < reachDistance && world.isAirBlock(targetPos.getX(), targetPos.getY(), targetPos.getZ())) {
                    targetPos = targetPos.offset(side.getOpposite());
                }

                if (stack.getItem()
                    .onItemUse(
                        stack,
                        player,
                        world,
                        targetPos.getX(),
                        targetPos.getY(),
                        targetPos.getZ(),
                        sideOrdinal,
                        offsetX,
                        offsetY,
                        offsetZ)) {
                    if (stack.stackSize <= 0) {
                        PlayerHelpers.setHeldItemSilent(player, null);
                    }
                    returnPlayerInventory(player);
                    return null;
                }
            }

            // 5. Interact with entity
            AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
            @SuppressWarnings("unchecked")
            List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, box);
            if (entities != null && !entities.isEmpty()) {
                Entity entity = getEntity(entities);
                if (player.interactWith(entity)) {
                    returnPlayerInventory(player);
                    return null;
                }
            } else {
                returnPlayerInventory(player);
                return null;
            }
        } else {
            // Left click handling
            if (!world.isAirBlock(x, y, z)) {
                // Break block
                int durabilityRemaining = player.theItemInWorldManager.durabilityRemainingOnBlock;
                if (durabilityRemaining < 0) {
                    player.theItemInWorldManager.onBlockClicked(x, y, z, sideOrdinal);
                } else if (durabilityRemaining >= 9) {
                    player.theItemInWorldManager.tryHarvestBlock(x, y, z);
                    cancelDestroyingBlock(player);
                } else {
                    player.theItemInWorldManager.updateBlockRemoving();
                }
                returnPlayerInventory(player);
                return null;
            } else {
                // Attack entity
                cancelDestroyingBlock(player);

                AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
                @SuppressWarnings("unchecked")
                List<Entity> entities = world.selectEntitiesWithinAABB(Entity.class, box, CAN_BE_ATTACKED);
                if (entities != null && !entities.isEmpty()) {
                    Entity entity = getEntity(entities);
                    player.attackTargetEntityWithCurrentItem(entity);
                    returnPlayerInventory(player);
                    return null;
                } else {
                    return stack;
                }
            }
        }

        return stack;
    }

    @Override
    public ItemStack extract(ItemStack prototype, Integer matchCondition, boolean simulate) {
        return null;
    }

    @Override
    public ItemStack extract(long maxQuantity, boolean simulate) {
        return null;
    }
}
