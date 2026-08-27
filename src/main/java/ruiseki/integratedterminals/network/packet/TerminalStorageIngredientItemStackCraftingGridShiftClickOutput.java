package ruiseki.integratedterminals.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabCommon;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentItemStackCraftingCommon;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;
import ruiseki.okcore.helper.ItemHandlerHelpers;
import ruiseki.okcore.item.capability.wrapper.PlayerMainInvWrapper;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for sending a storage slot click event from client to server.
 *
 * @author rubensworks
 *
 */
public class TerminalStorageIngredientItemStackCraftingGridShiftClickOutput extends PacketCodec {

    @CodecField
    private String tabId;
    @CodecField
    private int channel;

    public TerminalStorageIngredientItemStackCraftingGridShiftClickOutput() {

    }

    public TerminalStorageIngredientItemStackCraftingGridShiftClickOutput(String tabId, int channel) {
        this.tabId = tabId;
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
        if (player.openContainer instanceof ContainerTerminalStorageBase) {
            ContainerTerminalStorageBase<?> container = ((ContainerTerminalStorageBase) player.openContainer);
            ITerminalStorageTabCommon tabCommon = container.getTabCommon(tabId);
            if (tabCommon instanceof TerminalStorageTabIngredientComponentItemStackCraftingCommon) {
                TerminalStorageTabIngredientComponentItemStackCraftingCommon tabCommonCrafting = (TerminalStorageTabIngredientComponentItemStackCraftingCommon) tabCommon;
                ITerminalStorageTabCommon.IVariableInventory variableInventory = container.getVariableInventory()
                    .get();

                SlotCrafting slotCrafting = tabCommonCrafting.getSlotCrafting();
                if (slotCrafting == null || slotCrafting.getStack() == null) {
                    return;
                }

                ItemStack currentCraftingItem = slotCrafting.getStack()
                    .copy();
                ItemStack resultStack;
                int craftedAmount = 0;

                do {
                    ItemStack craftingStack = slotCrafting.getStack();
                    if (craftingStack == null) {
                        break;
                    }

                    // Break the loop if we cannot add the result into the player inventory anymore
                    ItemStack simulatedRemaining = ItemHandlerHelpers
                        .insertItem(new PlayerMainInvWrapper(player.inventory), craftingStack, true);
                    if (simulatedRemaining != null && simulatedRemaining.stackSize == craftingStack.stackSize) {
                        break;
                    }

                    // Break the loop if we are crafting a different item
                    if (!ItemHandlerHelpers.canItemStacksStackRelaxed(currentCraftingItem, craftingStack)) {
                        break;
                    }

                    // Extract output stack & handle pickup triggers (e.g., achievements, statistics)
                    ItemStack takenStack = slotCrafting.decrStackSize(64);
                    if (takenStack != null) {
                        slotCrafting.onPickupFromSlot(player, takenStack);
                        resultStack = takenStack;
                    } else {
                        resultStack = null;
                    }

                    if (resultStack != null && resultStack.stackSize > 0) {
                        craftedAmount += resultStack.stackSize;

                        // Move result into player inventory (or drop on ground if full)
                        ItemHandlerHelpers.giveItemToPlayer(player, resultStack.copy());

                        // Re-calculate recipe output
                        tabCommonCrafting.updateCraftingResult(player, player.openContainer, variableInventory);
                    } else {
                        break;
                    }

                } while (slotCrafting.getStack() != null && craftedAmount < currentCraftingItem.getMaxStackSize());
            }
        }
    }

}
