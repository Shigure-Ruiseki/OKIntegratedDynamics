package ruiseki.integratedterminals.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.item.ItemTerminalStoragePortable;
import ruiseki.integratedterminals.item.ItemTerminalStoragePortableConfig;
import ruiseki.okcore.inventory.ItemLocation;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

public class TerminalStorageIngredientItemOpenGenericPacket extends PacketCodec {

    @CodecField
    private ItemLocation itemLocation;

    public TerminalStorageIngredientItemOpenGenericPacket() {}

    public TerminalStorageIngredientItemOpenGenericPacket(ItemLocation itemLocation) {
        this.itemLocation = itemLocation;
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
        openServer(world, itemLocation, player);
    }

    public static void openServer(World world, ItemLocation itemLocation, EntityPlayerMP player) {
        ((ItemTerminalStoragePortable) ItemTerminalStoragePortableConfig._instance.getInstance())
            .openGuiForItemIndex(world, player, itemLocation);
    }

    public static void send(ItemLocation itemLocation) {
        IntegratedTerminals._instance.getPacketHandler()
            .sendToServer(new TerminalStorageIngredientItemOpenGenericPacket(itemLocation));
    }
}
