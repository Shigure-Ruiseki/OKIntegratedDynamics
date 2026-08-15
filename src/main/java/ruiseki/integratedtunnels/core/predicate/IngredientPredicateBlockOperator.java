package ruiseki.integratedtunnels.core.predicate;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.operator.IOperator;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.write.IPartStateWriter;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeBlock;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.okcore.helper.BlockHelpers;
import ruiseki.okcore.helper.LangHelpers;

/**
 * @author rubensworks
 */
public class IngredientPredicateBlockOperator extends IngredientPredicate<ItemStack, Integer> {

    private final IOperator predicate;
    private final PartTarget partTarget;

    public IngredientPredicateBlockOperator(int amount, boolean exactAmount, IOperator predicate,
        PartTarget partTarget) {
        super(IngredientComponent.ITEMSTACK, false, false, amount, exactAmount);
        this.predicate = predicate;
        this.partTarget = partTarget;
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        ValueObjectTypeBlock.ValueBlock valueBlock = ValueObjectTypeBlock.ValueBlock.of(
            input != null && input.getItem() instanceof ItemBlock ? BlockHelpers.getBlockStateFromItemStack(input)
                : null);
        try {
            IValue result = ValueHelpers.evaluateOperator(predicate, valueBlock);
            ValueHelpers.validatePredicateOutput(predicate, result);
            return ((ValueTypeBoolean.ValueBoolean) result).getRawValue();
        } catch (EvaluationException e) {
            PartHelpers.PartStateHolder<?, ?> partData = PartHelpers.getPart(partTarget.getCenter());
            if (partData != null) {
                IPartStateWriter partState = (IPartStateWriter) partData.getState();
                partState.addError(partState.getActiveAspect(), new LangHelpers.UnlocalizedString(e.getMessage()));
                partState.setDeactivated(true);
            }
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IngredientPredicateBlockOperator)) {
            return false;
        }
        IngredientPredicateBlockOperator that = (IngredientPredicateBlockOperator) obj;
        return super.equals(obj) && this.predicate.equals(that.predicate) && this.partTarget.equals(that.partTarget);
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.predicate.hashCode() ^ this.partTarget.hashCode();
    }
}
