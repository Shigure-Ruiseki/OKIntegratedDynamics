package ruiseki.integratedterminals.network.packet;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentItemStackCraftingCommon;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentServer;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorage;
import ruiseki.okcore.helper.ItemHandlerHelpers;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for telling the server that the crafting grid must be balanced (MC 1.7.10).
 * 
 * @author rubensworks
 */
public class TerminalStorageIngredientItemStackCraftingGridBalance extends PacketCodec {

    @CodecField
    private String tabId;

    public TerminalStorageIngredientItemStackCraftingGridBalance() {

    }

    public TerminalStorageIngredientItemStackCraftingGridBalance(String tabId) {
        this.tabId = tabId;
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
        if (player.openContainer instanceof ContainerTerminalStorage) {
            ContainerTerminalStorage container = ((ContainerTerminalStorage) player.openContainer);
            if (container.getTabServer(tabId) instanceof TerminalStorageTabIngredientComponentServer) {
                TerminalStorageTabIngredientComponentItemStackCraftingCommon tabCommon = (TerminalStorageTabIngredientComponentItemStackCraftingCommon) container
                    .getTabCommon(tabId);
                tabCommon.getInventoryCraftResult()
                    .setInventorySlotContents(0, null);
                balanceGrid(tabCommon.getInventoryCrafting());
            }
        }
    }

    public static void balanceGrid(InventoryCrafting craftingGrid) {
        // Init bins
        List<Pair<ItemStack, List<Pair<Integer, Integer>>>> bins = Lists
            .newArrayListWithExpectedSize(craftingGrid.getSizeInventory());
        for (int slot = 0; slot < craftingGrid.getSizeInventory(); slot++) {
            ItemStack itemStack = craftingGrid.getStackInSlot(slot);
            if (itemStack != null) {
                int amount = itemStack.stackSize;
                itemStack = itemStack.copy();
                itemStack.stackSize = 1;
                int bin = 0;
                boolean addedToBin = false;
                while (bin < bins.size() && !addedToBin) {
                    Pair<ItemStack, List<Pair<Integer, Integer>>> pair = bins.get(bin);
                    ItemStack original = pair.getLeft()
                        .copy();
                    original.stackSize = 1;

                    if (ItemHandlerHelpers.canItemStacksStack(original, itemStack)) {
                        pair.getLeft().stackSize += amount;
                        pair.getRight()
                            .add(new MutablePair<>(slot, 0));
                        addedToBin = true;
                    }
                    bin++;
                }

                if (!addedToBin) {
                    itemStack.stackSize = amount;
                    bins.add(
                        new MutablePair<>(
                            itemStack,
                            Lists.newArrayList((Pair<Integer, Integer>) new MutablePair<>(slot, 0))));
                }
            }
        }

        // Balance bins
        for (Pair<ItemStack, List<Pair<Integer, Integer>>> pair : bins) {
            int division = pair.getLeft().stackSize / pair.getRight()
                .size();
            int modulus = pair.getLeft().stackSize % pair.getRight()
                .size();
            for (Pair<Integer, Integer> slot : pair.getRight()) {
                slot.setValue(division + Math.max(0, Math.min(1, modulus--)));
            }
        }

        // Set bins to slots
        for (Pair<ItemStack, List<Pair<Integer, Integer>>> pair : bins) {
            for (Pair<Integer, Integer> slot : pair.getRight()) {
                int stackSize = slot.getRight();
                if (stackSize > 0) {
                    ItemStack itemStack = pair.getKey()
                        .copy();
                    itemStack.stackSize = stackSize;
                    craftingGrid.setInventorySlotContents(slot.getKey(), itemStack);
                } else {
                    craftingGrid.setInventorySlotContents(slot.getKey(), null);
                }
            }
        }
    }
}
