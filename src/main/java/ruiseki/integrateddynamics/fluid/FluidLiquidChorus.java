package ruiseki.integrateddynamics.fluid;

import net.minecraft.item.EnumRarity;
import net.minecraftforge.fluids.Fluid;

/**
 * The Liquid Chorus {@link net.minecraftforge.fluids.Fluid}.
 *
 * @author rubensworks
 *
 */
public class FluidLiquidChorus extends Fluid {

    private static FluidLiquidChorus _instance = null;

    /**
     * Get the unique instance.
     *
     * @return The unique instance.
     */
    public static FluidLiquidChorus getInstance() {
        return _instance;
    }

    public FluidLiquidChorus(String name) {
        super(name);
        setDensity(1500); // How tick the fluid is, affects movement inside the liquid.
        setViscosity(3000); // How fast the fluid flows.
        setRarity(EnumRarity.epic);
    }

}
