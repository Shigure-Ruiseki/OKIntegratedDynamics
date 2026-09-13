package ruiseki.integrateddynamics.core.evaluate.operator;

import net.minecraft.util.ResourceLocation;

import ruiseki.integrateddynamics.api.evaluate.operator.IOperator;
import ruiseki.integrateddynamics.api.evaluate.operator.IOperatorSerializer;

/**
 * The default serializer for operators.
 *
 * @author rubensworks
 */
public class OperatorSerializerDefault implements IOperatorSerializer<IOperator> {

    @Override
    public boolean canHandle(IOperator operator) {
        return true;
    }

    @Override
    public ResourceLocation getUniqueName() {
        return null; // Only the default serializer can have name null
    }

    @Override
    public String serialize(IOperator operator) {
        return operator.getUniqueName()
            .toString();
    }

    @Override
    public IOperator deserialize(String value) {
        return Operators.REGISTRY.getOperator(new ResourceLocation(value));
    }
}
