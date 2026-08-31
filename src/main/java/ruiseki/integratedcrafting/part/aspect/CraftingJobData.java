package ruiseki.integratedcrafting.part.aspect;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integratedcrafting.api.network.ICraftingNetwork;
import ruiseki.integratedcrafting.core.CraftingHelpers;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectProperties;

/**
 * @author rubensworks
 */
public class CraftingJobData<T, M> {

    private final IAspectProperties properties;
    private final IngredientComponent<T, M> ingredientComponent;
    private final T instance;
    private final PartPos center;
    private final INetwork network;
    private final ICraftingNetwork craftingNetwork;

    public CraftingJobData(IAspectProperties properties, IngredientComponent<T, M> ingredientComponent, T instance,
        PartPos center) {
        this.properties = properties;
        this.ingredientComponent = ingredientComponent;
        this.instance = instance;
        this.center = center;
        this.network = CraftingHelpers.getNetworkChecked(center);
        this.craftingNetwork = CraftingHelpers.getCraftingNetworkChecked(network);
    }

    public IAspectProperties getProperties() {
        return properties;
    }

    public IngredientComponent<T, M> getIngredientComponent() {
        return ingredientComponent;
    }

    public T getInstance() {
        return instance;
    }

    public PartPos getCenter() {
        return center;
    }

    public INetwork getNetwork() {
        return network;
    }

    public ICraftingNetwork getCraftingNetwork() {
        return craftingNetwork;
    }
}
