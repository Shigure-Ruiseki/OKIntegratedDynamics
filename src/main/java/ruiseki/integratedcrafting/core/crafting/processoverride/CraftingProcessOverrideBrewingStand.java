package ruiseki.integratedcrafting.core.crafting.processoverride;

import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.commoncapabilities.api.ingredient.IMixedIngredients;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integratedcrafting.api.crafting.ICraftingProcessOverride;
import ruiseki.integratedcrafting.api.crafting.ICraftingResultsSink;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.TileHelpers;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.handler.IItemHandler;

/**
 * A crafting process override for brewing stands
 * Overrides side restrictions, allowing dynamic insertion into valid slots.
 * 
 * @author rubensworks
 */
public class CraftingProcessOverrideBrewingStand implements ICraftingProcessOverride {

    private static final ForgeDirection SIDE_INGREDIENT = ForgeDirection.UP;
    private static final ForgeDirection SIDE_BOTTLE = ForgeDirection.NORTH;

    @Override
    public boolean isApplicable(PartPos target) {
        return getTile(target) != null;
    }

    @Nullable
    private TileEntityBrewingStand getTile(PartPos target) {
        if (target == null || target.getPos() == null) {
            return null;
        }
        return TileHelpers.getSafeTile(target.getPos(), TileEntityBrewingStand.class);
    }

    /**
     * Checks if a stack is a valid ingredient for the top brewing slot (Slot 3).
     */
    private boolean isValidIngredient(ItemStack stack) {
        return stack != null && stack.getItem() != null
            && stack.getItem()
                .isPotionIngredient(stack);
    }

    /**
     * Checks if a stack is a valid container/potion for bottle slots (Slots 0, 1, 2).
     */
    private boolean isValidBottle(ItemStack stack) {
        return stack != null && stack.getItem() != null
            && (stack.getItem() instanceof ItemPotion || stack.getItem() == Items.glass_bottle);
    }

    @Override
    public boolean craft(Function<IngredientComponent<?, ?>, PartPos> targetGetter, IMixedIngredients ingredients,
        ICraftingResultsSink resultsSink, boolean simulate) {

        List<ItemStack> instances = ingredients.getInstances(IngredientComponent.ITEMSTACK);
        if (instances.size() != 4 || ingredients.getComponents()
            .size() != 1) {
            return false;
        }

        PartPos target = targetGetter.apply(IngredientComponent.ITEMSTACK);
        TileEntityBrewingStand tile = getTile(target);
        if (tile == null) {
            return false;
        }

        IItemHandler ingredientHandler = CapabilityHelpers
            .getCapability(tile, CapabilityItemHandler.ITEM_HANDLER, SIDE_INGREDIENT)
            .getOrNull();
        IItemHandler bottleHandler = CapabilityHelpers
            .getCapability(tile, CapabilityItemHandler.ITEM_HANDLER, SIDE_BOTTLE)
            .getOrNull();

        if (ingredientHandler == null || bottleHandler == null) {
            return false;
        }

        int bottleSlotIndex = 0; // Targets slots 0, 1, 2 through SIDE_BOTTLE wrapper

        for (ItemStack instance : instances) {
            if (instance == null) {
                continue;
            }

            if (isValidIngredient(instance)) {
                // Ingredient belongs to Slot 3 (accessed via UP side handler, index 0 in that handler)
                ItemStack leftover = ingredientHandler.insertItem(0, instance, simulate);
                if (leftover != null && leftover.stackSize > 0) {
                    return false;
                }
            } else if (isValidBottle(instance)) {
                // Bottles belong to slots 0, 1, 2
                if (bottleSlotIndex >= bottleHandler.getSlots()) {
                    return false;
                }

                ItemStack leftover = bottleHandler.insertItem(bottleSlotIndex++, instance, simulate);
                if (leftover != null && leftover.stackSize > 0) {
                    return false;
                }
            } else {
                // Invalid item for brewing stand
                return false;
            }
        }

        return true;
    }
}
