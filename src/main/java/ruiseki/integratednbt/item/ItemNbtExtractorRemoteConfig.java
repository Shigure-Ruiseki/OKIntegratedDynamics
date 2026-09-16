package ruiseki.integratednbt.item;

import ruiseki.integratednbt.IntegratedNbt;
import ruiseki.okcore.config.extendedconfig.ItemConfig;

/**
 * Config for the remote NBT extractor.
 *
 * @author rubensworks
 */
public class ItemNbtExtractorRemoteConfig extends ItemConfig {

    /**
     * The unique instance.
     */
    public static ItemNbtExtractorRemoteConfig _instance;

    /**
     * Make a new instance.
     */
    public ItemNbtExtractorRemoteConfig() {
        super(IntegratedNbt._instance, true, "nbt_extractor_remote", null, config -> new ItemNbtExtractorRemote());
    }
}
