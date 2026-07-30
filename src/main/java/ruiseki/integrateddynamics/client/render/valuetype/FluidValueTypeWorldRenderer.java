package ruiseki.integrateddynamics.client.render.valuetype;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.tuple.Triple;

import com.google.common.base.Optional;

import ruiseki.integrateddynamics.api.client.render.valuetype.IValueTypeWorldRenderer;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeFluidStack;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.RenderHelpers;

/**
 * A value type world renderer for fluids.
 * 
 * @author rubensworks
 */
public class FluidValueTypeWorldRenderer implements IValueTypeWorldRenderer {

    @Override
    public void renderValue(IPartContainer partContainer, double x, double y, double z, float partialTick,
        int destroyStage, ForgeDirection direction, IPartType partType, IValue value,
        TileEntityRendererDispatcher rendererDispatcher, float alpha) {
        Optional<FluidStack> fluidStackOptional = ((ValueObjectTypeFluidStack.ValueFluidStack) value).getRawValue();
        if (fluidStackOptional.isPresent() && fluidStackOptional.get() != null) {
            FluidStack fluidStack = fluidStackOptional.get();

            // Render Fluid Texture Quad
            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();

            Minecraft.getMinecraft()
                .getTextureManager()
                .bindTexture(TextureMap.locationBlocksTexture);

            IIcon icon = RenderHelpers.getFluidIcon(fluidStack, ForgeDirection.NORTH);
            if (icon != null) {
                float min = 0F;
                float max = 12.5F;
                float u1 = icon.getMinU();
                float u2 = icon.getMaxU();
                float v1 = icon.getMinV();
                float v2 = icon.getMaxV();

                Triple<Float, Float, Float> colorParts = RenderHelpers.getFluidVertexBufferColor(fluidStack);
                float r = colorParts.getLeft();
                float g = colorParts.getMiddle();
                float b = colorParts.getRight();

                Tessellator tessellator = Tessellator.instance;
                tessellator.startDrawingQuads();
                tessellator.setColorRGBA_F(r, g, b, alpha);

                tessellator.addVertexWithUV(max, max, 0, u2, v2);
                tessellator.addVertexWithUV(max, min, 0, u2, v1);
                tessellator.addVertexWithUV(min, min, 0, u1, v1);
                tessellator.addVertexWithUV(min, max, 0, u1, v2);

                tessellator.draw();
            }
            GlStateManager.popMatrix();

            // Render Fluid Amount Text
            GlStateManager.pushMatrix();
            GlStateManager.translate(7F, 8.5F, 0.1F);
            GlStateManager.pushMatrix();

            String string = String.valueOf(fluidStack.amount);

            FontRenderer fontRenderer = rendererDispatcher != null ? rendererDispatcher.getFontRenderer()
                : Minecraft.getMinecraft().fontRenderer;
            if (fontRenderer == null) {
                fontRenderer = Minecraft.getMinecraft().fontRenderer;
            }

            int stringWidth = fontRenderer.getStringWidth(string);
            float scale = stringWidth > 0 ? 5.0F / stringWidth : 1.0F;

            GlStateManager.scale(scale, scale, 1F);
            fontRenderer.drawString(string, 0, 0, Helpers.RGBAToInt(200, 200, 200, (int) (alpha * 255F)));

            GlStateManager.popMatrix();
            GlStateManager.popMatrix();
        }
    }
}
