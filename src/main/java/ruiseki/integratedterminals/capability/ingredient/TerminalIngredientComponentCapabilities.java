package ruiseki.integratedterminals.capability.ingredient;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.commoncapabilities.Reference;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.capability.IngredientComponentCapabilityAttacherAdapter;
import ruiseki.commoncapabilities.api.ingredient.capability.IngredientComponentCapabilityAttacherManager;
import ruiseki.integrateddynamics.capability.ingredient.IngredientComponentCapabilities;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.modcompat.capabilities.DefaultCapabilityProvider;

/**
 * @author rubensworks
 */
public class TerminalIngredientComponentCapabilities {

    public static void load() {
        IngredientComponentCapabilityAttacherManager attacherManager = new IngredientComponentCapabilityAttacherManager();

        // Views
        ResourceLocation capabilityIngredientComponentViewHandler = new ResourceLocation(
            Reference.MOD_ID,
            "viewHandler");

        attacherManager.addAttacher(
            new IngredientComponentCapabilityAttacherAdapter<ItemStack, Integer>(
                IngredientComponentCapabilities.INGREDIENT_ITEMSTACK_NAME,
                capabilityIngredientComponentViewHandler) {

                @Override
                public ICapabilityProvider createCapabilityProvider(
                    IngredientComponent<ItemStack, Integer> ingredientComponent) {
                    return new DefaultCapabilityProvider<>(
                        () -> IngredientComponentTerminalStorageHandlerConfig.CAPABILITY,
                        new IngredientComponentTerminalStorageHandlerItemStack(ingredientComponent));
                }
            });

        attacherManager.addAttacher(
            new IngredientComponentCapabilityAttacherAdapter<FluidStack, Integer>(
                IngredientComponentCapabilities.INGREDIENT_FLUIDSTACK_NAME,
                capabilityIngredientComponentViewHandler) {

                @Override
                public ICapabilityProvider createCapabilityProvider(
                    IngredientComponent<FluidStack, Integer> ingredientComponent) {
                    return new DefaultCapabilityProvider<>(
                        () -> IngredientComponentTerminalStorageHandlerConfig.CAPABILITY,
                        new IngredientComponentTerminalStorageHandlerFluidStack(ingredientComponent));
                }
            });

        attacherManager.addAttacher(
            new IngredientComponentCapabilityAttacherAdapter<Long, Boolean>(
                IngredientComponentCapabilities.INGREDIENT_ENERGY_NAME,
                capabilityIngredientComponentViewHandler) {

                @Override
                public ICapabilityProvider createCapabilityProvider(
                    IngredientComponent<Long, Boolean> ingredientComponent) {
                    return new DefaultCapabilityProvider<>(
                        () -> IngredientComponentTerminalStorageHandlerConfig.CAPABILITY,
                        new IngredientComponentTerminalStorageHandlerEnergy(ingredientComponent));
                }
            });
    }
}
