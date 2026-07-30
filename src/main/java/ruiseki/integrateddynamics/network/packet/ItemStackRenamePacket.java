package ruiseki.integrateddynamics.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.inventory.container.ContainerLabeller;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for renaming a regular itemstack.
 *
 * @author rubensworks
 *
 */
public class ItemStackRenamePacket extends PacketCodec {

    @CodecField
    private String name;

    public ItemStackRenamePacket() {

    }

    public ItemStackRenamePacket(String name) {
        this.name = name;
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
        if (player.openContainer instanceof ContainerLabeller) {
            ((ContainerLabeller) player.openContainer).setItemStackName(this.name);
        }
    }

}
