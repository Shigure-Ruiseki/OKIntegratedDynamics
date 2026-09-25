package ruiseki.integrateddynamics.client.gui;

import net.minecraft.util.ResourceLocation;

import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.inventory.container.ContainerCoalGenerator;
import ruiseki.integrateddynamics.tileentity.TileCoalGenerator;
import ruiseki.okcore.client.gui.container.GuiContainerExtended;

/**
 * Gui for the coal generator.
 *
 * @author rubensworks
 */
public class GuiCoalGenerator extends GuiContainerExtended<ContainerCoalGenerator> {

    /**
     * Make a new instance.
     */
    public GuiCoalGenerator(ContainerCoalGenerator container) {
        super(container);
    }

    @Override
    protected ResourceLocation constructGuiTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/coal_generator.png");
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        int lastProgress = getContainer().getProgress();
        if (lastProgress >= 0) {
            this.drawTexturedModalRect(
                getGuiLeftTotal() + 81,
                getGuiTopTotal() + 30 + lastProgress,
                176,
                lastProgress,
                14,
                TileCoalGenerator.MAX_PROGRESS - lastProgress + 1);
        }
    }
}
