package ruiseki.integratedtunnels;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import ruiseki.integrateddynamics.core.part.PartTypes;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integratedtunnels.api.world.IBlockBreakHandlerRegistry;
import ruiseki.integratedtunnels.api.world.IBlockPlaceHandlerRegistry;
import ruiseki.integratedtunnels.capability.ingredient.TunnelIngredientComponentCapabilities;
import ruiseki.integratedtunnels.capability.network.FluidNetworkConfig;
import ruiseki.integratedtunnels.capability.network.ItemNetworkConfig;
import ruiseki.integratedtunnels.capability.network.TunnelNetworkCapabilityConstructors;
import ruiseki.integratedtunnels.core.world.BlockBreakHandlerRegistry;
import ruiseki.integratedtunnels.core.world.BlockBreakHandlers;
import ruiseki.integratedtunnels.core.world.BlockBreakPlaceRegistry;
import ruiseki.integratedtunnels.core.world.BlockPlaceHandlers;
import ruiseki.integratedtunnels.part.TunnelPartTypes;
import ruiseki.integratedtunnels.part.aspect.TunnelAspects;
import ruiseki.integratedtunnels.part.aspect.listproxy.TunnelValueTypeListProxyFactories;
import ruiseki.okcore.config.ConfigHandler;
import ruiseki.okcore.init.ModBaseVersionable;
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
public class IntegratedTunnels extends ModBaseVersionable {

    /**
     * The proxy of this mod, depending on 'side' a different proxy will be inside this field.
     *
     * @see SidedProxy
     */
    @SidedProxy(
        clientSide = "ruiseki.integratedtunnels.proxy.ClientProxy",
        serverSide = "ruiseki.integratedtunnels.proxy.CommonProxy")
    public static ICommonProxy proxy;

    /**
     * The unique instance of this mod.
     */
    @Mod.Instance(value = Reference.MOD_ID)
    public static IntegratedTunnels _instance;

    public IntegratedTunnels() {
        super(Reference.MOD_ID, Reference.MOD_NAME, Reference.MOD_VERSION);
    }

    /**
     * The pre-initialization, will register required configs.
     *
     * @param event The Forge event required for this.
     */
    @Mod.EventHandler
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        // Registries
        getRegistryManager().addRegistry(IBlockBreakHandlerRegistry.class, BlockBreakHandlerRegistry.getInstance());
        getRegistryManager().addRegistry(IBlockPlaceHandlerRegistry.class, BlockBreakPlaceRegistry.getInstance());

        TunnelIngredientComponentCapabilities.load();
        TunnelAspects.load();
        TunnelPartTypes.load();
        TunnelValueTypeListProxyFactories.load();
        BlockBreakHandlers.load();
        BlockPlaceHandlers.load();
        super.preInit(event);

        MinecraftForge.EVENT_BUS.register(new TunnelNetworkCapabilityConstructors());

        // Inject aspects into ID parts
        AspectRegistry.getInstance()
            .register(
                PartTypes.NETWORK_READER,
                Lists.newArrayList(
                    TunnelAspects.Read.Item.LONG_COUNT,
                    TunnelAspects.Read.Item.LONG_COUNTMAX,
                    TunnelAspects.Read.Item.LIST_ITEMSTACKS,
                    TunnelAspects.Read.Item.OPERATOR_GETITEMCOUNT,
                    TunnelAspects.Read.Item.INTEGER_INTERFACES,

                    TunnelAspects.Read.Fluid.LONG_COUNT,
                    TunnelAspects.Read.Fluid.LONG_COUNTMAX,
                    TunnelAspects.Read.Fluid.LIST_FLUIDSTACKS,
                    TunnelAspects.Read.Fluid.OPERATOR_GETFLUIDCOUNT,
                    TunnelAspects.Read.Fluid.INTEGER_INTERFACES));
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
        configHandler.add(new ItemNetworkConfig());
        configHandler.add(new FluidNetworkConfig());
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
        IntegratedTunnels._instance.getLoggerHelper()
            .log(level, message);
    }

    public static void clog(Level level, String message, Object... params) {
        IntegratedTunnels._instance.log(level, message, params);
    }
}
