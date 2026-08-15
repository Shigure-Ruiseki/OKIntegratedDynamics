package ruiseki.integratedtunnels.capability.ingredient;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.capability.IngredientComponentCapabilityAttacherAdapter;
import ruiseki.commoncapabilities.api.ingredient.capability.IngredientComponentCapabilityAttacherManager;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.capability.ingredient.IngredientComponentCapabilities;
import ruiseki.integrateddynamics.capability.network.PositionedAddonsNetworkIngredientsHandlerConfig;
import ruiseki.integratedtunnels.capability.network.FluidNetworkConfig;
import ruiseki.integratedtunnels.capability.network.ItemNetworkConfig;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.modcompat.capabilities.DefaultCapabilityProvider;

/**
 * Value handlers for ingredient components.
 * 
 * @author rubensworks
 */
public class TunnelIngredientComponentCapabilities {

    public static void load() {
        IngredientComponentCapabilityAttacherManager attacherManager = new IngredientComponentCapabilityAttacherManager();

        // Network handler
        ResourceLocation networkHandler = new ResourceLocation(Reference.MOD_ID, "networkHandler");
        attacherManager.addAttacher(
            new IngredientComponentCapabilityAttacherAdapter<ItemStack, Integer>(
                IngredientComponentCapabilities.INGREDIENT_ITEMSTACK_NAME,
                networkHandler) {

                @Override
                public ICapabilityProvider createCapabilityProvider(
                    IngredientComponent<ItemStack, Integer> ingredientComponent) {
                    return new DefaultCapabilityProvider<>(
                        () -> PositionedAddonsNetworkIngredientsHandlerConfig.CAPABILITY,
                        (network) -> network.getCapability(ItemNetworkConfig.CAPABILITY)
                            .getOrNull());
                }
            });
        attacherManager.addAttacher(
            new IngredientComponentCapabilityAttacherAdapter<FluidStack, Integer>(
                IngredientComponentCapabilities.INGREDIENT_FLUIDSTACK_NAME,
                networkHandler) {

                @Override
                public ICapabilityProvider createCapabilityProvider(
                    IngredientComponent<FluidStack, Integer> ingredientComponent) {
                    return new DefaultCapabilityProvider<>(
                        () -> PositionedAddonsNetworkIngredientsHandlerConfig.CAPABILITY,
                        (network) -> network.getCapability(FluidNetworkConfig.CAPABILITY)
                            .getOrNull());
                }
            });
    }

}
