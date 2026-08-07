package ruiseki.integrateddynamics.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.core.network.diagnostics.GuiNetworkDiagnostics;
import ruiseki.integrateddynamics.core.network.diagnostics.RawNetworkData;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for subscribing a network update to a player.
 * 
 * @author rubensworks
 *
 */
public class NetworkDiagnosticsNetworkPacket extends PacketCodec {

    @CodecField
    private NBTTagCompound networkData;

    public NetworkDiagnosticsNetworkPacket() {

    }

    public NetworkDiagnosticsNetworkPacket(NBTTagCompound networkData) {
        this.networkData = networkData;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void actionClient(World world, EntityPlayer player) {
        RawNetworkData networkData = RawNetworkData.fromNbt(this.networkData);
        GuiNetworkDiagnostics.setNetworkData(networkData.getId(), networkData.isKilled() ? null : networkData);
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {

    }

}
