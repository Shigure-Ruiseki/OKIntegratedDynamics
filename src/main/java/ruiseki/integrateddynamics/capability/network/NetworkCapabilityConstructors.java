package ruiseki.integrateddynamics.capability.network;

import net.minecraft.util.ResourceLocation;

import cofh.api.energy.IEnergyStorage;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.api.network.AttachCapabilitiesEventNetwork;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetwork;
import ruiseki.integrateddynamics.core.network.EnergyNetwork;
import ruiseki.integrateddynamics.core.network.PartNetwork;
import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.modcompat.capabilities.DefaultCapabilityProvider;

public class NetworkCapabilityConstructors {

    @SubscribeEvent
    public void onNetworkLoad(AttachCapabilitiesEventNetwork event) {
        INetwork network = event.getNetwork();
        PartNetwork partNetwork = new PartNetwork();
        EnergyNetwork energyNetwork = new EnergyNetwork(IngredientComponent.ENERGY);
        IEnergyStorage energyChannel = energyNetwork
            .getChannelExternal(CapabilityEnergy.ENERGY, IPositionedAddonsNetwork.DEFAULT_CHANNEL);
        event.addCapability(
            new ResourceLocation(Reference.MOD_ID, "partNetwork"),
            new DefaultCapabilityProvider<>(() -> PartNetworkConfig.CAPABILITY, partNetwork));
        event.addCapability(
            new ResourceLocation(Reference.MOD_ID, "energyNetwork"),
            new DefaultCapabilityProvider<>(() -> CapabilityEnergy.ENERGY, energyChannel));

        event.addFullNetworkListener(partNetwork);
        event.addFullNetworkListener(energyNetwork);
    }
}
