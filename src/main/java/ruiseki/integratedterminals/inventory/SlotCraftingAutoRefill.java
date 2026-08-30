package ruiseki.integratedterminals.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2FPacketSetSlot;

import ruiseki.commoncapabilities.api.capability.itemhandler.ItemMatch;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.commoncapabilities.ingredient.storage.IngredientComponentStorageWrapperHandlerItemStack;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentItemStackCraftingCommon;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentServer;
import ruiseki.integratedterminals.core.terminalstorage.button.TerminalButtonItemStackCraftingGridAutoRefill;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;
import ruiseki.okcore.item.capability.wrapper.PlayerInvWrapper;

/**
 * A crafting slot that will automatically auto-refill from the storage (if enabled).
 *
 * @author rubensworks
 */
public class SlotCraftingAutoRefill extends SlotCrafting {

    private final InventoryCrafting inventoryCrafting;
    private final TerminalStorageTabIngredientComponentItemStackCraftingCommon tabCommon;
    private final TerminalStorageTabIngredientComponentServer<ItemStack, Integer> tabServer;
    private final ContainerTerminalStorageBase container;

    public SlotCraftingAutoRefill(EntityPlayer player, InventoryCrafting inventoryCrafting, IInventory inventoryIn,
        int slotIndex, int xPosition, int yPosition,
        TerminalStorageTabIngredientComponentItemStackCraftingCommon tabCommon,
        TerminalStorageTabIngredientComponentServer<ItemStack, Integer> tabServer,
        ContainerTerminalStorageBase container) {
        super(player, inventoryCrafting, inventoryIn, slotIndex, xPosition, yPosition);
        this.inventoryCrafting = inventoryCrafting;
        this.tabCommon = tabCommon;
        this.tabServer = tabServer;
        this.container = container;
    }

    @Override
    public void onPickupFromSlot(EntityPlayer thePlayer, ItemStack stack) {
        TerminalButtonItemStackCraftingGridAutoRefill.AutoRefillType autoRefill = tabCommon.getAutoRefill();
        if (!thePlayer.worldObj.isRemote
            && autoRefill != TerminalButtonItemStackCraftingGridAutoRefill.AutoRefillType.DISABLED) {
            List<ItemStack> beforeCraft = inventoryToList(inventoryCrafting, true);
            super.onPickupFromSlot(thePlayer, stack);
            List<ItemStack> afterCraft = inventoryToList(inventoryCrafting, false);

            List<ItemStack> removed = getRemoved(beforeCraft, afterCraft);
            // Attempt to get and re-add removed stacks from storage
            IIngredientComponentStorage<ItemStack, Integer> storage = tabServer.getIngredientNetwork()
                .getChannelInternal(this.container.getSelectedChannel());
            IIngredientComponentStorage<ItemStack, Integer> player = new IngredientComponentStorageWrapperHandlerItemStack.ComponentStorageWrapper(
                IngredientComponent.ITEMSTACK,
                new PlayerInvWrapper(thePlayer.inventory));

            for (int i = 0; i < removed.size(); i++) {
                ItemStack removedStack = removed.get(i);
                if (removedStack != null && removedStack.stackSize > 0) {
                    ItemStack extracted;

                    // Different source priorities
                    switch (autoRefill) {
                        case STORAGE:
                            extracted = storage.extract(removedStack, ItemMatch.EXACT, false);
                            break;
                        case PLAYER:
                            extracted = player.extract(removedStack, ItemMatch.EXACT, false);
                            break;
                        case STORAGE_PLAYER:
                            extracted = storage.extract(removedStack, ItemMatch.EXACT, false);
                            if (extracted == null) {
                                extracted = player.extract(removedStack, ItemMatch.EXACT, false);
                            }
                            break;
                        case PLAYER_STORAGE:
                            extracted = player.extract(removedStack, ItemMatch.EXACT, false);
                            if (extracted == null) {
                                extracted = storage.extract(removedStack, ItemMatch.EXACT, false);
                            }
                            break;
                        default:
                            extracted = null;
                            break;
                    }
                    thePlayer.openContainer.detectAndSendChanges();

                    if (extracted != null && extracted.stackSize > 0) {
                        ItemStack existingStack = inventoryCrafting.getStackInSlot(i);
                        if (existingStack != null) {
                            existingStack.stackSize += extracted.stackSize;
                        } else {
                            existingStack = extracted.copy();
                        }
                        inventoryCrafting.setInventorySlotContents(i, existingStack);

                        ((EntityPlayerMP) thePlayer).playerNetServerHandler.sendPacket(
                            new S2FPacketSetSlot(
                                thePlayer.openContainer.windowId,
                                i + this.slotNumber + 1,
                                inventoryCrafting.getStackInSlot(i)));
                    }
                }
            }
            return;
        }
        super.onPickupFromSlot(thePlayer, stack);
    }

    public static List<ItemStack> inventoryToList(IInventory inventory, boolean copy) {
        List<ItemStack> list = new ArrayList<>(Collections.nCopies(inventory.getSizeInventory(), null));
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stackInSlot = inventory.getStackInSlot(i);
            list.set(i, copy && stackInSlot != null ? stackInSlot.copy() : stackInSlot);
        }
        return list;
    }

    public static List<ItemStack> getRemoved(List<ItemStack> before, List<ItemStack> after) {
        List<ItemStack> removed = new ArrayList<>(Collections.nCopies(before.size(), null));
        for (int i = 0; i < before.size(); i++) {
            ItemStack beforeStack = before.get(i);
            ItemStack afterStack = after.get(i);

            int beforeCount = beforeStack != null ? beforeStack.stackSize : 0;
            int afterCount = afterStack != null ? afterStack.stackSize : 0;

            if (beforeCount > afterCount) {
                ItemStack removedStack = beforeStack.copy();
                removedStack.stackSize = beforeCount - afterCount;
                removed.set(i, removedStack);
            }
        }
        return removed;
    }
}
