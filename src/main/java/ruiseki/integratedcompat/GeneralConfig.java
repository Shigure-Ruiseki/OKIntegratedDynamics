package ruiseki.integratedcompat;

import ruiseki.okcore.config.ConfigurableProperty;
import ruiseki.okcore.config.ConfigurableType;
import ruiseki.okcore.config.extendedconfig.DummyConfig;

/**
 * A config with general options for this mod.
 *
 * @author rubensworks
 *
 */
public class GeneralConfig extends DummyConfig {

    @ConfigurableProperty(
        category = "core",
        comment = "If JEI recipe filling should heuristically try to determine item tags from recipes.",
        requiresMcRestart = true)
    public static boolean jeiHeuristicTags = false;

    /**
     * The type of this config.
     */
    public static ConfigurableType TYPE = ConfigurableType.DUMMY;

    /**
     * Create a new instance.
     */
    public GeneralConfig() {
        super(IntegratedCompat._instance, true, "general", null);
    }

    @Override
    public void onRegistered() {}

    @Override
    public boolean isEnabled() {
        return true;
    }
}
