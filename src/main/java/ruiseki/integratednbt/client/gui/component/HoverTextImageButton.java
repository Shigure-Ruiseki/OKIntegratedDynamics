package ruiseki.integratednbt.client.gui.component;

import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.helper.GuiHelpers;

@SideOnly(Side.CLIENT)
public class HoverTextImageButton extends ImageButton {

    private GuiContainer gui;
    private List<String> hoverText;

    public HoverTextImageButton(GuiContainer gui, TexturePart textureNormal, TexturePart textureHover, int x, int y,
        OnPress onPress) {
        super(textureNormal, textureHover, x, y, onPress);
        this.gui = gui;
        this.gui = gui;
    }

    public HoverTextImageButton(GuiContainer gui, int x, int y, OnPress onPress) {
        super(x, y, onPress);
        this.gui = gui;
    }

    public void setHoverText(List<String> hoverText) {
        this.hoverText = hoverText;
    }

    public void setHoverTextRaw(List<String> hoverText) {
        this.hoverText = hoverText;
    }

    public void drawHover(GuiContainer gui, int mouseX, int mouseY) {
        if (this.visible && this.field_146123_n && this.hoverText != null && !this.hoverText.isEmpty()) {
            GuiHelpers.drawTooltip(gui, this.hoverText, mouseX, mouseY);
        }
    }
}
