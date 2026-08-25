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

import ruiseki.integrateddynamics.tileentity.TileDryingBasin;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.fluid.FluidHelpers;
import ruiseki.okcore.helper.RenderHelpers;

/**
 * Renderer for the item and fluid inside the Drying Basin.
 *
 * @author rubensworks
 */
public class RenderTileEntityDryingBasin extends TileEntitySpecialRenderer
    implements RenderHelpers.IFluidContextRender {

    private TileDryingBasin lastTile;

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (te instanceof TileDryingBasin tile) {

            ItemStack stack = tile.getStackInSlot(0);
            if (stack != null) {
                renderItem(tile.getWorldObj(), stack, tile.getRandomRotation(), x, y, z);
            }

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

    private void renderItem(World world, ItemStack itemStack, float rotation, double x, double y, double z) {
        GlStateManager.pushMatrix();

        if (itemStack.getItem() instanceof ItemBlock) {
            GlStateManager.translate((float) x + 0.5F, (float) y + 0.25F, (float) z + 0.5F);
            GlStateManager.scale(2, 2, 2);
        } else {
            GlStateManager.translate((float) x + 0.5F, (float) y + 0.20F, (float) z + 0.5F);
            GlStateManager.rotate(90F, 1, 0, 0);
            GlStateManager.rotate(rotation, 0, 0, 1);
            GlStateManager.scale(2, 2, 2);
        }

        GlStateManager.pushAttrib();
        RenderHelper.enableStandardItemLighting();

        RenderHelpers.renderItem(world, itemStack, 0.0D, 0.0D, 0.0D);

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

        double height = (fluid.amount * 0.55D) / FluidHelpers.BUCKET_VOLUME + 0.35D;

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
        float a = 1.0F;

        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.setBrightness(brightness);
        t.setColorRGBA_F(r, g, b, a);
        t.setNormal(0.0F, 1.0F, 0.0F);

        t.addVertexWithUV(0.125F, height, 0.125F, icon.getMinU(), icon.getMaxV());
        t.addVertexWithUV(0.125F, height, 0.875F, icon.getMinU(), icon.getMinV());
        t.addVertexWithUV(0.875F, height, 0.875F, icon.getMaxU(), icon.getMinV());
        t.addVertexWithUV(0.875F, height, 0.125F, icon.getMaxU(), icon.getMaxV());

        t.draw();
    }
}
