package ruiseki.integrateddynamics.core.logicprogrammer;

import java.util.List;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.evaluate.operator.IOperator;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElementType;
import ruiseki.integrateddynamics.core.evaluate.operator.Operators;

/**
 * Operator element type.
 *
 * @author rubensworks
 */
public class OperatorElementType implements ILogicProgrammerElementType<OperatorElement> {

    @Override
    public OperatorElement getByName(String name) {
        return new OperatorElement(Operators.REGISTRY.getOperator(name));
    }

    @Override
    public String getName(OperatorElement element) {
        return element.getOperator()
            .getUniqueName();
    }

    @Override
    public String getName() {
        return "operator";
    }

    @Override
    public List<OperatorElement> createElements() {
        List<OperatorElement> elements = Lists.newArrayList();
        for (IOperator operator : Operators.REGISTRY.getOperators()) {
            elements.add(new OperatorElement(operator));
        }
        return elements;
    }
}
