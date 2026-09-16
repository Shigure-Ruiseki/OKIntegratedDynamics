package ruiseki.integratednbt.client.gui.component;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.client.gui.component.button.GuiButtonExtended;
import ruiseki.okcore.client.renderer.GlStateManager;

/**
 * Custom ImageButton
 */
@SideOnly(Side.CLIENT)
public class ImageButton extends GuiButtonExtended {

    private TexturePart textureNormal;
    private TexturePart textureHover;

    public ImageButton(TexturePart textureNormal, TexturePart textureHover, int x, int y, OnPress onPress) {
        super(
            x,
            y,
            textureNormal != null ? textureNormal.getWidth() : 0,
            textureNormal != null ? textureNormal.getHeight() : 0,
            "",
            onPress,
            false);
        this.textureNormal = textureNormal;
        this.textureHover = textureHover;
    }

    /**
     * For lazy initialization of textures.
     */
    public ImageButton(int x, int y, OnPress onPress) {
        super(x, y, 1, 1, "", onPress, false);
    }

    public void setTexture(TexturePart textureNormal, TexturePart textureHover) {
        this.textureNormal = textureNormal;
        this.textureHover = textureHover;
        if (textureNormal != null) {
            this.width = textureNormal.getWidth();
            this.height = textureNormal.getHeight();
        }
    }

    @Override
    protected void drawButtonInner(int mouseX, int mouseY, boolean mouseOver) {
        TexturePart texturePart = mouseOver && this.textureHover != null ? this.textureHover : this.textureNormal;

        if (texturePart != null) {
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableDepth();
            texturePart.renderTo(this, this.getX(), this.getY(), 0xffffff);
        }
    }
}
