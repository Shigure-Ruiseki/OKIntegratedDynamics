package ruiseki.integratednbt;

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
import ruiseki.integrateddynamics.core.evaluate.operator.OperatorRegistry;
import ruiseki.integrateddynamics.core.item.VariableFacadeHandlerRegistry;
import ruiseki.integratednbt.block.BlockNbtExtractorConfig;
import ruiseki.integratednbt.client.model.VariableModelProviders;
import ruiseki.integratednbt.evaluate.operator.NbtExtractionOperatorSerializer;
import ruiseki.integratednbt.evaluate.variable.NbtExtractedVariableFacadeHandler;
import ruiseki.integratednbt.item.ItemNbtExtractorRemoteConfig;
import ruiseki.okcore.config.ConfigHandler;
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
    guiFactory = "GuiConfigOverview$ExtendedConfigGuiFactory")
public class IntegratedNbt extends ModBaseVersionable {

    /**
     * The proxy of this mod, depending on 'side' a different proxy will be inside this field.
     *
     * @see SidedProxy
     */
    @SidedProxy(
        clientSide = "ruiseki.integratednbt.proxy.ClientProxy",
        serverSide = "ruiseki.integratednbt.proxy.CommonProxy")
    public static ICommonProxy proxy;

    /**
     * The unique instance of this mod.
     */
    @Mod.Instance(value = Reference.MOD_ID)
    public static IntegratedNbt _instance;

    public IntegratedNbt() {
        super(Reference.MOD_ID, Reference.MOD_NAME, Reference.MOD_VERSION);
    }

    @Mod.EventHandler
    @Override
    public void preInit(FMLPreInitializationEvent event) {

        VariableFacadeHandlerRegistry.getInstance()
            .registerHandler(new NbtExtractedVariableFacadeHandler());
        OperatorRegistry.getInstance()
            .registerSerializer(new NbtExtractionOperatorSerializer());

        if (MinecraftHelpers.isClientSide()) {
            VariableModelProviders.load();
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
    }

    @Mod.EventHandler
    @Override
    public void onServerStopping(FMLServerStoppingEvent event) {
        super.onServerStopping(event);
    }

    @Mod.EventHandler
    @Override
    public void onServerStopped(FMLServerStoppedEvent event) {
        super.onServerStopped(event);
    }

    @Override
    public CreativeTabs constructDefaultCreativeTab() {
        return new ItemCreativeTab(this, () -> ItemNbtExtractorRemoteConfig._instance.getInstance());
    }

    @Override
    public void onGeneralConfigsRegister(ConfigHandler configHandler) {
        configHandler.add(new GeneralConfig());
    }

    @Override
    public void onMainConfigsRegister(ConfigHandler configHandler) {
        configHandler.add(new ItemNbtExtractorRemoteConfig());
        configHandler.add(new BlockNbtExtractorConfig());
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
        IntegratedNbt._instance.getLoggerHelper()
            .log(level, message);
    }

    public static void clog(Level level, String message, Object... params) {
        IntegratedNbt._instance.log(level, message, params);
    }
}
