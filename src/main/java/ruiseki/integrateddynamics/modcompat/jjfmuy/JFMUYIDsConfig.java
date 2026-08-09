package ruiseki.integrateddynamics.modcompat.jjfmuy;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import ruiseki.integrateddynamics.core.block.IgnoredBlock;
import ruiseki.jfmuy.api.IModPlugin;
import ruiseki.jfmuy.api.IModRegistry;
import ruiseki.jfmuy.api.JFMUYPlugin;
import ruiseki.jfmuy.api.ingredients.IIngredientBlacklist;

@JFMUYPlugin
public class JFMUYIDsConfig implements IModPlugin {

    @Override
    public void register(IModRegistry registry) {
        IIngredientBlacklist blacklist = registry.getJFMUYHelpers().getIngredientBlacklist();

        for (Object blockObj : Block.blockRegistry) {
            Block block = (Block) blockObj;
            if (block instanceof IgnoredBlock) {
                blacklist.addIngredientToBlacklist(new ItemStack(block));
            }
        }
    }
}
