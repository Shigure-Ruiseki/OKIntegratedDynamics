package ruiseki.integrateddynamics.core.logicprogrammer;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammerBase;
import ruiseki.integrateddynamics.core.evaluate.variable.gui.GuiElementValueTypeStringRenderPattern;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;

/**
 * @author rubensworks
 */
@SideOnly(Side.CLIENT)
public class ValueTypeStringLPElementRenderPattern
    extends GuiElementValueTypeStringRenderPattern<RenderPattern, GuiLogicProgrammerBase, ContainerLogicProgrammerBase>
    implements IRenderPatternValueTypeTooltip {

    private boolean renderTooltip = true;

    public ValueTypeStringLPElementRenderPattern(ValueTypeStringLPElement element, int baseX, int baseY, int maxWidth,
        int maxHeight, GuiLogicProgrammerBase gui, ContainerLogicProgrammerBase container) {
        super(element.getInnerGuiElement(), baseX, baseY, maxWidth, maxHeight, gui, container);
    }

    @Override
    public void drawGuiContainerForegroundLayer(int guiLeft, int guiTop, TextureManager textureManager,
        FontRenderer fontRenderer, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(guiLeft, guiTop, textureManager, fontRenderer, mouseX, mouseY);
        this.drawTooltipForeground(gui, container, guiLeft, guiTop, mouseX, mouseY, element.getValueType());
    }

    @Override
    public boolean isRenderTooltip() {
        return this.renderTooltip;
    }

    @Override
    public void setRenderTooltip(boolean renderTooltip) {
        this.renderTooltip = renderTooltip;
    }
}
