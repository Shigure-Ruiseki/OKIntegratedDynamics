package ruiseki.integratedterminals.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentItemStackCraftingCommon;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorage;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for telling the server that the crafting grid must be cleared.
 * 
 * @author rubensworks
 *
 */
public class TerminalStorageIngredientItemStackCraftingGridSetResult extends PacketCodec {

    @CodecField
    private String tabId;
    @CodecField
    private ItemStack itemStack;

    public TerminalStorageIngredientItemStackCraftingGridSetResult() {

    }

    public TerminalStorageIngredientItemStackCraftingGridSetResult(String tabId, ItemStack itemStack) {
        this.tabId = tabId;
        this.itemStack = itemStack;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void actionClient(World world, EntityPlayer player) {
        if (player.openContainer instanceof ContainerTerminalStorage) {
            ContainerTerminalStorage container = ((ContainerTerminalStorage) player.openContainer);
            TerminalStorageTabIngredientComponentItemStackCraftingCommon tabCommon = (TerminalStorageTabIngredientComponentItemStackCraftingCommon) container
                .getTabCommon(tabId);
            tabCommon.getSlotCrafting()
                .putStack(this.itemStack);
        }
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {

    }

}
