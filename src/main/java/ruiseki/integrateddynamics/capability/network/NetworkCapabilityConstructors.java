package ruiseki.integrateddynamics.capability.network;

import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.api.network.AttachCapabilitiesEventNetwork;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.core.network.EnergyNetwork;
import ruiseki.integrateddynamics.core.network.PartNetwork;
import ruiseki.okcore.modcompat.capabilities.DefaultCapabilityProvider;

public class NetworkCapabilityConstructors {

    @SubscribeEvent
    public void onNetworkLoad(AttachCapabilitiesEventNetwork event) {
        INetwork network = event.getNetwork();
        PartNetwork partNetwork = new PartNetwork();
        EnergyNetwork energyNetwork = new EnergyNetwork();
        event.addCapability(
            new ResourceLocation(Reference.MOD_ID, "partNetwork"),
            new DefaultCapabilityProvider<>(() -> PartNetworkConfig.CAPABILITY, partNetwork));
        event.addCapability(
            new ResourceLocation(Reference.MOD_ID, "energyNetwork"),
            new DefaultCapabilityProvider<>(() -> EnergyNetworkConfig.CAPABILITY, energyNetwork));
        event.addFullNetworkListener(partNetwork);
        event.addFullNetworkListener(energyNetwork);
    }
}
