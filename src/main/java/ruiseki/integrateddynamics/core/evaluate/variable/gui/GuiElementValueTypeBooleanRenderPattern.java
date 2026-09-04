package ruiseki.integrateddynamics.core.evaluate.variable.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.inventory.Container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Getter;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.client.gui.subgui.ISubGuiBox;
import ruiseki.integrateddynamics.core.logicprogrammer.IRenderPatternValueTypeTooltip;
import ruiseki.integrateddynamics.core.logicprogrammer.RenderPattern;
import ruiseki.integrateddynamics.network.packet.LogicProgrammerValueTypeBooleanValueChangedPacket;
import ruiseki.okcore.client.gui.component.button.GuiButtonCheckbox;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.persist.IDirtyMarkListener;

/**
 * @author rubensworks
 */
@SideOnly(Side.CLIENT)
public class GuiElementValueTypeBooleanRenderPattern<S extends ISubGuiBox, G extends Gui, C extends Container>
    extends RenderPattern<GuiElementValueTypeBoolean<G, C>, G, C> implements IRenderPatternValueTypeTooltip {

    @Getter
    protected final GuiElementValueTypeBoolean<G, C> element;
    private boolean renderTooltip = true;
    @Getter
    private GuiButtonCheckbox checkbox = null;

    public GuiElementValueTypeBooleanRenderPattern(GuiElementValueTypeBoolean<G, C> element, int baseX, int baseY,
        int maxWidth, int maxHeight, G gui, C container) {
        super(element, baseX, baseY, maxWidth, maxHeight, gui, container);
        this.element = element;
    }

    @Override
    public void initGui(int guiLeft, int guiTop) {
        super.initGui(guiLeft, guiTop);

        this.checkbox = new GuiButtonCheckbox(
            0,
            guiLeft + getX(),
            guiTop + getY(),
            getElement().getRenderPattern()
                .getWidth(),
            getElement().getRenderPattern()
                .getHeight(),
            LangHelpers.localize(
                this.getElement()
                    .getValueType()
                    .getUnlocalizedName()),
            false) {

            @Override
            public void setChecked(boolean checked) {
                super.setChecked(checked);
                onChecked(checked);
            }
        };
        boolean value = element.getInputBoolean();
        this.checkbox.setChecked(value);
        this.buttonList.add(checkbox);
    }

    @Override
    public boolean isRenderTooltip() {
        return this.renderTooltip;
    }

    @Override
    public void setRenderTooltip(boolean renderTooltip) {
        this.renderTooltip = renderTooltip;
    }

    protected void onChecked(boolean checked) {
        if (container instanceof IDirtyMarkListener) {
            ((IDirtyMarkListener) container).onDirty();
        }
        this.getElement()
            .setInputBoolean(checked);
        sendValueToServer();
    }

    @Override
    public void sendValueToServer() {
        super.sendValueToServer();
        IntegratedDynamics._instance.getPacketHandler()
            .sendToServer(
                new LogicProgrammerValueTypeBooleanValueChangedPacket(
                    this.getElement()
                        .getInputBoolean()));
    }
}
