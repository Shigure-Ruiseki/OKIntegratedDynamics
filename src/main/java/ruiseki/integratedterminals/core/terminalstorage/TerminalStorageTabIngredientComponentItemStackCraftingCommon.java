package ruiseki.integratedterminals.core.terminalstorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabCommon;
import ruiseki.integratedterminals.core.terminalstorage.button.TerminalButtonItemStackCraftingGridAutoRefill;
import ruiseki.integratedterminals.inventory.InventoryCraftingDirtyable;
import ruiseki.integratedterminals.inventory.SlotCraftingAutoRefill;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientItemStackCraftingGridSetResult;
import ruiseki.okcore.helper.GuiHelpers;
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
    private List<Pair<Slot, ISlotPositionCallback>> slots;
    private TerminalButtonItemStackCraftingGridAutoRefill.AutoRefillType autoRefill = TerminalButtonItemStackCraftingGridAutoRefill.AutoRefillType.STORAGE;

    public TerminalStorageTabIngredientComponentItemStackCraftingCommon(
        ContainerTerminalStorageBase containerTerminalStorage, ResourceLocation name,
        IngredientComponent<ItemStack, Integer> ingredientComponent) {
        super(containerTerminalStorage, name, ingredientComponent);
    }

    public static int getCraftingResultSlotIndex(Container container, ResourceLocation name) {
        ITerminalStorageTabCommon tabCommon = ((ContainerTerminalStorageBase) container).getTabCommon(name.toString());
        TerminalStorageTabIngredientComponentItemStackCraftingCommon tabCommonCrafting = (TerminalStorageTabIngredientComponentItemStackCraftingCommon) tabCommon;
        return tabCommonCrafting.getSlotCrafting().slotNumber;
    }

    @Override
    public List<Pair<Slot, ISlotPositionCallback>> loadSlots(Container container, int startIndex, EntityPlayer player,
        Optional<IVariableInventory> variableInventoryOptional) {
        IVariableInventory variableInventory = variableInventoryOptional.orElse(null);
        slots = Lists.newArrayListWithCapacity(10);

        // Reload the recipe when the input slots are updated
        IDirtyMarkListener dirtyListener = () -> updateCraftingResult(player, container, variableInventory);

        this.inventoryCraftResult = new InventoryCraftResult() {

            @Override
            public void markDirty() {
                dirtyListener.onDirty();
                super.markDirty();
            }
        };
        this.inventoryCrafting = new InventoryCraftingDirtyable(container, 3, 3, dirtyListener);

        slots.add(
            Pair.of(
                slotCrafting = new SlotCraftingAutoRefill(
                    player,
                    this.inventoryCrafting,
                    this.inventoryCraftResult,
                    0,
                    0,
                    0,
                    this,
                    (TerminalStorageTabIngredientComponentServer<ItemStack, Integer>) ((ContainerTerminalStorageBase) container)
                        .getTabServer(getName().toString()),
                    (ContainerTerminalStorageBase) container),
                factors -> Pair.of(
                    factors.offsetX() + (factors.gridXSize() / 2)
                        - factors.playerInventoryOffsetX()
                        + 62
                        - (factors.playerInventoryOffsetX() > 0 ? 47 : 0),
                    factors.offsetY() + factors.gridYSize()
                        + factors.playerInventoryOffsetY()
                        + 10
                        + (factors.playerInventoryOffsetX() > 0 ? 68 : 0))));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                int finalJ = j;
                int finalI = i;
                slots.add(
                    Pair.of(
                        new Slot(this.inventoryCrafting, j + i * 3, 31 + j * 18 + 28, 58 + i * 18 + 7),
                        factors -> Pair.of(
                            factors.offsetX() + (factors.gridXSize() / 2)
                                - factors.playerInventoryOffsetX()
                                + finalJ * GuiHelpers.SLOT_SIZE
                                - 22
                                - (factors.playerInventoryOffsetX() > 0 ? 47 : 0),
                            factors.offsetY() + factors.gridYSize()
                                + factors.playerInventoryOffsetY()
                                + finalI * GuiHelpers.SLOT_SIZE
                                - 8
                                + (factors.playerInventoryOffsetX() > 0 ? 68 : 0))));
            }
        }

        if (variableInventory != null) {
            List<ItemStack> tabItems = variableInventory.getNamedInventory(
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
        }

        List<Pair<Slot, ISlotPositionCallback>> returnSlots = Lists.newArrayList(slots);
        returnSlots.addAll(
            ((ContainerTerminalStorageBase<?>) container).getTabSlots(
                ingredientComponent.getName()
                    .toString()));
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
        ITerminalStorageTabCommon.IVariableInventory variableInventory) {
        if (!player.worldObj.isRemote) {
            ItemStack itemstack = CraftingManager.getInstance()
                .findMatchingRecipe(inventoryCrafting, player.worldObj);

            inventoryCraftResult.setInventorySlotContents(0, itemstack);
            IntegratedTerminals._instance.getPacketHandler()
                .sendToPlayer(
                    new TerminalStorageIngredientItemStackCraftingGridSetResult(getName().toString(), itemstack),
                    (EntityPlayerMP) player);
        }

        if (variableInventory != null) {
            List<ItemStack> latestItems = new ArrayList<>();
            for (Pair<Slot, ISlotPositionCallback> slot : slots) {
                latestItems.add(
                    slot.getLeft()
                        .getStack());
            }
            variableInventory.setNamedInventory(
                this.getName()
                    .toString(),
                latestItems);
        }
    }
}
