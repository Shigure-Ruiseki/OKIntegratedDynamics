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
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;
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
public class TerminalStorageIngredientPartOpenPacket extends PacketCodec {

    @CodecField
    private BlockPos pos;
    @CodecField
    private ForgeDirection side;
    @CodecField
    private String tabName;
    @CodecField
    private int channel;

    public TerminalStorageIngredientPartOpenPacket() {

    }

    public TerminalStorageIngredientPartOpenPacket(BlockPos pos, ForgeDirection side, String tabName, int channel) {
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
        IntegratedTerminals._instance.getGuiHandler()
            .setTemporaryData(
                ExtendedGuiHandler.TERMINAL_STORAGE,
                Pair.of(side, new ContainerTerminalStorageBase.InitTabData(tabName, channel)));
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        openServer(world, pos, side, player, tabName, channel);
    }

    public static void openServer(World world, BlockPos pos, ForgeDirection side, EntityPlayerMP player, String tabName,
        int channel) {
        IntegratedTerminals._instance.getGuiHandler()
            .setTemporaryData(
                ExtendedGuiHandler.TERMINAL_STORAGE,
                Pair.of(side, new ContainerTerminalStorageBase.InitTabData(tabName, channel)));

        IntegratedTerminals._instance.getPacketHandler()
            .sendToPlayer(new TerminalStorageIngredientPartOpenPacket(pos, side, tabName, channel), player);

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
                Pair.of(side, new ContainerTerminalStorageBase.InitTabData(tabName, channel)));

        IntegratedTerminals._instance.getPacketHandler()
            .sendToServer(new TerminalStorageIngredientPartOpenPacket(pos, side, tabName, channel));
    }

}
