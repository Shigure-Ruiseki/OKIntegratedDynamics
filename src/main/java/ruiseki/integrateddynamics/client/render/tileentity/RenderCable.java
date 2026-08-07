package ruiseki.integrateddynamics.client.render.tileentity;

import java.util.Map;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integrateddynamics.api.client.render.part.IPartOverlayRenderer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.client.render.part.PartOverlayRenderers;
import ruiseki.integrateddynamics.core.tileentity.TileMultipartTicking;

/**
 * Renderer for cable components.
 * Breaking overlay rendering code inspired by MCMultiPart:
 * https://github.com/amadornes/MCMultiPart/blob/master/src/main/java/mcmultipart/client/multipart/MultipartContainerSpecialRenderer.java
 *
 * @author rubensworks
 */
public class RenderCable extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTick) {
        if (!(tileEntity instanceof TileMultipartTicking tile)) return;
        if (MinecraftForgeClient.getRenderPass() == 0) {
            for (Map.Entry<ForgeDirection, IPartType<?, ?>> entry : tile.getPartContainer()
                .getParts()
                .entrySet()) {
                for (IPartOverlayRenderer renderer : PartOverlayRenderers.REGISTRY.getRenderers(entry.getValue())) {
                    renderer.renderPartOverlay(
                        tile.getPartContainer(),
                        x,
                        y,
                        z,
                        partialTick,
                        0,
                        entry.getKey(),
                        entry.getValue(),
                        field_147501_a);
                }
            }
        }
    }

}
