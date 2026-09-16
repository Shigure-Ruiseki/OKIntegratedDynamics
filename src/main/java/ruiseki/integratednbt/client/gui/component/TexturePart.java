package ruiseki.integratednbt.client.gui.component;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

/**
 * Represents a part in a texture; Offers help method for quick rendering
 */
public class TexturePart {

    private Texture texture;
    private int x;
    private int y;
    private int width;
    private int height;

    public TexturePart(Texture texture, int x, int y, int width, int height) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void renderTo(Gui gui, int x, int y) {
        this.texture.bind();
        gui.drawTexturedModalRect(x, y, this.x, this.y, this.width, this.height);
    }

    private void setColorInt(int color) {
        float r = (float) (color >> 16 & 255) / 255.0f;
        float g = (float) (color >> 8 & 255) / 255.0f;
        float b = (float) (color & 255) / 255.0f;
        GL11.glColor4f(r, g, b, 1.0f);
    }

    public void renderTo(Gui gui, int x, int y, int color) {
        this.setColorInt(color);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        this.renderTo(gui, x, y);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public void renderToScaled(Gui gui, int x, int y, int width, int height) {
        int destWidth = width == -1 ? this.width : width;
        int destHeight = height == -1 ? this.height : height;

        this.texture.bind();

        float u1 = (float) this.x * 0.00390625F;
        float v1 = (float) this.y * 0.00390625F;
        float u2 = (float) (this.x + this.width) * 0.00390625F;
        float v2 = (float) (this.y + this.height) * 0.00390625F;

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + destHeight, 0, u1, v2);
        tessellator.addVertexWithUV(x + destWidth, y + destHeight, 0, u2, v2);
        tessellator.addVertexWithUV(x + destWidth, y, 0, u2, v1);
        tessellator.addVertexWithUV(x, y, 0, u1, v1);
        tessellator.draw();
    }

    public void renderToScaled(Gui gui, int x, int y, int width, int height, int color) {
        this.setColorInt(color);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        this.renderToScaled(gui, x, y, width, height);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
