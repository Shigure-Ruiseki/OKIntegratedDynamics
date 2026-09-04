package ruiseki.integrateddynamics.core.recipe.type;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.ingredient.Ingredient;
import ruiseki.okcore.recipe.type.crafting.SpecialRecipe;

/**
 * Crafting recipe to clear item NBT data.
 * 
 * @author rubensworks
 */
public class RecipeNbtClear extends SpecialRecipe {

    private final Ingredient inputIngredient;

    public RecipeNbtClear(ResourceLocation id, Ingredient inputIngredient) {
        super(id);
        this.inputIngredient = inputIngredient;
    }

    public Ingredient getInputIngredient() {
        return inputIngredient;
    }

    @Override
    public boolean matchesOK(InventoryCrafting inv, World worldIn) {
        return getCraftingResult(inv) != null;
    }

    @Override
    public ItemStack assemble(InventoryCrafting inv) {
        ItemStack ret = null;
        for (int j = 0; j < inv.getSizeInventory(); j++) {
            ItemStack element = inv.getStackInSlot(j);
            if (element != null) {
                if (this.inputIngredient.test(element)) {
                    if (ret != null) {
                        return null;
                    }
                    // Create copy of the stack WITHOUT the NBT tag.
                    ret = new ItemStack(element.getItem());
                } else {
                    return null;
                }
            }
        }
        return ret;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public ItemStack getResultItem() {
        if (inputIngredient != null) {
            ItemStack[] matchingStacks = inputIngredient.getItems();
            if (matchingStacks != null && matchingStacks.length > 0) {
                return matchingStacks[0];
            }
        }
        return null; // Return null if no items exist
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        return NonNullList.withSize(inv.getSizeInventory(), null);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.withSize(1, inputIngredient);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeSerializerNbtClearConfig._instance.getInstance();
    }
}
