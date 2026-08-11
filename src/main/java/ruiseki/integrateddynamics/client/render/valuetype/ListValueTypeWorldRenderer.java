package ruiseki.integrateddynamics.client.render.valuetype;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.client.render.valuetype.IValueTypeWorldRenderer;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeList;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.Helpers;

/**
 * A text-based value type world renderer for lists.
 *
 * @author rubensworks
 */
public class ListValueTypeWorldRenderer implements IValueTypeWorldRenderer {

    private static final int MAX_LINES = 20;
    private static final float MAX = 12.5F;
    private static final float MARGIN_FACTOR = 1.1F;

    @Override
    public void renderValue(IPartContainer partContainer, double x, double y, double z, float partialTick,
        int destroyStage, ForgeDirection direction, IPartType partType, IValue value,
        TileEntityRendererDispatcher rendererDispatcher, float distanceAlpha) {
        FontRenderer fontRenderer = rendererDispatcher.getFontRenderer();
        float maxWidth = 0;

        List<Pair<String, Integer>> lines = Lists.newLinkedList();
        IValueType listType = ((ValueTypeList.ValueList<?, ?>) value).getRawValue()
            .getValueType();
        for (IValue element : ((ValueTypeList.ValueList<?, ?>) value).getRawValue()) {
            if (lines.size() >= MAX_LINES) {
                lines.add(Pair.of("...", listType.getDisplayColor()));
                break;
            } else {
                IValueType elementType = element.getType();
                String string = " - " + elementType.toCompactString(element);
                float width = fontRenderer.getStringWidth(string) - 1;
                lines.add(Pair.of(string, elementType.getDisplayColor()));
                maxWidth = Math.max(maxWidth, width);
            }
        }

        float singleHeight = fontRenderer.FONT_HEIGHT;
        float totalHeight = singleHeight * lines.size();

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();

        float scaleX = MAX / (maxWidth * MARGIN_FACTOR);
        float scaleY = MAX / (totalHeight * MARGIN_FACTOR);
        float scale = Math.min(scaleX, scaleY); // Maintain aspect ratio
        float newWidth = maxWidth * scale;
        float newHeight = totalHeight * scale;
        GlStateManager.translate((MAX - newWidth) / 2, (MAX - newHeight) / 2, 0F);
        GlStateManager.scale(scale, scale, 1F);

        int offset = 0;
        for (Pair<String, Integer> line : lines) {
            int color = Helpers.addAlphaToColor(line.getRight(), distanceAlpha);
            rendererDispatcher.getFontRenderer()
                .drawString(line.getLeft(), 0, offset, color);
            offset += singleHeight;
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }
}
