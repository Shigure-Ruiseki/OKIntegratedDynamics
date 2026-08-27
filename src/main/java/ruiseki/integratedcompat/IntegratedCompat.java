package ruiseki.integratedcompat;

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
import ruiseki.integratedcompat.modcompat.capabilities.worker.WorkerCoalGeneratorTileCompat;
import ruiseki.integratedcompat.modcompat.capabilities.worker.WorkerDryingBasinTileCompat;
import ruiseki.integratedcompat.modcompat.capabilities.worker.WorkerMechanicalMachineTileCompat;
import ruiseki.integratedcompat.modcompat.capabilities.worker.WorkerSqueezerTileCompat;
import ruiseki.integratedcompat.modcompat.jjfmuy.JFMUYModCompat;
import ruiseki.integratedcompat.modcompat.nei.NEIModCompat;
import ruiseki.integratedcompat.modcompat.waila.WailaModCompat;
import ruiseki.integrateddynamics.tileentity.TileCoalGenerator;
import ruiseki.integrateddynamics.tileentity.TileDryingBasin;
import ruiseki.integrateddynamics.tileentity.TileMechanicalDryingBasin;
import ruiseki.integrateddynamics.tileentity.TileMechanicalSqueezer;
import ruiseki.integrateddynamics.tileentity.TileSqueezer;
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
    guiFactory = Reference.GUI_FACTORY)
public class IntegratedCompat extends ModBaseVersionable {

    /**
     * The proxy of this mod, depending on 'side' a different proxy will be inside this field.
     *
     * @see SidedProxy
     */
    @SidedProxy(
        clientSide = "ruiseki.integratedcompat.proxy.ClientProxy",
        serverSide = "ruiseki.integratedcompat.proxy.CommonProxy")
    public static ICommonProxy proxy;

    /**
     * The unique instance of this mod.
     */
    @Mod.Instance(value = Reference.MOD_ID)
    public static IntegratedCompat _instance;

    public IntegratedCompat() {
        super(Reference.MOD_ID, Reference.MOD_NAME, Reference.MOD_VERSION);
    }

    @Override
    protected void loadModCompats(ModCompatLoader modCompatLoader) {
        super.loadModCompats(modCompatLoader);
        modCompatLoader.addModCompat(new WailaModCompat());
        // modCompatLoader.addModCompat(new ThaumcraftModCompat());
        modCompatLoader.addModCompat(new JFMUYModCompat());
        modCompatLoader.addModCompat(new NEIModCompat());
        // modCompatLoader.addModCompat(new TConstructModCompat());
        // modCompatLoader.addModCompat(new ForestryModCompat());
        // modCompatLoader.addModCompat(new Ic2ModCompat());
        // modCompatLoader.addModCompat(new TopModCompat());
        // modCompatLoader.addModCompat(new TeslaApiCompat());
        // modCompatLoader.addModCompat(new RefinedStorageModCompat());
        // modCompatLoader.addModCompat(new ImmersiveEngineeringModCompat());
        // modCompatLoader.addModCompat(new CraftTweakerModCompat());
        // modCompatLoader.addModCompat(new SignalsModCompat());
    }

    /**
     * The pre-initialization, will register required configs.
     *
     * @param event The Forge event required for this.
     */
    @Mod.EventHandler
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        // Capabilities
        getCapabilityConstructorRegistry().registerTile(TileDryingBasin.class, new WorkerDryingBasinTileCompat());
        getCapabilityConstructorRegistry().registerTile(TileSqueezer.class, new WorkerSqueezerTileCompat());
        getCapabilityConstructorRegistry().registerTile(TileCoalGenerator.class, new WorkerCoalGeneratorTileCompat());
        getCapabilityConstructorRegistry()
            .registerTile(TileMechanicalDryingBasin.class, new WorkerMechanicalMachineTileCompat<>());
        getCapabilityConstructorRegistry()
            .registerTile(TileMechanicalSqueezer.class, new WorkerMechanicalMachineTileCompat<>());

        super.preInit(event);
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
        IntegratedCompat._instance.getLoggerHelper()
            .log(level, message);
    }

    public static void clog(Level level, String message, Object... params) {
        IntegratedCompat._instance.log(level, message, params);
    }
}
