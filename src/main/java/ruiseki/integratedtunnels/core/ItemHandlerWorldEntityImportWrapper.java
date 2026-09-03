package ruiseki.integratedtunnels.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.commoncapabilities.api.ingredient.IIngredientMatcher;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.integratedtunnels.GeneralConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.ingredient.collection.FilteredIngredientCollectionIterator;

/**
 * An item handler for importing item entities from the world.
 *
 * @author rubensworks
 */
public class ItemHandlerWorldEntityImportWrapper implements IIngredientComponentStorage<ItemStack, Integer> {

    private final WorldServer world;
    private final BlockPos pos;
    private final ForgeDirection facing;
    private final List<EntityItem> entities;

    public ItemHandlerWorldEntityImportWrapper(WorldServer world, BlockPos pos, ForgeDirection facing,
        final boolean ignorePickupDelay) {
        this(
            world,
            pos,
            facing,
            AxisAlignedBB
                .getBoundingBox(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1),
            ignorePickupDelay);
    }

    @SuppressWarnings("unchecked")
    public ItemHandlerWorldEntityImportWrapper(WorldServer world, BlockPos pos, ForgeDirection facing,
        AxisAlignedBB area, final boolean ignorePickupDelay) {
        this.world = world;
        this.pos = pos;
        this.facing = facing;
        this.entities = (List<EntityItem>) (List<?>) world
            .getEntitiesWithinAABBExcludingEntity(null, area, new IEntitySelector() {

                @Override
                public boolean isEntityApplicable(Entity entity) {
                    if (!(entity instanceof EntityItem itemEntity) || entity.isDead) {
                        return false;
                    }
                    return ignorePickupDelay || itemEntity.delayBeforeCanPickup <= 0;
                }
            });
    }

    public List<EntityItem> getEntities() {
        return entities;
    }

    @Override
    public IngredientComponent<ItemStack, Integer> getComponent() {
        return IngredientComponent.ITEMSTACK;
    }

    @Override
    public Iterator<ItemStack> iterator() {
        List<ItemStack> stacks = new ArrayList<ItemStack>();
        for (EntityItem entity : this.entities) {
            ItemStack stack = entity.getEntityItem();
            if (!ItemHelpers.isEmpty(stack)) {
                stacks.add(stack);
            }
        }
        return stacks.iterator();
    }

    @Override
    public Iterator<ItemStack> iterator(ItemStack prototype, Integer matchCondition) {
        return new FilteredIngredientCollectionIterator<ItemStack, Integer>(
            this,
            getComponent().getMatcher(),
            prototype,
            matchCondition);
    }

    @Override
    public long getMaxQuantity() {
        return 64L * entities.size();
    }

    @Override
    public ItemStack insert(ItemStack ingredient, boolean simulate) {
        return ingredient;
    }

    protected void postExtract(EntityItem entity, ItemStack itemStack) {
        if (ItemHelpers.isEmpty(itemStack)) {
            entity.setDead();
        } else {
            entity.setEntityItemStack(itemStack);
        }
        if (GeneralConfig.worldInteractionEvents) {
            world.playAuxSFX(1000, pos.getX(), pos.getY(), pos.getZ(), 0); // Sound
            BlockPos targetPos = pos.offset(facing.getOpposite());
            world.playAuxSFX(
                2000,
                targetPos.getX(),
                targetPos.getY(),
                targetPos.getZ(),
                facing.offsetX + 1 + (facing.offsetZ + 1) * 3); // Particles
        }
    }

    @Override
    public ItemStack extract(ItemStack prototype, Integer matchCondition, boolean simulate) {
        if (ItemHelpers.isEmpty(prototype) || this.entities.isEmpty()) {
            return ItemHelpers.EMPTY;
        }

        IIngredientMatcher<ItemStack, Integer> matcher = getComponent().getMatcher();
        Integer quantityFlag = getComponent().getPrimaryQuantifier()
            .getMatchCondition();
        Integer subMatchCondition = matcher.withoutCondition(
            matchCondition,
            getComponent().getPrimaryQuantifier()
                .getMatchCondition());

        for (EntityItem entity : this.entities) {
            ItemStack itemStack = entity.getEntityItem();

            if (!ItemHelpers.isEmpty(itemStack) && matcher.matches(prototype, itemStack, subMatchCondition)
                && (!matcher.hasCondition(matchCondition, quantityFlag)
                    || itemStack.stackSize >= prototype.stackSize)) {

                itemStack = ItemHelpers.copy(itemStack);

                int extractCount = Math.min(itemStack.stackSize, Helpers.castSafe(prototype.stackSize));
                ItemStack ret = ItemHelpers.split(itemStack, extractCount);

                if (!simulate) {
                    postExtract(entity, itemStack);
                }

                return ret;
            }
        }

        return ItemHelpers.EMPTY;
    }

    @Override
    public ItemStack extract(long maxQuantity, boolean simulate) {
        if (this.entities.isEmpty()) {
            return ItemHelpers.EMPTY;
        }

        EntityItem entity = this.entities.get(0);
        ItemStack itemStack = entity.getEntityItem();

        if (ItemHelpers.isEmpty(itemStack)) {
            return ItemHelpers.EMPTY;
        }

        itemStack = ItemHelpers.copy(itemStack);

        int extractCount = Math.min(itemStack.stackSize, Helpers.castSafe(maxQuantity));
        ItemStack ret = ItemHelpers.split(itemStack, extractCount);

        if (!simulate) {
            postExtract(entity, itemStack);
        }

        return ret;
    }
}
