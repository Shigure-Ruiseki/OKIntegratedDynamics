package ruiseki.integratedterminals.capability.ingredient;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.commoncapabilities.api.capability.itemhandler.ItemMatch;
import ruiseki.commoncapabilities.api.ingredient.IIngredientMatcher;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.integratedterminals.GeneralConfig;
import ruiseki.integratedterminals.api.ingredient.IIngredientComponentTerminalStorageHandler;
import ruiseki.integratedterminals.api.ingredient.IIngredientInstanceSorter;
import ruiseki.integratedterminals.capability.ingredient.sorter.ItemStackIdSorter;
import ruiseki.integratedterminals.capability.ingredient.sorter.ItemStackNameSorter;
import ruiseki.integratedterminals.capability.ingredient.sorter.ItemStackQuantitySorter;
import ruiseki.integratedterminals.core.client.gui.GuiTerminalStorage;
import ruiseki.integratedterminals.core.helpers.TerminalClientUtils;
import ruiseki.integratedterminals.core.terminalstorage.query.SearchMode;
import ruiseki.okcore.client.gui.RenderItemExtendedSlotCount;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.GuiHelpers;
import ruiseki.okcore.helper.ItemHandlerHelpers;
import ruiseki.okcore.helper.TagHelpers;

/**
 * Terminal storage handler for items.
 *
 * @author rubensworks
 */
public class IngredientComponentTerminalStorageHandlerItemStack
    implements IIngredientComponentTerminalStorageHandler<ItemStack, Integer> {

    private final IngredientComponent<ItemStack, Integer> ingredientComponent;

    public IngredientComponentTerminalStorageHandlerItemStack(
        IngredientComponent<ItemStack, Integer> ingredientComponent) {
        this.ingredientComponent = ingredientComponent;
    }

    @Override
    public IngredientComponent<ItemStack, Integer> getComponent() {
        return ingredientComponent;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Blocks.chest);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawInstance(@Nullable ItemStack instance, long maxQuantity, @Nullable String label, GuiContainer gui,
        GuiTerminalStorage.DrawLayer layer, float partialTick, int x, int y, int mouseX, int mouseY,
        @Nullable List<String> additionalTooltipLines) {

        // GUARD: Early return if instance is null or invalid
        if (instance == null || instance.getItem() == null) {
            return;
        }

        // Make a copy of the item to make sure that any changes in the NBT tag that the mod may make during rendering
        // does not propagate into our client-side index. Otherwise, the client may think it has different items than
        // the server, which will cause these items not to be extractable by the client from the terminal.
        // See https://github.com/CyclopsMC/IntegratedTerminals/issues/106
        final ItemStack instanceCopy = instance.copy();

        RenderItemExtendedSlotCount renderItem = RenderItemExtendedSlotCount.getInstance();
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableDepth();
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        if (layer == GuiTerminalStorage.DrawLayer.BACKGROUND) {
            RenderItem.getInstance()
                .renderItemAndEffectIntoGUI(
                    TerminalClientUtils.getFontRenderer(),
                    TerminalClientUtils.getTextureManager(),
                    instanceCopy,
                    x,
                    y);
            renderItem.renderItemOverlayIntoGUI(
                TerminalClientUtils.getFontRenderer(),
                TerminalClientUtils.getTextureManager(),
                instanceCopy,
                x,
                y,
                label);
        } else {
            GuiHelpers.preItemToolTip(instanceCopy);
            GuiHelpers.renderTooltip(
                gui,
                x,
                y,
                GuiHelpers.SLOT_SIZE_INNER,
                GuiHelpers.SLOT_SIZE_INNER,
                mouseX,
                mouseY,
                () -> {
                    // Safe call to getTooltip
                    List<String> lines = TerminalClientUtils.getTooltip(instanceCopy);
                    if (lines == null) {
                        lines = Lists.newArrayList();
                    }
                    if (additionalTooltipLines != null) {
                        lines.addAll(additionalTooltipLines);
                    }
                    addQuantityTooltip(lines, instanceCopy);
                    return lines;
                });
            GuiHelpers.postItemToolTip();
        }
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    @Override
    public String formatQuantity(ItemStack instance) {
        return String
            .format(Locale.ROOT, "%,d", (instance != null && instance.getItem() != null) ? instance.stackSize : 0);
    }

    @Override
    public boolean isInstance(ItemStack itemStack) {
        return itemStack != null && itemStack.getItem() != null;
    }

    @Override
    public ItemStack getInstance(ItemStack itemStack) {
        return itemStack;
    }

    @Override
    public long getMaxQuantity(ItemStack itemStack) {
        return (itemStack != null && itemStack.getItem() != null) ? itemStack.getMaxStackSize() : 0;
    }

    @Override
    public int getInitialInstanceMovementQuantity() {
        return GeneralConfig.guiStorageItemInitialQuantity;
    }

    @Override
    public int getIncrementalInstanceMovementQuantity() {
        return GeneralConfig.guiStorageItemIncrementalQuantity;
    }

    @Override
    public int throwIntoWorld(IIngredientComponentStorage<ItemStack, Integer> storage, ItemStack maxInstance,
        EntityPlayer player) {
        if (maxInstance == null || maxInstance.getItem() == null || maxInstance.stackSize <= 0) {
            return 0;
        }

        ItemStack targetInstance = maxInstance.copy();
        int maxStackSize = targetInstance.getMaxStackSize();
        if (targetInstance.stackSize > maxStackSize) {
            targetInstance.stackSize = maxStackSize;
        }

        ItemStack extracted = storage.extract(targetInstance, ItemMatch.EXACT, false);
        if (extracted != null && extracted.stackSize > 0) {
            extracted.stackSize = Math.min(extracted.stackSize, maxStackSize);

            player.dropPlayerItemWithRandomChoice(extracted, true);
            return extracted.stackSize;
        }
        return 0;
    }

    @Override
    public ItemStack insertIntoContainer(IIngredientComponentStorage<ItemStack, Integer> storage, Container container,
        int containerSlotIndex, ItemStack maxInstance, @Nullable EntityPlayer player, boolean transferFullSelection) {
        if (maxInstance == null || maxInstance.getItem() == null) {
            return null;
        }

        IIngredientMatcher<ItemStack, Integer> matcher = IngredientComponent.ITEMSTACK.getMatcher();

        // Limit transfer to 64 at a time
        if (maxInstance.stackSize > 64) {
            maxInstance.stackSize = 64;
        }

        Slot containerSlot = container.getSlot(containerSlotIndex);
        if (transferFullSelection && player != null && player.inventory.getItemStack() == null) {
            // Pick up container slot contents if not empty
            ItemStack containerStack = containerSlot.getStack();
            if (containerStack != null
                && !matcher.matches(containerStack, maxInstance, matcher.getExactMatchNoQuantityCondition())
                && containerSlot.canTakeStack(player)) {
                containerSlot.onPickupFromSlot(player, containerStack);
                player.inventory.setItemStack(containerStack);
                containerSlot.putStack(null);
            }
        }

        long requiredQuantity = matcher.getQuantity(maxInstance);
        long movedTotal = 0;
        while (movedTotal < requiredQuantity) {
            ItemStack extracted = storage.extract(maxInstance, matcher.getExactMatchNoQuantityCondition(), true);
            if (extracted == null) {
                break;
            }
            ItemStack playerStack = containerSlot.getStack();
            if ((playerStack == null || ItemHandlerHelpers.canItemStacksStack(extracted, playerStack))
                && containerSlot.isItemValid(extracted)) {

                int currentCount = playerStack != null ? playerStack.stackSize : 0;
                int newCount = Math.min(currentCount + extracted.stackSize, extracted.getMaxStackSize());
                int inserted = newCount - currentCount;

                ItemStack moved = storage.extract(
                    matcher.withQuantity(maxInstance, inserted),
                    matcher.getExactMatchNoQuantityCondition(),
                    false);
                if (moved == null) {
                    break;
                }
                movedTotal += moved.stackSize;

                containerSlot.putStack(
                    matcher.withQuantity(maxInstance, currentCount + moved.stackSize)
                        .copy());
                container.detectAndSendChanges();
            } else {
                break;
            }
        }
        return matcher.withQuantity(maxInstance, (int) movedTotal);
    }

    @Override
    public void extractActiveStackFromPlayerInventory(IIngredientComponentStorage<ItemStack, Integer> storage,
        InventoryPlayer playerInventory, long moveQuantityPlayerSlot) {
        ItemStack activeStack = playerInventory.getItemStack();
        if (activeStack != null && activeStack.getItem() != null) {
            ItemStack playerStack = IngredientComponent.ITEMSTACK.getMatcher()
                .withQuantity(activeStack, (int) moveQuantityPlayerSlot);
            ItemStack remainingStack = storage.insert(playerStack.copy(), false);
            int remaining = remainingStack != null ? remainingStack.stackSize : 0;
            int moved = (int) (moveQuantityPlayerSlot - remaining);

            activeStack.stackSize -= moved;
            if (activeStack.stackSize <= 0) {
                playerInventory.setItemStack(null);
            }
        }
    }

    @Override
    public void extractMaxFromContainerSlot(IIngredientComponentStorage<ItemStack, Integer> storage,
        Container container, int containerSlot, InventoryPlayer playerInventory, int limit) {
        Slot slot = container.getSlot(containerSlot);
        if (slot.canTakeStack(playerInventory.player)) {
            ItemStack toMove = slot.decrStackSize(limit == -1 ? Integer.MAX_VALUE : limit);
            if (toMove != null) {
                // The following code is a bit convoluted to handle cases where the container and the storage point to
                // the same inventory.
                ItemStack remainingStack = storage.insert(toMove, false);
                if (remainingStack != null) {
                    // Check if the slot is still empty, because the storage may be linked to the container in some
                    // exotic cases (e.g. player interfaces).
                    if (!slot.getHasStack()) {
                        slot.putStack(remainingStack);
                    } else {
                        // Simply add the remainder to the player's container
                        playerInventory.addItemStackToInventory(remainingStack);
                    }
                }
                container.detectAndSendChanges();
            }
        }
    }

    @Override
    public long getActivePlayerStackQuantity(InventoryPlayer playerInventory) {
        ItemStack activeStack = playerInventory.getItemStack();
        return (activeStack != null && activeStack.getItem() != null) ? activeStack.stackSize : 0;
    }

    @Override
    public void drainActivePlayerStackQuantity(InventoryPlayer playerInventory, long quantity) {
        ItemStack activeStack = playerInventory.getItemStack();
        if (activeStack != null && activeStack.getItem() != null) {
            activeStack.stackSize -= (int) quantity;
            if (activeStack.stackSize <= 0) {
                playerInventory.setItemStack(null);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Predicate<ItemStack> getInstanceFilterPredicate(SearchMode searchMode, String query) {
        final String lowerQuery = query.toLowerCase(Locale.ENGLISH);
        switch (searchMode) {
            case MOD:
                return i -> {
                    if (i == null || i.getItem() == null) return false;
                    GameRegistry.UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(i.getItem());
                    String modId = (id != null) ? id.modId : "minecraft";
                    return modId.toLowerCase(Locale.ENGLISH)
                        .contains(lowerQuery);
                };
            case TOOLTIP:
                return i -> {
                    if (i == null || i.getItem() == null) return false;
                    List<String> tooltip = TerminalClientUtils.getTooltip(i);
                    if (tooltip == null) return false;
                    return tooltip.stream()
                        .anyMatch(
                            s -> s != null && s.toLowerCase(Locale.ENGLISH)
                                .contains(lowerQuery));
                };
            case DICT:
                return i -> {
                    if (i == null || i.getItem() == null) return false;
                    return Arrays.stream(OreDictionary.getOreIDs(i))
                        .mapToObj(OreDictionary::getOreName)
                        .anyMatch(
                            name -> name != null && name.toLowerCase(Locale.ENGLISH)
                                .contains(lowerQuery));
                };
            case TAG:
                return i -> {
                    if (i == null || i.getItem() == null) return false;
                    return TagHelpers.getTags(i)
                        .stream()
                        .map(
                            tagKey -> tagKey.location()
                                .toString())
                        .anyMatch(
                            name -> name != null && name.toLowerCase(Locale.ENGLISH)
                                .contains(lowerQuery));
                };
            case DEFAULT:
                return i -> i != null && i.getItem() != null
                    && i.getDisplayName() != null
                    && i.getDisplayName()
                        .toLowerCase(Locale.ENGLISH)
                        .contains(lowerQuery);
        }
        return null;
    }

    @Override
    public Collection<IIngredientInstanceSorter<ItemStack>> getInstanceSorters() {
        return Lists.newArrayList(new ItemStackNameSorter(), new ItemStackIdSorter(), new ItemStackQuantitySorter());
    }
}
