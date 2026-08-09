package ruiseki.integrateddynamics.client.render.valuetype;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.client.render.valuetype.IValueTypeWorldRenderer;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeNbt;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.Helpers;

/**
 * A text-based value type world renderer for NBT tags.
 * 
 * @author rubensworks
 */
public class NbtValueTypeWorldRenderer implements IValueTypeWorldRenderer {

    private static final int MAX_LINES = 30;
    private static final float MAX = 12.5F;
    private static final float MARGIN_FACTOR = 1.1F;

    @Override
    public void renderValue(IPartContainer partContainer, double x, double y, double z, float partialTick,
        int destroyStage, ForgeDirection direction, IPartType partType, IValue value,
        TileEntityRendererDispatcher rendererDispatcher, float distanceAlpha) {
        FontRenderer fontRenderer = rendererDispatcher.getFontRenderer();
        float maxWidth = 0;

        List<String> lines = Lists.newLinkedList();
        NBTTagCompound tag = ((ValueTypeNbt.ValueNbt) value).getRawValue();
        lines.add("{");
        for (String key : tag.func_150296_c()) {
            if (lines.size() >= MAX_LINES) {
                lines.add("...");
                break;
            } else {
                NBTBase subTag = tag.getTag(key);
                if (subTag instanceof NBTTagCompound) {
                    subTag = ValueTypes.NBT.filterBlacklistedTags((NBTTagCompound) subTag);
                }
                String string = "  " + key + ": " + StringUtils.abbreviate(subTag.toString(), 40) + "";
                float width = fontRenderer.getStringWidth(string) - 1;
                lines.add(string);
                maxWidth = Math.max(maxWidth, width);
            }
        }
        lines.add("}");

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
        for (String line : lines) {
            int color = Helpers.addAlphaToColor(ValueTypes.NBT.getDisplayColor(), distanceAlpha);
            rendererDispatcher.getFontRenderer()
                .drawString(line, 0, offset, color);
            offset += singleHeight;
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }
}
