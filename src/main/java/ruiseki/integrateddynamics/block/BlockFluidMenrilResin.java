package ruiseki.integrateddynamics.block;

import net.minecraft.block.material.Material;

import ruiseki.integrateddynamics.fluid.FluidMenrilResinConfig;
import ruiseki.okcore.fluid.BlockFluidBase;
import ruiseki.okcore.helper.MinecraftHelpers;

/**
 * A blockState for the {@link ruiseki.integrateddynamics.fluid.FluidMenrilResin} fluid.
 *
 * @author rubensworks
 *
 */
public class BlockFluidMenrilResin extends BlockFluidBase {

    public BlockFluidMenrilResin() {
        super(FluidMenrilResinConfig._instance.getInstance(), Material.water);

        if (MinecraftHelpers.isClientSide()) this.setParticleColor(0.654901961F, 0.870588235F, 0.780392157F);
    }
}
