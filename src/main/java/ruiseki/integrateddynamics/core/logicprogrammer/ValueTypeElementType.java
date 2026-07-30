package ruiseki.integrateddynamics.core.logicprogrammer;

import java.util.List;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElementType;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;

/**
 * Value type element type.
 * 
 * @author rubensworks
 */
public class ValueTypeElementType implements ILogicProgrammerElementType<ValueTypeElement> {

    @Override
    public ValueTypeElement getByName(String name) {
        return getByValueType(ValueTypes.REGISTRY.getValueType(name));
    }

    /**
     * Get the element by value type.
     * 
     * @param valueType The value type.
     * @return The corresponding element.
     */
    public ValueTypeElement getByValueType(IValueType valueType) {
        return new ValueTypeElement(valueType);
    }

    @Override
    public String getName(ValueTypeElement element) {
        return element.getInnerGuiElement()
            .getValueType()
            .getUnlocalizedName();
    }

    @Override
    public String getName() {
        return "valuetype";
    }

    @Override
    public List<ValueTypeElement> createElements() {
        List<ValueTypeElement> elements = Lists.newLinkedList();
        for (IValueType valueType : ValueTypes.REGISTRY.getValueTypes()) {
            if (!valueType.isCategory() && !valueType.isObject()) {
                elements.add(new ValueTypeElement(valueType));
            }
        }
        return elements;
    }

    /**
     * @return All possible value types in this element type.
     */
    public List<IValueType> getValueTypes() {
        List<IValueType> elements = Lists.newLinkedList();
        for (IValueType valueType : ValueTypes.REGISTRY.getValueTypes()) {
            if (!valueType.isCategory() && !valueType.isObject()) {
                elements.add(valueType);
            }
        }
        return elements;
    }

}
