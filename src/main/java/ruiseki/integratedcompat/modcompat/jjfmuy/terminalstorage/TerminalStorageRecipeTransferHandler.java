package ruiseki.integratedcompat.modcompat.jjfmuy.terminalstorage;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ruiseki.commoncapabilities.api.capability.itemhandler.ItemMatch;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integratedcompat.IntegratedCompat;
import ruiseki.integratedcompat.network.packet.TerminalStorageIngredientItemStackCraftingGridSetRecipe;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentClient;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentItemStackCrafting;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;
import ruiseki.jfmuy.api.gui.IGuiIngredient;
import ruiseki.jfmuy.api.gui.IRecipeLayout;
import ruiseki.jfmuy.api.recipe.VanillaRecipeCategoryUid;
import ruiseki.jfmuy.api.recipe.transfer.IRecipeTransferError;
import ruiseki.jfmuy.api.recipe.transfer.IRecipeTransferHandler;
import ruiseki.jfmuy.api.recipe.transfer.IRecipeTransferHandlerHelper;
import ruiseki.jfmuy.util.Translator;
import ruiseki.okcore.ingredient.collection.IIngredientCollectionMutable;
import ruiseki.okcore.ingredient.collection.IngredientCollectionPrototypeMap;

/**
 * Handles recipe clicking from JEI.
 *
 * @author rubensworks
 */
public class TerminalStorageRecipeTransferHandler implements IRecipeTransferHandler<ContainerTerminalStorageBase> {

    private final IRecipeTransferHandlerHelper recipeTransferHandlerHelper;

    public TerminalStorageRecipeTransferHandler(IRecipeTransferHandlerHelper recipeTransferHandlerHelper) {
        this.recipeTransferHandlerHelper = recipeTransferHandlerHelper;
    }

    @Override
    public Class<ContainerTerminalStorageBase> getContainerClass() {
        return ContainerTerminalStorageBase.class;
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(ContainerTerminalStorageBase container, IRecipeLayout recipeLayout,
        EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
        if (!recipeLayout.getRecipeCategory()
            .getUid()
            .equals(VanillaRecipeCategoryUid.CRAFTING)) {
            return new TransferError();
        }

        if (Objects.equals(
            container.getSelectedTab(),
            TerminalStorageTabIngredientComponentItemStackCrafting.NAME.toString())) {
            if (!doTransfer) {
                // Check in the local client view if the required recipe ingredients are available
                TerminalStorageTabIngredientComponentClient tabClient = (TerminalStorageTabIngredientComponentClient) container
                    .getTabClient(container.getSelectedTab());
                List<TerminalStorageTabIngredientComponentClient.InstanceWithMetadata<ItemStack>> unfilteredIngredients = tabClient
                    .getUnfilteredIngredientsView(container.getSelectedChannel());
                IIngredientCollectionMutable<ItemStack, Integer> hayStack = new IngredientCollectionPrototypeMap<>(
                    IngredientComponent.ITEMSTACK);
                hayStack.addAll(
                    unfilteredIngredients.stream()
                        .filter(i -> i.getCraftingOption() == null)
                        .map(TerminalStorageTabIngredientComponentClient.InstanceWithMetadata::getInstance)
                        .collect(Collectors.toList()));
                List<Integer> slotsMissingItems = Lists.newArrayList();

                for (Map.Entry<Integer, ? extends IGuiIngredient<ItemStack>> entry : recipeLayout.getItemStacks()
                    .getGuiIngredients()
                    .entrySet()) {
                    IGuiIngredient<ItemStack> ingredient = entry.getValue();
                    if (ingredient != null && ingredient.isInput()) {
                        int slot = entry.getKey();
                        if (!ingredient.getAllIngredients()
                            .isEmpty()) {
                            boolean found = false;
                            for (ItemStack itemStack : ingredient.getAllIngredients()) {
                                if (hayStack.contains(itemStack, ItemMatch.ITEM | ItemMatch.DAMAGE | ItemMatch.NBT)) {
                                    hayStack.remove(itemStack);
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                slotsMissingItems.add(slot);
                            }

                        }
                    }
                }

                if (!slotsMissingItems.isEmpty()) {
                    String message = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.missing");
                    return recipeTransferHandlerHelper.createUserErrorForSlots(message, slotsMissingItems);
                }

                return null;
            } else {
                // Send a packet to the server if the recipe effectively needs to be applied to the grid
                Map<Integer, List<ItemStack>> slottedIngredients = Maps.newHashMap();
                for (Map.Entry<Integer, ? extends IGuiIngredient<ItemStack>> entry : recipeLayout.getItemStacks()
                    .getGuiIngredients()
                    .entrySet()) {
                    IGuiIngredient<ItemStack> ingredient = entry.getValue();
                    if (ingredient != null && ingredient.isInput()) {
                        int slot = entry.getKey();

                        slottedIngredients.put(slot, ingredient.getAllIngredients());
                    }
                }

                IntegratedCompat._instance.getPacketHandler()
                    .sendToServer(
                        new TerminalStorageIngredientItemStackCraftingGridSetRecipe(
                            container.getSelectedTab(),
                            container.getSelectedChannel(),
                            maxTransfer,
                            slottedIngredients));
                return null;
            }
        }

        return new TransferError();
    }

    public static class TransferError implements IRecipeTransferError {

        @Override
        public Type getType() {
            return Type.INTERNAL;
        }

        @Override
        public void showError(Minecraft minecraft, int mouseX, int mouseY, IRecipeLayout recipeLayout, int recipeX,
            int recipeY) {
            // Silently fail
        }
    }
}
