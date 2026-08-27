package ruiseki.integratedterminals.proxy.guiprovider;

import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.core.client.gui.ExtendedGuiHandler;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.inventory.IGuiContainerProvider;

/**
 * @author rubensworks
 */
public class GuiProviders {

    public static int ID_GUI_TERMINAL_STORAGE_CRAFTING_OPTION_AMOUNT_PART;
    public static IGuiContainerProvider GUI_TERMINAL_STORAGE_CRAFTING_OPTION_AMOUNT_PART;

    public static int ID_GUI_TERMINAL_STORAGE_CRAFTING_PLAN_PART;
    public static IGuiContainerProvider GUI_TERMINAL_STORAGE_CRAFTING_PLAN_PART;

    public static int ID_GUI_TERMINAL_CRAFTING_JOBS_PLAN_PART;
    public static IGuiContainerProvider GUI_TERMINAL_CRAFTING_JOBS_PLAN_PART;

    public static int ID_GUI_TERMINAL_STORAGE_CRAFTING_OPTION_AMOUNT_ITEM;
    public static IGuiContainerProvider GUI_TERMINAL_STORAGE_CRAFTING_OPTION_AMOUNT_ITEM;

    public static int ID_GUI_TERMINAL_STORAGE_CRAFTING_PLAN_ITEM;
    public static IGuiContainerProvider GUI_TERMINAL_STORAGE_CRAFTING_PLAN_ITEM;
    /**
     * This is a variant of the default terminal storage gui constructor (which is register by ID).
     * This alternative allows additional init data to be passed to the constructor.
     */
    public static int ID_GUI_TERMINAL_STORAGE_PART_INIT;
    public static int ID_GUI_TERMINAL_STORAGE_ITEM_INIT;

    public static void register() {
        IntegratedTerminals._instance.getGuiHandler()
            .registerGUI(
                GUI_TERMINAL_STORAGE_CRAFTING_OPTION_AMOUNT_PART = new GuiProviderTerminalStorageCraftingOptionAmountPart(
                    ID_GUI_TERMINAL_STORAGE_CRAFTING_OPTION_AMOUNT_PART = Helpers
                        .getNewId(IntegratedTerminals._instance, Helpers.IDType.GUI),
                    IntegratedTerminals._instance),
                ExtendedGuiHandler.CRAFTING_OPTION_PART);

        IntegratedTerminals._instance.getGuiHandler()
            .registerGUI(
                GUI_TERMINAL_STORAGE_CRAFTING_PLAN_PART = new GuiProviderTerminalStorageCraftingPlanPart(
                    ID_GUI_TERMINAL_STORAGE_CRAFTING_PLAN_PART = Helpers
                        .getNewId(IntegratedTerminals._instance, Helpers.IDType.GUI),
                    IntegratedTerminals._instance),
                ExtendedGuiHandler.CRAFTING_OPTION_PART);

        IntegratedTerminals._instance.getGuiHandler()
            .registerGUI(
                GUI_TERMINAL_CRAFTING_JOBS_PLAN_PART = new GuiProviderTerminalCraftingJobsPlan(
                    ID_GUI_TERMINAL_CRAFTING_JOBS_PLAN_PART = Helpers
                        .getNewId(IntegratedTerminals._instance, Helpers.IDType.GUI),
                    IntegratedTerminals._instance),
                ExtendedGuiHandler.CRAFTING_PLAN_PART);

        IntegratedTerminals._instance.getGuiHandler()
            .registerGUI(
                new GuiProviderTerminalStoragePartInit(
                    ID_GUI_TERMINAL_STORAGE_PART_INIT = Helpers
                        .getNewId(IntegratedTerminals._instance, Helpers.IDType.GUI),
                    IntegratedTerminals._instance),
                ExtendedGuiHandler.TERMINAL_STORAGE_PART);

        IntegratedTerminals._instance.getGuiHandler()
            .registerGUI(
                GUI_TERMINAL_STORAGE_CRAFTING_OPTION_AMOUNT_ITEM = new GuiProviderTerminalStorageCraftingOptionAmountItem(
                    ID_GUI_TERMINAL_STORAGE_CRAFTING_OPTION_AMOUNT_ITEM = Helpers
                        .getNewId(IntegratedTerminals._instance, Helpers.IDType.GUI),
                    IntegratedTerminals._instance),
                ExtendedGuiHandler.CRAFTING_OPTION_ITEM);

        IntegratedTerminals._instance.getGuiHandler()
            .registerGUI(
                GUI_TERMINAL_STORAGE_CRAFTING_PLAN_ITEM = new GuiProviderTerminalStorageCraftingPlanItem(
                    ID_GUI_TERMINAL_STORAGE_CRAFTING_PLAN_ITEM = Helpers
                        .getNewId(IntegratedTerminals._instance, Helpers.IDType.GUI),
                    IntegratedTerminals._instance),
                ExtendedGuiHandler.CRAFTING_OPTION_ITEM);

        IntegratedTerminals._instance.getGuiHandler()
            .registerGUI(
                new GuiProviderTerminalStorageItemInit(
                    ID_GUI_TERMINAL_STORAGE_ITEM_INIT = Helpers
                        .getNewId(IntegratedTerminals._instance, Helpers.IDType.GUI),
                    IntegratedTerminals._instance),
                ExtendedGuiHandler.TERMINAL_STORAGE_ITEM);
    }

}
