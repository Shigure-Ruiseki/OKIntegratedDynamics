package ruiseki.integratedcompat.proxy;

import ruiseki.integratedcompat.network.packet.CPacketSetSlot;
import ruiseki.integratedcompat.network.packet.CPacketValueTypeRecipeLPElementSetRecipe;
import ruiseki.integratedcompat.network.packet.TerminalStorageIngredientItemStackCraftingGridSetRecipe;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.network.PacketHandler;
import ruiseki.okcore.proxy.CommonProxyComponent;

/**
 * Proxy for server and client side.
 *
 * @author rubensworks
 *
 */
public class CommonProxy extends CommonProxyComponent {

    @Override
    public ModBase getMod() {
        return IntegratedTerminals._instance;
    }

    @Override
    public void registerPacketHandlers(PacketHandler packetHandler) {
        super.registerPacketHandlers(packetHandler);

        // Register packets.
        packetHandler.register(CPacketSetSlot.class);
        packetHandler.register(CPacketValueTypeRecipeLPElementSetRecipe.class);
        packetHandler.register(TerminalStorageIngredientItemStackCraftingGridSetRecipe.class);

        IntegratedDynamics.clog("Registered packet handler.");
    }
}
