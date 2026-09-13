package ruiseki.integratedtunnels.part.aspect.listproxy;

import net.minecraft.util.ResourceLocation;

import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeFluidStack;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeItemStack;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeListProxyFactories;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeListProxyNBTFactory;
import ruiseki.integratedtunnels.Reference;

/**
 * @author rubensworks
 */
public class TunnelValueTypeListProxyFactories {

    public static ValueTypeListProxyNBTFactory<ValueObjectTypeItemStack, ValueObjectTypeItemStack.ValueItemStack, ValueTypeListProxyPositionedItemNetwork> POSITIONED_ITEM_NETWORK;
    public static ValueTypeListProxyNBTFactory<ValueObjectTypeFluidStack, ValueObjectTypeFluidStack.ValueFluidStack, ValueTypeListProxyPositionedFluidNetwork> POSITIONED_FLUID_NETWORK;

    public static void load() {
        if (POSITIONED_ITEM_NETWORK == null) {
            POSITIONED_ITEM_NETWORK = ValueTypeListProxyFactories.REGISTRY.register(
                new ValueTypeListProxyNBTFactory<>(
                    new ResourceLocation(Reference.MOD_ID, "positioned_item_network"),
                    ValueTypeListProxyPositionedItemNetwork.class));
            POSITIONED_FLUID_NETWORK = ValueTypeListProxyFactories.REGISTRY.register(
                new ValueTypeListProxyNBTFactory<>(
                    new ResourceLocation(Reference.MOD_ID, "positioned_fluid_network"),
                    ValueTypeListProxyPositionedFluidNetwork.class));
        }
    }

}
