package ruiseki.integrateddynamics.core.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.DirectionProperty;
import ruiseki.okcore.config.configurable.ConfigurableBlock;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;

/**
 * A block that is not visible to the player.
 * Just used for providing models, until a better way for doing this comes around.
 * 
 * @author rubensworks
 */
public class IgnoredBlock extends ConfigurableBlock {

    @BlockProperty
    public static final DirectionProperty FACING = DirectionProperty.facing();

    /**
     * Make a new blockState instance.
     *
     * @param eConfig Config for this blockState.
     */
    public IgnoredBlock(ExtendedConfig<BlockConfig> eConfig) {
        super(eConfig, Material.glass);
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        // Don't show block in creative tab
    }
}
