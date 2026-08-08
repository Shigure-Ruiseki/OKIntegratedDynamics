package ruiseki.integrateddynamics.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.core.network.diagnostics.GuiNetworkDiagnostics;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for opening network diagnostics at a client.
 * 
 * @author rubensworks
 *
 */
public class NetworkDiagnosticsOpenClient extends PacketCodec {

    public NetworkDiagnosticsOpenClient() {

    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void actionClient(World world, EntityPlayer player) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                GuiNetworkDiagnostics.clearNetworkData();
                IntegratedDynamics._instance.getPacketHandler()
                    .sendToServer(NetworkDiagnosticsSubscribePacket.subscribe());
                GuiNetworkDiagnostics.start();
            }
        }).start();
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {

    }

}
