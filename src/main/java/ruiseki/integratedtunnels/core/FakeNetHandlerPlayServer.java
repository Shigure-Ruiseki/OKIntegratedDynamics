package ruiseki.integratedtunnels.core;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * A fake {@link NetHandlerPlayServer} for 1.7.10.
 *
 * @author rubensworks
 */
public class FakeNetHandlerPlayServer extends NetHandlerPlayServer {

    public FakeNetHandlerPlayServer(MinecraftServer server, EntityPlayerMP player) {
        super(server, createFakeNetworkManager(), player);
    }

    private static NetworkManager createFakeNetworkManager() {
        return new NetworkManager(false) {

            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {}

            @Override
            public void channelInactive(ChannelHandlerContext ctx) {}

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {}

            @Override
            public void setNetHandler(INetHandler handler) {}

            @Override
            public void scheduleOutboundPacket(Packet inPacket, GenericFutureListener... futureListeners) {}

            @Override
            public boolean isChannelOpen() {
                return false;
            }

            @Override
            public INetHandler getNetHandler() {
                return null;
            }

            @Override
            public IChatComponent getExitMessage() {
                return null;
            }

            @Override
            public void disableAutoRead() {}
        };
    }

    @Override
    public void onNetworkTick() {}

    @Override
    public void kickPlayerFromServer(String reason) {}

    @Override
    public void processInput(C0CPacketInput packet) {}

    @Override
    public void processPlayer(C03PacketPlayer packet) {}

    @Override
    public void setPlayerLocation(double x, double y, double z, float yaw, float pitch) {}

    @Override
    public void processPlayerDigging(C07PacketPlayerDigging packet) {}

    @Override
    public void processPlayerBlockPlacement(C08PacketPlayerBlockPlacement packet) {}

    @Override
    public void onDisconnect(IChatComponent reason) {}

    @Override
    public void sendPacket(Packet packet) {}

    @Override
    public void processHeldItemChange(C09PacketHeldItemChange packet) {}

    @Override
    public void processChatMessage(C01PacketChatMessage packet) {}

    @Override
    public void processAnimation(C0APacketAnimation packet) {}

    @Override
    public void processEntityAction(C0BPacketEntityAction packet) {}

    @Override
    public void processUseEntity(C02PacketUseEntity packet) {}

    @Override
    public void processClientStatus(C16PacketClientStatus packet) {}

    @Override
    public void processCloseWindow(C0DPacketCloseWindow packet) {}

    @Override
    public void processClickWindow(C0EPacketClickWindow packet) {}

    @Override
    public void processEnchantItem(C11PacketEnchantItem packet) {}

    @Override
    public void processCreativeInventoryAction(C10PacketCreativeInventoryAction packet) {}

    @Override
    public void processConfirmTransaction(C0FPacketConfirmTransaction packet) {}

    @Override
    public void processUpdateSign(C12PacketUpdateSign packet) {}

    @Override
    public void processKeepAlive(C00PacketKeepAlive packet) {}

    @Override
    public void processPlayerAbilities(C13PacketPlayerAbilities packet) {}

    @Override
    public void processTabComplete(C14PacketTabComplete packet) {}

    @Override
    public void processClientSettings(C15PacketClientSettings packet) {}

    @Override
    public void processVanilla250Packet(C17PacketCustomPayload packetIn) {}
}
