package ruiseki.integrateddynamics.client.gui;

import net.minecraft.client.gui.FontRenderer;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.core.client.gui.container.GuiMultipart;
import ruiseki.integrateddynamics.core.part.panel.PartTypePanelVariableDriven;
import ruiseki.integrateddynamics.inventory.container.ContainerPartPanelVariableDriven;
import ruiseki.okcore.client.gui.component.button.GuiButtonText;
import ruiseki.okcore.client.key.KeyConflictContext;
import ruiseki.okcore.client.key.KeyModifier;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.GuiHelpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.RenderHelpers;

/**
 * Gui for a writer part.
 *
 * @author rubensworks
 */
public class GuiPartDisplay<P extends PartTypePanelVariableDriven<P, S>, S extends PartTypePanelVariableDriven.State<P, S>>
    extends GuiMultipart<P, S, ContainerPartPanelVariableDriven<P, S>> {

    private static final int ERROR_X = 104;
    private static final int ERROR_Y = 16;
    private static final int OK_X = 104;
    private static final int OK_Y = 16;

    public GuiPartDisplay(ContainerPartPanelVariableDriven<P, S> container) {
        super(container);
    }

    @Override
    public void initGui() {
        super.initGui();

        addRenderableWidget(
            new GuiButtonText(
                getGuiLeftTotal() + 128,
                getGuiTopTotal() + 32,
                30,
                12,
                LangHelpers.localize("gui.integrateddynamics.button.copy"),
                (button) -> valueToClipboard(),
                true));
    }

    @Override
    protected String getNameId() {
        return "part_display";
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        String readValue = getContainer().getReadValue();
        int readValueColor = getContainer().getReadValueColor();
        boolean ok = false;
        if (readValue != null) {
            ok = true;
            FontRenderer fontRenderer = fontRendererObj;
            RenderHelpers.drawScaledCenteredString(
                fontRenderer,
                readValue,
                getGuiLeftTotal() + 53,
                getGuiTopTotal() + 38,
                70,
                readValueColor);
        }

        GlStateManager.color(1, 1, 1, 1);
        displayErrors.drawBackground(
            getContainer().getReadErrors(),
            ERROR_X,
            ERROR_Y,
            OK_X,
            OK_Y,
            this,
            this.guiLeft,
            this.guiTop,
            ok);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        // Render error tooltip
        displayErrors.drawForeground(
            getContainer().getReadErrors(),
            ERROR_X,
            ERROR_Y,
            mouseX,
            mouseY,
            this,
            this.guiLeft,
            this.guiTop);
        // Draw tooltip over copy button
        GuiHelpers.renderTooltip(
            this,
            128,
            32,
            30,
            12,
            mouseX,
            mouseY,
            () -> Lists.newArrayList(LangHelpers.localize("gui.integrateddynamics.button.copy.info")));
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        if (Keyboard.KEY_C == keyCode && KeyModifier.CONTROL.isActive(KeyConflictContext.GUI)) {
            valueToClipboard();
            return true;
        }
        return super.charTyped(typedChar, keyCode);
    }

    @Override
    protected int getBaseXSize() {
        return 176;
    }

    @Override
    protected int getBaseYSize() {
        return 128;
    }

    protected void valueToClipboard() {
        String readValue = getContainer().getReadValue();
        if (readValue != null) {
            setClipboardString(readValue);
        }
    }
}
