package ruiseki.integratedcompat.modcompat.jjfmuy;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.integratedcompat.modcompat.jjfmuy.dryingbasin.DryingBasinRecipeCategory;
import ruiseki.integratedcompat.modcompat.jjfmuy.logicprogrammer.LogicProgrammerTransferHandler;
import ruiseki.integratedcompat.modcompat.jjfmuy.mechanicaldryingbasin.MechanicalDryingBasinRecipeCategory;
import ruiseki.integratedcompat.modcompat.jjfmuy.mechanicalsqueezer.MechanicalSqueezerRecipeCategory;
import ruiseki.integratedcompat.modcompat.jjfmuy.squeezer.SqueezerRecipeCategory;
import ruiseki.integratedcompat.modcompat.jjfmuy.terminalstorage.TerminalStorageAdvancedGuiHandler;
import ruiseki.integratedcompat.modcompat.jjfmuy.terminalstorage.TerminalStorageRecipeTransferHandler;
import ruiseki.integratedcompat.modcompat.jjfmuy.terminalstorage.button.TerminalButtonItemStackCraftingGridSearchSync;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.block.BlockDryingBasinConfig;
import ruiseki.integrateddynamics.block.BlockLogicProgrammerConfig;
import ruiseki.integrateddynamics.block.BlockMechanicalDryingBasinConfig;
import ruiseki.integrateddynamics.block.BlockMechanicalSqueezerConfig;
import ruiseki.integrateddynamics.block.BlockSqueezerConfig;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammerBase;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammer;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerPortable;
import ruiseki.integrateddynamics.item.ItemPortableLogicProgrammerConfig;
import ruiseki.integratedterminals.api.terminalstorage.event.TerminalStorageTabClientLoadButtonsEvent;
import ruiseki.integratedterminals.api.terminalstorage.event.TerminalStorageTabClientSearchFieldUpdateEvent;
import ruiseki.integratedterminals.client.gui.image.Images;
import ruiseki.integratedterminals.core.client.gui.GuiTerminalStorage;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageItem;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStoragePart;
import ruiseki.integratedterminals.part.TerminalPartTypes;
import ruiseki.jfmuy.api.IJFMUYRuntime;
import ruiseki.jfmuy.api.IModPlugin;
import ruiseki.jfmuy.api.IModRegistry;
import ruiseki.jfmuy.api.JFMUYPlugin;
import ruiseki.jfmuy.api.recipe.IRecipeCategoryRegistration;
import ruiseki.jfmuy.api.recipe.VanillaRecipeCategoryUid;
import ruiseki.okcore.client.gui.component.input.GuiTextFieldExtended;
import ruiseki.okcore.event.input.KeyboardInputEvent;
import ruiseki.okcore.event.input.MouseInputEvent;

@JFMUYPlugin
public class JFMUYIDsConfig implements IModPlugin {

    private IJFMUYRuntime jfmuyRuntime;

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        if (!JFMUYModCompat.canBeUsed) return;
        if (IntegratedDynamics._instance.getConfigHandler()
            .isConfigEnabled(BlockDryingBasinConfig.class)) {
            DryingBasinRecipeCategory.register(registry);
        }
        if (IntegratedDynamics._instance.getConfigHandler()
            .isConfigEnabled(BlockSqueezerConfig.class)) {
            SqueezerRecipeCategory.register(registry);
        }
        if (IntegratedDynamics._instance.getConfigHandler()
            .isConfigEnabled(BlockMechanicalDryingBasinConfig.class)) {
            MechanicalDryingBasinRecipeCategory.register(registry);
        }
        if (IntegratedDynamics._instance.getConfigHandler()
            .isConfigEnabled(BlockMechanicalSqueezerConfig.class)) {
            MechanicalSqueezerRecipeCategory.register(registry);
        }
    }

    @Override
    public void register(IModRegistry registry) {
        if (!JFMUYModCompat.canBeUsed) return;
        registry.addGhostIngredientHandler(GuiLogicProgrammerBase.class, new LPGhostIngredientHandler<>());
        if (IntegratedDynamics._instance.getConfigHandler()
            .isConfigEnabled(BlockDryingBasinConfig.class)) {
            DryingBasinRecipeCategory.initialize(registry);
        }
        if (IntegratedDynamics._instance.getConfigHandler()
            .isConfigEnabled(BlockSqueezerConfig.class)) {
            SqueezerRecipeCategory.initialize(registry);
        }
        if (IntegratedDynamics._instance.getConfigHandler()
            .isConfigEnabled(BlockMechanicalDryingBasinConfig.class)) {
            MechanicalDryingBasinRecipeCategory.initialize(registry);
        }
        if (IntegratedDynamics._instance.getConfigHandler()
            .isConfigEnabled(BlockMechanicalSqueezerConfig.class)) {
            MechanicalSqueezerRecipeCategory.initialize(registry);
        }

        if (IntegratedDynamics._instance.getConfigHandler()
            .isConfigEnabled(BlockLogicProgrammerConfig.class)) {
            registry.getRecipeTransferRegistry()
                .addUniversalRecipeTransferHandler(
                    new LogicProgrammerTransferHandler<>(ContainerLogicProgrammer.class));
        }
        if (IntegratedDynamics._instance.getConfigHandler()
            .isConfigEnabled(ItemPortableLogicProgrammerConfig.class)) {
            registry.getRecipeTransferRegistry()
                .addUniversalRecipeTransferHandler(
                    new LogicProgrammerTransferHandler<>(ContainerLogicProgrammerPortable.class));
        }

        registry.getRecipeTransferRegistry()
            .addRecipeTransferHandler(
                new TerminalStorageRecipeTransferHandler<>(
                    ContainerTerminalStorageItem.class,
                    registry.getJFMUYHelpers()
                        .recipeTransferHandlerHelper()),
                VanillaRecipeCategoryUid.CRAFTING);
        registry.getRecipeTransferRegistry()
            .addRecipeTransferHandler(
                new TerminalStorageRecipeTransferHandler<>(
                    ContainerTerminalStoragePart.class,
                    registry.getJFMUYHelpers()
                        .recipeTransferHandlerHelper()),
                VanillaRecipeCategoryUid.CRAFTING);
        registry.addAdvancedGuiHandlers(new TerminalStorageAdvancedGuiHandler());
        registry.addRecipeCatalyst(
            new ItemStack(TerminalPartTypes.TERMINAL_STORAGE.getItem()),
            VanillaRecipeCategoryUid.CRAFTING);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onRuntimeAvailable(IJFMUYRuntime jfmuyRuntime) {
        this.jfmuyRuntime = jfmuyRuntime;
    }

    @SubscribeEvent
    public void onTerminalStorageButtons(TerminalStorageTabClientLoadButtonsEvent event) {
        if (jfmuyRuntime != null && !event.getButtons()
            .stream()
            .anyMatch((button) -> button instanceof TerminalButtonItemStackCraftingGridSearchSync)) {
            event.getButtons()
                .add(
                    new TerminalButtonItemStackCraftingGridSearchSync(
                        "jei",
                        event.getContainer()
                            .getGuiState(),
                        event.getClientTab(),
                        Images.BUTTON_MIDDLE_JEI_SYNC));
        }
    }

    @SubscribeEvent
    public void onSearchFieldUpdated(TerminalStorageTabClientSearchFieldUpdateEvent event) {
        // Copy the terminal search box contents into the JEI search box.
        if (jfmuyRuntime != null
            && TerminalButtonItemStackCraftingGridSearchSync.isSearchSynced(event.getClientTab())) {
            jfmuyRuntime.getIngredientFilter()
                .setFilterText(event.getSearchString() + "");
        }
    }

    protected void syncJeiSearchBox(GuiTerminalStorage<?, ?> screen) {
        // Copy the JEI search box contents into the terminal search box.
        if (jfmuyRuntime != null && jfmuyRuntime.getIngredientListOverlay()
            .hasKeyboardFocus()) {
            screen.getSelectedClientTab()
                .ifPresent(tab -> {
                    if (TerminalButtonItemStackCraftingGridSearchSync.isSearchSynced(tab)) {
                        GuiTextFieldExtended fieldSearch = screen.getFieldSearch();
                        fieldSearch.setText(
                            jfmuyRuntime.getIngredientFilter()
                                .getFilterText());
                        tab.setInstanceFilter(
                            screen.getContainer()
                                .getSelectedChannel(),
                            fieldSearch.getText() + "");
                    }
                });
        }
    }

    @SubscribeEvent
    public void onKeyTyped(KeyboardInputEvent.Post event) {
        if (event.gui instanceof GuiTerminalStorage<?, ?>screen) {
            syncJeiSearchBox(screen);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMouseClicked(MouseInputEvent.Pre event) {
        if (event.gui instanceof GuiTerminalStorage<?, ?>screen) {
            Minecraft.getMinecraft()
                .func_152344_a(() -> syncJeiSearchBox(screen));
        }
    }
}
