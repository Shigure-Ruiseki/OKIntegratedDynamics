package ruiseki.integrateddynamics.client.render.part;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.client.render.valuetype.IValueTypeWorldRenderer;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.client.render.valuetype.ValueTypeWorldRenderers;
import ruiseki.integrateddynamics.part.PartTypePanelDisplay;
import ruiseki.okcore.client.gui.image.Images;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.datastructure.BlockPos;

/**
 * Overlay renderer for the display part to display values on the part.
 *
 * @author rubensworks
 */
@SideOnly(Side.CLIENT)
public class DisplayPartOverlayRenderer extends PartOverlayRendererBase {

    protected static final float pixel = 0.0625F; // 0.0625 == 1/16

    @Override
    protected void setMatrixOrientation(ForgeDirection direction) {
        super.setMatrixOrientation(direction);
        float translateX = -1F - direction.offsetX + 4 * pixel;
        float translateY = 1F - direction.offsetY - 4 * pixel;
        float translateZ = direction.offsetZ - pixel + 0.0025F;
        if (direction == ForgeDirection.NORTH) {
            translateZ += 1F;
        } else if (direction == ForgeDirection.EAST) {
            translateX += 1F;
            translateZ += 1F;
        } else if (direction == ForgeDirection.SOUTH) {
            translateX += 1F;
        } else if (direction == ForgeDirection.UP) {
            translateX += 1F;
            translateZ += 1F;
        } else if (direction == ForgeDirection.DOWN) {
            translateX += 1F;
            translateY -= 1F;
        }
        GlStateManager.translate(translateX, translateY, translateZ);
    }

    @Override
    public void renderPartOverlay(IPartContainer partContainer, double x, double y, double z, float partialTick,
        int destroyStage, ForgeDirection direction, IPartType partType,
        TileEntityRendererDispatcher rendererDispatcher) {
        BlockPos pos = partContainer.getPosition()
            .getBlockPos();
        if (!shouldRender(pos)) return;

        // Calculate the alpha to be used when the player is almost out of rendering bounds.
        Entity renderEntity = FMLClientHandler.instance()
            .getClient().thePlayer;
        float distanceFactor = (float) ((getMaxRenderDistance()
            - renderEntity.getDistance(pos.getX(), pos.getY(), pos.getZ())) / 5);
        float distanceAlpha = Math.min(1.0F, distanceFactor);
        if (distanceAlpha < 0.05F) distanceAlpha = 0.05F; // Can't be 0 because the MC font renderer doesn't handle 0
                                                          // alpha's properly.

        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.disableLighting();

        GlStateManager.enableRescaleNormal();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        float scale = 0.04F;
        GlStateManager.translate((float) x, (float) y, (float) z);
        setMatrixOrientation(direction);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.scale(1, -1, 1);
        GlStateManager.disableRescaleNormal();

        PartTypePanelDisplay.State partState = (PartTypePanelDisplay.State) partContainer.getPartState(direction);
        if (partState == null || partState.getFacingRotation() == null) {
            drawError(rendererDispatcher, distanceAlpha);
        } else {
            int rotation = partState.getFacingRotation()
                .ordinal() - 2;
            GlStateManager.translate(6, 6, 0);
            GlStateManager.rotate(rotation * 90, 0, 0, 1);
            GlStateManager.translate(-6, -6, 0);

            IValue value = partState.getDisplayValue();
            if (value != null && partState.isEnabled()) {
                IValueType<?> valueType = value.getType();
                IValueTypeWorldRenderer renderer = ValueTypeWorldRenderers.REGISTRY.getRenderer(valueType);
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
                    value,
                    rendererDispatcher,
                    distanceAlpha);
            } else if (!partState.getInventory()
                .isEmpty()) {
                    drawError(rendererDispatcher, distanceAlpha);
                }
        }

        GlStateManager.enableLighting();
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    protected void drawError(TileEntityRendererDispatcher rendererDispatcher, float distanceAlpha) {
        Images.ERROR.drawWorldWithAlpha(rendererDispatcher.field_147553_e, 12.5F, 12.5F, distanceAlpha);
    }
}
