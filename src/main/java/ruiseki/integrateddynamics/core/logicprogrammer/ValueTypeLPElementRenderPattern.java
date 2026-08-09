package ruiseki.integrateddynamics.core.logicprogrammer;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammer;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammerBase;
import ruiseki.integrateddynamics.core.evaluate.variable.GuiElementValueTypeStringRenderPattern;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;

/**
 * @author rubensworks
 */
@SideOnly(Side.CLIENT)
public class ValueTypeLPElementRenderPattern extends
    GuiElementValueTypeStringRenderPattern<RenderPattern, GuiLogicProgrammerBase, ContainerLogicProgrammerBase> {

    public ValueTypeLPElementRenderPattern(ValueTypeLPElementBase element, int baseX, int baseY, int maxWidth,
        int maxHeight, GuiLogicProgrammerBase gui, ContainerLogicProgrammerBase container) {
        super(element.getInnerGuiElement(), baseX, baseY, maxWidth, maxHeight, gui, container);
    }

    @Override
    public void drawGuiContainerForegroundLayer(int guiLeft, int guiTop, TextureManager textureManager,
        FontRenderer fontRenderer, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(guiLeft, guiTop, textureManager, fontRenderer, mouseX, mouseY);
        IValueType valueType = element.getValueType();

        // Output type tooltip
        if (!container.hasWriteItemInSlot()) {
            if (gui.func_146978_c(
                ContainerLogicProgrammerBase.OUTPUT_X,
                ContainerLogicProgrammerBase.OUTPUT_Y,
                GuiLogicProgrammer.BOX_HEIGHT,
                GuiLogicProgrammer.BOX_HEIGHT,
                mouseX,
                mouseY)) {
                gui.drawTooltip(getValueTypeTooltip(valueType), mouseX - guiLeft, mouseY - guiTop);
            }
        }
    }

}
