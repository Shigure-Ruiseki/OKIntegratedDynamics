package ruiseki.integratedcompat.network.packet;

import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.commoncapabilities.api.capability.itemhandler.ItemMatch;
import ruiseki.commoncapabilities.api.ingredient.IIngredientMatcher;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabCommon;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentItemStackCraftingCommon;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentServer;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorage;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientItemStackCraftingGridClear;
import ruiseki.integratedterminals.part.PartTypeTerminalStorage;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for setting the crafting grid recipe and filling it with items.
 * 
 * @author rubensworks
 *
 */
public class TerminalStorageIngredientItemStackCraftingGridSetRecipe extends PacketCodec {

    @CodecField
    private String tabId;
    @CodecField
    private int channel;
    @CodecField
    private boolean maxTransfer;
    @CodecField
    private Map<Integer, List<ItemStack>> slottedIngredients;

    public TerminalStorageIngredientItemStackCraftingGridSetRecipe() {

    }

    public TerminalStorageIngredientItemStackCraftingGridSetRecipe(String tabId, int channel, boolean maxTransfer,
        Map<Integer, List<ItemStack>> slottedIngredients) {
        this.tabId = tabId;
        this.channel = channel;
        this.maxTransfer = maxTransfer;
        this.slottedIngredients = slottedIngredients;
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
            ITerminalStorageTabCommon tabCommon = container.getTabCommon(tabId);
            if (tabCommon instanceof TerminalStorageTabIngredientComponentItemStackCraftingCommon) {
                TerminalStorageTabIngredientComponentServer<ItemStack, Integer> tabServerCrafting = (TerminalStorageTabIngredientComponentServer<ItemStack, Integer>) container
                    .getTabServer(tabId);
                TerminalStorageTabIngredientComponentItemStackCraftingCommon tabCommonCrafting = (TerminalStorageTabIngredientComponentItemStackCraftingCommon) tabCommon;
                PartTypeTerminalStorage.State partState = container.getPartState();
                int slotOffset = tabCommonCrafting.getSlotCrafting().slotNumber;

                // Clear current grid into storage
                TerminalStorageIngredientItemStackCraftingGridClear
                    .clearGrid(tabCommonCrafting, tabServerCrafting, channel, false, player);

                // Try filling the grid with the given recipe
                IIngredientComponentStorage<ItemStack, Integer> storage = tabServerCrafting.getIngredientNetwork()
                    .getChannel(channel);
                IIngredientMatcher<ItemStack, Integer> matcher = IngredientComponent.ITEMSTACK.getMatcher();
                for (Map.Entry<Integer, List<ItemStack>> entry : this.slottedIngredients.entrySet()) {
                    int slotId = entry.getKey() + slotOffset;
                    Slot slot = container.getSlot(slotId);

                    if (!slot.getHasStack()) {
                        ItemStack extracted = null;
                        for (ItemStack itemStack : entry.getValue()) {
                            extracted = storage.extract(itemStack, ItemMatch.EXACT, false);
                            if (extracted != null) {
                                break;
                            }
                        }
                        if (extracted != null) {
                            slot.putStack(extracted);
                        }
                    }
                }

                // Notify the client
                // TODO?
            }
        }
    }

}
