package ruiseki.integrateddynamics.recipe;

import java.util.Objects;
import java.util.stream.StreamSupport;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.common.registry.GameData;
import ruiseki.integrateddynamics.item.ItemFacade;
import ruiseki.integrateddynamics.item.ItemFacadeConfig;
import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.helper.BlockHelpers;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.ingredient.Ingredient;
import ruiseki.okcore.recipe.type.crafting.SpecialRecipe;

/**
 * Recipe for combining facades with blocks.
 *
 * @author rubensworks
 *
 */
public class ItemFacadeRecipe extends SpecialRecipe {

    private NonNullList<Ingredient> ingredients;

    public ItemFacadeRecipe(ResourceLocation id) {
        super(id);
    }

    public NonNullList<Ingredient> getIngredients() {
        if (ingredients == null) {
            // Catch runtime errors if other mods call this method before items have been registered
            try {
                ingredients = NonNullList
                    .from(Ingredient.EMPTY, Ingredient.of(getRecipeOutput()), new BlocksIngredient());
            } catch (RuntimeException e) {
                return NonNullList.create();
            }
        }
        return ingredients;
    }

    @Override
    public boolean matchesOK(InventoryCrafting grid, World world) {
        return assemble(grid) != null;
    }

    @Override
    public ItemStack getResultItem() {
        return new ItemStack(ItemFacadeConfig._instance.getInstance());
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inventory) {
        NonNullList<ItemStack> aitemstack = NonNullList.withSize(inventory.getSizeInventory(), null);

        for (int i = 0; i < aitemstack.size(); ++i) {
            ItemStack itemstack = inventory.getStackInSlot(i);
            aitemstack.set(
                i,
                itemstack.getItem()
                    .getContainerItem(itemstack));
        }

        return aitemstack;
    }

    @Override
    public ItemStack assemble(InventoryCrafting grid) {
        ItemStack output = getRecipeOutput().copy();

        int facades = 0;
        ItemStack block = null;

        for (int j = 0; j < grid.getSizeInventory(); j++) {
            ItemStack element = grid.getStackInSlot(j);
            if (element != null) {
                if (element.getItem() == output.getItem()) {
                    facades++;
                } else if (block == null && element.getItem() instanceof ItemBlock) {
                    block = element;
                } else {
                    return null;
                }
            }
        }

        if (facades != 1 || block == null) {
            return null;
        }

        ((ItemFacade) ItemFacadeConfig._instance.getInstance())
            .writeFacadeBlock(output, BlockHelpers.getBlockStateFromItemStack(block));
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ItemFacadeRecipeConfig._instance.getInstance();
    }

    public static class BlocksIngredient extends Ingredient {

        @SuppressWarnings("unchecked")
        protected BlocksIngredient() {
            super(
                StreamSupport.stream(
                    GameData.getBlockRegistry()
                        .spliterator(),
                    false)
                    .filter(object -> ((Block) object).isNormalCube())
                    .map(block -> Item.getItemFromBlock((Block) block))
                    .filter(Objects::nonNull)
                    .map(item -> new ItemStack((Item) item))
                    .map(stack -> new Ingredient.SingleItemList((ItemStack) stack)));
        }

        @Override
        public boolean test(@Nullable ItemStack itemStack) {
            return itemStack != null && itemStack.getItem() instanceof ItemBlock;
        }
    }

}
