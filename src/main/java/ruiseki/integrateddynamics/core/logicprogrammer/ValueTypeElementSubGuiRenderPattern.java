package ruiseki.integrateddynamics.core.logicprogrammer;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammer;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeSubGuiRenderPattern;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammer;

/**
 * @author rubensworks
 */
@SideOnly(Side.CLIENT)
public class ValueTypeElementSubGuiRenderPattern
    extends ValueTypeSubGuiRenderPattern<SubGuiConfigRenderPattern, GuiLogicProgrammer, ContainerLogicProgrammer> {

    public ValueTypeElementSubGuiRenderPattern(ValueTypeElement element, int baseX, int baseY, int maxWidth,
        int maxHeight, GuiLogicProgrammer gui, ContainerLogicProgrammer container) {
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
                ContainerLogicProgrammer.OUTPUT_X,
                ContainerLogicProgrammer.OUTPUT_Y,
                GuiLogicProgrammer.BOX_HEIGHT,
                GuiLogicProgrammer.BOX_HEIGHT,
                mouseX,
                mouseY)) {
                gui.drawTooltip(getValueTypeTooltip(valueType), mouseX - guiLeft, mouseY - guiTop);
            }
        }
    }

}
