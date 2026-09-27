package ruiseki.integratedcompat.modcompat.nei.logicprogrammer;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.Lists;

import codechicken.nei.PositionedStack;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.recipe.IRecipeHandler;
import ruiseki.integratedcompat.GeneralConfig;
import ruiseki.integratedcompat.IntegratedCompat;
import ruiseki.integratedcompat.network.packet.CPacketSetSlot;
import ruiseki.integratedcompat.network.packet.CPacketValueTypeRecipeLPElementSetRecipe;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElement;
import ruiseki.integrateddynamics.core.ingredient.ItemMatchProperties;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeRecipeLPElement;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.TagHelpers;
import ruiseki.okcore.tag.TagEntry;
import ruiseki.okcore.tag.TagKey;
import ruiseki.okcore.tag.TagManager;

public class LogicProgrammerOverlayHandler implements IOverlayHandler {

    @Override
    public void overlayRecipe(GuiContainer firstGui, IRecipeHandler recipe, int recipeIndex, boolean maxTransfer) {
        if (!(firstGui.inventorySlots instanceof ContainerLogicProgrammerBase container)) {
            return;
        }

        ILogicProgrammerElement element = container.getActiveElement();
        if (element != null) {
            if (element instanceof ValueTypeRecipeLPElement) {
                handleRecipeElement((ValueTypeRecipeLPElement) element, container, recipe, recipeIndex);
            } else {
                handleDefaultElement(element, container, recipe, recipeIndex);
            }
        }
    }

    protected ResourceLocation getHeuristicItemsTag(PositionedStack positionedStack) {
        if (!GeneralConfig.jeiHeuristicTags) {
            return null;
        }

        ItemStack[] ingredients = positionedStack.items;
        if (ingredients == null || ingredients.length <= 1) {
            return null;
        }

        ItemStack firstStack = ingredients[0];
        Set<TagKey<Item>> candidateTags = TagHelpers.getTags(firstStack);

        if (candidateTags.isEmpty()) {
            return null;
        }

        TagManager tagManager = TagManager.getManager();

        for (TagKey<Item> tagKey : candidateTags) {
            Set<TagEntry> tagEntries = tagManager.getEntries(tagKey);
            if (tagEntries.size() != ingredients.length) {
                continue;
            }

            boolean match = Arrays.stream(ingredients)
                .allMatch(stack -> {
                    if (stack == null || stack.getItem() == null) return false;

                    ResourceLocation itemId = Helpers.getLocation(stack.getItem());
                    int meta = stack.getItemDamage();

                    for (TagEntry entry : tagEntries) {
                        if (entry.id()
                            .equals(itemId)) {
                            if (entry.meta() == TagEntry.WILDCARD || entry.meta() == meta) {
                                return true;
                            }
                        }
                    }
                    return false;
                });

            if (match) {
                return tagKey.location();
            }
        }

        return null;
    }

    protected void handleRecipeElement(ValueTypeRecipeLPElement element, ContainerLogicProgrammerBase container,
        IRecipeHandler recipe, int recipeIndex) {
        List<ItemMatchProperties> itemInputs = Lists.newArrayList();
        for (int i = 0; i < 9; i++) {
            itemInputs.add(new ItemMatchProperties(null));
        }

        List<FluidStack> fluidInputs = Lists.newArrayList();
        List<ItemStack> itemOutputs = Lists.newArrayList();
        List<FluidStack> fluidOutputs = Lists.newArrayList();

        List<PositionedStack> ingredients = recipe.getIngredientStacks(recipeIndex);
        for (int i = 0; i < ingredients.size(); i++) {
            PositionedStack stack = ingredients.get(i);
            if (stack != null && stack.items != null && stack.items.length > 0) {
                int col = (stack.relx - 25) / 18;
                int row = (stack.rely - 6) / 18;
                int gridIndex = -1;

                if (col >= 0 && col < 3 && row >= 0 && row < 3) {
                    gridIndex = row * 3 + col;
                } else if (i < 9) {
                    gridIndex = i;
                }

                if (gridIndex >= 0 && gridIndex < 9) {
                    ResourceLocation heuristicTag = getHeuristicItemsTag(stack);
                    if (heuristicTag != null) {
                        itemInputs.set(gridIndex, new ItemMatchProperties(null, false, heuristicTag.toString(), 1));
                    } else {
                        itemInputs.set(gridIndex, new ItemMatchProperties(stack.items[0].copy()));
                    }
                }
            }
        }

        PositionedStack result = recipe.getResultStack(recipeIndex);
        if (result != null && result.items != null && result.items.length > 0) {
            itemOutputs.add(result.items[0].copy());
        }

        if (!element.isValidForRecipeGrid(itemInputs, fluidInputs, itemOutputs, fluidOutputs)) {
            return;
        }

        element.setRecipeGrid(container, itemInputs, fluidInputs, itemOutputs, fluidOutputs);
        IntegratedCompat._instance.getPacketHandler()
            .sendToServer(
                new CPacketValueTypeRecipeLPElementSetRecipe(
                    container.windowId,
                    itemInputs,
                    fluidInputs,
                    itemOutputs,
                    fluidOutputs));
    }

    protected void handleDefaultElement(ILogicProgrammerElement element, ContainerLogicProgrammerBase container,
        IRecipeHandler recipe, int recipeIndex) {
        PositionedStack result = recipe.getResultStack(recipeIndex);
        if (result != null && result.items != null && result.items.length > 0) {
            ItemStack itemStack = result.items[0];
            if (element.isItemValidForSlot(0, itemStack)) {
                setStackInSlot(container, 0, itemStack);
            }
        }
    }

    protected void setStackInSlot(ContainerLogicProgrammerBase container, int slot, ItemStack itemStack) {
        int slotId = container.inventorySlots.size() - 37 + slot; // Player inventory offset - 1
        container.putStackInSlot(slotId, itemStack.copy());
        IntegratedCompat._instance.getPacketHandler()
            .sendToServer(new CPacketSetSlot(container.windowId, slotId, itemStack));
    }
}
