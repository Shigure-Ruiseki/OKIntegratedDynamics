package ruiseki.integrateddynamics.core.evaluate.variable.gui;

import java.io.IOException;
import java.util.Set;

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
import ruiseki.integrateddynamics.core.client.gui.GuiTextFieldDropdown;
import ruiseki.integrateddynamics.core.client.gui.IDropdownEntry;
import ruiseki.integrateddynamics.core.client.gui.IDropdownEntryListener;
import ruiseki.integrateddynamics.core.logicprogrammer.RenderPattern;
import ruiseki.integrateddynamics.network.packet.LogicProgrammerValueTypeStringValueChangedPacket;
import ruiseki.okcore.persist.IDirtyMarkListener;

/**
 * A render pattern for value types that can be read from and written to strings.
 *
 * @author rubensworks
 */
@SideOnly(Side.CLIENT)
public class GuiElementValueTypeDropdownListRenderPattern<T, S extends ISubGuiBox, G extends Gui, C extends Container>
    extends RenderPattern<GuiElementValueTypeDropdownList<T, G, C>, G, C> implements IDropdownEntryListener<T> {

    @Getter
    protected final GuiElementValueTypeDropdownList<T, G, C> element;
    @Getter
    private GuiTextFieldDropdown<T> searchField = null;

    public GuiElementValueTypeDropdownListRenderPattern(GuiElementValueTypeDropdownList<T, G, C> element, int baseX,
        int baseY, int maxWidth, int maxHeight, G gui, C container) {
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
        this.searchField = new GuiTextFieldDropdown<>(
            0,
            fontRenderer,
            guiLeft + searchX,
            guiTop + searchY,
            searchWidth,
            fontRenderer.FONT_HEIGHT + 3,
            true,
            getDropdownPossibilities());
        this.searchField.setDropdownEntryListener(this);
        this.searchField.setMaxStringLength(64);
        this.searchField.setEnableBackgroundDrawing(false);
        this.searchField.setVisible(true);
        this.searchField.setTextColor(16777215);
        this.searchField.setCanLoseFocus(true);
        String value = element.getInputString();
        if (value == null) {
            value = "";
        }
        this.searchField.setText(value);
        element.setInputString(searchField.getText());
        this.searchField.xPosition = guiLeft + (searchX + searchWidth) - this.searchField.getWidth();
    }

    protected Set<IDropdownEntry<T>> getDropdownPossibilities() {
        return element.getDropdownPossibilities();
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
                element.setInputString(searchField.getText());
                if (container instanceof IDirtyMarkListener) {
                    ((IDirtyMarkListener) container).onDirty();
                }
                IntegratedDynamics._instance.getPacketHandler()
                    .sendToServer(new LogicProgrammerValueTypeStringValueChangedPacket(element.getInputString()));
                return true;
            }
        }
        return super.keyTyped(checkHotbarKeys, typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        searchField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onSetDropdownPossiblity(IDropdownEntry dropdownEntry) {
        element.onSetDropdownPossiblity(dropdownEntry);
        if (container instanceof IDirtyMarkListener) {
            ((IDirtyMarkListener) container).onDirty();
        }
    }
}
