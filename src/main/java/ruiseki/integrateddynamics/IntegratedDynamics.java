package ruiseki.integrateddynamics;

import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Level;

import com.gtnewhorizon.gtnhlib.client.model.loading.ModelRegistry;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import ruiseki.integrateddynamics.api.client.model.IVariableModelProviderRegistry;
import ruiseki.integrateddynamics.api.client.render.part.IPartOverlayRendererRegistry;
import ruiseki.integrateddynamics.api.client.render.valuetype.IValueTypeWorldRendererRegistry;
import ruiseki.integrateddynamics.api.evaluate.operator.IOperatorRegistry;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueCastRegistry;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeLightLevelRegistry;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeListProxyFactoryTypeRegistry;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeRegistry;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElementTypeRegistry;
import ruiseki.integrateddynamics.api.part.IPartTypeRegistry;
import ruiseki.integrateddynamics.api.part.aspect.IAspectRegistry;
import ruiseki.integrateddynamics.capability.network.NetworkCapabilityConstructors;
import ruiseki.integrateddynamics.client.render.part.PartOverlayRendererRegistry;
import ruiseki.integrateddynamics.client.render.part.PartOverlayRenderers;
import ruiseki.integrateddynamics.client.render.valuetype.ValueTypeWorldRendererRegistry;
import ruiseki.integrateddynamics.client.render.valuetype.ValueTypeWorldRenderers;
import ruiseki.integrateddynamics.command.CommandCrash;
import ruiseki.integrateddynamics.command.CommandNetworkDiagnostics;
import ruiseki.integrateddynamics.core.NoteBlockEventReceiver;
import ruiseki.integrateddynamics.core.TickHandler;
import ruiseki.integrateddynamics.core.client.gui.ExtendedGuiHandler;
import ruiseki.integrateddynamics.core.client.model.VariableModelProviderRegistry;
import ruiseki.integrateddynamics.core.client.model.VariableModelProviders;
import ruiseki.integrateddynamics.core.evaluate.DelayVariableFacadeHandler;
import ruiseki.integrateddynamics.core.evaluate.ProxyVariableFacadeHandler;
import ruiseki.integrateddynamics.core.evaluate.operator.OperatorRegistry;
import ruiseki.integrateddynamics.core.evaluate.operator.Operators;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueCastMappings;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueCastRegistry;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeLightLevelRegistry;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeLightLevels;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeListProxyFactories;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeListProxyFactoryTypeRegistry;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeRegistry;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.item.VariableFacadeHandlerRegistry;
import ruiseki.integrateddynamics.core.logicprogrammer.LogicProgrammerElementTypeRegistry;
import ruiseki.integrateddynamics.core.logicprogrammer.LogicProgrammerElementTypes;
import ruiseki.integrateddynamics.core.part.PartTypeRegistry;
import ruiseki.integrateddynamics.core.part.PartTypes;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.core.persist.world.LabelsWorldStorage;
import ruiseki.integrateddynamics.core.persist.world.NetworkWorldStorage;
import ruiseki.integrateddynamics.modcompat.nei.NEIModCompat;
import ruiseki.integrateddynamics.part.PartTypeConnectorOmniDirectional;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.okcore.client.gui.GuiHandler;
import ruiseki.okcore.config.ConfigHandler;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.init.ModBaseVersionable;
import ruiseki.okcore.item.BucketRegistry;
import ruiseki.okcore.item.IBucketRegistry;
import ruiseki.okcore.modcompat.ModCompatLoader;
import ruiseki.okcore.persist.world.GlobalCounters;
import ruiseki.okcore.proxy.ICommonProxy;

@Mod(
    modid = Reference.MOD_ID,
    name = Reference.MOD_NAME,
    version = Reference.MOD_VERSION,
    dependencies = Reference.DEPENDENCIES,
    guiFactory = Reference.GUI_FACTORY)
public class IntegratedDynamics extends ModBaseVersionable {

    /**
     * The proxy of this mod, depending on 'side' a different proxy will be inside this field.
     *
     * @see SidedProxy
     */
    @SidedProxy(serverSide = Reference.PROXY_COMMON, clientSide = Reference.PROXY_CLIENT)
    public static ICommonProxy proxy;

    /**
     * The unique instance of this mod.
     */
    @Mod.Instance(Reference.MOD_ID)
    public static IntegratedDynamics _instance;

    public static GlobalCounters globalCounters = null;

    public IntegratedDynamics() {
        super(Reference.MOD_ID, Reference.MOD_NAME, Reference.MOD_VERSION);

        // Register world storages
        registerWorldStorage(NetworkWorldStorage.getInstance(this));
        registerWorldStorage(globalCounters = new GlobalCounters(this));
        registerWorldStorage(LabelsWorldStorage.getInstance(this));
    }

    @Override
    protected GuiHandler constructGuiHandler() {
        return new ExtendedGuiHandler(this);
    }

    @Override
    protected LiteralArgumentBuilder<ICommandSender> constructBaseCommand(MinecraftServer server) {
        LiteralArgumentBuilder<ICommandSender> root = super.constructBaseCommand(server);
        root.then(new CommandNetworkDiagnostics(this).make());
        root.then(new CommandCrash(this).make());
        return root;
    }

    @Override
    protected void loadModCompats(ModCompatLoader modCompatLoader) {
        super.loadModCompats(modCompatLoader);
        modCompatLoader.addModCompat(new NEIModCompat());
    }

    @Override
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        getRegistryManager().addRegistry(IBucketRegistry.class, new BucketRegistry());

        getRegistryManager()
            .addRegistry(IVariableFacadeHandlerRegistry.class, VariableFacadeHandlerRegistry.getInstance());
        getRegistryManager().addRegistry(IValueTypeRegistry.class, ValueTypeRegistry.getInstance());
        getRegistryManager().addRegistry(IValueCastRegistry.class, ValueCastRegistry.getInstance());
        getRegistryManager().addRegistry(
            IValueTypeListProxyFactoryTypeRegistry.class,
            ValueTypeListProxyFactoryTypeRegistry.getInstance());
        getRegistryManager().addRegistry(IValueTypeLightLevelRegistry.class, ValueTypeLightLevelRegistry.getInstance());
        getRegistryManager().addRegistry(IPartTypeRegistry.class, PartTypeRegistry.getInstance());
        getRegistryManager().addRegistry(IAspectRegistry.class, AspectRegistry.getInstance());
        getRegistryManager().addRegistry(IOperatorRegistry.class, OperatorRegistry.getInstance());
        getRegistryManager()
            .addRegistry(ILogicProgrammerElementTypeRegistry.class, LogicProgrammerElementTypeRegistry.getInstance());
        if (MinecraftHelpers.isClientSide()) {
            getRegistryManager()
                .addRegistry(IPartOverlayRendererRegistry.class, PartOverlayRendererRegistry.getInstance());
            getRegistryManager()
                .addRegistry(IValueTypeWorldRendererRegistry.class, ValueTypeWorldRendererRegistry.getInstance());
            getRegistryManager()
                .addRegistry(IVariableModelProviderRegistry.class, VariableModelProviderRegistry.getInstance());
            getRegistryManager().getRegistry(IVariableFacadeHandlerRegistry.class)
                .registerHandler(DelayVariableFacadeHandler.getInstance());
        }
        getRegistryManager().getRegistry(IVariableFacadeHandlerRegistry.class)
            .registerHandler(ProxyVariableFacadeHandler.getInstance());

        addInitListeners(getRegistryManager().getRegistry(IPartTypeRegistry.class));

        ValueTypes.load();
        ValueCastMappings.load();
        ValueTypeLightLevels.load();
        ValueTypeListProxyFactories.load();
        Operators.load();
        Aspects.load();
        PartTypes.load();
        LogicProgrammerElementTypes.load();
        if (MinecraftHelpers.isClientSide()) {
            PartOverlayRenderers.load();
            ValueTypeWorldRenderers.load();
            VariableModelProviders.load();
        }

        super.preInit(event);
        if (MinecraftHelpers.isClientSide()) {
            ModelRegistry.registerModid(Reference.MOD_ID);
        }

        FMLCommonHandler.instance()
            .bus()
            .register(TickHandler.getInstance());
        MinecraftForge.EVENT_BUS.register(NoteBlockEventReceiver.getInstance());
        MinecraftForge.EVENT_BUS.register(new NetworkCapabilityConstructors());
    }

    @Override
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Override
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    @Override
    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        super.onServerStarting(event);
    }

    @Override
    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        PartTypeConnectorOmniDirectional.LOADED_GROUPS.onStartedEvent(event);
        super.onServerStarted(event);
    }

    @Override
    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        super.onServerStopping(event);
    }

    @Override
    @Mod.EventHandler
    public void onServerStopped(FMLServerStoppedEvent event) {
        super.onServerStopped(event);
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
        Configs.registerBlocks(configHandler);
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
        IntegratedDynamics._instance.log(Level.INFO, message);
    }

    /**
     * Log a new message of the given level for this mod.
     *
     * @param level   The level in which the message must be shown.
     * @param message The message to show.
     */
    public static void clog(Level level, String message) {
        IntegratedDynamics._instance.log(level, message);
    }

    /**
     * Log a new message of the given level for this mod.
     *
     * @param level   The level in which the message must be shown.
     * @param message The message to show.
     * @param params  Parameters to replace in the message.
     */
    public static void clog(Level level, String message, Object... params) {
        IntegratedDynamics._instance.log(level, message, params);
    }
}
