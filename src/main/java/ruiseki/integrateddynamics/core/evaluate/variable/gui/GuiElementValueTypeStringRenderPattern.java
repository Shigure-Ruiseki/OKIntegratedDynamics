package ruiseki.integrateddynamics.core.evaluate.variable.gui;

import java.io.IOException;

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
    private GuiTextFieldExtended searchField = null;

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
        this.searchField = new GuiTextFieldExtended(
            0,
            fontRenderer,
            guiLeft + searchX,
            guiTop + searchY,
            searchWidth,
            fontRenderer.FONT_HEIGHT + 3,
            true);
        this.searchField.setMaxStringLength(512);
        this.searchField.setEnableBackgroundDrawing(false);
        this.searchField.setVisible(true);
        this.searchField.setTextColor(16777215);
        this.searchField.setCanLoseFocus(true);
        String value = element.getInputString();
        if (value == null) {
            value = element.getDefaultInputString();
        }
        this.searchField.setText(value);
        element.setInputString(searchField.getText());
        this.searchField.width = searchWidth;
        this.searchField.xPosition = guiLeft + (searchX + searchWidth) - this.searchField.width;
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
        searchField.drawTextBox(Minecraft.getMinecraft(), mouseX, mouseY);
    }

    @Override
    public boolean keyTyped(boolean checkHotbarKeys, char typedChar, int keyCode) throws IOException {
        if (!checkHotbarKeys) {
            if (searchField.textboxKeyTyped(typedChar, keyCode)) {
                onTyped();
                return true;
            }
        }
        return super.keyTyped(checkHotbarKeys, typedChar, keyCode);
    }

    private void onTyped() {
        element.setInputString(searchField.getText());
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
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        searchField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
