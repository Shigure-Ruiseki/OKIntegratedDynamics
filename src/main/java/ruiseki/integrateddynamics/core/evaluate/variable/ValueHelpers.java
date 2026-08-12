package ruiseki.integrateddynamics.core.evaluate.variable;

import net.minecraft.nbt.NBTTagCompound;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.api.PartStateException;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.operator.IOperator;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.core.evaluate.operator.CurriedOperator;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;

/**
 * A collection of helpers for variables, values and value types.
 *
 * @author rubensworks
 */
public class ValueHelpers {

    /**
     * Create a new value type array from the given variable array element-wise.
     * If a variable would be null, that corresponding value type would be null as well.
     *
     * @param variables The variables.
     * @return The value types array corresponding element-wise to the variables array.
     */
    public static IValueType[] from(IVariable... variables) {
        IValueType[] valueTypes = new IValueType[variables.length];
        for (int i = 0; i < valueTypes.length; i++) {
            IVariable variable = variables[i];
            valueTypes[i] = variable == null ? null : variable.getType();
        }
        return valueTypes;
    }

    /**
     * Create a new value type array from the given variableFacades array element-wise.
     * If a variableFacade would be null, that corresponding value type would be null as well.
     *
     * @param variableFacades The variables facades.
     * @return The value types array corresponding element-wise to the variables array.
     */
    public static IValueType[] from(IVariableFacade... variableFacades) {
        IValueType[] valueTypes = new IValueType[variableFacades.length];
        for (int i = 0; i < valueTypes.length; i++) {
            IVariableFacade variableFacade = variableFacades[i];
            valueTypes[i] = variableFacade == null ? null : variableFacade.getOutputType();
        }
        return valueTypes;
    }

    /**
     * Create a new unlocalized name array from the given variableFacades array element-wise.
     *
     * @param valueTypes The value types.
     * @return The unlocalized names array corresponding element-wise to the value types array.
     */
    public static LangHelpers.UnlocalizedString[] from(IValueType... valueTypes) {
        LangHelpers.UnlocalizedString[] names = new LangHelpers.UnlocalizedString[valueTypes.length];
        for (int i = 0; i < valueTypes.length; i++) {
            IValueType valueType = valueTypes[i];
            names[i] = new LangHelpers.UnlocalizedString(valueType.getUnlocalizedName());
        }
        return names;
    }

    /**
     * Check if the two given values are equal.
     * If they are both null, they are also considered equal.
     *
     * @param v1 Value one
     * @param v2 Value two
     * @return If they are equal.
     */
    public static boolean areValuesEqual(@Nullable IValue v1, @Nullable IValue v2) {
        return v1 == null && v2 == null || (!(v1 == null || v2 == null) && v1.equals(v2));
    }

    /**
     * Bidirectional checking of correspondence.
     *
     * @param t1 First type.
     * @param t2 Second type.
     * @return If they correspond to each other in some direction.
     */
    public static boolean correspondsTo(IValueType t1, IValueType t2) {
        return t1.correspondsTo(t2) || t2.correspondsTo(t1);
    }

    /**
     * Evaluate an operator for the given values.
     *
     * @param operator The operator.
     * @param values   The values.
     * @return The resulting value.
     * @throws EvaluationException If something went wrong during operator evaluation.
     */
    public static IValue evaluateOperator(IOperator operator, IValue... values) throws EvaluationException {
        IVariable[] variables = new IVariable[values.length];
        for (int i = 0; i < variables.length; i++) {
            IValue value = values[i];
            variables[i] = new Variable<>(value.getType(), value);
        }
        return ValueHelpers.evaluateOperator(operator, variables);
    }

    /**
     * Evaluate an operator for the given variables.
     *
     * @param operator  The operator.
     * @param variables The variables.
     * @return The resulting value.
     * @throws EvaluationException If something went wrong during operator evaluation.
     */
    public static IValue evaluateOperator(IOperator operator, IVariable... variables) throws EvaluationException {
        int requiredLength = operator.getRequiredInputLength();
        if (requiredLength == variables.length) {
            return operator.evaluate(variables);
        } else {
            if (variables.length > requiredLength) { // We have MORE variables as input than the operator accepts
                IVariable[] acceptableVariables = ArrayUtils.subarray(variables, 0, requiredLength);
                IVariable[] remainingVariables = ArrayUtils.subarray(variables, requiredLength, variables.length);

                // Pass all required variables to the operator, and forward all remaining ones to the resulting operator
                IValue result = evaluateOperator(operator, acceptableVariables);

                // Error if the result is NOT an operatorø
                if (result.getType() != ValueTypes.OPERATOR) {
                    throw new EvaluationException(
                        String.format(
                            L10NValues.OPERATOR_ERROR_CURRYINGOVERFLOW,
                            operator.getUnlocalizedName(),
                            requiredLength,
                            variables.length,
                            result.getType()));
                }

                // Pass all remaining variables to the resulting operator
                IOperator nextOperator = ((ValueTypeOperator.ValueOperator) result).getRawValue();
                return evaluateOperator(nextOperator, remainingVariables);

            } else { // Else, the given variables only partially take up the required input
                return ValueTypeOperator.ValueOperator.of(new CurriedOperator(operator, variables));
            }
        }
    }

    /**
     * Serialize the given value to a raw string.
     *
     * @param value The value.
     * @return The NBT tag.
     */
    public static String serializeRaw(IValue value) {
        String raw = value.getType()
            .serialize(value);
        if (raw.length() >= GeneralConfig.maxValueByteSize) {
            return "TOO LONG";
        }
        return raw;
    }

    /**
     * Serialize the given value to NBT.
     *
     * @param value The value.
     * @return The NBT tag.
     */
    public static NBTTagCompound serialize(IValue value) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString(
            "valueType",
            value.getType()
                .getUnlocalizedName());
        tag.setString("value", serializeRaw(value));
        return tag;
    }

    /**
     * Deserialize the given NBT tag to a value.
     *
     * @param tag The NBT tag containing a value.
     * @return The value.
     */
    public static IValue deserialize(NBTTagCompound tag) {
        IValueType valueType = ValueTypes.REGISTRY.getValueType(tag.getString("valueType"));
        if (valueType == null) {
            return null;
        }
        return deserializeRaw(valueType, tag.getString("value"));
    }

    /**
     * Deserialize the given value string to a value.
     *
     * @param valueType   The value type to deserialize for.
     * @param valueString The value string.
     * @param <T>         The type of value.
     * @return The value.
     */
    public static <T extends IValue> T deserializeRaw(IValueType<T> valueType, String valueString) {
        if ("TOO LONG".equals(valueString)) {
            return valueType.getDefault();
        }
        return valueType.deserialize(valueString);
    }

    /**
     * Check if the given result (from the given operator) is a boolean.
     *
     * @param predicate A predicate, used for error logging.
     * @param result    A result from the given predicate
     * @throws EvaluationException If the value was not a boolean.
     */
    public static void validatePredicateOutput(IOperator predicate, IValue result) throws EvaluationException {
        if (!(result instanceof ValueTypeBoolean.ValueBoolean)) {
            LangHelpers.UnlocalizedString error = new LangHelpers.UnlocalizedString(
                L10NValues.OPERATOR_ERROR_WRONGPREDICATE,
                predicate.getLocalizedNameFull(),
                result.getType(),
                ValueTypes.BOOLEAN);
            throw new EvaluationException(error.localize());
        }
    }

    /**
     * Get the human readable value of the given value in a safe way.
     *
     * @param variable A nullable variable.
     * @return A pair of a string and color.
     */
    public static Pair<String, Integer> getSafeReadableValue(@Nullable IVariable variable) {
        String readValue = "";
        int readValueColor = 0;
        if (!NetworkHelpers.shouldWork()) {
            readValue = "SAFE-MODE";
        } else if (variable != null) {
            try {
                IValue value = variable.getValue();
                readValue = value.getType()
                    .toCompactString(value);
                readValueColor = value.getType()
                    .getDisplayColor();
            } catch (EvaluationException | NullPointerException | PartStateException e) {
                readValue = "ERROR";
                readValueColor = Helpers.RGBToInt(255, 0, 0);
            }
        }
        return Pair.of(readValue, readValueColor);
    }
}
