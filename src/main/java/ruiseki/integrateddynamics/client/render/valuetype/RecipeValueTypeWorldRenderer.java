package ruiseki.integrateddynamics.client.render.valuetype;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import ruiseki.commoncapabilities.api.capability.itemhandler.ItemMatch;
import ruiseki.commoncapabilities.api.capability.recipehandler.IPrototypedIngredientAlternatives;
import ruiseki.commoncapabilities.api.capability.recipehandler.IRecipeDefinition;
import ruiseki.commoncapabilities.api.ingredient.IIngredientMatcher;
import ruiseki.commoncapabilities.api.ingredient.IPrototypedIngredient;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.PrototypedIngredient;
import ruiseki.integrateddynamics.api.client.render.valuetype.IValueTypeWorldRenderer;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.ingredient.IIngredientComponentHandler;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.client.render.part.DisplayPartOverlayRenderer;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeIngredients;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeRecipe;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.ingredient.IngredientComponentHandlers;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.ItemStackHelpers;
import ruiseki.okcore.helper.LangHelpers;

/**
 * A value type world renderer for blocks.
 *
 * @author rubensworks
 */
public class RecipeValueTypeWorldRenderer implements IValueTypeWorldRenderer {

    private static final IValueTypeWorldRenderer INGREDIENTS_RENDERER = ValueTypeWorldRenderers.REGISTRY
        .getRenderer(ValueTypes.OBJECT_INGREDIENTS);

    @Override
    public void renderValue(IPartContainer partContainer, double x, double y, double z, float partialTick,
        int destroyStage, ForgeDirection direction, IPartType partType, IValue value,
        TileEntityRendererDispatcher rendererDispatcher, float alpha) {
        Optional<IRecipeDefinition> recipeOptional = ((ValueObjectTypeRecipe.ValueRecipe) value).getRawValue();
        if (recipeOptional.isPresent()) {
            IRecipeDefinition recipe = recipeOptional.get();

            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5, 0.5, 1);

            GlStateManager.pushMatrix();
            GlStateManager.scale(0.3, 0.3, 1);
            rendererDispatcher.getFontRenderer()
                .drawString(
                    LangHelpers.localize("gui.integrateddynamics.input_short"),
                    8,
                    15,
                    Helpers.RGBToInt(255, 255, 255));
            rendererDispatcher.getFontRenderer()
                .drawString(
                    LangHelpers.localize("gui.integrateddynamics.output_short"),
                    46,
                    15,
                    Helpers.RGBToInt(255, 255, 255));
            GlStateManager.popMatrix();

            GlStateManager.translate(0, 2 * DisplayPartOverlayRenderer.MAX / 3, 0);
            renderInput(
                partContainer,
                x,
                y,
                z,
                partialTick,
                destroyStage,
                direction,
                partType,
                recipe,
                rendererDispatcher,
                alpha);
            GlStateManager.translate(DisplayPartOverlayRenderer.MAX, 0, 0);
            INGREDIENTS_RENDERER.renderValue(
                partContainer,
                x,
                y,
                z,
                partialTick,
                destroyStage,
                direction,
                partType,
                ValueObjectTypeIngredients.ValueIngredients.of(recipe.getOutput()),
                rendererDispatcher,
                alpha);

            GlStateManager.popMatrix();
        }
    }

    protected void renderInput(IPartContainer partContainer, double x, double y, double z, float partialTick,
        int destroyStage, ForgeDirection direction, IPartType partType, IRecipeDefinition recipe,
        TileEntityRendererDispatcher rendererDispatcher, float alpha) {
        // Get a list of all values
        int ingredientCount = recipe.getInputComponents()
            .stream()
            .mapToInt(
                (c) -> recipe.getInputs(c)
                    .size())
            .sum();
        List<IValue> values = Lists.newArrayListWithExpectedSize(ingredientCount);

        // For ingredients with multiple possibilities, vary them based on the current tick
        int tick = ((int) Minecraft.getMinecraft().theWorld.getTotalWorldTime()) / 30;
        for (IngredientComponent<?, ?> component : recipe.getInputComponents()) {
            IIngredientMatcher<?, ?> matcher = component.getMatcher();
            IIngredientComponentHandler componentHandler = IngredientComponentHandlers.REGISTRY
                .getComponentHandler(component);
            Stream<List<IPrototypedIngredient>> inputs = enhanceRecipeInputs(component, recipe);
            inputs.forEach(
                element -> values.add(
                    componentHandler.toValue(
                        IngredientsValueTypeWorldRenderer
                            .prepareElementForTick(
                                element,
                                tick,
                                () -> new PrototypedIngredient(
                                    component,
                                    matcher.getEmptyInstance(),
                                    matcher.getAnyMatchCondition()))
                            .getPrototype())));
        }

        // Render ingredients in a square matrix
        IngredientsValueTypeWorldRenderer.renderGrid(
            partContainer,
            x,
            y,
            z,
            partialTick,
            destroyStage,
            direction,
            partType,
            values,
            rendererDispatcher,
            alpha);
    }

    protected <T, M> Stream<List<IPrototypedIngredient>> enhanceRecipeInputs(
        IngredientComponent<T, M> ingredientComponent, IRecipeDefinition recipe) {
        Stream<IPrototypedIngredientAlternatives<T, M>> inputs = recipe.getInputs(ingredientComponent)
            .stream();
        if (ingredientComponent == IngredientComponent.ITEMSTACK) {
            IIngredientMatcher<ItemStack, Integer> matcher = (IIngredientMatcher<ItemStack, Integer>) ingredientComponent
                .getMatcher();
            return ((Stream<IPrototypedIngredientAlternatives<ItemStack, Integer>>) (Stream) inputs).map(
                input -> input.getAlternatives()
                    .stream()
                    .map(prototypedIngredient -> {
                        if (!matcher.hasCondition(prototypedIngredient.getCondition(), ItemMatch.DAMAGE)) {
                            return ItemStackHelpers.getSubItems(prototypedIngredient.getPrototype())
                                .stream()
                                .map(
                                    stack -> new PrototypedIngredient(
                                        IngredientComponent.ITEMSTACK,
                                        stack,
                                        prototypedIngredient.getCondition()))
                                .collect(Collectors.toList());
                        } else {
                            return Collections.singletonList(prototypedIngredient);
                        }
                    })
                    .flatMap(List::stream)
                    .collect(Collectors.toList()));
        } else {
            return ((Stream<IPrototypedIngredientAlternatives<?, ?>>) (Stream) inputs)
                .map(p -> Lists.newArrayList(p.getAlternatives()));
        }
    }
}
