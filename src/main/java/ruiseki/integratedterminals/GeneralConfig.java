package ruiseki.integratedterminals;

import ruiseki.okcore.config.ConfigurableProperty;
import ruiseki.okcore.config.ConfigurableType;
import ruiseki.okcore.config.ConfigurableTypeCategory;
import ruiseki.okcore.config.extendedconfig.DummyConfig;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.tracking.Versions;

/**
 * A config with general options for this mod.
 *
 * @author rubensworks
 *
 */
public class GeneralConfig extends DummyConfig {

    /**
     * The current mod version, will be used to check if the player's config isn't out of date and
     * warn the player accordingly.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.CORE,
        comment = "Config version for " + Reference.MOD_NAME + ".\nDO NOT EDIT MANUALLY!")
    public static String version = Reference.MOD_VERSION;

    /**
     * If the debug mode should be enabled. @see Debug
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.CORE,
        comment = "Set 'true' to enable development debug mode. This will result in a lower performance!",
        requiresMcRestart = true)
    public static boolean debug = false;

    /**
     * If the recipe loader should crash when finding invalid recipes.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.CORE,
        comment = "If the recipe loader should crash when finding invalid recipes.",
        requiresMcRestart = true)
    public static boolean crashOnInvalidRecipe = false;

    /**
     * If mod compatibility loader should crash hard if errors occur in that process.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.CORE,
        comment = "If mod compatibility loader should crash hard if errors occur in that process.",
        requiresMcRestart = true)
    public static boolean crashOnModCompatCrash = false;

    /**
     * If an anonymous mod startup analytics request may be sent to our analytics service.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.CORE,
        comment = "If an anonymous mod startup analytics request may be sent to our analytics service.")
    public static boolean analytics = true;

    /**
     * If the version checker should be enabled.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.CORE,
        comment = "If the version checker should be enabled.")
    public static boolean versionChecker = true;

    /**
     * The maximum number of terminal storage instances that can be sent in a single packet. Reduce this when you have
     * packet overflows.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.CORE,
        comment = "The maximum number of terminal storage instances that can be sent in a single packet. Reduce this when you have packet overflows.",
        isCommandable = true)
    public static int terminalStoragePacketMaxInstances = 512;
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.CORE,
        comment = "The maximum number of terminal storage crafting recipes that can be sent in a single packet. Reduce this when you have packet overflows.",
        isCommandable = true)
    public static int terminalStoragePacketMaxRecipes = 128;

    @ConfigurableProperty(
        category = ConfigurableTypeCategory.CORE,
        comment = "If crafting plans should default to the tree-based view. If false, it will default to the flattened view.",
        isCommandable = true)
    public static boolean terminalStorageDefaultToCraftingPlanTree = false;
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.CORE,
        comment = "The limit for the number of leaves in a tree-based crafting plan after which it won't be sent to the client anymore.",
        isCommandable = true)
    public static int terminalStorageMaxTreePlanSize = 64;

    /**
     * The number that should be selected when clicking on an item in the storage terminal.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.MACHINE,
        comment = "The number of items that should be selected when clicking on an item in the storage terminal.",
        isCommandable = true)
    public static int guiStorageItemInitialQuantity = 64;

    /**
     * The number that should be removed when right-clicking when an item is selected in the storage terminal.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.MACHINE,
        comment = "The number of items that should be removed when right-clicking when an item is selected in the storage terminal.",
        isCommandable = true)
    public static int guiStorageItemIncrementalQuantity = 1;

    /**
     * The number that should be selected when clicking on a fluid in the storage terminal.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.MACHINE,
        comment = "The number of items that should be selected when clicking on a fluid in the storage terminal.",
        isCommandable = true)
    public static int guiStorageFluidInitialQuantity = 100000;

    /**
     * The number that should be removed when right-clicking when a fluid is selected in the storage terminal.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.MACHINE,
        comment = "The number of items that should be removed when right-clicking when a fluid is selected in the storage terminal.",
        isCommandable = true)
    public static int guiStorageFluidIncrementalQuantity = 1000;

    /**
     * The number that should be selected when clicking on energy in the storage terminal.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.MACHINE,
        comment = "The number of items that should be selected when clicking on energy in the storage terminal.",
        isCommandable = true)
    public static int guiStorageEnergyInitialQuantity = 100000;

    /**
     * The number that should be removed when right-clicking when energy is selected in the storage terminal.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.MACHINE,
        comment = "The number of items that should be removed when right-clicking when energy is selected in the storage terminal.",
        isCommandable = true)
    public static int guiStorageEnergyIncrementalQuantity = 1000;

    /**
     * The update frequency in milliseconds for the crafting jobs gui.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.MACHINE,
        comment = "The update frequency in milliseconds for the crafting jobs gui.",
        isCommandable = true)
    public static int guiTerminalCraftingJobsUpdateFrequency = 1000;

    /**
     * The number of threads that the crafting plan calculator can use.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.CORE,
        comment = "The number of threads that the crafting plan calculator can use.",
        minimalValue = 1,
        requiresMcRestart = true)
    public static int craftingPlannerThreads = 2;

    /**
     * If the crafting planners can work on separate thread.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.CORE,
        comment = "If the crafting planners can work on separate thread.",
        isCommandable = true)
    public static boolean craftingPlannerEnableMultithreading = true;

    @ConfigurableProperty(
        category = ConfigurableTypeCategory.CORE,
        comment = "If client-directed packets should be serialized in a separate thread.",
        isCommandable = true)
    public static boolean packetSerializationEnableMultithreading = true;

    /**
     * The base energy usage for the crafting terminal.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.GENERAL,
        comment = "The base energy usage for the crafting terminal.",
        minimalValue = 0)
    public static int terminalCraftingBaseConsumption = 1;

    /**
     * The base energy usage for the storage terminal.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.GENERAL,
        comment = "The base energy usage for the storage terminal.",
        minimalValue = 0)
    public static int terminalStorageBaseConsumption = 2;

    @ConfigurableProperty(
        category = ConfigurableTypeCategory.GENERAL,
        comment = "If the search box and button states should be synchronized between the item storage and crafting tabs.")
    public static boolean syncItemStorageAndCraftingTabStates = true;

    @ConfigurableProperty(
        category = ConfigurableTypeCategory.GENERAL,
        comment = "If shift-clicking on the crafting terminal's crafting result slot should only produce a single result.")
    public static boolean shiftClickCraftingResultLimit = false;

    @ConfigurableProperty(
        category = ConfigurableTypeCategory.GENERAL,
        comment = "The number of rows in the small scale of the storage terminal.")
    public static int guiStorageScaleSmallRows = 5;
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.GENERAL,
        comment = "The number of columns in the small scale of the storage terminal.")
    public static int guiStorageScaleSmallColumns = 9;
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.GENERAL,
        comment = "The number of rows in the medium scale of the storage terminal.")
    public static int guiStorageScaleMediumRows = 7;
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.GENERAL,
        comment = "The number of columns in the medium scale of the storage terminal.")
    public static int guiStorageScaleMediumColumns = 10;
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.GENERAL,
        comment = "The number of rows in the large scale of the storage terminal.")
    public static int guiStorageScaleLargeRows = 9;
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.GENERAL,
        comment = "The number of columns in the large scale of the storage terminal.")
    public static int guiStorageScaleLargeColumns = 11;
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.GENERAL,
        comment = "The number of columns in the height-based scale of the storage terminal.")
    public static int guiStorageScaleHeightColumns = 9;
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.GENERAL,
        comment = "The number of rows in the width-based scale of the storage terminal.")
    public static int guiStorageScaleWidthRows = 5;
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.GENERAL,
        comment = "The maximum number of rows in when scaling the storage terminal.")
    public static int guiStorageScaleMaxRows = 20;
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.GENERAL,
        comment = "The maximum number of columns in when scaling the storage terminal.")
    public static int guiStorageScaleMaxColumns = 32;

    @ConfigurableProperty(
        category = ConfigurableTypeCategory.GENERAL,
        comment = "If the crafting grid should always be shown centrally, and not be responsive based on screen size.")
    public static boolean guiStorageForceCraftingGridCenter = false;

    @ConfigurableProperty(
        category = ConfigurableTypeCategory.GENERAL,
        comment = "If the automatic re-sorting of the storage terminal contents should be paused while the shift key is held down.",
        isCommandable = true)
    public static boolean guiStoragePauseSortingWhileShifting = true;

    /**
     * The type of this config.
     */
    public static ConfigurableType TYPE = ConfigurableType.DUMMY;

    /**
     * Create a new instance.
     */
    public GeneralConfig() {
        super(IntegratedTerminals._instance, true, "general", null);
    }

    @Override
    public void onRegistered() {
        getMod().putGenericReference(ModBase.REFKEY_CRASH_ON_INVALID_RECIPE, GeneralConfig.crashOnInvalidRecipe);
        getMod().putGenericReference(ModBase.REFKEY_DEBUGCONFIG, GeneralConfig.debug);
        getMod().putGenericReference(ModBase.REFKEY_CRASH_ON_MODCOMPAT_CRASH, GeneralConfig.crashOnModCompatCrash);

        if (versionChecker) {
            Versions.registerMod(getMod(), IntegratedTerminals._instance, Reference.VERSION_URL);
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
