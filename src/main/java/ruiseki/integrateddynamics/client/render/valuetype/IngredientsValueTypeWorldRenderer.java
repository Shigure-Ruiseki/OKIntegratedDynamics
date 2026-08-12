package ruiseki.integrateddynamics.client.render.valuetype;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import ruiseki.commoncapabilities.api.ingredient.IMixedIngredients;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.api.client.render.valuetype.IValueTypeWorldRenderer;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.ingredient.IIngredientComponentHandler;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.client.render.part.DisplayPartOverlayRenderer;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeIngredients;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeItemStack;
import ruiseki.integrateddynamics.core.ingredient.IngredientComponentHandlers;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.ItemStackHelpers;

/**
 * A value type world renderer for blocks.
 *
 * @author rubensworks
 */
public class IngredientsValueTypeWorldRenderer implements IValueTypeWorldRenderer {

    @Override
    public void renderValue(IPartContainer partContainer, double x, double y, double z, float partialTick,
        int destroyStage, ForgeDirection direction, IPartType partType, IValue value,
        TileEntityRendererDispatcher rendererDispatcher, float alpha) {
        Optional<IMixedIngredients> ingredientsOptional = ((ValueObjectTypeIngredients.ValueIngredients) value)
            .getRawValue();
        if (ingredientsOptional.isPresent()) {
            IMixedIngredients ingredients = ingredientsOptional.get();

            // Get a list of all values
            List<IValue> values = Lists.newArrayList();
            for (IngredientComponent<?, ?> component : ingredients.getComponents()) {
                IIngredientComponentHandler componentHandler = IngredientComponentHandlers.REGISTRY
                    .getComponentHandler(component);
                for (Object instance : ingredients.getInstances(component)) {
                    values.add(componentHandler.toValue(instance));
                }
            }

            // Render ingredients in a square matrix
            renderGrid(
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
    }

    public static void renderGrid(IPartContainer partContainer, double x, double y, double z, float partialTick,
        int destroyStage, ForgeDirection direction, IPartType partType, List<IValue> values,
        TileEntityRendererDispatcher rendererDispatcher, float alpha) {
        GlStateManager.pushMatrix();
        int matrixRadius = getSmallestSquareFrom(values.size());
        double scale = (double) 1 / matrixRadius;
        GlStateManager.scale(scale, scale, 1);
        for (int i = 0; i < matrixRadius; i++) {
            for (int j = 0; j < matrixRadius; j++) {
                int realIndex = i * matrixRadius + j;
                if (realIndex >= values.size()) {
                    break;
                }
                IValue renderValue = values.get(realIndex);
                if (renderValue != null) {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(j * DisplayPartOverlayRenderer.MAX, i * DisplayPartOverlayRenderer.MAX, 0);

                    if (renderValue instanceof ValueObjectTypeItemStack.ValueItemStack) {
                        ValueObjectTypeItemStack.ValueItemStack itemValue = (ValueObjectTypeItemStack.ValueItemStack) renderValue;
                        if (itemValue.getRawValue()
                            .isPresent()) {
                            ItemStack itemStackRaw = itemValue.getRawValue()
                                .get();
                            if (itemStackRaw.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                                List<ItemStack> subItems = ItemStackHelpers.getSubItems(itemStackRaw);
                                int subtick = ((int) Minecraft.getMinecraft().theWorld.getWorldTime()) / 10;
                                ItemStack itemStack = prepareElementForTick(subItems, subtick, () -> null);
                                renderValue = ValueObjectTypeItemStack.ValueItemStack.of(itemStack);
                            }
                        }
                    }

                    // Call value renderer for each value
                    IValueTypeWorldRenderer renderer = ValueTypeWorldRenderers.REGISTRY
                        .getRenderer(renderValue.getType());
                    if (renderer == null) {
                        renderer = ValueTypeWorldRenderers.DEFAULT;
                    }
                    renderer.renderValue(
                        partContainer,
                        x,
                        y,
                        z,
                        partialTick,
                        destroyStage,
                        direction,
                        partType,
                        renderValue,
                        rendererDispatcher,
                        alpha);
                    GlStateManager.popMatrix();
                }
            }
        }
        GlStateManager.popMatrix();
    }

    @Nullable
    protected static <T> T prepareElementForTick(List<T> elements, int tick, Supplier<T> defaultFactory) {
        return elements.size() > 0 ? elements.get(tick % elements.size()) : defaultFactory.get();
    }

    protected static int getSmallestSquareFrom(int n) {
        for (; !isInt(Math.sqrt(n)); n++);
        return (int) Math.sqrt(n);
    }

    protected static final boolean isInt(double n) {
        return n == Math.floor(n) && !Double.isInfinite(n);
    }
}
