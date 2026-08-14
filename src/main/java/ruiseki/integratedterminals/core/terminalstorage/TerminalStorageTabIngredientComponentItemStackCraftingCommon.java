package ruiseki.integratedterminals.core.terminalstorage;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.Lists;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabCommon;
import ruiseki.integratedterminals.core.terminalstorage.button.TerminalButtonItemStackCraftingGridAutoRefill;
import ruiseki.integratedterminals.inventory.InventoryCraftingDirtyable;
import ruiseki.integratedterminals.inventory.SlotCraftingAutoRefill;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorage;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientItemStackCraftingGridSetResult;
import ruiseki.integratedterminals.part.PartTypeTerminalStorage;
import ruiseki.okcore.persist.IDirtyMarkListener;

/**
 * A common-side storage terminal ingredient tab for crafting with {@link ItemStack} instances.
 * 1.7.10 Backport
 *
 * @author rubensworks
 */
public class TerminalStorageTabIngredientComponentItemStackCraftingCommon
    extends TerminalStorageTabIngredientComponentCommon<ItemStack, Integer> {

    private InventoryCrafting inventoryCrafting;
    private InventoryCraftResult inventoryCraftResult;
    private SlotCrafting slotCrafting;
    private List<Slot> slots;
    private TerminalButtonItemStackCraftingGridAutoRefill.AutoRefillType autoRefill = TerminalButtonItemStackCraftingGridAutoRefill.AutoRefillType.STORAGE;

    public TerminalStorageTabIngredientComponentItemStackCraftingCommon(
        ContainerTerminalStorage containerTerminalStorage, ResourceLocation name,
        IngredientComponent<ItemStack, Integer> ingredientComponent) {
        super(containerTerminalStorage, name, ingredientComponent);
    }

    public static int getCraftingResultSlotIndex(Container container, ResourceLocation name) {
        ITerminalStorageTabCommon tabCommon = ((ContainerTerminalStorage) container).getTabCommon(name.toString());
        TerminalStorageTabIngredientComponentItemStackCraftingCommon tabCommonCrafting = (TerminalStorageTabIngredientComponentItemStackCraftingCommon) tabCommon;
        return tabCommonCrafting.getSlotCrafting().slotNumber;
    }

    @Override
    public List<Slot> loadSlots(Container container, int startIndex, EntityPlayer player,
        PartTypeTerminalStorage.State partState) {
        slots = Lists.newArrayListWithCapacity(10);

        // Reload the recipe when the input slots are updated
        IDirtyMarkListener dirtyListener = () -> updateCraftingResult(player, container, partState);

        this.inventoryCraftResult = new InventoryCraftResult() {

            @Override
            public void markDirty() {
                dirtyListener.onDirty();
                super.markDirty();
            }
        };
        this.inventoryCrafting = new InventoryCraftingDirtyable(container, 3, 3, dirtyListener);

        slots.add(
            slotCrafting = new SlotCraftingAutoRefill(
                player,
                this.inventoryCrafting,
                this.inventoryCraftResult,
                0,
                115,
                76,
                this,
                (TerminalStorageTabIngredientComponentServer<ItemStack, Integer>) ((ContainerTerminalStorage) container)
                    .getTabServer(getName().toString()),
                (ContainerTerminalStorage) container));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                slots.add(new Slot(this.inventoryCrafting, j + i * 3, 31 + j * 18, 58 + i * 18));
            }
        }

        // Load the items that were stored in the part state into the crafting grid slots
        List<ItemStack> tabItems = partState.getNamedInventory(
            this.getName()
                .toString());
        if (tabItems != null) {
            int i = 0;
            for (ItemStack tabItem : tabItems) {
                if (i == 0) {
                    this.inventoryCraftResult.setInventorySlotContents(i++, tabItem);
                } else {
                    this.inventoryCrafting.setInventorySlotContents(i++ - 1, tabItem);
                }
            }
        }

        List<Slot> returnSlots = Lists.newArrayList(slots);
        for (Triple<Slot, Integer, Integer> slot : ((ContainerTerminalStorage) container).getTabSlots(
            ingredientComponent.getName()
                .toString())) {
            returnSlots.add(slot.getLeft());
        }
        return returnSlots;
    }

    public InventoryCrafting getInventoryCrafting() {
        return inventoryCrafting;
    }

    public InventoryCraftResult getInventoryCraftResult() {
        return inventoryCraftResult;
    }

    public SlotCrafting getSlotCrafting() {
        return slotCrafting;
    }

    public TerminalButtonItemStackCraftingGridAutoRefill.AutoRefillType getAutoRefill() {
        return autoRefill;
    }

    public void setAutoRefill(TerminalButtonItemStackCraftingGridAutoRefill.AutoRefillType autoRefill) {
        this.autoRefill = autoRefill;
    }

    public void updateCraftingResult(EntityPlayer player, Container container,
        PartTypeTerminalStorage.State partState) {
        if (!player.worldObj.isRemote) {
            ItemStack itemstack = CraftingManager.getInstance()
                .findMatchingRecipe(inventoryCrafting, player.worldObj);

            inventoryCraftResult.setInventorySlotContents(0, itemstack);
            IntegratedTerminals._instance.getPacketHandler()
                .sendToPlayer(
                    new TerminalStorageIngredientItemStackCraftingGridSetResult(getName().toString(), itemstack),
                    (EntityPlayerMP) player);
        }

        // Save changes into the part state
        List<ItemStack> latestItems = new ArrayList<>();
        for (Slot slot : slots) {
            latestItems.add(slot.getStack());
        }
        partState.setNamedInventory(
            this.getName()
                .toString(),
            latestItems);
    }
}
