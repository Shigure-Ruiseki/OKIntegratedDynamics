package ruiseki.integrateddynamics.client.gui;

import net.minecraft.util.ResourceLocation;

import ruiseki.integrateddynamics.inventory.container.ContainerVariablestore;
import ruiseki.integrateddynamics.tileentity.TileVariablestore;
import ruiseki.okcore.client.gui.container.GuiContainerExtended;
import ruiseki.okcore.client.renderer.GlStateManager;

/**
 * Gui for the variablestore
 *
 * @author rubensworks
 */
public class GuiVariablestore extends GuiContainerExtended<ContainerVariablestore> {

    public GuiVariablestore(ContainerVariablestore container) {
        super(container);
    }

    @Override
    protected ResourceLocation constructGuiTexture() {
        return new ResourceLocation("textures/gui/container/generic_54.png");
    }

    @Override
    protected int getBaseYSize() {
        return TileVariablestore.ROWS * 18 + 17 + 96;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager()
            .bindTexture(texture);
        this.drawTexturedModalRect(
            guiLeft + offsetX,
            guiTop + offsetY,
            0,
            0,
            this.xSize,
            TileVariablestore.ROWS * 18 + 17);
        this.drawTexturedModalRect(
            guiLeft + offsetX,
            guiTop + offsetY + TileVariablestore.ROWS * 18 + 17,
            0,
            126,
            this.xSize,
            96);
    }
}
