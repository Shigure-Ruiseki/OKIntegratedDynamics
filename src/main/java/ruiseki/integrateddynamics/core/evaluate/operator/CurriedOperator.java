package ruiseki.integrateddynamics.core.evaluate.operator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.util.Constants;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.operator.IOperator;
import ruiseki.integrateddynamics.api.evaluate.operator.IOperatorSerializer;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.evaluate.variable.Variable;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.okcore.helper.LangHelpers;

/**
 * An operator that is partially being applied.
 *
 * @author rubensworks
 */
public class CurriedOperator implements IOperator {

    private final IOperator baseOperator;
    private final IVariable[] appliedVariables;

    public CurriedOperator(IOperator baseOperator, IVariable... appliedVariables) {
        this.baseOperator = baseOperator;
        this.appliedVariables = appliedVariables;
    }

    protected String getAppliedSymbol() {
        String symbol = "";
        for (IVariable appliedVariable : appliedVariables) {
            symbol += appliedVariable.getType()
                .getTypeName() + ";";
        }
        return symbol;
    }

    @Override
    public String getSymbol() {
        StringBuilder sb = new StringBuilder();
        sb.append(baseOperator.getSymbol());
        sb.append(" [");
        sb.append(getAppliedSymbol());
        sb.append("]");
        return sb.toString();
    }

    @Override
    public ResourceLocation getUniqueName() {
        return new ResourceLocation("curried_operator");
    }

    @Override
    public String getUnlocalizedName() {
        return baseOperator.getUnlocalizedName();
    }

    @Override
    public String getUnlocalizedCategoryName() {
        return baseOperator.getUnlocalizedCategoryName();
    }

    @Override
    public String getLocalizedNameFull() {
        return LangHelpers.localize(
            L10NValues.OPERATOR_APPLIED_OPERATORNAME,
            baseOperator.getLocalizedNameFull(),
            getAppliedSymbol());
    }

    @Override
    public void loadTooltip(List<String> lines, boolean appendOptionalInfo) {
        baseOperator.loadTooltip(lines, appendOptionalInfo);
        lines.add(LangHelpers.localize(L10NValues.OPERATOR_APPLIED_TYPE, getAppliedSymbol()));
    }

    @Override
    public IValueType[] getInputTypes() {
        IValueType[] baseInputTypes = baseOperator.getInputTypes();
        return Arrays.copyOfRange(baseInputTypes, appliedVariables.length, baseInputTypes.length);
    }

    @Override
    public IValueType getOutputType() {
        return baseOperator.getOutputType();
    }

    protected IVariable[] deriveFullInputVariables(IVariable[] partialInput) {
        IVariable[] fullInput = new IVariable[Math
            .min(baseOperator.getRequiredInputLength(), partialInput.length + appliedVariables.length)];
        for (int i = 0; i < appliedVariables.length; i++) {
            fullInput[i] = appliedVariables[i];
        }
        System
            .arraycopy(partialInput, 0, fullInput, appliedVariables.length, fullInput.length - appliedVariables.length);
        return fullInput;
    }

    protected IValueType[] deriveFullInputTypes(IValueType[] partialInput) {
        IValueType[] fullInput = new IValueType[Math
            .min(baseOperator.getRequiredInputLength(), partialInput.length + appliedVariables.length)];
        for (int i = 0; i < appliedVariables.length; i++) {
            fullInput[i] = appliedVariables[i].getType();
        }
        System
            .arraycopy(partialInput, 0, fullInput, appliedVariables.length, fullInput.length - appliedVariables.length);
        return fullInput;
    }

    @Override
    public IValueType getConditionalOutputType(IVariable[] input) {
        return baseOperator.getConditionalOutputType(deriveFullInputVariables(input));
    }

    @Override
    public IValue evaluate(IVariable[] input) throws EvaluationException {
        return baseOperator.evaluate(deriveFullInputVariables(input));
    }

    @Override
    public int getRequiredInputLength() {
        return baseOperator.getRequiredInputLength() - appliedVariables.length;
    }

    @Override
    public LangHelpers.UnlocalizedString validateTypes(IValueType[] input) {
        return baseOperator.validateTypes(deriveFullInputTypes(input));
    }

    @Override
    public IConfigRenderPattern getRenderPattern() {
        return IConfigRenderPattern.NONE;
    }

    @Override
    public IOperator materialize() throws EvaluationException {
        IVariable[] variables = new IVariable[appliedVariables.length];
        for (int i = 0; i < appliedVariables.length; i++) {
            IVariable appliedVariable = appliedVariables[i];
            variables[i] = new Variable<>(
                appliedVariable.getType(),
                appliedVariable.getType()
                    .materialize(appliedVariable.getValue()));
        }
        return new CurriedOperator(baseOperator, variables);
    }

    public IOperator getBaseOperator() {
        return baseOperator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurriedOperator)) return false;
        CurriedOperator that = (CurriedOperator) o;
        return Objects.equals(baseOperator, that.baseOperator)
            && Arrays.equals(appliedVariables, that.appliedVariables);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(baseOperator);
        result = 31 * result + Arrays.hashCode(appliedVariables);
        return result;
    }

    public static class Serializer implements IOperatorSerializer<CurriedOperator> {

        @Override
        public boolean canHandle(IOperator operator) {
            return operator instanceof CurriedOperator;
        }

        @Override
        public ResourceLocation getUniqueName() {
            return new ResourceLocation(Reference.MOD_ID, "curry");
        }

        @Override
        public NBTBase serialize(CurriedOperator operator) {
            NBTTagList list = new NBTTagList();
            for (int i = 0; i < operator.appliedVariables.length; i++) {
                IVariable appliedVariable = operator.appliedVariables[i];
                IValue value;
                try {
                    value = appliedVariable.getValue();
                } catch (EvaluationException e) {
                    value = appliedVariable.getType()
                        .getDefault();
                }
                NBTTagCompound valueTag = new NBTTagCompound();
                IValueType valueType = value.getType();
                valueTag.setString(
                    "valueType",
                    valueType.getUniqueName()
                        .toString());
                valueTag.setTag("value", ValueHelpers.serializeRaw(value));
                list.appendTag(valueTag);
            }

            NBTTagCompound tag = new NBTTagCompound();
            tag.setTag("values", list);
            tag.setTag("baseOperator", Operators.REGISTRY.serialize(operator.baseOperator));
            return tag;
        }

        @Override
        public CurriedOperator deserialize(NBTBase valueOperator) throws EvaluationException {
            NBTTagCompound tag;
            try {
                tag = (NBTTagCompound) valueOperator;
            } catch (ClassCastException e) {
                e.printStackTrace();
                throw new EvaluationException(
                    LangHelpers.localize(L10NValues.VALUETYPE_ERROR_DESERIALIZE, valueOperator, e.getMessage()));
            }
            NBTTagList list = tag.getTagList("values", Constants.NBT.TAG_COMPOUND);
            IVariable[] variables = new IVariable[list.tagCount()];
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound valuetag = list.getCompoundTagAt(i);
                IValueType valueType = ValueTypes.REGISTRY
                    .getValueType(new ResourceLocation(valuetag.getString("valueType")));
                IValue value = ValueHelpers.deserializeRaw(valueType, valuetag.getTag("value"));
                variables[i] = new Variable(valueType, value);
            }
            IOperator baseOperator = Objects
                .requireNonNull(Operators.REGISTRY.deserialize(tag.getCompoundTag("baseOperator")));
            return new CurriedOperator(baseOperator, variables);
        }
    }
}
