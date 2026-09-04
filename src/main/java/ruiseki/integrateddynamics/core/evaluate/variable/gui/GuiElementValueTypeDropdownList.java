package ruiseki.integrateddynamics.core.evaluate.variable.gui;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.client.gui.Gui;
import net.minecraft.inventory.Container;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Data;
import ruiseki.integrateddynamics.api.client.gui.subgui.IGuiInputElementValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import ruiseki.integrateddynamics.core.client.gui.IDropdownEntry;
import ruiseki.integrateddynamics.core.client.gui.IDropdownEntryListener;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.logicprogrammer.RenderPattern;
import ruiseki.okcore.helper.LangHelpers;

/**
 * GUI element for value type that are displayed using a dropdown list.
 *
 * @author rubensworks
 */
@Data
public class GuiElementValueTypeDropdownList<T, G extends Gui, C extends Container>
    implements IGuiInputElementValueType<RenderPattern, G, C>, IDropdownEntryListener<T> {

    private final IValueType valueType;
    private Predicate<IValue> validator;
    private final IConfigRenderPattern renderPattern;
    private String inputString;
    private Set<IDropdownEntry<T>> dropdownPossibilities = Collections.emptySet();
    private IDropdownEntryListener<T> dropdownEntryListener = null;

    public GuiElementValueTypeDropdownList(IValueType valueType, IConfigRenderPattern renderPattern) {
        this.valueType = valueType;
        this.validator = Predicates.alwaysTrue();
        this.renderPattern = renderPattern;
    }

    @Override
    public void setValidator(Predicate<IValue> validator) {
        this.validator = validator;
    }

    @Override
    public void setValue(IValue value) {
        throw new UnsupportedOperationException("This method has not been implemented yet");
    }

    @Override
    public void setValueInGui(RenderPattern subGui, boolean sendToServer) {
        throw new UnsupportedOperationException("This method has not been implemented yet");
    }

    @Override
    public IValue getValue() {
        throw new UnsupportedOperationException("This method has not been implemented yet");
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
        this.inputString = "";
    }

    @Override
    public void deactivate() {
        this.inputString = null;
    }

    @Override
    public LangHelpers.UnlocalizedString validate() {
        IValue value = ValueHelpers.deserializeRaw(getValueType(), inputString);
        if (!this.validator.apply(value)) {
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
    public void onSetDropdownPossiblity(IDropdownEntry dropdownEntry) {
        if (dropdownEntryListener != null) {
            dropdownEntryListener.onSetDropdownPossiblity(dropdownEntry);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiElementValueTypeDropdownListRenderPattern<T, ?, G, C> createSubGui(int baseX, int baseY, int maxWidth,
        int maxHeight, G gui, C container) {
        return new GuiElementValueTypeDropdownListRenderPattern<>(
            this,
            baseX,
            baseY,
            maxWidth,
            maxHeight,
            gui,
            container);
    }
}
