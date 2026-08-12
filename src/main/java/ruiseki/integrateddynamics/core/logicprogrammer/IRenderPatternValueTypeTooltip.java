package ruiseki.integrateddynamics.core.logicprogrammer;

import java.util.List;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammerBase;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import ruiseki.okcore.helper.LangHelpers;

/**
 * @author rubensworks
 */
public interface IRenderPatternValueTypeTooltip {

    public default List<String> getValueTypeTooltip(IValueType<?> valueType) {
        List<String> lines = Lists.newLinkedList();
        lines.add(valueType.getDisplayColorFormat() + LangHelpers.localize(valueType.getUnlocalizedName()));
        return lines;
    }

    public abstract boolean isRenderTooltip();

    public abstract void setRenderTooltip(boolean renderTooltip);

    public default void drawTooltipForeground(GuiLogicProgrammerBase gui, ContainerLogicProgrammerBase container,
        int guiLeft, int guiTop, int mouseX, int mouseY, IValueType valueType) {
        if (isRenderTooltip()) {
            // Output type tooltip
            if (!container.hasWriteItemInSlot()) {
                if (gui.func_146978_c(
                    ContainerLogicProgrammerBase.OUTPUT_X,
                    ContainerLogicProgrammerBase.OUTPUT_Y,
                    GuiLogicProgrammerBase.BOX_HEIGHT,
                    GuiLogicProgrammerBase.BOX_HEIGHT,
                    mouseX,
                    mouseY)) {
                    gui.drawTooltip(getValueTypeTooltip(valueType), mouseX - guiLeft, mouseY - guiTop);
                }
            }
        }
    }

}
