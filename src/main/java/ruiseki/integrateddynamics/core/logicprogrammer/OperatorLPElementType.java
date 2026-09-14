package ruiseki.integrateddynamics.core.logicprogrammer;

import java.util.List;

import net.minecraft.util.ResourceLocation;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.api.evaluate.operator.IOperator;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElementType;
import ruiseki.integrateddynamics.core.evaluate.operator.Operators;

/**
 * Operator element type.
 *
 * @author rubensworks
 */
public class OperatorLPElementType implements ILogicProgrammerElementType<OperatorLPElement> {

    @Override
    public OperatorLPElement getByName(ResourceLocation name) {
        return new OperatorLPElement(Operators.REGISTRY.getOperator(name));
    }

    @Override
    public ResourceLocation getName(OperatorLPElement element) {
        return element.getOperator()
            .getUniqueName();
    }

    @Override
    public ResourceLocation getUniqueName() {
        return new ResourceLocation(Reference.MOD_ID, "operator");
    }

    @Override
    public List<OperatorLPElement> createElements() {
        List<OperatorLPElement> elements = Lists.newArrayList();
        for (IOperator operator : Operators.REGISTRY.getOperators()) {
            elements.add(new OperatorLPElement(operator));
        }
        return elements;
    }
}
