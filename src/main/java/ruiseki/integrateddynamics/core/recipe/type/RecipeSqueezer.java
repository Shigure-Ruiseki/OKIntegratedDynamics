package ruiseki.integrateddynamics.core.recipe.type;

import java.util.Objects;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.ingredient.Ingredient;

/**
 * Squeezer recipe
 *
 * @author rubensworks
 */
public class RecipeSqueezer implements IRecipeOK<IInventory> {

    private final ResourceLocation id;
    private final Ingredient inputIngredient;
    private final NonNullList<ItemStackChance> outputItems;
    private final FluidStack outputFluid;

    public RecipeSqueezer(ResourceLocation id, Ingredient inputIngredient, NonNullList<ItemStackChance> outputItems,
        FluidStack outputFluid) {
        this.id = id;
        this.inputIngredient = inputIngredient;
        this.outputItems = outputItems;
        this.outputFluid = outputFluid;
    }

    public Ingredient getInputIngredient() {
        return inputIngredient;
    }

    public NonNullList<ItemStackChance> getOutputItems() {
        return outputItems;
    }

    public FluidStack getOutputFluid() {
        return outputFluid;
    }

    @Override
    public boolean matchesOK(IInventory inv, World worldIn) {
        return inputIngredient.test(inv.getStackInSlot(0));
    }

    @Override
    public ItemStack assemble(IInventory inventory) {
        // Should not be called, but lets provide a good fallback
        if (this.outputItems.isEmpty()) {
            return null;
        }
        return this.outputItems.get(0)
            .getItemStack()
            .copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height <= 1;
    }

    @Override
    public int getRecipeSize() {
        return 1;
    }

    @Override
    public ItemStack getResultItem() {
        // Should not be called, but lets provide a good fallback
        if (this.outputItems.isEmpty()) {
            return null;
        }
        return this.outputItems.get(0)
            .getItemStack()
            .copy();
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeSerializerSqueezerConfig._instance.getInstance();
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipeTypeSqueezerConfig._instance.getInstance();
    }

    public static class ItemStackChance {

        private final ItemStack itemStack;
        private final float chance;

        public ItemStackChance(ItemStack itemStack, float chance) {
            this.itemStack = Objects.requireNonNull(itemStack);
            this.chance = chance;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public float getChance() {
            return chance;
        }

    }

}
