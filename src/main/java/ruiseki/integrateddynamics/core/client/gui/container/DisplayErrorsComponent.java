package ruiseki.integrateddynamics.core.client.gui.container;

import java.util.List;

import net.minecraft.util.EnumChatFormatting;

import com.google.common.collect.Lists;

import ruiseki.okcore.client.gui.container.GuiContainerExtended;
import ruiseki.okcore.client.gui.image.Images;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.StringHelpers;

/**
 * A component for displaying errors.
 * 
 * @author rubensworks
 */
public class DisplayErrorsComponent {

    public void drawForeground(List<LangHelpers.UnlocalizedString> errors, int errorX, int errorY, int mouseX,
        int mouseY, GuiContainerExtended gui, int guiLeft, int guiTop) {
        if (!errors.isEmpty()) {
            if (gui.func_146978_c(
                errorX,
                errorY,
                Images.ERROR.getSheetWidth(),
                Images.ERROR.getSheetHeight(),
                mouseX,
                mouseY)) {
                List<String> lines = Lists.newLinkedList();
                for (LangHelpers.UnlocalizedString error : errors) {
                    lines.addAll(
                        StringHelpers.splitLines(
                            error.localize(),
                            LangHelpers.MAX_TOOLTIP_LINE_LENGTH,
                            EnumChatFormatting.RED.toString()));
                }
                gui.drawTooltip(lines, mouseX - guiLeft, mouseY - guiTop);
            }
        }
    }

    public void drawBackground(List<LangHelpers.UnlocalizedString> errors, int errorX, int errorY, int okX, int okY,
        GuiContainerExtended gui, int guiLeft, int guiTop, boolean okCondition) {
        // Render error symbol
        if (!errors.isEmpty()) {
            Images.ERROR.draw(gui, guiLeft + errorX, guiTop + errorY);
        } else if (okCondition) {
            Images.OK.draw(gui, guiLeft + okX, guiTop + okY);
        }
    }

}
