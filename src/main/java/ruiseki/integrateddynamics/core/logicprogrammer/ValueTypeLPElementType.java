package ruiseki.integrateddynamics.core.logicprogrammer;

import java.util.List;

import com.google.common.collect.ImmutableList;

import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElementType;
import ruiseki.integrateddynamics.api.logicprogrammer.IValueTypeLogicProgrammerElement;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;

/**
 * Value type element type.
 * 
 * @author rubensworks
 */
public class ValueTypeLPElementType implements ILogicProgrammerElementType<IValueTypeLogicProgrammerElement> {

    @Override
    public IValueTypeLogicProgrammerElement getByName(String name) {
        return ValueTypes.REGISTRY.getValueType(name)
            .createLogicProgrammerElement();
    }

    @Override
    public String getName(IValueTypeLogicProgrammerElement element) {
        return element.getValueType()
            .getUnlocalizedName();
    }

    @Override
    public String getName() {
        return "valuetype";
    }

    @Override
    public List<IValueTypeLogicProgrammerElement> createElements() {
        ImmutableList.Builder<IValueTypeLogicProgrammerElement> builder = ImmutableList.builder();
        for (IValueType valueType : ValueTypes.REGISTRY.getValueTypes()) {
            IValueTypeLogicProgrammerElement valueTypeLPElement = valueType.createLogicProgrammerElement();
            if (valueTypeLPElement != null) {
                builder.add(valueTypeLPElement);
            }
        }
        return builder.build();
    }

    /**
     * @return All possible value types in this element type.
     */
    public List<IValueType> getValueTypes() {
        ImmutableList.Builder<IValueType> builder = ImmutableList.builder();
        for (IValueType valueType : ValueTypes.REGISTRY.getValueTypes()) {
            if (!valueType.isCategory() && valueType.createLogicProgrammerElement() != null) {
                builder.add(valueType);
            }
        }
        return builder.build();
    }

}
