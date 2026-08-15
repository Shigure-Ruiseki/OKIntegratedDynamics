package ruiseki.integrateddynamics.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;

import com.cleanroommc.modularui.utils.GlStateManager;
import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.client.gui.container.GuiMultipart;
import ruiseki.integrateddynamics.core.part.panel.PartTypePanelVariableDriven;
import ruiseki.integrateddynamics.inventory.container.ContainerPartDisplay;
import ruiseki.okcore.client.gui.component.button.GuiButtonText;
import ruiseki.okcore.client.key.KeyConflictContext;
import ruiseki.okcore.client.key.KeyModifier;
import ruiseki.okcore.helper.GuiHelpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.RenderHelpers;

/**
 * Gui for a writer part.
 *
 * @author rubensworks
 */
public class GuiPartDisplay<P extends PartTypePanelVariableDriven<P, S>, S extends PartTypePanelVariableDriven.State<P, S>>
    extends GuiMultipart<P, S> {

    private static final int ERROR_X = 104;
    private static final int ERROR_Y = 16;
    private static final int OK_X = 104;
    private static final int OK_Y = 16;

    private static final int BUTTON_COPY = 0;

    /**
     * Make a new instance.
     *
     * @param partTarget    The target.
     * @param player        The player.
     * @param partContainer The part container.
     * @param partType      The targeted part type.
     */
    public GuiPartDisplay(EntityPlayer player, PartTarget partTarget, IPartContainer partContainer,
        IPartType partType) {
        super(new ContainerPartDisplay<>(player, partTarget, partContainer, partType));
    }

    @Override
    public void initGui() {
        super.initGui();

        this.buttonList.add(
            new GuiButtonText(
                BUTTON_COPY,
                getGuiLeftTotal() + 128,
                getGuiTopTotal() + 32,
                30,
                12,
                LangHelpers.localize("gui.integrateddynamics.button.copy"),
                true));
    }

    @Override
    protected String getNameId() {
        return "part_display";
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        String readValue = ((ContainerPartDisplay<?, ?>) getContainer()).getReadValue();
        int readValueColor = ((ContainerPartDisplay<?, ?>) getContainer()).getReadValueColor();
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
            getPartState().getGlobalErrors(),
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
            getPartState().getGlobalErrors(),
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
    protected void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
        if (Keyboard.KEY_C == keyCode && KeyModifier.CONTROL.isActive(KeyConflictContext.GUI)) {
            valueToClipboard();
        }
    }

    @Override
    protected int getBaseXSize() {
        return 176;
    }

    @Override
    protected int getBaseYSize() {
        return 128;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        if (button.id == BUTTON_COPY) {
            valueToClipboard();
        }
    }

    protected void valueToClipboard() {
        String readValue = ((ContainerPartDisplay<?, ?>) getContainer()).getReadValue();
        if (readValue != null) {
            setClipboardString(readValue);
        }
    }
}
