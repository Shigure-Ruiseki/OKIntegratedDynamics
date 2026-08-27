package ruiseki.integratedterminals.capability.ingredient;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.integratedterminals.GeneralConfig;
import ruiseki.integratedterminals.api.ingredient.IIngredientComponentTerminalStorageHandler;
import ruiseki.integratedterminals.api.ingredient.IIngredientInstanceSorter;
import ruiseki.integratedterminals.capability.ingredient.sorter.FluidStackIdSorter;
import ruiseki.integratedterminals.capability.ingredient.sorter.FluidStackNameSorter;
import ruiseki.integratedterminals.capability.ingredient.sorter.FluidStackQuantitySorter;
import ruiseki.integratedterminals.core.client.gui.GuiTerminalStorage;
import ruiseki.integratedterminals.core.terminalstorage.query.SearchMode;
import ruiseki.okcore.client.gui.RenderItemExtendedSlotCount;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.fluid.FluidHelpers;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandlerItem;
import ruiseki.okcore.fluid.handler.IFluidTankProperties;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.GuiHelpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.ingredient.storage.InconsistentIngredientInsertionException;
import ruiseki.okcore.ingredient.storage.IngredientStorageHelpers;

/**
 * Terminal storage handler for fluids.
 *
 * @author rubensworks
 */
public class IngredientComponentTerminalStorageHandlerFluidStack
    implements IIngredientComponentTerminalStorageHandler<FluidStack, Integer> {

    private final IngredientComponent<FluidStack, Integer> ingredientComponent;

    public IngredientComponentTerminalStorageHandlerFluidStack(
        IngredientComponent<FluidStack, Integer> ingredientComponent) {
        this.ingredientComponent = ingredientComponent;
    }

    @Override
    public IngredientComponent<FluidStack, Integer> getComponent() {
        return ingredientComponent;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.water_bucket);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawInstance(FluidStack instance, long maxQuantity, @Nullable String label, GuiContainer gui,
        GuiTerminalStorage.DrawLayer layer, float partialTick, int x, int y, int mouseX, int mouseY,
        @Nullable List<String> additionalTooltipLines) {
        if (instance != null) {
            if (layer == GuiTerminalStorage.DrawLayer.BACKGROUND) {
                // Draw fluid
                GuiHelpers.renderFluidSlot(gui, instance, x, y);

                // Draw amount
                RenderItemExtendedSlotCount.drawSlotText(
                    Minecraft.getMinecraft().fontRenderer,
                    label != null ? label : GuiHelpers.quantityToScaledString(instance.amount),
                    x,
                    y);
                GlStateManager.disableLighting();
            } else {
                GuiHelpers.renderTooltip(
                    gui,
                    x,
                    y,
                    GuiHelpers.SLOT_SIZE_INNER,
                    GuiHelpers.SLOT_SIZE_INNER,
                    mouseX,
                    mouseY,
                    () -> {
                        List<String> lines = Lists.newArrayList();
                        lines.add(
                            instance.getFluid()
                                .getRarity().rarityColor + instance.getLocalizedName());
                        addQuantityTooltip(lines, instance);
                        if (additionalTooltipLines != null) {
                            lines.addAll(additionalTooltipLines);
                        }
                        return lines;
                    });
            }
        }
    }

    @Override
    public String formatQuantity(FluidStack instance) {
        return LangHelpers.localize(
            "gui.integratedterminals.terminal_storage.tooltip.fluid.amount",
            String.format("%,d", FluidHelpers.getAmount(instance)));
    }

    @Override
    public boolean isInstance(ItemStack itemStack) {
        return CapabilityHelpers.getCapability(itemStack, CapabilityFluidHandler.FLUID_HANDLER_ITEM)
            .isPresent();
    }

    @Override
    public FluidStack getInstance(ItemStack itemStack) {
        IFluidHandlerItem fluidHandler = CapabilityHelpers
            .getCapability(itemStack, CapabilityFluidHandler.FLUID_HANDLER_ITEM)
            .getOrNull();
        if (fluidHandler != null) {
            IFluidTankProperties[] props = fluidHandler.getTankProperties();
            if (props.length > 0) {
                return props[0].getContents();
            }
        }
        return null;
    }

    @Override
    public long getMaxQuantity(ItemStack itemStack) {
        IFluidHandlerItem fluidHandler = CapabilityHelpers
            .getCapability(itemStack, CapabilityFluidHandler.FLUID_HANDLER_ITEM)
            .getOrNull();
        if (fluidHandler != null) {
            IFluidTankProperties[] props = fluidHandler.getTankProperties();
            if (props.length > 0) {
                return props[0].getCapacity();
            }
        }
        return 0;
    }

    @Override
    public int getInitialInstanceMovementQuantity() {
        return GeneralConfig.guiStorageFluidInitialQuantity;
    }

    @Override
    public int getIncrementalInstanceMovementQuantity() {
        return GeneralConfig.guiStorageFluidIncrementalQuantity;
    }

    @Override
    public int throwIntoWorld(IIngredientComponentStorage<FluidStack, Integer> storage, FluidStack maxInstance,
        EntityPlayer player) {
        return 0; // Dropping fluids in the world is not supported
    }

    @Override
    public FluidStack insertIntoContainer(IIngredientComponentStorage<FluidStack, Integer> storage, Container container,
        int containerSlot, FluidStack maxInstance, @Nullable EntityPlayer player, boolean transferFullSelection) {
        ItemStack stack = container.getSlot(containerSlot)
            .getStack();
        IFluidHandlerItem fluidHandler = CapabilityHelpers
            .getCapability(stack, CapabilityFluidHandler.FLUID_HANDLER_ITEM)
            .getOrNull();
        if (fluidHandler != null) {
            IIngredientComponentStorage<FluidStack, Integer> itemStorage = getFluidStorage(
                storage.getComponent(),
                fluidHandler);
            FluidStack moved = null;
            try {
                moved = IngredientStorageHelpers.moveIngredientsIterative(
                    storage,
                    itemStorage,
                    maxInstance,
                    ingredientComponent.getMatcher()
                        .getExactMatchNoQuantityCondition(),
                    false);
            } catch (InconsistentIngredientInsertionException e) {
                // Ignore
            }

            container.getSlot(containerSlot)
                .putStack(fluidHandler.getContainer());
            container.detectAndSendChanges();
            return moved;
        }
        return null;
    }

    protected IIngredientComponentStorage<FluidStack, Integer> getFluidStorage(
        IngredientComponent<FluidStack, Integer> component, IFluidHandlerItem fluidHandler) {
        return component.getStorageWrapperHandler(CapabilityFluidHandler.FLUID_HANDLER_ITEM)
            .wrapComponentStorage(fluidHandler);
    }

    @Override
    public void extractActiveStackFromPlayerInventory(IIngredientComponentStorage<FluidStack, Integer> storage,
        InventoryPlayer playerInventory, long moveQuantityPlayerSlot) {
        ItemStack playerStack = playerInventory.getItemStack();
        IFluidHandlerItem fluidHandler = CapabilityHelpers
            .getCapability(playerStack, CapabilityFluidHandler.FLUID_HANDLER_ITEM)
            .getOrNull();
        if (fluidHandler != null) {
            IIngredientComponentStorage<FluidStack, Integer> itemStorage = getFluidStorage(
                storage.getComponent(),
                fluidHandler);
            try {
                IngredientStorageHelpers.moveIngredientsIterative(itemStorage, storage, moveQuantityPlayerSlot, false);
            } catch (InconsistentIngredientInsertionException e) {
                // Ignore
            }

            playerInventory.setItemStack(fluidHandler.getContainer());
        }
    }

    @Override
    public void extractMaxFromContainerSlot(IIngredientComponentStorage<FluidStack, Integer> storage,
        Container container, int containerSlot, InventoryPlayer playerInventory, int limit) {
        Slot slot = container.getSlot(containerSlot);
        if (slot.canTakeStack(playerInventory.player)) {
            ItemStack toMoveStack = slot.getStack();
            CapabilityHelpers.getCapability(toMoveStack, CapabilityFluidHandler.FLUID_HANDLER_ITEM)
                .ifPresent(fluidHandler -> {
                    IIngredientComponentStorage<FluidStack, Integer> itemStorage = getFluidStorage(
                        storage.getComponent(),
                        fluidHandler);
                    try {
                        IngredientStorageHelpers.moveIngredientsIterative(
                            itemStorage,
                            storage,
                            limit == -1 ? Long.MAX_VALUE : limit,
                            false);
                    } catch (InconsistentIngredientInsertionException e) {
                        // Ignore
                    }

                    container.getSlot(containerSlot)
                        .putStack(fluidHandler.getContainer());
                    container.detectAndSendChanges();
                });
        }
    }

    @Override
    public long getActivePlayerStackQuantity(InventoryPlayer playerInventory) {
        ItemStack toMoveStack = playerInventory.getItemStack();
        IFluidHandlerItem fluidHandler = CapabilityHelpers
            .getCapability(toMoveStack, CapabilityFluidHandler.FLUID_HANDLER_ITEM)
            .getOrNull();
        if (fluidHandler != null) {
            IFluidTankProperties[] props = fluidHandler.getTankProperties();
            if (props.length > 0) {
                return FluidHelpers.getAmount(props[0].getContents());
            }
        }
        return 0;
    }

    @Override
    public void drainActivePlayerStackQuantity(InventoryPlayer playerInventory, long quantity) {
        ItemStack toMoveStack = playerInventory.getItemStack();
        IFluidHandlerItem fluidHandler = CapabilityHelpers
            .getCapability(toMoveStack, CapabilityFluidHandler.FLUID_HANDLER_ITEM)
            .getOrNull();
        if (fluidHandler != null) {
            while (quantity > 0) {
                int drained = FluidHelpers.getAmount(fluidHandler.drain((int) quantity, true));
                if (drained <= 0) {
                    break;
                }
                quantity -= drained;
            }
            playerInventory.setItemStack(fluidHandler.getContainer());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Predicate<FluidStack> getInstanceFilterPredicate(SearchMode searchMode, String query) {
        switch (searchMode) {
            case MOD:
                return i -> Optional.ofNullable(FluidHelpers.getModId(i.getFluid()))
                    .orElse("minecraft")
                    .toLowerCase(Locale.ENGLISH)
                    .matches(".*" + query + ".*");
            case TOOLTIP:
                return i -> false; // Fluids have no tooltip
            case DICT:
                return i -> false; // There is no fluid dictionary
            case DEFAULT:
                return i -> i != null && i.getLocalizedName()
                    .toLowerCase(Locale.ENGLISH)
                    .matches(".*" + query + ".*");
        }
        return null;
    }

    @Override
    public Collection<IIngredientInstanceSorter<FluidStack>> getInstanceSorters() {
        return Lists.newArrayList(new FluidStackNameSorter(), new FluidStackIdSorter(), new FluidStackQuantitySorter());
    }
}
