package ruiseki.integratedcompat.modcompat.jjfmuy.logicprogrammer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import ruiseki.integratedcompat.GeneralConfig;
import ruiseki.integratedcompat.IntegratedCompat;
import ruiseki.integratedcompat.network.packet.CPacketSetSlot;
import ruiseki.integratedcompat.network.packet.CPacketValueTypeRecipeLPElementSetRecipe;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElement;
import ruiseki.integrateddynamics.core.ingredient.ItemMatchProperties;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeRecipeLPElement;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import ruiseki.jfmuy.api.gui.IGuiIngredient;
import ruiseki.jfmuy.api.gui.IRecipeLayout;
import ruiseki.jfmuy.api.recipe.IFocus;
import ruiseki.jfmuy.api.recipe.transfer.IRecipeTransferError;
import ruiseki.jfmuy.api.recipe.transfer.IRecipeTransferHandler;
import ruiseki.jfmuy.gui.TooltipRenderer;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandlerItem;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.TagHelpers;
import ruiseki.okcore.tag.TagEntry;
import ruiseki.okcore.tag.TagKey;
import ruiseki.okcore.tag.TagManager;

/**
 * Allows recipe transferring to Logic Programmer elements with slots.
 *
 * @author rubensworks
 */
public class LogicProgrammerTransferHandler<T extends ContainerLogicProgrammerBase>
    implements IRecipeTransferHandler<T> {

    private final Class<T> clazz;

    public LogicProgrammerTransferHandler(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Class<T> getContainerClass() {
        return clazz;
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(T container, IRecipeLayout recipeLayout, EntityPlayer player,
        boolean maxTransfer, boolean doTransfer) {
        ILogicProgrammerElement element = container.getActiveElement();

        if (element != null) {
            if (element instanceof ValueTypeRecipeLPElement) {
                return handleRecipeElement((ValueTypeRecipeLPElement) element, container, recipeLayout, doTransfer);
            } else {
                return handleDefaultElement(element, container, recipeLayout, doTransfer);
            }
        }

        return null;
    }

    @Nullable
    protected ResourceLocation getHeuristicItemsTag(IGuiIngredient<ItemStack> jeiIngredient) {
        // Allow disabling this heuristic
        if (!GeneralConfig.jeiHeuristicTags) {
            return null;
        }

        List<ItemStack> ingredients = jeiIngredient.getAllIngredients();
        if (ingredients == null || ingredients.size() <= 1) {
            return null;
        }

        ItemStack firstStack = ingredients.getFirst();
        Set<TagKey<Item>> candidateTags = TagHelpers.getTags(firstStack);

        if (candidateTags.isEmpty()) {
            return null;
        }

        TagManager tagManager = TagManager.getManager();

        for (TagKey<Item> tagKey : candidateTags) {
            Set<TagEntry> tagEntries = tagManager.getEntries(tagKey);
            if (tagEntries.size() != ingredients.size()) {
                continue;
            }

            boolean match = ingredients.stream()
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

    protected IRecipeTransferError handleRecipeElement(ValueTypeRecipeLPElement element, T container,
        IRecipeLayout recipeLayout, boolean doTransfer) {
        List<ItemMatchProperties> itemInputs = Lists.newArrayList();
        for (int i = 0; i < 9; i++) {
            itemInputs.add(new ItemMatchProperties(null));
        }
        List<FluidStack> fluidInputs = Lists.newArrayList();
        List<ItemStack> itemOutputs = Lists.newArrayList();
        List<FluidStack> fluidOutputs = Lists.newArrayList();

        // Collect items
        for (Map.Entry<Integer, ? extends IGuiIngredient<ItemStack>> entry : recipeLayout.getItemStacks()
            .getGuiIngredients()
            .entrySet()) {

            int slotIndex = entry.getKey();
            ItemStack firstStack = Iterables.getFirst(
                entry.getValue().getAllIngredients(), null);

            ItemStack stack = (firstStack != null) ? firstStack.copy() : null;

            if (entry.getValue().isInput()) {
                int gridIndex = slotIndex - 1;

                if (gridIndex >= 0 && gridIndex < 9) {
                    if (stack == null) {
                        continue;
                    }

                    ResourceLocation heuristicTag = getHeuristicItemsTag(entry.getValue());
                    if (heuristicTag != null) {
                        itemInputs.set(gridIndex, new ItemMatchProperties(null, false, heuristicTag.toString(), 1));
                    } else {
                        itemInputs.set(gridIndex, new ItemMatchProperties(stack));
                    }
                }
            } else {
                if (stack != null) {
                    itemOutputs.add(stack);
                }
            }
        }

        // Collect fluids
        for (Map.Entry<Integer, ? extends IGuiIngredient<FluidStack>> entry : recipeLayout.getFluidStacks()
            .getGuiIngredients()
            .entrySet()) {
            FluidStack stack = Iterables.getFirst(
                entry.getValue()
                    .getAllIngredients(),
                null);
            if (entry.getValue()
                .isInput()) {
                fluidInputs.add(stack);
            } else {
                fluidOutputs.add(stack);
            }
        }

        if (!element.isValidForRecipeGrid(itemInputs, fluidInputs, itemOutputs, fluidOutputs)) {
            return new IRecipeTransferError() {

                @Override
                public Type getType() {
                    return Type.USER_FACING;
                }

                @Override
                public void showError(Minecraft minecraft, int mouseX, int mouseY, IRecipeLayout recipeLayout,
                    int recipeX, int recipeY) {
                    TooltipRenderer.drawHoveringText(
                        minecraft,
                        Collections.singletonList(
                            LangHelpers.localize("error.jei.integrateddynamics.recipetransfer.recipe.toobig.desc")),
                        mouseX,
                        mouseY,
                        minecraft.fontRenderer);
                }
            };
        }

        if (doTransfer) {
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

        return null;
    }

    protected IRecipeTransferError handleDefaultElement(ILogicProgrammerElement element, T container,
        IRecipeLayout recipeLayout, boolean doTransfer) {
        // Always work with ItemStacks
        ItemStack itemStack = null;
        IFocus<?> focus = recipeLayout.getFocus();
        if (focus != null) {
            Object focusElement = focus.getValue();
            if (focusElement instanceof ItemStack) {
                itemStack = (ItemStack) focusElement;
            } else if (focusElement instanceof FluidStack) {
                itemStack = new ItemStack(Items.bucket);
                IFluidHandlerItem fluidHandler = CapabilityHelpers
                    .getCapability(itemStack, CapabilityFluidHandler.FLUID_HANDLER_ITEM)
                    .orElseThrow(
                        () -> new IllegalStateException(
                            "Could not find a fluid handler on the bucket item, some mod must be messing with things."));
                fluidHandler.fill((FluidStack) focusElement, true);
                itemStack = fluidHandler.getContainer();
            }
        }
        if (itemStack != null) {
            if (element.isItemValidForSlot(0, itemStack)) {
                if (doTransfer) {
                    setStackInSlot(container, 0, itemStack);
                }
            } else {
                return new IRecipeTransferError() {

                    @Override
                    public Type getType() {
                        return Type.USER_FACING;
                    }

                    @Override
                    public void showError(Minecraft minecraft, int mouseX, int mouseY, IRecipeLayout recipeLayout,
                        int recipeX, int recipeY) {

                    }
                };
            }
        }
        return null;
    }

    protected void setStackInSlot(T container, int slot, ItemStack itemStack) {
        int slotId = container.inventorySlots.size() - 37 + slot; // Player inventory - 1
        container.putStackInSlot(slotId, itemStack.copy());
        IntegratedCompat._instance.getPacketHandler()
            .sendToServer(new CPacketSetSlot(container.windowId, slotId, itemStack));
    }

}
