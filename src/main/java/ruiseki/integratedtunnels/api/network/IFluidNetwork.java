package ruiseki.integratedtunnels.api.network;

import net.minecraftforge.fluids.FluidStack;

import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;

/**
 * A network capability that holds fluids.
 * 
 * @author rubensworks
 */
public interface IFluidNetwork extends IPositionedAddonsNetworkIngredients<FluidStack, Integer> {

}
