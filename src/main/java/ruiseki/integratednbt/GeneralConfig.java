package ruiseki.integratednbt;

import ruiseki.integratedrest.IntegratedRest;
import ruiseki.integratedrest.Reference;
import ruiseki.okcore.config.ConfigLocation;
import ruiseki.okcore.config.ConfigurableProperty;
import ruiseki.okcore.config.extendedconfig.DummyConfig;
import ruiseki.okcore.tracking.Versions;

/**
 * A config with general options for this mod.
 *
 * @author rubensworks
 *
 */
public class GeneralConfig extends DummyConfig {

    @ConfigurableProperty(category = "core", comment = "If the version checker should be enabled.")
    public static boolean versionChecker = true;

    @ConfigurableProperty(
        category = "general",
        comment = "The base energy usage for the NBT Extractor.",
        minimalValue = 0,
        configLocation = ConfigLocation.SERVER)
    public static int nbtExtractorBaseConsumption = 2;

    public GeneralConfig() {
        super(IntegratedNbt._instance, true, "general", null);
    }

    @Override
    public void onRegistered() {
        if (versionChecker) {
            Versions.registerMod(getMod(), IntegratedRest._instance, Reference.VERSION_URL);
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
