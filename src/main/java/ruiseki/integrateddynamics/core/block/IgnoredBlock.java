package ruiseki.integrateddynamics.core.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import com.gtnewhorizon.gtnhlib.api.BlockModelInfo;

import ruiseki.integrateddynamics.block.BlockInvisibleLight;
import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.DirectionProperty;
import ruiseki.okcore.config.configurable.ConfigurableBlock;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;

/**
 * A block that is not visible to the player.
 * Just used for providing models, until a better way for doing this comes around.
 *
 * @author rubensworks
 */
public class IgnoredBlock extends ConfigurableBlock implements BlockModelInfo {

    @BlockProperty
    public static final DirectionProperty FACING = DirectionProperty.facing();

    private static BlockInvisibleLight _instance = null;

    public static BlockInvisibleLight getInstance() {
        return _instance;
    }

    /**
     * Make a new blockState instance.
     *
     * @param eConfig Config for this blockState.
     */
    public IgnoredBlock(ExtendedConfig eConfig) {
        super(eConfig, Material.glass);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        // Don't show block in creative tab
    }

    @Override
    public boolean nhlib$isModeled() {
        return false;
    }

    @Override
    public void nhlib$setModeled(boolean modeled) {

    }
}
