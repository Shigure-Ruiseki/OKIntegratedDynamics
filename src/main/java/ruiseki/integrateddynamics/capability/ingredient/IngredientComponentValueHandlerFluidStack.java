package ruiseki.integrateddynamics.capability.ingredient;

import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeFluidStack;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;

/**
 * @author rubensworks
 */
public class IngredientComponentValueHandlerFluidStack implements
    IIngredientComponentValueHandler<ValueObjectTypeFluidStack, ValueObjectTypeFluidStack.ValueFluidStack, FluidStack, Integer> {

    private final IngredientComponent<FluidStack, Integer> ingredientComponent;

    public IngredientComponentValueHandlerFluidStack(IngredientComponent<FluidStack, Integer> ingredientComponent) {
        this.ingredientComponent = ingredientComponent;
    }

    @Override
    public ValueObjectTypeFluidStack getValueType() {
        return ValueTypes.OBJECT_FLUIDSTACK;
    }

    @Override
    public IngredientComponent<FluidStack, Integer> getComponent() {
        return ingredientComponent;
    }

    @Override
    public ValueObjectTypeFluidStack.ValueFluidStack toValue(@Nullable FluidStack instance) {
        return ValueObjectTypeFluidStack.ValueFluidStack.of(instance);
    }

    @Override
    @Nullable
    public FluidStack toInstance(ValueObjectTypeFluidStack.ValueFluidStack value) {
        return value.getRawValue();
    }

}
