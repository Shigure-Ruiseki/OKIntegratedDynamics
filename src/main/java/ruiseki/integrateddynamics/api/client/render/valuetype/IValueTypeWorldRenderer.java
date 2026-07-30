package ruiseki.integrateddynamics.api.client.render.valuetype;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;

/**
 * Renderer used to display values for a given value type in the world.
 * 
 * @author rubensworks
 */
public interface IValueTypeWorldRenderer {

    /**
     * Render the overlay.
     * 
     * @param partContainer      The part container for this part.
     * @param x                  The center block X
     * @param y                  The center block Y
     * @param z                  The center block Z
     * @param partialTick        The partial tick
     * @param destroyStage       The destroy stage
     * @param direction          The direction this part is facing.
     * @param partType           The part type that is being overlayed
     * @param value              The value to render
     * @param rendererDispatcher The render dispatcher
     * @param alpha              The alpha to render with.
     */
    public void renderValue(IPartContainer partContainer, double x, double y, double z, float partialTick,
        int destroyStage, ForgeDirection direction, IPartType partType, IValue value,
        TileEntityRendererDispatcher rendererDispatcher, float alpha);

}
