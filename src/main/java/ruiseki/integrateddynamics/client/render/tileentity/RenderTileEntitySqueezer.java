package ruiseki.integrateddynamics.client.render.tileentity;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.integrateddynamics.tileentity.TileSqueezer;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.FluidHelpers;
import ruiseki.okcore.helper.RenderHelpers;

/**
 * Renderer for the item and fluid inside the {@link ruiseki.integrateddynamics.block.BlockSqueezer}.
 *
 * @author rubensworks
 */
public class RenderTileEntitySqueezer extends TileEntitySpecialRenderer implements RenderHelpers.IFluidContextRender {

    private static final double OFFSET = 0.01D;
    private static final double MINY = 0.0625D;
    private static final double MAXY = 0.125D - OFFSET;
    private static final double MIN = 0D + OFFSET;
    private static final double MAX = 1D - OFFSET;

    private static final double[][][] coordinates = { { // DOWN
        { MIN, MINY, MIN }, { MIN, MINY, MAX }, { MAX, MINY, MAX }, { MAX, MINY, MIN } },
        { // UP
            { MIN, MAXY, MIN }, { MIN, MAXY, MAX }, { MAX, MAXY, MAX }, { MAX, MAXY, MIN } },
        { // NORTH
            { MIN, MINY, MIN }, { MIN, MAXY, MIN }, { MAX, MAXY, MIN }, { MAX, MINY, MIN } },
        { // SOUTH
            { MIN, MINY, MAX }, { MIN, MAXY, MAX }, { MAX, MAXY, MAX }, { MAX, MINY, MAX } },
        { // WEST
            { MIN, MINY, MIN }, { MIN, MAXY, MIN }, { MIN, MAXY, MAX }, { MIN, MINY, MAX } },
        { // EAST
            { MAX, MINY, MIN }, { MAX, MAXY, MIN }, { MAX, MAXY, MAX }, { MAX, MINY, MAX } } };

    private TileSqueezer lastTile;

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (te instanceof TileSqueezer tile) {
            ItemStack stack = tile.getStackInSlot(0);
            if (stack != null) {
                renderItem(tile.getWorldObj(), stack, tile, x, y, z);
            }

            if (!tile.getTank()
                .isEmpty()) {
                lastTile = tile;
                RenderHelpers.renderTileFluidContext(
                    tile.getTank()
                        .getFluid(),
                    x,
                    y,
                    z,
                    tile,
                    this);
            }
        }
    }

    private void renderItem(World world, ItemStack itemStack, TileSqueezer tile, double x, double y, double z) {
        GlStateManager.pushMatrix();

        float heightFactor = (8 - tile.getItemHeight()) * 0.125F;

        if (itemStack.getItem() instanceof ItemBlock) {
            float renderY = (float) y + 0.125F + (heightFactor * 0.5F);
            GlStateManager.translate((float) x + 0.5F, renderY, (float) z + 0.5F);
            float scaleXZ = 2.0F;
            float scaleY = Math.max(0.05F, heightFactor * scaleXZ);

            GlStateManager.scale(scaleXZ, scaleY, scaleXZ);
        } else {
            GlStateManager.translate((float) x + 0.5F, (float) y + 0.13F, (float) z + 0.75F);
            GlStateManager.rotate(90F, 1, 0, 0);
        }

        GlStateManager.pushAttrib();
        RenderHelper.enableStandardItemLighting();
        RenderHelpers.renderItem(world, itemStack, 0.0D, -0.2D, 0.0D);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popAttrib();

        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    @Override
    public void renderFluid(FluidStack fluid) {
        if (fluid == null || fluid.getFluid() == null || lastTile == null) {
            return;
        }

        double height = Math
            .max(0.0625D - OFFSET, ((double) fluid.amount) * 0.0625D / FluidHelpers.BUCKET_VOLUME + 0.0625D - OFFSET);

        int brightness = lastTile.getWorldObj()
            .getLightBrightnessForSkyBlocks(
                lastTile.xCoord,
                lastTile.yCoord,
                lastTile.zCoord,
                fluid.getFluid()
                    .getLuminosity(fluid));

        IIcon icon = RenderHelpers.getFluidIcon(fluid, ForgeDirection.UP);
        if (icon == null) return;

        int color = fluid.getFluid()
            .getColor(fluid);
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;

        Tessellator t = Tessellator.instance;

        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            t.startDrawingQuads();
            t.setBrightness(brightness);
            t.setColorRGBA_F(r, g, b, 1.0F);

            double[][] c = coordinates[side.ordinal()];
            double replacedMaxV = (side == ForgeDirection.UP || side == ForgeDirection.DOWN) ? icon.getMaxV()
                : ((icon.getMaxV() - icon.getMinV()) * height + icon.getMinV());

            t.addVertexWithUV(c[0][0], getHeight(side, c[0][1], height), c[0][2], icon.getMinU(), replacedMaxV);
            t.addVertexWithUV(c[1][0], getHeight(side, c[1][1], height), c[1][2], icon.getMinU(), icon.getMinV());
            t.addVertexWithUV(c[2][0], getHeight(side, c[2][1], height), c[2][2], icon.getMaxU(), icon.getMinV());
            t.addVertexWithUV(c[3][0], getHeight(side, c[3][1], height), c[3][2], icon.getMaxU(), replacedMaxV);

            t.draw();
        }
    }

    private static double getHeight(ForgeDirection side, double height, double replaceHeight) {
        if (height == MAXY) {
            return replaceHeight;
        }
        return height;
    }
}
