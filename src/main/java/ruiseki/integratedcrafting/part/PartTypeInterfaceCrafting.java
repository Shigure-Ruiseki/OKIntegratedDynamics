package ruiseki.integratedcrafting.part;

import java.util.Collections;
import java.util.List;

import ruiseki.commoncapabilities.api.capability.recipehandler.IRecipeDefinition;
import ruiseki.integratedcrafting.GeneralConfig;
import ruiseki.integratedcrafting.core.part.PartTypeInterfaceCraftingVariableBase;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeRecipe;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;

/**
 * Interface for auto crafting, with one recipe variable per slot.
 * 
 * @author rubensworks
 */
public class PartTypeInterfaceCrafting
    extends PartTypeInterfaceCraftingVariableBase<PartTypeInterfaceCrafting, PartTypeInterfaceCrafting.State> {

    public static final int INVENTORY_SIZE = 9;

    public PartTypeInterfaceCrafting(String name) {
        super(name);
    }

    @Override
    public int getConsumptionRate(State state) {
        return state.getCraftingJobHandler()
            .getProcessingCraftingJobs()
            .size() * GeneralConfig.interfaceCraftingBaseConsumption;
    }

    @Override
    public IValueType<?> getSlotValueType() {
        return ValueTypes.OBJECT_RECIPE;
    }

    @Override
    protected PartTypeInterfaceCrafting.State constructDefaultState() {
        return new PartTypeInterfaceCrafting.State();
    }

    public static class State extends
        PartTypeInterfaceCraftingVariableBase.State<PartTypeInterfaceCrafting, PartTypeInterfaceCrafting.State> {

        public State() {
            super(INVENTORY_SIZE);
        }

        @Override
        protected PartTypeInterfaceCrafting getPartTypeInstance() {
            return CraftingPartTypes.INTERFACE_CRAFTING;
        }

        @Override
        protected List<IRecipeDefinition> extractRecipes(int slot, IValue value) {
            return ((ValueObjectTypeRecipe.ValueRecipe) value).getRawValue()
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());
        }
    }
}
