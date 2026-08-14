package ruiseki.integratedterminals.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.core.client.gui.ExtendedGuiHandler;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorage;
import ruiseki.integratedterminals.proxy.guiprovider.GuiProviders;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for telling the server that the storage terminal gui should be opened on a specific tab.
 * 
 * @author rubensworks
 *
 */
public class TerminalStorageIngredientOpenPacket extends PacketCodec {

    @CodecField
    private BlockPos pos;
    @CodecField
    private ForgeDirection side;
    @CodecField
    private String tabName;
    @CodecField
    private int channel;

    public TerminalStorageIngredientOpenPacket() {

    }

    public TerminalStorageIngredientOpenPacket(BlockPos pos, ForgeDirection side, String tabName, int channel) {
        this.pos = pos;
        this.side = side;
        this.tabName = tabName;
        this.channel = channel;
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
        IntegratedTerminals._instance.getGuiHandler()
            .setTemporaryData(
                ExtendedGuiHandler.TERMINAL_STORAGE,
                Pair.of(side, new ContainerTerminalStorage.InitTabData(tabName, channel)));
        player.openGui(
            IntegratedTerminals._instance,
            GuiProviders.ID_GUI_TERMINAL_STORAGE_INIT,
            world,
            pos.getX(),
            pos.getY(),
            pos.getZ());
    }

    public static void send(BlockPos pos, ForgeDirection side, String tabName, int channel) {
        IntegratedTerminals._instance.getGuiHandler()
            .setTemporaryData(
                ExtendedGuiHandler.TERMINAL_STORAGE,
                Pair.of(side, new ContainerTerminalStorage.InitTabData(tabName, channel)));
        IntegratedTerminals._instance.getPacketHandler()
            .sendToServer(new TerminalStorageIngredientOpenPacket(pos, side, tabName, channel));
    }

}
