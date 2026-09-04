package ruiseki.integrateddynamics.capability.ingredient;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.commoncapabilities.Reference;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.capability.IngredientComponentCapabilityAttacherAdapter;
import ruiseki.commoncapabilities.api.ingredient.capability.IngredientComponentCapabilityAttacherManager;
import ruiseki.integrateddynamics.capability.network.EnergyNetworkConfig;
import ruiseki.integrateddynamics.capability.network.PositionedAddonsNetworkIngredientsHandlerConfig;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.modcompat.capabilities.DefaultCapabilityProvider;

/**
 * Value handlers for ingredient components.
 *
 * @author rubensworks
 */
public class IngredientComponentCapabilities {

    public static final ResourceLocation INGREDIENT_ITEMSTACK_NAME = new ResourceLocation("minecraft", "itemstack");
    public static final ResourceLocation INGREDIENT_FLUIDSTACK_NAME = new ResourceLocation("minecraft", "fluidstack");
    public static final ResourceLocation INGREDIENT_ENERGY_NAME = new ResourceLocation("minecraft", "energy");

    public static void load() {
        IngredientComponentCapabilityAttacherManager attacherManager = new IngredientComponentCapabilityAttacherManager();

        // Value handlers
        ResourceLocation capabilityIngredientComponentValueHandler = new ResourceLocation(
            Reference.MOD_ID,
            "ingredientComponentValueHandler");
        attacherManager.addAttacher(
            new IngredientComponentCapabilityAttacherAdapter<ItemStack, Integer>(
                INGREDIENT_ITEMSTACK_NAME,
                capabilityIngredientComponentValueHandler) {

                @Override
                public ICapabilityProvider createCapabilityProvider(
                    IngredientComponent<ItemStack, Integer> ingredientComponent) {
                    return new DefaultCapabilityProvider<>(
                        () -> IngredientComponentValueHandlerConfig.CAPABILITY,
                        new IngredientComponentValueHandlerItemStack(ingredientComponent));
                }
            });
        attacherManager.addAttacher(
            new IngredientComponentCapabilityAttacherAdapter<FluidStack, Integer>(
                INGREDIENT_FLUIDSTACK_NAME,
                capabilityIngredientComponentValueHandler) {

                @Override
                public ICapabilityProvider createCapabilityProvider(
                    IngredientComponent<FluidStack, Integer> ingredientComponent) {
                    return new DefaultCapabilityProvider<>(
                        () -> IngredientComponentValueHandlerConfig.CAPABILITY,
                        new IngredientComponentValueHandlerFluidStack(ingredientComponent));
                }
            });
        attacherManager.addAttacher(
            new IngredientComponentCapabilityAttacherAdapter<Long, Boolean>(
                INGREDIENT_ENERGY_NAME,
                capabilityIngredientComponentValueHandler) {

                @Override
                public ICapabilityProvider createCapabilityProvider(
                    IngredientComponent<Long, Boolean> ingredientComponent) {
                    return new DefaultCapabilityProvider<>(
                        () -> IngredientComponentValueHandlerConfig.CAPABILITY,
                        new IngredientComponentValueHandlerEnergy(ingredientComponent));
                }
            });

        // Network handler
        ResourceLocation networkHandler = new ResourceLocation(Reference.MOD_ID, "network_handler");
        attacherManager.addAttacher(
            new IngredientComponentCapabilityAttacherAdapter<Integer, Boolean>(INGREDIENT_ENERGY_NAME, networkHandler) {

                @Override
                public ICapabilityProvider createCapabilityProvider(
                    IngredientComponent<Integer, Boolean> ingredientComponent) {
                    return new DefaultCapabilityProvider<>(
                        () -> PositionedAddonsNetworkIngredientsHandlerConfig.CAPABILITY,
                        (network) -> network.getCapability(EnergyNetworkConfig.CAPABILITY)
                            .cast());
                }
            });
    }

}
