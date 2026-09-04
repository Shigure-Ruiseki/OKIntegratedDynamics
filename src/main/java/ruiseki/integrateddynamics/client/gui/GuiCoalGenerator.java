package ruiseki.integrateddynamics.client.gui;

import net.minecraft.entity.player.InventoryPlayer;

import ruiseki.integrateddynamics.inventory.container.ContainerCoalGenerator;
import ruiseki.integrateddynamics.tileentity.TileCoalGenerator;
import ruiseki.okcore.client.gui.container.GuiContainerConfigurable;

/**
 * Gui for the coal generator.
 * 
 * @author rubensworks
 */
public class GuiCoalGenerator extends GuiContainerConfigurable<ContainerCoalGenerator> {

    /**
     * Make a new instance.
     * 
     * @param inventory The player inventory.
     * @param tile      The part.
     */
    public GuiCoalGenerator(InventoryPlayer inventory, TileCoalGenerator tile) {
        super(new ContainerCoalGenerator(inventory, tile));
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
