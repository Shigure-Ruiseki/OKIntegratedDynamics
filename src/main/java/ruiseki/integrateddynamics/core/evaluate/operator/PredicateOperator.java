package ruiseki.integrateddynamics.core.evaluate.operator;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.operator.IOperator;
import ruiseki.integrateddynamics.api.evaluate.operator.IOperatorSerializer;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.okcore.helper.MinecraftHelpers;

/**
 * An operator that wraps around a predicate.
 *
 * @author rubensworks
 */
public class PredicateOperator<T extends IValueType<V>, V extends IValue> extends OperatorBase {

    private final String unlocalizedType;
    private final T inputType;
    private final List<V> rawValues;

    public PredicateOperator(T inputType, List<V> rawValues) {
        this(rawValues::contains, inputType, rawValues);
    }

    public PredicateOperator(Predicate<V> predicate, T inputType, List<V> rawValues) {
        super(
            "pred",
            "pred",
            new IValueType[] { inputType },
            ValueTypes.BOOLEAN,
            variables -> ValueTypeBoolean.ValueBoolean.of(predicate.test(variables.getValue(0))),
            IConfigRenderPattern.PREFIX_1);
        this.inputType = inputType;
        this.rawValues = rawValues;
        this.unlocalizedType = "predicate";
    }

    @Override
    protected String getUnlocalizedType() {
        return unlocalizedType;
    }

    @Override
    public IOperator materialize() {
        return this;
    }

    public static class Serializer implements IOperatorSerializer<PredicateOperator<IValueType<IValue>, IValue>> {

        @Override
        public boolean canHandle(IOperator operator) {
            return operator instanceof PredicateOperator;
        }

        @Override
        public String getUniqueName() {
            return "predicate";
        }

        @Override
        public String serialize(PredicateOperator<IValueType<IValue>, IValue> operator) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("valueType", operator.inputType.getUnlocalizedName());
            NBTTagList list = new NBTTagList();
            for (IValue rawValue : operator.rawValues) {
                list.appendTag(new NBTTagString(operator.inputType.serialize(rawValue)));
            }
            tag.setTag("values", list);
            return tag.toString();
        }

        @Override
        public PredicateOperator<IValueType<IValue>, IValue> deserialize(String value) throws EvaluationException {
            try {
                NBTTagCompound tag = (NBTTagCompound) JsonToNBT.func_150315_a(value);
                IValueType<IValue> valueType = ValueTypes.REGISTRY.getValueType(tag.getString("valueType"));
                NBTTagList list = tag.getTagList("values", MinecraftHelpers.NBTTag_Types.NBTTagString.ordinal());
                List<IValue> values = Lists.newArrayList();
                for (Object subTag : list.tagList) {
                    values.add(ValueHelpers.deserializeRaw(valueType, ((NBTTagString) subTag).func_150285_a_()));
                }
                return new PredicateOperator<>(valueType, values);
            } catch (NBTException e) {
                throw new EvaluationException(String.format("Something went wrong while deserializing '%s'.", value));
            }
        }
    }
}
