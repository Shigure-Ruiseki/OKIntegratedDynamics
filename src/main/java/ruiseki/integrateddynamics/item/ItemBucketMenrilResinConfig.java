package ruiseki.integrateddynamics.item;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.block.BlockFluidMenrilResin;
import ruiseki.integrateddynamics.fluid.FluidMenrilResin;
import ruiseki.okcore.config.configurable.ConfigurableBlockFluidClassic;
import ruiseki.okcore.config.configurable.ConfigurableFluid;
import ruiseki.okcore.config.configurable.ConfigurableItemBucket;
import ruiseki.okcore.config.configurable.IConfigurable;
import ruiseki.okcore.config.extendedconfig.ItemBucketConfig;

/**
 * Config for the Menril Resin Bucket.
 *
 * @author rubensworks
 *
 */
public class ItemBucketMenrilResinConfig extends ItemBucketConfig {

    /**
     * The unique instance.
     */
    public static ItemBucketMenrilResinConfig _instance;

    /**
     * Make a new instance.
     */
    public ItemBucketMenrilResinConfig() {
        super(IntegratedDynamics._instance, true, "bucket_menril_resin", null, null);
    }

    @Override
    protected IConfigurable initSubInstance() {
        return new ConfigurableItemBucket(this, BlockFluidMenrilResin.getInstance());
    }

    @Override
    public ConfigurableFluid getFluidInstance() {
        return FluidMenrilResin.getInstance();
    }

    @Override
    public ConfigurableBlockFluidClassic getFluidBlockInstance() {
        return BlockFluidMenrilResin.getInstance();
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
