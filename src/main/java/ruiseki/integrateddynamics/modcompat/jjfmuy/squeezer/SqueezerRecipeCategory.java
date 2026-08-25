package ruiseki.integrateddynamics.modcompat.jjfmuy.squeezer;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.block.BlockSqueezerConfig;
import ruiseki.integrateddynamics.core.recipe.type.RecipeSqueezer;
import ruiseki.integrateddynamics.core.recipe.type.RecipeTypeSqueezerConfig;
import ruiseki.jfmuy.api.IGuiHelper;
import ruiseki.jfmuy.api.IJFMUYHelpers;
import ruiseki.jfmuy.api.IModRegistry;
import ruiseki.jfmuy.api.gui.IDrawable;
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
public class SqueezerRecipeCategory implements IRecipeCategory<SqueezerRecipeWrapper> {

    public static final String UID = Reference.MOD_ID + ".squeezer";

    public static void register(IRecipeCategoryRegistration registry) {
        IJFMUYHelpers jeiHelpers = registry.getJFMUYHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        registry.addRecipeCategories(new SqueezerRecipeCategory(guiHelper));
    }

    public static void initialize(IModRegistry registry) {
        try {
            registry.addRecipes(getRecipes(), UID);
            registry.addRecipeCatalyst(new ItemStack(BlockSqueezerConfig._instance.getInstance()), UID);
        } catch (Throwable t) {
            IntegratedDynamics.clog(Level.ERROR, "Failed to initialize Squeezer recipe category!", t);
        }
    }

    public static List<SqueezerRecipeWrapper> getRecipes() {
        List<SqueezerRecipeWrapper> wrappers = new ArrayList<>();
        List<RecipeSqueezer> recipes = RecipeManager.getManager()
            .getAllRecipesFor(RecipeTypeSqueezerConfig._instance.getInstance());

        for (RecipeSqueezer recipe : recipes) {
            wrappers.add(new SqueezerRecipeWrapper(recipe));
        }
        return wrappers;
    }

    private static final int INPUT_SLOT = 0;
    private static final int FLUIDOUTPUT_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;

    private final IDrawable background;
    private final IDrawableStatic arrowDrawable;

    public SqueezerRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation resourceLocation = new ResourceLocation(
            Reference.MOD_ID + ":"
                + IntegratedDynamics._instance.getReferenceValue(ModBase.REFKEY_TEXTURE_PATH_GUI)
                + BlockSqueezerConfig._instance.getNamedId()
                + "_gui_jei.png");
        this.background = guiHelper.createDrawable(resourceLocation, 0, 0, 116, 53);
        this.arrowDrawable = guiHelper.createDrawable(resourceLocation, 41, 32, 12, 2);
    }

    @Nonnull
    @Override
    public String getUid() {
        return UID;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return LangHelpers.localize(BlockSqueezerConfig._instance.getFullUnlocalizedName());
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
        int height = (int) ((minecraft.theWorld.getTotalWorldTime() / 4) % 7);
        arrowDrawable.draw(minecraft, 41, 18 + height * 2);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, SqueezerRecipeWrapper recipe, IIngredients ingredients) {
        recipeLayout.getItemStacks()
            .init(INPUT_SLOT, true, 1, 17);

        List<RecipeSqueezer.ItemStackChance> outputItems = recipe.getOutputItems();
        int offset = 0;
        for (int i = 0; i < outputItems.size(); i++) {
            recipeLayout.getItemStacks()
                .init(OUTPUT_SLOT + i, false, 75 + (i % 2 > 0 ? 22 : 0), 7 + offset + (i > 1 ? 22 : 0));
        }

        recipeLayout.getItemStacks()
            .addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
                if (slotIndex >= OUTPUT_SLOT && slotIndex < OUTPUT_SLOT + outputItems.size()) {
                    float chance = recipe.getOutputChances()
                        .get(slotIndex - OUTPUT_SLOT);
                    tooltip.add(EnumChatFormatting.GRAY + "Chance: " + (int) (chance * 100.0F) + "%");
                }
            });

        if (!recipe.getInputItem()
            .isEmpty()) {
            recipeLayout.getItemStacks()
                .set(INPUT_SLOT, recipe.getInputItem());
        }

        for (int i = 0; i < outputItems.size(); i++) {
            RecipeSqueezer.ItemStackChance stackChance = outputItems.get(i);
            if (stackChance != null && stackChance.getItemStack() != null) {
                recipeLayout.getItemStacks()
                    .set(OUTPUT_SLOT + i, stackChance.getItemStack());
            }
        }

        recipeLayout.getFluidStacks()
            .init(FLUIDOUTPUT_SLOT, false, 98, 30, 16, 16, 1000, false, null);
        if (recipe.getOutputFluid() != null) {
            recipeLayout.getFluidStacks()
                .set(FLUIDOUTPUT_SLOT, recipe.getOutputFluid());
        }
    }
}
