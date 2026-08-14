package ruiseki.integratedterminals;

import net.minecraft.creativetab.CreativeTabs;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabRegistry;
import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalStorageTabIngredientCraftingHandlerRegistry;
import ruiseki.integratedterminals.capability.ingredient.IngredientComponentTerminalStorageHandlerConfig;
import ruiseki.integratedterminals.capability.ingredient.TerminalIngredientComponentCapabilities;
import ruiseki.integratedterminals.core.client.gui.ExtendedGuiHandler;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientCraftingHandlerRegistry;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabRegistry;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabs;
import ruiseki.integratedterminals.core.terminalstorage.crafting.TerminalStorageTabIngredientCraftingHandlers;
import ruiseki.integratedterminals.part.TerminalPartTypes;
import ruiseki.okcore.client.gui.GuiHandler;
import ruiseki.okcore.config.ConfigHandler;
import ruiseki.okcore.init.ModBaseVersionable;
import ruiseki.okcore.modcompat.ModCompatLoader;
import ruiseki.okcore.proxy.ICommonProxy;

/**
 * The main mod class of this mod.
 *
 * @author rubensworks (aka kroeserr)
 *
 */
@Mod(
    modid = Reference.MOD_ID,
    name = Reference.MOD_NAME,
    useMetadata = true,
    version = Reference.MOD_VERSION,
    dependencies = Reference.MOD_DEPENDENCIES,
    guiFactory = "ruiseki.integratedterminals.GuiConfigOverview$ExtendedConfigGuiFactory")
public class IntegratedTerminals extends ModBaseVersionable {

    /**
     * The proxy of this mod, depending on 'side' a different proxy will be inside this field.
     *
     * @see SidedProxy
     */
    @SidedProxy(
        clientSide = "ruiseki.integratedterminals.proxy.ClientProxy",
        serverSide = "ruiseki.integratedterminals.proxy.CommonProxy")
    public static ICommonProxy proxy;

    /**
     * The unique instance of this mod.
     */
    @Mod.Instance(value = Reference.MOD_ID)
    public static IntegratedTerminals _instance;

    public IntegratedTerminals() {
        super(Reference.MOD_ID, Reference.MOD_NAME, Reference.MOD_VERSION);
    }

    @Override
    protected GuiHandler constructGuiHandler() {
        return new ExtendedGuiHandler(this);
    }

    @Override
    protected void loadModCompats(ModCompatLoader modCompatLoader) {
        super.loadModCompats(modCompatLoader);

        // Mod compats
        // modCompatLoader.addModCompat(new IntegratedCraftingModCompat());
    }

    /**
     * The pre-initialization, will register required configs.
     *
     * @param event The Forge event required for this.
     */
    @Mod.EventHandler
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        getRegistryManager().addRegistry(ITerminalStorageTabRegistry.class, new TerminalStorageTabRegistry());
        getRegistryManager().addRegistry(
            ITerminalStorageTabIngredientCraftingHandlerRegistry.class,
            TerminalStorageTabIngredientCraftingHandlerRegistry.getInstance());

        TerminalPartTypes.load();
        TerminalIngredientComponentCapabilities.load();
        TerminalStorageTabs.load();
        TerminalStorageTabIngredientCraftingHandlers.load();
    }

    /**
     * Register the config dependent things like world generation and proxy handlers.
     *
     * @param event The Forge event required for this.
     */
    @Mod.EventHandler
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    /**
     * Register the event hooks.
     *
     * @param event The Forge event required for this.
     */
    @Mod.EventHandler
    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    /**
     * Register the things that are related to server starting, like commands.
     *
     * @param event The Forge event required for this.
     */
    @Mod.EventHandler
    @Override
    public void onServerStarting(FMLServerStartingEvent event) {
        super.onServerStarting(event);
    }

    /**
     * Register the things that are related to server starting.
     *
     * @param event The Forge event required for this.
     */
    @Mod.EventHandler
    @Override
    public void onServerStarted(FMLServerStartedEvent event) {
        super.onServerStarted(event);
    }

    /**
     * Register the things that are related to server stopping, like persistent storage.
     *
     * @param event The Forge event required for this.
     */
    @Mod.EventHandler
    @Override
    public void onServerStopping(FMLServerStoppingEvent event) {
        super.onServerStopping(event);
    }

    @Override
    public CreativeTabs constructDefaultCreativeTab() {
        return null;
    }

    @Override
    public void onGeneralConfigsRegister(ConfigHandler configHandler) {
        configHandler.add(new GeneralConfig());
    }

    @Override
    public void onMainConfigsRegister(ConfigHandler configHandler) {
        super.onMainConfigsRegister(configHandler);

        configHandler.add(new IngredientComponentTerminalStorageHandlerConfig());
    }

    @Override
    public ICommonProxy getProxy() {
        return proxy;
    }

    /**
     * Log a new info message for this mod.
     *
     * @param message The message to show.
     */
    public static void clog(String message) {
        clog(Level.INFO, message);
    }

    /**
     * Log a new message of the given level for this mod.
     *
     * @param level   The level in which the message must be shown.
     * @param message The message to show.
     */
    public static void clog(Level level, String message) {
        IntegratedTerminals._instance.getLoggerHelper()
            .log(level, message);
    }

    public static void clog(Level level, String message, Object... params) {
        IntegratedTerminals._instance.log(level, message, params);
    }
}
