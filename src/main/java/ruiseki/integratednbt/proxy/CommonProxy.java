package ruiseki.integratednbt.proxy;

import ruiseki.integratednbt.IntegratedNbt;
import ruiseki.integratednbt.network.packet.NbtExtractorSetExtractionPathPacket;
import ruiseki.integratednbt.network.packet.NbtExtractorSetOutputModePacket;
import ruiseki.integratednbt.network.packet.NbtExtractorUpdateAutoRefreshPacket;
import ruiseki.integratednbt.network.packet.OpenNbtExtractorRemoteGuiPacket;
import ruiseki.integratednbt.network.packet.UpdateClientNbtExtractorPacket;
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
        return IntegratedNbt._instance;
    }

    @Override
    public void registerPacketHandlers(PacketHandler packetHandler) {
        super.registerPacketHandlers(packetHandler);

        // Register packets.
        packetHandler.register(UpdateClientNbtExtractorPacket.class);
        packetHandler.register(OpenNbtExtractorRemoteGuiPacket.class);
        packetHandler.register(NbtExtractorUpdateAutoRefreshPacket.class);
        packetHandler.register(NbtExtractorSetExtractionPathPacket.class);
        packetHandler.register(NbtExtractorSetOutputModePacket.class);
    }

}
