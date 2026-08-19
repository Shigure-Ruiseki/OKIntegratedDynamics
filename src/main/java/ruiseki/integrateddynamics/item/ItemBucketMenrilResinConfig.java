package ruiseki.integrateddynamics.item;

import net.minecraft.block.Block;
import net.minecraftforge.fluids.Fluid;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.block.BlockFluidMenrilResinConfig;
import ruiseki.integrateddynamics.fluid.FluidMenrilResinConfig;
import ruiseki.okcore.config.extendedconfig.ItemBucketConfig;
import ruiseki.okcore.item.ItemBucketBase;

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
        super(
            IntegratedDynamics._instance,
            true,
            "bucket_menril_resin",
            null,
            config -> new ItemBucketBase(BlockFluidMenrilResinConfig._instance.getInstance()));
    }

    @Override
    public Fluid getFluidInstance() {
        return FluidMenrilResinConfig._instance.getInstance();
    }

    @Override
    public Block getFluidBlockInstance() {
        return BlockFluidMenrilResinConfig._instance.getInstance();
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
