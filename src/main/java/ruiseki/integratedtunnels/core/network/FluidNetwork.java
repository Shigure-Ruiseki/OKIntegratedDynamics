package ruiseki.integratedtunnels.core.network;

import net.minecraftforge.fluids.FluidStack;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.core.network.PositionedAddonsNetworkIngredients;
import ruiseki.integratedtunnels.GeneralConfig;
import ruiseki.integratedtunnels.api.network.IFluidNetwork;

/**
 * A network that can hold fluids.
 * 
 * @author rubensworks
 */
public class FluidNetwork extends PositionedAddonsNetworkIngredients<FluidStack, Integer> implements IFluidNetwork {

    public FluidNetwork(IngredientComponent<FluidStack, Integer> component) {
        super(component);
    }

    @Override
    public long getRateLimit() {
        return GeneralConfig.fluidRateLimit;
    }
}
