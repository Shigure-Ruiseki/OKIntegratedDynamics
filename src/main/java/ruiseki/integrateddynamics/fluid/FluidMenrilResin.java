package ruiseki.integrateddynamics.fluid;

import net.minecraft.item.EnumRarity;

import ruiseki.okcore.config.configurable.ConfigurableFluid;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.config.extendedconfig.FluidConfig;

/**
 * The Menril Resin {@link net.minecraftforge.fluids.Fluid}.
 * 
 * @author rubensworks
 *
 */
public class FluidMenrilResin extends ConfigurableFluid {

    private static FluidMenrilResin _instance = null;

    /**
     * Get the unique instance.
     * 
     * @return The unique instance.
     */
    public static FluidMenrilResin getInstance() {
        return _instance;
    }

    public FluidMenrilResin(ExtendedConfig<FluidConfig> eConfig) {
        super(eConfig);
        setDensity(1500); // How tick the fluid is, affects movement inside the liquid.
        setViscosity(3000); // How fast the fluid flows.
        setRarity(EnumRarity.rare);
    }

}
