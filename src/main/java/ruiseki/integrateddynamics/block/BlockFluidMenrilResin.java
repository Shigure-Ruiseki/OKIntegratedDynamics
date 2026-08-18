package ruiseki.integrateddynamics.block;

import net.minecraft.block.material.Material;

import ruiseki.integrateddynamics.fluid.FluidMenrilResin;
import ruiseki.okcore.config.configurable.ConfigurableBlockFluidClassic;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.helper.MinecraftHelpers;

/**
 * A blockState for the {@link ruiseki.integrateddynamics.fluid.FluidMenrilResin} fluid.
 * 
 * @author rubensworks
 *
 */
public class BlockFluidMenrilResin extends ConfigurableBlockFluidClassic {

    private static BlockFluidMenrilResin _instance = null;

    /**
     * Get the unique instance.
     * 
     * @return The instance.
     */
    public static BlockFluidMenrilResin getInstance() {
        return _instance;
    }

    public BlockFluidMenrilResin(ExtendedConfig<BlockConfig> eConfig) {
        super(eConfig, FluidMenrilResin.getInstance(), Material.water);

        if (MinecraftHelpers.isClientSide()) this.setParticleColor(0.654901961F, 0.870588235F, 0.780392157F);
    }
}
