package ruiseki.integrateddynamics.core.ingredient;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.ingredient.IIngredientComponentHandler;
import ruiseki.integrateddynamics.api.ingredient.IIngredientComponentHandlerRegistry;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeFluidStack;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeItemStack;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeInteger;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;

/**
 * Value handlers for ingredient components.
 *
 * @author rubensworks
 */
public class IngredientComponentHandlers {

    public static final IIngredientComponentHandlerRegistry REGISTRY = constructRegistry();

    private static IIngredientComponentHandlerRegistry constructRegistry() {
        if (MinecraftHelpers.isModdedEnvironment()) {
            return IntegratedDynamics._instance.getRegistryManager()
                .getRegistry(IIngredientComponentHandlerRegistry.class);
        } else {
            return IngredientComponentHandlerRegistry.getInstance();
        }
    }

    public static void load() {
        IngredientComponent componentItem = IngredientComponent.REGISTRY
            .get(new ResourceLocation("minecraft:itemstack"));
        IngredientComponent componentFluid = IngredientComponent.REGISTRY
            .get(new ResourceLocation("minecraft:fluidstack"));
        IngredientComponent componentEnergy = IngredientComponent.REGISTRY
            .get(new ResourceLocation("minecraft:energy"));

        if (componentItem != null) {
            REGISTRY.register(
                new IIngredientComponentHandler<ValueObjectTypeItemStack, ValueObjectTypeItemStack.ValueItemStack, ItemStack, Integer>() {

                    @Override
                    public ValueObjectTypeItemStack getValueType() {
                        return ValueTypes.OBJECT_ITEMSTACK;
                    }

                    @Override
                    public IngredientComponent<ItemStack, Integer> getComponent() {
                        return componentItem;
                    }

                    @Override
                    public ValueObjectTypeItemStack.ValueItemStack toValue(ItemStack instance) {
                        return ValueObjectTypeItemStack.ValueItemStack.of(instance);
                    }

                    @Override
                    @Nullable
                    public ItemStack toInstance(ValueObjectTypeItemStack.ValueItemStack value) {
                        return value.getRawValue()
                            .orNull();
                    }
                });
        }

        if (componentFluid != null) {
            REGISTRY.register(
                new IIngredientComponentHandler<ValueObjectTypeFluidStack, ValueObjectTypeFluidStack.ValueFluidStack, FluidStack, Integer>() {

                    @Override
                    public ValueObjectTypeFluidStack getValueType() {
                        return ValueTypes.OBJECT_FLUIDSTACK;
                    }

                    @Override
                    public IngredientComponent<FluidStack, Integer> getComponent() {
                        return componentFluid;
                    }

                    @Override
                    public ValueObjectTypeFluidStack.ValueFluidStack toValue(@Nullable FluidStack instance) {
                        return ValueObjectTypeFluidStack.ValueFluidStack.of(instance);
                    }

                    @Override
                    @Nullable
                    public FluidStack toInstance(ValueObjectTypeFluidStack.ValueFluidStack value) {
                        return value.getRawValue()
                            .orNull();
                    }
                });
        }

        if (componentEnergy != null) {
            REGISTRY.register(
                new IIngredientComponentHandler<ValueTypeInteger, ValueTypeInteger.ValueInteger, Integer, Boolean>() {

                    @Override
                    public ValueTypeInteger getValueType() {
                        return ValueTypes.INTEGER;
                    }

                    @Override
                    public IngredientComponent<Integer, Boolean> getComponent() {
                        return componentEnergy;
                    }

                    @Override
                    public ValueTypeInteger.ValueInteger toValue(@Nullable Integer instance) {
                        return ValueTypeInteger.ValueInteger.of(instance == null ? 0 : instance);
                    }

                    @Nullable
                    @Override
                    public Integer toInstance(ValueTypeInteger.ValueInteger value) {
                        return value.getRawValue();
                    }

                    @Override
                    public String toCompactString(ValueTypeInteger.ValueInteger ingredientValue) {
                        String value = getValueType().toCompactString(ingredientValue);
                        value += " " + LangHelpers.localize(L10NValues.GENERAL_ENERGY_UNIT);
                        return value;
                    }
                });
        }

        IntegratedDynamics.clog(Level.INFO, "Registered IngredientComponentHandlers successfully.");
    }
}
