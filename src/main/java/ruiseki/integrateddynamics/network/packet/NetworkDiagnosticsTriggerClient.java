package ruiseki.integrateddynamics.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.core.network.diagnostics.NetworkDiagnosticsPartOverlayRenderer;
import ruiseki.integrateddynamics.core.network.diagnostics.http.DiagnosticsWebServer;
import ruiseki.integrateddynamics.proxy.ClientProxy;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for opening or closing network diagnostics at a client.
 *
 * @author rubensworks
 */
public class NetworkDiagnosticsTriggerClient extends PacketCodec {

    @CodecField
    private boolean start;
    @CodecField
    private int port;

    public NetworkDiagnosticsTriggerClient(boolean start, int port) {
        this.start = start;
        this.port = port;
    }

    public NetworkDiagnosticsTriggerClient() {
        this(true, 0);
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void actionClient(World world, EntityPlayer player) {
        if (start) {
            if (ClientProxy.DIAGNOSTICS_SERVER == null) {
                IntegratedDynamics._instance.getPacketHandler()
                    .sendToServer(NetworkDiagnosticsSubscribePacket.subscribe());
                new Thread(() -> {
                    ClientProxy.DIAGNOSTICS_SERVER = new DiagnosticsWebServer(port);
                    ClientProxy.DIAGNOSTICS_SERVER.initialize();

                    player.addChatComponentMessage(
                        new ChatComponentText("Diagnostics server has been started on ").appendSibling(
                            new ChatComponentText(ClientProxy.DIAGNOSTICS_SERVER.getUrl()).setChatStyle(
                                new ChatStyle().setUnderlined(true)
                                    .setChatClickEvent(
                                        new ClickEvent(
                                            ClickEvent.Action.OPEN_URL,
                                            ClientProxy.DIAGNOSTICS_SERVER.getUrl())))));
                }).start();
            } else {
                player.addChatComponentMessage(
                    new ChatComponentText("Diagnostics server is already running on ").appendSibling(
                        new ChatComponentText(ClientProxy.DIAGNOSTICS_SERVER.getUrl()).setChatStyle(
                            new ChatStyle().setUnderlined(true)
                                .setChatClickEvent(
                                    new ClickEvent(
                                        ClickEvent.Action.OPEN_URL,
                                        ClientProxy.DIAGNOSTICS_SERVER.getUrl())))));
            }
            IntegratedDynamics._instance.getPacketHandler()
                .sendToServer(NetworkDiagnosticsSubscribePacket.subscribe());
        } else {
            if (ClientProxy.DIAGNOSTICS_SERVER != null) {
                IntegratedDynamics._instance.getPacketHandler()
                    .sendToServer(NetworkDiagnosticsSubscribePacket.unsubscribe());
                new Thread(() -> {
                    IntegratedDynamics._instance.getPacketHandler()
                        .sendToServer(NetworkDiagnosticsSubscribePacket.unsubscribe());
                    NetworkDiagnosticsPartOverlayRenderer.getInstance()
                        .clearPositions();
                    ClientProxy.DIAGNOSTICS_SERVER.deinitialize();
                    ClientProxy.DIAGNOSTICS_SERVER = null;
                    player.addChatComponentMessage(new ChatComponentText("Stopped diagnostics server"));
                }).start();
            } else {
                player.addChatComponentMessage(new ChatComponentText("No diagnostics server is running"));
            }
        }
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {

    }

}
