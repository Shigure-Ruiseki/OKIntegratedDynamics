package ruiseki.integratedterminals.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.Pair;

import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.core.client.gui.ExtendedGuiHandler;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;
import ruiseki.integratedterminals.proxy.guiprovider.GuiProviders;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

public class TerminalStorageIngredientItemOpenPacket extends PacketCodec {

    @CodecField
    private int slotIndex;
    @CodecField
    private String tabName;
    @CodecField
    private int channel;

    public TerminalStorageIngredientItemOpenPacket() {

    }

    public TerminalStorageIngredientItemOpenPacket(int slotIndex, String tabName, int channel) {
        this.slotIndex = slotIndex;
        this.tabName = tabName;
        this.channel = channel;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {
        IntegratedTerminals._instance.getGuiHandler()
            .setTemporaryData(
                ExtendedGuiHandler.TERMINAL_STORAGE_ITEM,
                Pair.of(slotIndex, new ContainerTerminalStorageBase.InitTabData(tabName, channel)));
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        openServer(world, slotIndex, player, tabName, channel);
    }

    public static void openServer(World world, int slotIndex, EntityPlayerMP player, String tabName, int channel) {
        IntegratedTerminals._instance.getGuiHandler()
            .setTemporaryData(
                ExtendedGuiHandler.TERMINAL_STORAGE_ITEM,
                Pair.of(slotIndex, new ContainerTerminalStorageBase.InitTabData(tabName, channel)));

        IntegratedTerminals._instance.getPacketHandler()
            .sendToPlayer(new TerminalStorageIngredientItemOpenPacket(slotIndex, tabName, channel), player);

        player.openGui(
            IntegratedTerminals._instance,
            GuiProviders.ID_GUI_TERMINAL_STORAGE_ITEM_INIT,
            world,
            (int) player.posX,
            (int) player.posY,
            (int) player.posZ);
    }

    public static void send(int slotIndex, String tabName, int channel) {
        IntegratedTerminals._instance.getGuiHandler()
            .setTemporaryData(
                ExtendedGuiHandler.TERMINAL_STORAGE_ITEM,
                Pair.of(slotIndex, new ContainerTerminalStorageBase.InitTabData(tabName, channel)));

        IntegratedTerminals._instance.getPacketHandler()
            .sendToServer(new TerminalStorageIngredientItemOpenPacket(slotIndex, tabName, channel));
    }
}
