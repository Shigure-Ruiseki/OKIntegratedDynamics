package ruiseki.integrateddynamics.modcompat.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.core.tileentity.TileMultipartTicking;
import ruiseki.integrateddynamics.tileentity.TileProxy;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Waila support class.
 * 
 * @author rubensworks
 *
 */
public class Waila {

    /**
     * Waila callback.
     * 
     * @param registrar The Waila registrar.
     */
    public static void callbackRegister(IWailaRegistrar registrar) {
        registrar.addConfig(
            Reference.MOD_NAME,
            getPartConfigId(),
            LangHelpers.localize("gui." + Reference.MOD_ID + ".waila.partConfig"));
        registrar.addConfig(
            Reference.MOD_NAME,
            getProxyConfigId(),
            LangHelpers.localize("gui." + Reference.MOD_ID + ".waila.proxyConfig"));
        registrar.addConfig(
            Reference.MOD_NAME,
            getDryingBasinConfigId(),
            LangHelpers.localize("gui." + Reference.MOD_ID + ".waila.dryingBasinConfig"));
        registrar.registerBodyProvider(new PartDataProvider(), TileMultipartTicking.class);
        registrar.registerBodyProvider(new ProxyDataProvider(), TileProxy.class);
        // TODO: Add TileDryingBasin and TileSqueezer
        // registrar.registerBodyProvider(new DryingBasinDataProvider(), TileDryingBasin.class);
        // registrar.registerBodyProvider(new SqueezerDataProvider(), TileSqueezer.class);
    }

    /**
     * Part config ID.
     * 
     * @return The config ID.
     */
    public static String getPartConfigId() {
        return Reference.MOD_ID + ".part";
    }

    /**
     * Proxy config ID.
     * 
     * @return The config ID.
     */
    public static String getProxyConfigId() {
        return Reference.MOD_ID + ".proxy";
    }

    /**
     * Proxy config ID.
     * 
     * @return The config ID.
     */
    public static String getDryingBasinConfigId() {
        return Reference.MOD_ID + ".dryingBasin";
    }

}
