package ruiseki.integrateddynamics.client.render.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import ruiseki.integrateddynamics.block.BlockEnergyBattery;
import ruiseki.integrateddynamics.tileentity.TileEnergyBattery;
import ruiseki.okcore.client.renderer.GlStateManager;

/**
 * Renderer for rendering the energy overlay on the {@link ruiseki.integrateddynamics.block.BlockEnergyBattery}.
 *
 * @author rubensworks
 *
 */
public class RenderTileEntityEnergyBattery extends TileEntitySpecialRenderer {

    private static final double OFFSET = 0.001D;
    private static final double MINY = 0D;
    private static final double MAXY = 1D;
    private static final double MIN = 0D - OFFSET;
    private static final double MAX = 1D + OFFSET;

    private static final double[][][] coordinates = { { // DOWN
        { MIN, MINY, MIN }, { MIN, MINY, MAX }, { MAX, MINY, MAX }, { MAX, MINY, MIN } },
        { // UP
            { MIN, MAXY, MIN }, { MIN, MAXY, MAX }, { MAX, MAXY, MAX }, { MAX, MAXY, MIN } },
        { // NORTH
            { MIN, MINY, MIN }, { MIN, MAXY, MIN }, { MAX, MAXY, MIN }, { MAX, MINY, MIN } },
        { // SOUTH
            { MAX, MINY, MAX }, { MAX, MAXY, MAX }, { MIN, MAXY, MAX }, { MIN, MINY, MAX } },
        { // WEST
            { MIN, MINY, MAX }, { MIN, MAXY, MAX }, { MIN, MAXY, MIN }, { MIN, MINY, MIN } },
        { // EAST
            { MAX, MINY, MIN }, { MAX, MAXY, MIN }, { MAX, MAXY, MAX }, { MAX, MINY, MAX } } };

    private static final ForgeDirection[] VALID_DIRECTIONS = new ForgeDirection[] { ForgeDirection.NORTH,
        ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST };

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTickTime) {
        if (!(te instanceof TileEnergyBattery)) return;
        TileEnergyBattery tile = (TileEnergyBattery) te;

        if (tile.getEnergyStored() > 0) {
            double energyRatio = (double) tile.getEnergyStored() / tile.getMaxEnergyStored();

            // Re-scale energy visual ratio to matching texture frame [0.125, 0.875]
            double renderHeight = (energyRatio * 12.0D / 16.0D) + 0.125D;

            int brightness = tile.getWorldObj()
                .getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord, tile.zCoord, 0);
            int j = brightness % 65536;
            int k = brightness / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);

            GlStateManager.pushMatrix();

            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            GlStateManager.translate(x, y, z);

            Minecraft.getMinecraft()
                .getTextureManager()
                .bindTexture(TextureMap.locationBlocksTexture);

            float r = 1.0F;
            float g = 1.0F;
            float b = 1.0F;
            float a = 1.0F;

            if (tile.isCreative()) {
                float tickFactor = (((float) (tile.getWorldObj()
                    .getTotalWorldTime() % 20) + partialTickTime) / 10.0F);
                if (tickFactor > 1.0F) {
                    tickFactor = 2.0F - tickFactor;
                }
                r = 0.8F + 0.2F * tickFactor;
                g = 0.42F;
                b = 0.60F + 0.40F * tickFactor;
            }

            Tessellator t = Tessellator.instance;

            IIcon icon = BlockEnergyBattery.getInstance().iconOverlay;
            if (icon != null) {
                double minU = icon.getMinU();
                double maxU = icon.getMaxU();
                double minV = icon.getMinV();
                double maxV = icon.getMaxV();

                double replacedMinV = maxV - ((maxV - minV) * renderHeight);

                for (ForgeDirection side : VALID_DIRECTIONS) {
                    t.startDrawingQuads();
                    t.setColorRGBA_F(r, g, b, a);

                    double[][] c = coordinates[side.ordinal()];

                    t.addVertexWithUV(c[0][0], c[0][1] == MAXY ? renderHeight : MINY, c[0][2], minU, maxV);
                    t.addVertexWithUV(c[1][0], c[1][1] == MAXY ? renderHeight : MINY, c[1][2], minU, replacedMinV);
                    t.addVertexWithUV(c[2][0], c[2][1] == MAXY ? renderHeight : MINY, c[2][2], maxU, replacedMinV);
                    t.addVertexWithUV(c[3][0], c[3][1] == MAXY ? renderHeight : MINY, c[3][2], maxU, maxV);

                    t.draw();
                }
            }

            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }
}
