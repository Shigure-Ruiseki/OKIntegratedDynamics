package ruiseki.integrateddynamics.core.evaluate.operator;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
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
    public NBTBase serialize(IOperator operator) {
        return new NBTTagString(
            operator.getUniqueName()
                .toString());
    }

    @Override
    public IOperator deserialize(NBTBase value) {
        return Operators.REGISTRY.getOperator(new ResourceLocation(((NBTTagString) value).func_150285_a_()));
    }
}
