package ruiseki.integrateddynamics.core.evaluate.operator;

import java.util.List;

import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Base class for parse operators.
 * 
 * @author rubensworks/LostOfThought
 */
public class ParseOperator<T2 extends IValueType<V2>, V2 extends IValue> extends OperatorBase {

    private final T2 to;

    public ParseOperator(final T2 to, OperatorBase.IFunction operator) {
        super(
            "parse_" + LangHelpers.localize(to.getUnlocalizedName()),
            "parse_" + to.getUnlocalizedName(),
            constructInputVariables(1, ValueTypes.STRING),
            to,
            operator,
            IConfigRenderPattern.PREFIX_1_LONG);
        this.to = to;
    }

    @Override
    public String getUniqueName() {
        return "operator.operators." + getModId() + ".parse." + to.getUnlocalizedName();
    }

    @Override
    public String getUnlocalizedType() {
        return "parse";
    }

    @Override
    protected String getUnlocalizedPrefix() {
        return "operator.operators." + getModId() + "." + getUnlocalizedType();
    }

    @Override
    public void loadTooltip(List<String> lines, boolean appendOptionalInfo) {
        lines.add(
            LangHelpers.localize(
                "operator.operators.integrateddynamics.parse.tooltip",
                LangHelpers.localize(to.getUnlocalizedName())));
        super.loadTooltip(lines, appendOptionalInfo);
    }

}
