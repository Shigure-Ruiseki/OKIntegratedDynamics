package ruiseki.integratedterminals.proxy.guiprovider;

import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.core.client.gui.ExtendedGuiHandler;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.inventory.IGuiContainerProvider;

/**
 * @author rubensworks
 */
public class GuiProviders {

    public static int ID_GUI_TERMINAL_STORAGE;
    public static IGuiContainerProvider GUI_TERMINAL_STORAGE;
    public static int ID_GUI_TERMINAL_STORAGE_CRAFTNG_OPTION_AMOUNT;
    public static IGuiContainerProvider GUI_TERMINAL_STORAGE_CRAFTNG_OPTION_AMOUNT;
    public static int ID_GUI_TERMINAL_STORAGE_CRAFTNG_PLAN_PART;
    public static IGuiContainerProvider GUI_TERMINAL_STORAGE_CRAFTNG_PLAN_PART;
    public static int ID_GUI_TERMINAL_CRAFTING_JOBS_PLAN;
    public static IGuiContainerProvider GUI_TERMINAL_CRAFTING_JOBS_PLAN;
    /**
     * This is a variant of the default terminal storage gui constructor (which is register by ID).
     * This alternative allows additional init data to be passed to the constructor.
     */
    public static int ID_GUI_TERMINAL_STORAGE_INIT;

    public static void register() {
        IntegratedTerminals._instance.getGuiHandler()
            .registerGUI(
                GUI_TERMINAL_STORAGE = new GuiProviderTerminalStorageCraftingOptionAmountPart(
                    ID_GUI_TERMINAL_STORAGE = Helpers.getNewId(IntegratedTerminals._instance, Helpers.IDType.GUI),
                    IntegratedTerminals._instance),
                ExtendedGuiHandler.TERMINAL_STORAGE);
        IntegratedTerminals._instance.getGuiHandler()
            .registerGUI(
                GUI_TERMINAL_STORAGE_CRAFTNG_OPTION_AMOUNT = new GuiProviderTerminalStorageCraftingOptionAmountPart(
                    ID_GUI_TERMINAL_STORAGE_CRAFTNG_OPTION_AMOUNT = Helpers
                        .getNewId(IntegratedTerminals._instance, Helpers.IDType.GUI),
                    IntegratedTerminals._instance),
                ExtendedGuiHandler.CRAFTING_OPTION);

        IntegratedTerminals._instance.getGuiHandler()
            .registerGUI(
                GUI_TERMINAL_STORAGE_CRAFTNG_PLAN_PART = new GuiProviderTerminalStorageCraftingPlanPart(
                    ID_GUI_TERMINAL_STORAGE_CRAFTNG_PLAN_PART = Helpers
                        .getNewId(IntegratedTerminals._instance, Helpers.IDType.GUI),
                    IntegratedTerminals._instance),
                ExtendedGuiHandler.CRAFTING_OPTION);

        IntegratedTerminals._instance.getGuiHandler()
            .registerGUI(
                new GuiProviderTerminalStorageInit(
                    ID_GUI_TERMINAL_STORAGE_INIT = Helpers.getNewId(IntegratedTerminals._instance, Helpers.IDType.GUI),
                    IntegratedTerminals._instance),
                ExtendedGuiHandler.TERMINAL_STORAGE);

        IntegratedTerminals._instance.getGuiHandler()
            .registerGUI(
                GUI_TERMINAL_CRAFTING_JOBS_PLAN = new GuiProviderTerminalCraftingJobsPlan(
                    ID_GUI_TERMINAL_CRAFTING_JOBS_PLAN = Helpers
                        .getNewId(IntegratedTerminals._instance, Helpers.IDType.GUI),
                    IntegratedTerminals._instance),
                ExtendedGuiHandler.CRAFTING_PLAN);
    }

}
