package ruiseki.integratedterminals.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.item.ItemTerminalStoragePortable;
import ruiseki.integratedterminals.item.ItemTerminalStoragePortableConfig;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

public class TerminalStorageIngredientItemOpenGenericPacket extends PacketCodec {

    @CodecField
    private int slotIndex;

    public TerminalStorageIngredientItemOpenGenericPacket() {}

    public TerminalStorageIngredientItemOpenGenericPacket(int slotIndex) {
        this.slotIndex = slotIndex;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void actionClient(World world, EntityPlayer player) {
        ((ItemTerminalStoragePortable) ItemTerminalStoragePortableConfig._instance.getInstance())
            .openGuiForItemIndex(world, player, slotIndex);
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        openServer(world, slotIndex, player);

        IntegratedTerminals._instance.getPacketHandler()
            .sendToPlayer(new TerminalStorageIngredientItemOpenGenericPacket(slotIndex), player);
    }

    public static void openServer(World world, int slotIndex, EntityPlayerMP player) {
        ((ItemTerminalStoragePortable) ItemTerminalStoragePortableConfig._instance.getInstance())
            .openGuiForItemIndex(world, player, slotIndex);
    }

    public static void send(int slotIndex) {
        IntegratedTerminals._instance.getPacketHandler()
            .sendToServer(new TerminalStorageIngredientItemOpenGenericPacket(slotIndex));
    }
}
