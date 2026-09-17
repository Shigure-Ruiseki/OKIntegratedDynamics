package ruiseki.integrateddynamics.core.evaluate.variable.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.inventory.Container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Getter;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.client.gui.subgui.ISubGuiBox;
import ruiseki.integrateddynamics.core.logicprogrammer.RenderPattern;
import ruiseki.integrateddynamics.network.packet.LogicProgrammerValueTypeStringValueChangedPacket;
import ruiseki.okcore.client.gui.component.input.GuiTextFieldExtended;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.persist.IDirtyMarkListener;

/**
 * @author rubensworks
 */
@SideOnly(Side.CLIENT)
public class GuiElementValueTypeStringRenderPattern<S extends ISubGuiBox, G extends Gui, C extends Container>
    extends RenderPattern<GuiElementValueTypeString<G, C>, G, C> {

    @Getter
    protected final GuiElementValueTypeString<G, C> element;
    @Getter
    private GuiTextFieldExtended textField = null;

    public GuiElementValueTypeStringRenderPattern(GuiElementValueTypeString<G, C> element, int baseX, int baseY,
        int maxWidth, int maxHeight, G gui, C container) {
        super(element, baseX, baseY, maxWidth, maxHeight, gui, container);
        this.element = element;
    }

    @Override
    public void initGui(int guiLeft, int guiTop) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        int searchWidth = getElement().getRenderPattern()
            .getWidth() - 28;
        int searchX = getX() + 14;
        int searchY = getY() + 6;
        this.textField = new GuiTextFieldExtended(
            fontRenderer,
            guiLeft + searchX,
            guiTop + searchY,
            searchWidth,
            fontRenderer.FONT_HEIGHT + 3,
            LangHelpers.localize(
                this.getElement()
                    .getValueType()
                    .getUnlocalizedName()),
            true);
        this.textField.setMaxStringLength(512);
        this.textField.setEnableBackgroundDrawing(false);
        this.textField.setVisible(true);
        this.textField.setTextColor(16777215);
        this.textField.setCanLoseFocus(true);
        String value = element.getInputString();
        if (value == null) {
            value = element.getDefaultInputString();
        }
        this.textField.setText(value);
        element.setInputString(textField.getText());
        this.textField.width = searchWidth;
        this.textField.xPosition = guiLeft + (searchX + searchWidth) - this.textField.width;
    }

    @Override
    public void drawGuiContainerBackgroundLayer(int guiLeft, int guiTop, TextureManager textureManager,
        FontRenderer fontRenderer, float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(
            guiLeft,
            guiTop,
            textureManager,
            fontRenderer,
            partialTicks,
            mouseX,
            mouseY);

        // Textbox
        textField.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        if (textField.isFocused()) {
            if (textField.charTyped(typedChar, keyCode)) {
                onTyped();
                return true;
            }
        }
        return super.charTyped(typedChar, keyCode);
    }

    @Override
    public boolean keyPressed(int typedChar, int keyCode, int modifiers) {
        if (textField.isFocused()) {
            textField.keyPressed(typedChar, keyCode, modifiers);
            onTyped();
            return true;
        }
        return super.keyPressed(typedChar, keyCode, modifiers);
    }

    private void onTyped() {
        element.setInputString(textField.getText());
        if (container instanceof IDirtyMarkListener) {
            ((IDirtyMarkListener) container).onDirty();
        }
        sendValueToServer();
    }

    @Override
    public void sendValueToServer() {
        super.sendValueToServer();
        IntegratedDynamics._instance.getPacketHandler()
            .sendToServer(new LogicProgrammerValueTypeStringValueChangedPacket(element.getInputString()));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return textField.mouseClicked(mouseX, mouseY, mouseButton) || super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
