package ruiseki.integratedcompat.modcompat.jjfmuy.mechanicaldryingbasin;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.block.BlockDryingBasinConfig;
import ruiseki.integrateddynamics.block.BlockMechanicalDryingBasinConfig;
import ruiseki.integrateddynamics.core.recipe.type.RecipeMechanicalDryingBasin;
import ruiseki.integrateddynamics.core.recipe.type.RecipeTypeMechanicalDryingBasinConfig;
import ruiseki.jfmuy.api.IGuiHelper;
import ruiseki.jfmuy.api.IJFMUYHelpers;
import ruiseki.jfmuy.api.IModRegistry;
import ruiseki.jfmuy.api.gui.IDrawable;
import ruiseki.jfmuy.api.gui.IDrawableAnimated;
import ruiseki.jfmuy.api.gui.IDrawableStatic;
import ruiseki.jfmuy.api.gui.IRecipeLayout;
import ruiseki.jfmuy.api.ingredients.IIngredients;
import ruiseki.jfmuy.api.recipe.IRecipeCategory;
import ruiseki.jfmuy.api.recipe.IRecipeCategoryRegistration;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.recipe.RecipeManager;

/**
 * Category for the Drying Basin recipes.
 *
 * @author rubensworks
 */
public class MechanicalDryingBasinRecipeCategory implements IRecipeCategory<MechanicalDryingBasinRecipeWrapper> {

    public static final String UID = Reference.MOD_ID + ".mechanical_drying_basin";

    public static void register(IRecipeCategoryRegistration registry) {
        IJFMUYHelpers jeiHelpers = registry.getJFMUYHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        registry.addRecipeCategories(new MechanicalDryingBasinRecipeCategory(guiHelper));
    }

    public static void initialize(IModRegistry registry) {
        try {
            registry.addRecipes(getRecipes(), UID);
            registry.addRecipeCatalyst(new ItemStack(BlockMechanicalDryingBasinConfig._instance.getInstance()), UID);
        } catch (Throwable t) {
            IntegratedDynamics.clog(Level.ERROR, "Failed to initialize Mechanical Drying Basin recipe category!", t);
        }
    }

    public static List<MechanicalDryingBasinRecipeWrapper> getRecipes() {
        List<MechanicalDryingBasinRecipeWrapper> wrappers = new ArrayList<>();
        List<RecipeMechanicalDryingBasin> recipes = RecipeManager.getManager()
            .getAllRecipesFor(RecipeTypeMechanicalDryingBasinConfig._instance.getInstance());

        for (RecipeMechanicalDryingBasin recipe : recipes) {
            wrappers.add(new MechanicalDryingBasinRecipeWrapper(recipe));
        }
        return wrappers;
    }

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int FLUID_INPUT_SLOT = 2;
    private static final int FLUID_OUTPUT_SLOT = 3;

    private final IDrawable background;
    private final IDrawableAnimated arrow;

    public MechanicalDryingBasinRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation resourceLocation = new ResourceLocation(
            Reference.MOD_ID + ":"
                + IntegratedDynamics._instance.getReferenceValue(ModBase.REFKEY_TEXTURE_PATH_GUI)
                + BlockDryingBasinConfig._instance.getNamedId()
                + "_gui_jei.png");
        this.background = guiHelper.createDrawable(resourceLocation, 0, 0, 93, 53);
        IDrawableStatic arrowDrawable = guiHelper.createDrawable(resourceLocation, 94, 0, 11, 28);
        this.arrow = guiHelper
            .createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.BOTTOM, false);
    }

    @Nonnull
    @Override
    public String getUid() {
        return UID;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return LangHelpers.localize(BlockMechanicalDryingBasinConfig._instance.getFullUnlocalizedName());
    }

    @Override
    public String getModName() {
        return Reference.MOD_NAME;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        arrow.draw(minecraft, 43, 11);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, MechanicalDryingBasinRecipeWrapper recipe,
        IIngredients ingredients) {
        recipeLayout.getItemStacks()
            .init(INPUT_SLOT, true, 1, 7);
        recipeLayout.getItemStacks()
            .init(OUTPUT_SLOT, false, 75, 7);
        recipeLayout.getItemStacks()
            .init(FLUID_INPUT_SLOT, true, 6, 28);
        recipeLayout.getItemStacks()
            .init(FLUID_OUTPUT_SLOT, false, 80, 28);

        if (!recipe.getInputItem()
            .isEmpty()) {
            recipeLayout.getItemStacks()
                .set(INPUT_SLOT, recipe.getInputItem());
        }
        if (recipe.getOutputItem() != null) {
            recipeLayout.getItemStacks()
                .set(OUTPUT_SLOT, recipe.getOutputItem());
        }

        recipeLayout.getFluidStacks()
            .init(FLUID_INPUT_SLOT, true, 6, 28, 8, 9, 1000, true, null);
        if (recipe.getInputFluid() != null) {
            recipeLayout.getFluidStacks()
                .set(FLUID_INPUT_SLOT, recipe.getInputFluid());
        }
        recipeLayout.getFluidStacks()
            .init(FLUID_OUTPUT_SLOT, false, 80, 28, 8, 9, 1000, true, null);
        if (recipe.getOutputFluid() != null) {
            recipeLayout.getFluidStacks()
                .set(FLUID_OUTPUT_SLOT, recipe.getOutputFluid());
        }
    }
}
