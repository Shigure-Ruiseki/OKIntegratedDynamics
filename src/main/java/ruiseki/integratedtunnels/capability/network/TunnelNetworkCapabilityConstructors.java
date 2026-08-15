package ruiseki.integratedtunnels.capability.network;

import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.api.network.AttachCapabilitiesEventNetwork;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import ruiseki.integratedtunnels.core.network.FluidNetwork;
import ruiseki.integratedtunnels.core.network.ItemNetwork;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.handler.IItemHandler;
import ruiseki.okcore.modcompat.capabilities.DefaultCapabilityProvider;

/**
 * Constructor event for network capabilities.
 *
 * @author rubensworks
 */
public class TunnelNetworkCapabilityConstructors {

    @SubscribeEvent
    public void onNetworkLoad(AttachCapabilitiesEventNetwork event) {
        ItemNetwork itemNetwork = new ItemNetwork(IngredientComponent.ITEMSTACK);
        IItemHandler itemHandler = itemNetwork.getChannelExternal(
            CapabilityItemHandler.ITEM_HANDLER,
            IPositionedAddonsNetworkIngredients.DEFAULT_CHANNEL);
        event.addCapability(
            new ResourceLocation(Reference.MOD_ID, "itemNetwork"),
            new DefaultCapabilityProvider<>(() -> ItemNetworkConfig.CAPABILITY, itemNetwork));
        event.addCapability(
            new ResourceLocation(Reference.MOD_ID, "itemStorageNetwork"),
            new DefaultCapabilityProvider<>(() -> CapabilityItemHandler.ITEM_HANDLER, itemHandler));
        event.addFullNetworkListener(itemNetwork);

        FluidNetwork fluidNetwork = new FluidNetwork(IngredientComponent.FLUIDSTACK);
        IFluidHandler fluidChannel = fluidNetwork.getChannelExternal(
            CapabilityFluidHandler.FLUID_HANDLER,
            IPositionedAddonsNetworkIngredients.DEFAULT_CHANNEL);
        event.addCapability(
            new ResourceLocation(Reference.MOD_ID, "fluidNetwork"),
            new DefaultCapabilityProvider<>(() -> FluidNetworkConfig.CAPABILITY, fluidNetwork));
        event.addCapability(
            new ResourceLocation(Reference.MOD_ID, "fluidStorageNetwork"),
            new DefaultCapabilityProvider<>(() -> CapabilityFluidHandler.FLUID_HANDLER, fluidChannel));
        event.addFullNetworkListener(fluidNetwork);
    }

}
