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
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import ruiseki.integrateddynamics.core.evaluate.variable.gui.GuiElementValueTypeBoolean;
import ruiseki.integrateddynamics.core.evaluate.variable.gui.GuiElementValueTypeBooleanRenderPattern;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Element for the boolean value type that is controlled via a checkbox.
 *
 * @author rubensworks
 */
public class ValueTypeBooleanLPElement extends ValueTypeLPElementBase {

    private GuiElementValueTypeBoolean<GuiLogicProgrammerBase, ContainerLogicProgrammerBase> innerGuiElement;

    public ValueTypeBooleanLPElement(IValueType valueType) {
        super(valueType);
        this.innerGuiElement = createInnerGuiElement();
    }

    @Nullable
    @Override
    public <G2 extends Gui, C2 extends Container> GuiElementValueTypeBoolean<G2, C2> createInnerGuiElement() {
        return new GuiElementValueTypeBoolean<>((ValueTypeBoolean) getValueType(), getRenderPattern());
    }

    @Override
    public GuiElementValueTypeBoolean<GuiLogicProgrammerBase, ContainerLogicProgrammerBase> getInnerGuiElement() {
        return innerGuiElement;
    }

    @Override
    public void activate() {
        getInnerGuiElement().activate();
    }

    @Override
    public void deactivate() {
        getInnerGuiElement().deactivate();
    }

    @Override
    public LangHelpers.UnlocalizedString validate() {
        return getInnerGuiElement().validate();
    }

    @Override
    public IValue getValue() {
        return getInnerGuiElement().getValue();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ISubGuiBox createSubGui(int baseX, int baseY, int maxWidth, int maxHeight, GuiLogicProgrammerBase gui,
        ContainerLogicProgrammerBase container) {
        return new GuiElementValueTypeBooleanRenderPattern<>(
            this.getInnerGuiElement(),
            baseX,
            baseY,
            maxWidth,
            maxHeight,
            gui,
            container);
    }
}
