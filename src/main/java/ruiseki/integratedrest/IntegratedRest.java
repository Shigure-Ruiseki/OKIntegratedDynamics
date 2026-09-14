package ruiseki.integratedrest;

import net.minecraft.creativetab.CreativeTabs;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import ruiseki.integratedrest.api.http.request.IRequestHandlerRegistry;
import ruiseki.integratedrest.api.json.IValueTypeJsonHandlerRegistry;
import ruiseki.integratedrest.block.BlockHttpConfig;
import ruiseki.integratedrest.client.model.HttpVariableModelProviders;
import ruiseki.integratedrest.evaluate.HttpVariableFacadeHandler;
import ruiseki.integratedrest.http.HttpServer;
import ruiseki.integratedrest.http.request.RequestHandlerRegistry;
import ruiseki.integratedrest.http.request.RequestHandlers;
import ruiseki.integratedrest.json.ValueTypeJsonHandlerRegistry;
import ruiseki.integratedrest.json.ValueTypeJsonHandlers;
import ruiseki.okcore.config.ConfigHandler;
import ruiseki.okcore.config.extendedconfig.BlockItemConfigReference;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.init.ItemCreativeTab;
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
    version = Reference.MOD_VERSION,
    dependencies = Reference.MOD_DEPENDENCIES,
    guiFactory = "ruiseki.integratedrest.GuiConfigOverview$ExtendedConfigGuiFactory")
public class IntegratedRest extends ModBaseVersionable {

    /**
     * The proxy of this mod, depending on 'side' a different proxy will be inside this field.
     *
     * @see SidedProxy
     */
    @SidedProxy(
        clientSide = "ruiseki.integratedrest.proxy.ClientProxy",
        serverSide = "ruiseki.integratedrest.proxy.CommonProxy")
    public static ICommonProxy proxy;

    /**
     * The unique instance of this mod.
     */
    @Mod.Instance(value = Reference.MOD_ID)
    public static IntegratedRest _instance;

    protected final HttpServer server;

    public IntegratedRest() {
        super(Reference.MOD_ID, Reference.MOD_NAME, Reference.MOD_VERSION);
        server = new HttpServer();
    }

    @Mod.EventHandler
    @Override
    public void preInit(FMLPreInitializationEvent event) {

        getRegistryManager().addRegistry(IRequestHandlerRegistry.class, RequestHandlerRegistry.getInstance());
        getRegistryManager()
            .addRegistry(IValueTypeJsonHandlerRegistry.class, ValueTypeJsonHandlerRegistry.getInstance());

        if (MinecraftHelpers.isClientSide()) {
            HttpVariableModelProviders.load();
        }
        super.preInit(event);
    }

    @Mod.EventHandler
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Mod.EventHandler
    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);

        IVariableFacadeHandlerRegistry registry = IntegratedDynamics._instance.getRegistryManager()
            .getRegistry(IVariableFacadeHandlerRegistry.class);

        if (registry != null) {
            registry.registerHandler(HttpVariableFacadeHandler.getInstance());
        }

        RequestHandlers.load();
        ValueTypeJsonHandlers.load();
    }

    @Mod.EventHandler
    @Override
    public void onServerStarting(FMLServerStartingEvent event) {
        super.onServerStarting(event);
    }

    @Mod.EventHandler
    @Override
    public void onServerStarted(FMLServerStartedEvent event) {
        super.onServerStarted(event);
        if (GeneralConfig.startApi) {
            server.initialize();
        }
    }

    @Mod.EventHandler
    @Override
    public void onServerStopping(FMLServerStoppingEvent event) {
        super.onServerStopping(event);
        if (GeneralConfig.startApi) {
            server.deinitialize();
        }
    }

    @Mod.EventHandler
    @Override
    public void onServerStopped(FMLServerStoppedEvent event) {
        super.onServerStopped(event);
    }

    @Override
    public CreativeTabs constructDefaultCreativeTab() {
        return new ItemCreativeTab(this, new BlockItemConfigReference(BlockHttpConfig.class));
    }

    @Override
    public void onGeneralConfigsRegister(ConfigHandler configHandler) {
        configHandler.add(new GeneralConfig());
    }

    @Override
    public void onMainConfigsRegister(ConfigHandler configHandler) {
        super.onMainConfigsRegister(configHandler);
        configHandler.add(new BlockHttpConfig());
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
        IntegratedRest._instance.getLoggerHelper()
            .log(level, message);
    }

    public static void clog(Level level, String message, Object... params) {
        IntegratedRest._instance.log(level, message, params);
    }
}
