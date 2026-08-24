package ruiseki.integrateddynamics.core.recipe.type;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.okcore.fluid.handler.IFluidTankProperties;
import ruiseki.okcore.inventory.IInventoryFluid;
import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.ingredient.Ingredient;

/**
 * Drying basin recipe
 *
 * @author rubensworks
 */
public class RecipeDryingBasin implements IRecipeOK<IInventoryFluid> {

    private final ResourceLocation id;
    private final Ingredient inputIngredient;
    private final FluidStack inputFluid;
    private final ItemStack outputItem;
    private final FluidStack outputFluid;
    private final int duration;

    public RecipeDryingBasin(ResourceLocation id, Ingredient inputIngredient, FluidStack inputFluid,
        ItemStack outputItem, FluidStack outputFluid, int duration) {
        this.id = id;
        this.inputIngredient = inputIngredient;
        this.inputFluid = inputFluid;
        this.outputItem = outputItem;
        this.outputFluid = outputFluid;
        this.duration = duration;
    }

    public Ingredient getInputIngredient() {
        return inputIngredient;
    }

    public FluidStack getInputFluid() {
        return inputFluid;
    }

    public ItemStack getOutputItem() {
        return outputItem;
    }

    public FluidStack getOutputFluid() {
        return outputFluid;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public boolean matchesOK(IInventoryFluid inv, World world) {
        if (inv == null || inv.getFluidHandler() == null) {
            return false;
        }

        ItemStack slotStack = inv.getStackInSlot(0);

        if (inputIngredient != null && !inputIngredient.isEmpty()) {
            if (slotStack == null || !inputIngredient.test(slotStack)) {
                return false;
            }
        } else {
            if (slotStack != null && slotStack.getItem() != null) {
                return false;
            }
        }

        if (inputFluid != null) {
            IFluidTankProperties[] properties = inv.getFluidHandler()
                .getTankProperties();
            if (properties == null || properties.length == 0 || properties[0] == null) {
                return false;
            }

            FluidStack contents = properties[0].getContents();
            if (contents == null || contents.getFluid() == null) {
                return false;
            }

            if (inputFluid.getFluid() != contents.getFluid() || contents.amount < inputFluid.amount) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(IInventoryFluid inventory) {
        return this.outputItem == null ? null : this.outputItem.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height <= 1;
    }

    @Override
    public ItemStack getResultItem() {
        return this.outputItem;
    }

    @Override
    public int getRecipeSize() {
        return 1;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeSerializerDryingBasinConfig._instance.getInstance();
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipeTypeDryingBasinConfig._instance.getInstance();
    }
}
