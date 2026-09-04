package ruiseki.integrateddynamics.fluid;

import net.minecraft.item.EnumRarity;
import net.minecraftforge.fluids.Fluid;

/**
 * The Menril Resin {@link net.minecraftforge.fluids.Fluid}.
 *
 * @author rubensworks
 *
 */
public class FluidMenrilResin extends Fluid {

    public FluidMenrilResin(String name) {
        super(name);
        setDensity(1500); // How tick the fluid is, affects movement inside the liquid.
        setViscosity(3000); // How fast the fluid flows.
        setRarity(EnumRarity.rare);
    }

}
