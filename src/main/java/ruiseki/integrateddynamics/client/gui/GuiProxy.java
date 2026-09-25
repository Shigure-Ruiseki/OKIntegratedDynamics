package ruiseki.integrateddynamics.client.gui;

import net.minecraft.util.ResourceLocation;

import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.core.client.gui.GuiActiveVariableBase;
import ruiseki.integrateddynamics.inventory.container.ContainerProxy;
import ruiseki.integrateddynamics.tileentity.TileProxy;

/**
 * Gui for the proxy.
 *
 * @author rubensworks
 */
public class GuiProxy extends GuiActiveVariableBase<ContainerProxy, TileProxy> {

    private static final int ERROR_X = 110;
    private static final int ERROR_Y = 26;

    public GuiProxy(ContainerProxy container) {
        super(container);
    }

    @Override
    protected ResourceLocation constructGuiTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/proxy.png");
    }

    @Override
    protected int getBaseYSize() {
        return 189;
    }

    @Override
    protected int getErrorX() {
        return ERROR_X;
    }

    @Override
    protected int getErrorY() {
        return ERROR_Y;
    }
}
