package ruiseki.integratedcompat.modcompat.nei;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.core.block.IgnoredBlock;

public class NEIIntegratedDynamicsConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        if (NEIModCompat.canBeUsed) {
            hideIgnoredBlocks();
        }
    }

    public static void hideIgnoredBlocks() {
        for (Object obj : Block.blockRegistry) {
            if (obj instanceof IgnoredBlock block) {
                API.hideItem(new ItemStack(block, 1, OreDictionary.WILDCARD_VALUE));
            }
        }
    }

    @Override
    public String getName() {
        return Reference.MOD_NAME;
    }

    @Override
    public String getVersion() {
        return Reference.MOD_VERSION;
    }
}
