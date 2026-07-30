package ruiseki.integrateddynamics.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammer;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for sending a button packet to trigger variable labeling.
 * 
 * @author rubensworks
 *
 */
public class LogicProgrammerLabelPacket extends PacketCodec {

    @CodecField
    private String label;

    public LogicProgrammerLabelPacket() {

    }

    public LogicProgrammerLabelPacket(String label) {
        this.label = label;
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
        if (player.openContainer instanceof ContainerLogicProgrammer) {
            ((ContainerLogicProgrammer) player.openContainer).onLabelPacket(label);
        }
    }

}
