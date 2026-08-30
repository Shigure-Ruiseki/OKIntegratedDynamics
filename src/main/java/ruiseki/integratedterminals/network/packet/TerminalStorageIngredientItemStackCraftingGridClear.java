package ruiseki.integratedterminals.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentItemStackCraftingCommon;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentServer;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for telling the server that the crafting grid must be cleared.
 *
 * @author rubensworks
 *
 */
public class TerminalStorageIngredientItemStackCraftingGridClear extends PacketCodec {

    @CodecField
    private String tabId;
    @CodecField
    private int channel;
    @CodecField
    private boolean toStorage;

    public TerminalStorageIngredientItemStackCraftingGridClear() {

    }

    public TerminalStorageIngredientItemStackCraftingGridClear(String tabId, int channel, boolean toStorage) {
        this.tabId = tabId;
        this.channel = channel;
        this.toStorage = toStorage;
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
        if (player.openContainer instanceof ContainerTerminalStorageBase) {
            ContainerTerminalStorageBase container = ((ContainerTerminalStorageBase) player.openContainer);
            if (container.getTabServer(tabId) instanceof TerminalStorageTabIngredientComponentServer) {
                TerminalStorageTabIngredientComponentServer<ItemStack, Integer> tabServer = (TerminalStorageTabIngredientComponentServer<ItemStack, Integer>) container
                    .getTabServer(tabId);
                TerminalStorageTabIngredientComponentItemStackCraftingCommon tabCommon = (TerminalStorageTabIngredientComponentItemStackCraftingCommon) container
                    .getTabCommon(tabId);
                clearGrid(tabCommon, tabServer, channel, toStorage, player);
            }
        }
    }

    public static void clearGrid(TerminalStorageTabIngredientComponentItemStackCraftingCommon tabCommon,
        TerminalStorageTabIngredientComponentServer<ItemStack, Integer> tabServer, int channel, boolean toStorage,
        EntityPlayer player) {
        tabCommon.getInventoryCraftResult()
            .setInventorySlotContents(0, null);
        InventoryCrafting inventoryCrafting = tabCommon.getInventoryCrafting();

        for (int i = 0; i < inventoryCrafting.getSizeInventory(); i++) {
            ItemStack itemStack = inventoryCrafting.getStackInSlot(i);

            if (itemStack != null) {
                inventoryCrafting.setInventorySlotContents(i, null); // Clear from slot

                if (toStorage) {
                    // To storage
                    ItemStack remainder = tabServer.getIngredientNetwork()
                        .getChannelInternal(channel)
                        .insert(itemStack, false);
                    // Place any remainder back into the grid slot so items aren't lost
                    if (remainder != null && remainder.stackSize > 0) {
                        inventoryCrafting.setInventorySlotContents(i, remainder);
                    }
                } else {
                    // To player inventory (if full, drop item into world)
                    if (!player.inventory.addItemStackToInventory(itemStack)) {
                        player.dropPlayerItemWithRandomChoice(itemStack, false);
                    }
                    inventoryCrafting.setInventorySlotContents(i, null);
                }
            }
        }
    }

}
