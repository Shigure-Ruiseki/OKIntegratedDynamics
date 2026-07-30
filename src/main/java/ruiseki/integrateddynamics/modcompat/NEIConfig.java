package ruiseki.integrateddynamics.modcompat;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.core.block.IgnoredBlock;
import ruiseki.integrateddynamics.core.block.IgnoredBlockStatus;

public class NEIConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        hideBlock(IgnoredBlock.getInstance());
        hideBlock(IgnoredBlockStatus.getInstance());
    }

    private void hideBlock(Object blockOrItem) {
        if (blockOrItem == null) return;

        ItemStack stack = null;
        if (blockOrItem instanceof net.minecraft.block.Block block) {
            Item item = Item.getItemFromBlock(block);
            if (item != null) {
                stack = new ItemStack(item);
            }
        } else if (blockOrItem instanceof Item item) {
            stack = new ItemStack(item);
        }

        if (stack != null && stack.getItem() != null) {
            API.hideItem(stack);
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
