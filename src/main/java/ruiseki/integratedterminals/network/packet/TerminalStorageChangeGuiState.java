package ruiseki.integratedterminals.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;
import ruiseki.integratedterminals.inventory.container.TerminalStorageState;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for updating the gui state server-side.
 * 
 * @author rubensworks
 *
 */
public class TerminalStorageChangeGuiState extends PacketCodec {

    @CodecField
    private NBTTagCompound state;

    public TerminalStorageChangeGuiState() {

    }

    public TerminalStorageChangeGuiState(TerminalStorageState state) {
        this.state = (NBTTagCompound) state.getTag()
            .copy();
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
        if (player.openContainer instanceof ContainerTerminalStorageBase container) {
            container.getGuiState()
                .setTag(this.state);
        }
    }

}
