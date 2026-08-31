package ruiseki.integratedcrafting.capability.network;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integratedcrafting.core.CraftingHelpers;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkCraftingHandler;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;

/**
 * Exposes crafting capabilities to ingredient networks.
 *
 * @author rubensworks
 */
public class NetworkCraftingHandlerCraftingNetwork implements INetworkCraftingHandler {

    @Override
    public <T, M> boolean isCrafting(INetwork network, IPositionedAddonsNetworkIngredients<T, M> ingredientsNetwork,
        int channel, IngredientComponent<T, M> ingredientComponent, T instance, M matchCondition) {
        return CraftingHelpers.getCraftingNetworkChecked(network)
            .getCraftingJobs(channel, ingredientComponent, instance, matchCondition)
            .hasNext();
    }

    @Override
    public <T, M> boolean canCraft(INetwork network, IPositionedAddonsNetworkIngredients<T, M> ingredientsNetwork,
        int channel) {
        return !CraftingHelpers.getCraftingNetworkChecked(network)
            .getRecipeIndex(channel)
            .getRecipes()
            .isEmpty();
    }

    @Override
    public <T, M> boolean craft(INetwork network, IPositionedAddonsNetworkIngredients<T, M> ingredientsNetwork,
        int channel, IngredientComponent<T, M> ingredientComponent, T instance, M matchCondition,
        boolean ignoreExistingJobs) {
        if (!ignoreExistingJobs) {
            // Check if a job was already running
            if (isCrafting(network, ingredientsNetwork, channel, ingredientComponent, instance, matchCondition)) {
                return true;
            }
        }
        return CraftingHelpers.calculateAndScheduleCraftingJob(
            network,
            channel,
            ingredientComponent,
            instance,
            matchCondition,
            true,
            true,
            CraftingHelpers.getGlobalCraftingJobIdentifier(),
            null) != null;
    }
}
