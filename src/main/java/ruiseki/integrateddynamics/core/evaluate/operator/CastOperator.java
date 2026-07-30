package ruiseki.integrateddynamics.core.evaluate.operator;

import java.util.List;

import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueCastRegistry;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Base class for cast operators.
 * 
 * @author rubensworks
 */
public class CastOperator<T1 extends IValueType<V1>, T2 extends IValueType<V2>, V1 extends IValue, V2 extends IValue>
    extends OperatorBase {

    private final T1 from;
    private final T2 to;
    private final IValueCastRegistry.IMapping<T1, T2, V1, V2> mapping;

    public CastOperator(final T1 from, final T2 to, final IValueCastRegistry.IMapping<T1, T2, V1, V2> mapping) {
        super(
            "()",
            from.getUnlocalizedName() + "$" + to.getUnlocalizedName(),
            constructInputVariables(1, from),
            to,
            new IFunction() {

                @Override
                public IValue evaluate(SafeVariablesGetter variables) throws EvaluationException {
                    IValue value = variables.getValue(0);
                    if (value.getType() != from) {
                        throw new EvaluationException(
                            String.format(
                                "The value of type %s does not correspond to the " + "expected type %s to cast to %s",
                                value.getType(),
                                from,
                                to));
                    }
                    return mapping.cast((V1) value);
                }
            },
            IConfigRenderPattern.PREFIX_1);
        this.from = from;
        this.to = to;
        this.mapping = mapping;
    }

    @Override
    public String getUniqueName() {
        return "operator.operators." + getModId() + ".cast" + from.getUnlocalizedName() + "$" + to.getUnlocalizedName();
    }

    @Override
    public String getUnlocalizedType() {
        return "cast";
    }

    @Override
    protected String getUnlocalizedPrefix() {
        return "operator.operators." + getModId() + "." + getUnlocalizedType();
    }

    @Override
    public void loadTooltip(List<String> lines, boolean appendOptionalInfo) {
        lines.add(
            LangHelpers.localize(
                "operator.operators.integrateddynamics.cast.tooltip",
                LangHelpers.localize(from.getUnlocalizedName()),
                LangHelpers.localize(to.getUnlocalizedName())));
        super.loadTooltip(lines, appendOptionalInfo);
    }

}
