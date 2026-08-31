package ruiseki.integrateddynamics.core.evaluate.variable.gui;

import java.util.List;

import net.minecraft.client.gui.Gui;
import net.minecraft.inventory.Container;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Data;
import ruiseki.integrateddynamics.api.client.gui.subgui.IGuiInputElementValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.okcore.helper.LangHelpers;

/**
 * GUI element for boolean value types that can be read from and written to checkboxes.
 *
 * @author rubensworks
 */
@Data
public class GuiElementValueTypeBoolean<G extends Gui, C extends Container>
    implements IGuiInputElementValueType<GuiElementValueTypeBooleanRenderPattern, G, C> {

    private final ValueTypeBoolean valueType;
    private Predicate<IValue> validator;
    private final IConfigRenderPattern renderPattern;
    private final boolean defaultInputBoolean;
    private boolean inputBoolean;

    public GuiElementValueTypeBoolean(ValueTypeBoolean valueType, IConfigRenderPattern renderPattern) {
        this.valueType = valueType;
        this.validator = Predicates.alwaysTrue();
        this.renderPattern = renderPattern;
        defaultInputBoolean = valueType.getDefault()
            .getRawValue();
    }

    public boolean getDefaultInputBoolean() {
        return this.inputBoolean;
    }

    public boolean getInputBoolean() {
        return this.inputBoolean;
    }

    @Override
    public void setValue(IValue value) {
        setInputBoolean(((ValueTypeBoolean.ValueBoolean) value).getRawValue());
    }

    @Override
    public void setValueInGui(GuiElementValueTypeBooleanRenderPattern subGui, boolean sendToServer) {
        if (subGui != null) {
            subGui.getCheckbox()
                .setChecked(inputBoolean);
            if (sendToServer) {
                subGui.sendValueToServer();
            }
        }
    }

    public void setInputBoolean(boolean inputBoolean) {
        this.inputBoolean = inputBoolean;
    }

    @Override
    public void setValidator(Predicate<IValue> validator) {
        this.validator = validator;
    }

    @Override
    public IValue getValue() {
        return ValueTypeBoolean.ValueBoolean.of(getInputBoolean());
    }

    @Override
    public String getLocalizedNameFull() {
        return LangHelpers.localize(getValueType().getUnlocalizedName());
    }

    @Override
    public void loadTooltip(List<String> lines) {
        getValueType().loadTooltip(lines, true, null);
    }

    @Override
    public IConfigRenderPattern getRenderPattern() {
        return renderPattern;
    }

    @Override
    public void activate() {
        this.inputBoolean = defaultInputBoolean;
    }

    @Override
    public void deactivate() {
        // Do nothing
    }

    @Override
    public LangHelpers.UnlocalizedString validate() {
        if (!this.validator.apply(ValueTypeBoolean.ValueBoolean.of(inputBoolean))) {
            return new LangHelpers.UnlocalizedString(L10NValues.VALUE_ERROR);
        }
        return null;
    }

    @Override
    public int getColor() {
        return getValueType().getDisplayColor();
    }

    @Override
    public String getSymbol() {
        return LangHelpers.localize(getValueType().getUnlocalizedName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiElementValueTypeBooleanRenderPattern<?, G, C> createSubGui(int baseX, int baseY, int maxWidth,
        int maxHeight, G gui, C container) {
        return new GuiElementValueTypeBooleanRenderPattern<>(this, baseX, baseY, maxWidth, maxHeight, gui, container);
    }

}
