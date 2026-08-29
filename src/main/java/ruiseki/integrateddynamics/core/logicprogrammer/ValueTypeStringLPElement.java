package ruiseki.integrateddynamics.core.logicprogrammer;

import net.minecraft.client.gui.Gui;
import net.minecraft.inventory.Container;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.client.gui.subgui.ISubGuiBox;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammerBase;
import ruiseki.integrateddynamics.core.evaluate.variable.gui.GuiElementValueTypeString;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Element for value types that can be read from and written to strings.
 *
 * @author rubensworks
 */
public class ValueTypeStringLPElement extends ValueTypeLPElementBase {

    private GuiElementValueTypeString<GuiLogicProgrammerBase, ContainerLogicProgrammerBase> innerGuiElement;

    public ValueTypeStringLPElement(IValueType valueType) {
        super(valueType);
        this.innerGuiElement = new GuiElementValueTypeString<>(getValueType(), getRenderPattern());
    }

    @Nullable
    @Override
    public <G2 extends Gui, C2 extends Container> GuiElementValueTypeString<G2, C2> createInnerGuiElement() {
        return new GuiElementValueTypeString<>(getValueType(), getRenderPattern());
    }

    @Override
    public GuiElementValueTypeString<GuiLogicProgrammerBase, ContainerLogicProgrammerBase> getInnerGuiElement() {
        return innerGuiElement;
    }

    @Override
    public boolean canWriteElementPre() {
        return getInnerGuiElement().getInputString() != null;
    }

    @Override
    public boolean canCurrentlyReadFromOtherItem() {
        return this.getInnerGuiElement()
            .getInputString() == null || this.getInnerGuiElement()
                .getInputString()
                .equals(getInnerGuiElement().getDefaultInputString());
    }

    @Override
    public void activate() {
        getInnerGuiElement().setInputString(new String(getInnerGuiElement().getDefaultInputString()));
    }

    @Override
    public void deactivate() {
        getInnerGuiElement().setInputString(null);
    }

    @Override
    public LangHelpers.UnlocalizedString validate() {
        return getValueType().canDeserialize(getInnerGuiElement().getInputString());
    }

    @Override
    public IValue getValue() {
        return getInnerGuiElement().getValue();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isFocused(ISubGuiBox subGui) {
        return ((ValueTypeStringLPElementRenderPattern) subGui).getSearchField()
            .isFocused();

    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setFocused(ISubGuiBox subGui, boolean focused) {
        ((ValueTypeStringLPElementRenderPattern) subGui).getSearchField()
            .setFocused(focused);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ISubGuiBox createSubGui(int baseX, int baseY, int maxWidth, int maxHeight, GuiLogicProgrammerBase gui,
        ContainerLogicProgrammerBase container) {
        return new ValueTypeStringLPElementRenderPattern(this, baseX, baseY, maxWidth, maxHeight, gui, container);
    }
}
