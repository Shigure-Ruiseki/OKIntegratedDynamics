package ruiseki.integrateddynamics.core.block;

import net.minecraft.block.material.Material;

import ruiseki.integrateddynamics.core.tileentity.TileMultipartTicking;
import ruiseki.okcore.config.configurable.ConfigurableBlockContainer;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;

/**
 * A block that is buildReader up from different parts.
 * This block refers to a ticking tile entity.
 * 
 * @author rubensworks
 */
public abstract class BlockMultipartTicking extends ConfigurableBlockContainer {

    /**
     * Make a new block instance.
     * 
     * @param eConfig  Config for this block.
     * @param material The material for this block.
     */
    public BlockMultipartTicking(ExtendedConfig eConfig, Material material) {
        super(eConfig, material, TileMultipartTicking.class);
    }

}
