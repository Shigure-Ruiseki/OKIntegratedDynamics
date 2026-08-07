package ruiseki.integrateddynamics.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for subscribing a network update to a player.
 * 
 * @author rubensworks
 *
 */
public class PlayerTeleportPacket extends PacketCodec {

    @CodecField
    private int dimension;
    @CodecField
    private double x;
    @CodecField
    private double y;
    @CodecField
    private double z;
    @CodecField
    private float yaw;
    @CodecField
    private float pitch;

    public PlayerTeleportPacket() {

    }

    public PlayerTeleportPacket(int dimension, double x, double y, double z, float yaw, float pitch) {
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void actionClient(World world, EntityPlayer player) {

    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        if (this.dimension != player.dimension) {
            player.mcServer.getConfigurationManager()
                .transferPlayerToDimension(player, this.dimension);
        }
        player.playerNetServerHandler.setPlayerLocation(x + 0.5D, y + 0.5D, z + 0.5D, yaw, pitch);
    }

}
