package ruiseki.integrateddynamics.core.client.gui;

import ruiseki.integrateddynamics.core.client.gui.container.DisplayErrorsComponent;
import ruiseki.integrateddynamics.core.inventory.container.ContainerActiveVariableBase;
import ruiseki.integrateddynamics.core.tileentity.TileActiveVariableBase;
import ruiseki.okcore.client.gui.container.GuiContainerConfigurable;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.RenderHelpers;

/**
 * Base gui for part entities that can hold variables.
 *
 * @author rubensworks
 */
public abstract class GuiActiveVariableBase<C extends ContainerActiveVariableBase<T>, T extends TileActiveVariableBase<?>>
    extends GuiContainerConfigurable<C> {

    protected final DisplayErrorsComponent displayErrors = new DisplayErrorsComponent();

    /**
     * Make a new instance.
     *
     * @param container The container
     */
    public GuiActiveVariableBase(C container) {
        super(container);
    }

    protected abstract int getErrorX();

    protected abstract int getErrorY();

    protected int getValueX() {
        return 54;
    }

    protected int getValueY() {
        return 57;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        super.drawGuiContainerBackgroundLayer(f, x, y);

        String readValue = getContainer().getReadValue();
        int readValueColor = getContainer().getReadValueColor();
        boolean ok = false;
        if (getContainer().getTile()
            .hasVariable() && readValue != null) {
            ok = true;
            RenderHelpers.drawScaledCenteredString(
                fontRendererObj,
                readValue,
                getGuiLeftTotal() + getValueX(),
                getGuiTopTotal() + getValueY(),
                70,
                readValueColor);
        }

        GlStateManager.color(1f, 1f, 1f, 1f);
        displayErrors.drawBackground(
            getContainer().getTile()
                .getEvaluator()
                .getErrors(),
            getErrorX(),
            getErrorY(),
            getErrorX(),
            getErrorY(),
            this,
            this.guiLeft,
            this.guiTop,
            ok);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        displayErrors.drawForeground(
            getContainer().getTile()
                .getEvaluator()
                .getErrors(),
            getErrorX(),
            getErrorY(),
            mouseX,
            mouseY,
            this,
            this.guiLeft,
            this.guiTop);
    }
}
