package ruiseki.integrateddynamics.client.render.part;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.client.FMLClientHandler;
import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.api.client.render.part.IPartOverlayRenderer;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.datastructure.BlockPos;

/**
 * Base class for part overlay renderers.
 *
 * @author rubensworks
 */
public abstract class PartOverlayRendererBase implements IPartOverlayRenderer {

    protected int getMaxRenderDistance() {
        return GeneralConfig.partOverlayRenderdistance;
    }

    protected boolean shouldRender(BlockPos pos) {
        Entity renderEntity = FMLClientHandler.instance()
            .getClient().thePlayer;
        return renderEntity.getDistance(pos.getX(), pos.getY(), pos.getZ()) < getMaxRenderDistance();
    }

    /**
     * Sets the OpenGL matrix orientation for the given direction.
     *
     * @param direction The direction to orient the OpenGL matrix to.
     */
    protected void setMatrixOrientation(ForgeDirection direction) {
        short rotationY = 0;
        short rotationX = 0;
        if (direction == ForgeDirection.SOUTH) {
            rotationY = 0;
        } else if (direction == ForgeDirection.NORTH) {
            rotationY = 180;
        } else if (direction == ForgeDirection.EAST) {
            rotationY = 90;
        } else if (direction == ForgeDirection.WEST) {
            rotationY = -90;
        } else if (direction == ForgeDirection.UP) {
            rotationX = -90;
        } else if (direction == ForgeDirection.DOWN) {
            rotationX = 90;
        }
        GlStateManager.rotate((float) rotationY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float) rotationX, 1.0F, 0.0F, 0.0F);
    }

}
