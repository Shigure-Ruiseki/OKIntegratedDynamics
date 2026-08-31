package ruiseki.integrateddynamics.core.logicprogrammer;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.inventory.Container;

import org.apache.commons.lang3.tuple.Pair;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Getter;
import ruiseki.integrateddynamics.api.client.gui.subgui.IGuiInputElement;
import ruiseki.integrateddynamics.api.client.gui.subgui.ISubGuiBox;
import ruiseki.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import ruiseki.integrateddynamics.core.client.gui.subgui.SubGuiBox;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.RenderHelpers;

/**
 * Sub gui for logic programmer elements.
 *
 * @author rubensworks
 */
@SideOnly(Side.CLIENT)
public class RenderPattern<E extends IGuiInputElement, G extends Gui, C extends Container> extends SubGuiBox
    implements ISubGuiBox {

    @Getter
    protected final E element;
    private final int x, y;
    protected final G gui;
    protected final C container;

    public RenderPattern(E element, int baseX, int baseY, int maxWidth, int maxHeight, G gui, C container) {
        super(SubGuiBox.Box.LIGHT);
        this.element = element;
        IConfigRenderPattern configRenderPattern = element.getRenderPattern();
        this.x = calculateX(baseX, maxWidth, configRenderPattern);
        this.y = calculateY(baseY, maxHeight, configRenderPattern);
        this.gui = gui;
        this.container = container;
    }

    public static int calculateX(int baseX, int maxWidth, IConfigRenderPattern configRenderPattern) {
        return baseX + (maxWidth - configRenderPattern.getWidth()) / 2;
    }

    public static int calculateY(int baseY, int maxHeight, IConfigRenderPattern configRenderPattern) {
        return baseY + (maxHeight - configRenderPattern.getHeight()) / 2;
    }

    protected void drawSlot(int x, int y) {
        this.drawTexturedModalRect(x, y, 19, 0, 18, 18);
    }

    @Override
    public void initGui(int guiLeft, int guiTop) {
        super.initGui(guiLeft, guiTop);
    }

    protected boolean drawRenderPattern() {
        return true;
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
        if (drawRenderPattern()) {
            IConfigRenderPattern configRenderPattern = element.getRenderPattern();

            int baseX = getX() + guiLeft;
            int baseY = getY() + guiTop;

            for (Pair<Integer, Integer> slot : configRenderPattern.getSlotPositions()) {
                drawSlot(baseX + slot.getLeft(), baseY + slot.getRight());
            }

            if (configRenderPattern.getSymbolPosition() != null) {
                RenderHelpers.drawScaledCenteredString(
                    fontRenderer,
                    element.getSymbol(),
                    baseX + configRenderPattern.getSymbolPosition()
                        .getLeft(),
                    baseY + configRenderPattern.getSymbolPosition()
                        .getRight() + 8,
                    0,
                    1,
                    0);
            }
            GlStateManager.color(1, 1, 1, 1);
        }
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public int getWidth() {
        return element.getRenderPattern()
            .getWidth();
    }

    @Override
    public int getHeight() {
        return element.getRenderPattern()
            .getHeight();
    }

    public void sendValueToServer() {

    }

}
