package ruiseki.integrateddynamics.item;

import net.minecraft.block.Block;
import net.minecraftforge.fluids.Fluid;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.block.BlockFluidLiquidChorusConfig;
import ruiseki.integrateddynamics.fluid.FluidLiquidChorusConfig;
import ruiseki.okcore.config.extendedconfig.ItemBucketConfig;
import ruiseki.okcore.item.ItemBucketBase;

/**
 * Config for the Menril Resin Bucket.
 *
 * @author rubensworks
 *
 */
public class ItemBucketLiquidChorusConfig extends ItemBucketConfig {

    /**
     * The unique instance.
     */
    public static ItemBucketLiquidChorusConfig _instance;

    /**
     * Make a new instance.
     */
    public ItemBucketLiquidChorusConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "bucket_liquid_chorus",
            null,
            config -> new ItemBucketBase(BlockFluidLiquidChorusConfig._instance.getInstance()));
    }

    @Override
    public Fluid getFluidInstance() {
        return FluidLiquidChorusConfig._instance.getInstance();
    }

    @Override
    public Block getFluidBlockInstance() {
        return BlockFluidLiquidChorusConfig._instance.getInstance();
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
