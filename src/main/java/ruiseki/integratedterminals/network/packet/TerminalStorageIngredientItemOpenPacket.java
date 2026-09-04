package ruiseki.integratedterminals.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.Pair;

import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.core.client.gui.ExtendedGuiHandler;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;
import ruiseki.integratedterminals.inventory.container.TerminalStorageState;
import ruiseki.integratedterminals.proxy.guiprovider.GuiProviders;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

public class TerminalStorageIngredientItemOpenPacket extends PacketCodec {

    @CodecField
    private int slotIndex;
    @CodecField
    private ContainerTerminalStorageBase.InitTabData tabData;
    @CodecField
    TerminalStorageState state;

    public TerminalStorageIngredientItemOpenPacket() {

    }

    public TerminalStorageIngredientItemOpenPacket(int slotIndex, ContainerTerminalStorageBase.InitTabData tabData,
        TerminalStorageState state) {
        this.slotIndex = slotIndex;
        this.tabData = tabData;
        this.state = state;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {
        IntegratedTerminals._instance.getGuiHandler()
            .setTemporaryData(ExtendedGuiHandler.TERMINAL_STORAGE_ITEM, Pair.of(slotIndex, Pair.of(tabData, state)));
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        openServer(world, slotIndex, player, tabData, state);
    }

    public static void openServer(World world, int slotIndex, EntityPlayerMP player,
        ContainerTerminalStorageBase.InitTabData tabData, TerminalStorageState state) {
        IntegratedTerminals._instance.getGuiHandler()
            .setTemporaryData(ExtendedGuiHandler.TERMINAL_STORAGE_ITEM, Pair.of(slotIndex, Pair.of(tabData, state)));

        IntegratedTerminals._instance.getPacketHandler()
            .sendToPlayer(new TerminalStorageIngredientItemOpenPacket(slotIndex, tabData, state), player);

        player.openGui(
            IntegratedTerminals._instance,
            GuiProviders.ID_GUI_TERMINAL_STORAGE_ITEM_INIT,
            world,
            (int) player.posX,
            (int) player.posY,
            (int) player.posZ);
    }

    public static void send(int slotIndex, ContainerTerminalStorageBase.InitTabData tabData,
        TerminalStorageState state) {
        IntegratedTerminals._instance.getGuiHandler()
            .setTemporaryData(ExtendedGuiHandler.TERMINAL_STORAGE_ITEM, Pair.of(slotIndex, Pair.of(tabData, state)));

        IntegratedTerminals._instance.getPacketHandler()
            .sendToServer(new TerminalStorageIngredientItemOpenPacket(slotIndex, tabData, state));
    }
}
