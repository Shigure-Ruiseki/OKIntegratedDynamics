package ruiseki.integratedcrafting.capability.network;

import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.integratedcrafting.core.network.CraftingNetwork;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.api.network.AttachCapabilitiesEventNetwork;
import ruiseki.okcore.modcompat.capabilities.DefaultCapabilityProvider;

/**
 * Constructor event for network capabilities.
 * 
 * @author rubensworks
 */
public class CraftingNetworkCapabilityConstructors {

    @SubscribeEvent
    public void onNetworkLoad(AttachCapabilitiesEventNetwork event) {
        event.addCapability(
            new ResourceLocation(Reference.MOD_ID, "craftingNetwork"),
            new DefaultCapabilityProvider<>(() -> CraftingNetworkConfig.CAPABILITY, new CraftingNetwork()));
    }

}
