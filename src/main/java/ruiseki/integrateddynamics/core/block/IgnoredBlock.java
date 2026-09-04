package ruiseki.integrateddynamics.core.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.okcore.block.BlockBase;
import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.DirectionProperty;

/**
 * A block that is not visible to the player.
 * Just used for providing models, until a better way for doing this comes around.
 *
 * @author rubensworks
 */
public class IgnoredBlock extends BlockBase {

    @BlockProperty
    public static final DirectionProperty FACING = DirectionProperty.facing();

    /**
     * Make a new blockState instance.
     */
    public IgnoredBlock() {
        super(Material.glass);
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        // Don't show block in creative tab
    }
}
