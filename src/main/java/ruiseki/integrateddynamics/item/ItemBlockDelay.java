package ruiseki.integrateddynamics.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.item.ItemBlockNBT;

/**
 * The item for the delay.
 *
 * @author rubensworks
 */
public class ItemBlockDelay extends ItemBlockNBT {

    /**
     * Make a new instance.
     *
     * @param block The blockState instance.
     */
    public ItemBlockDelay(Block block) {
        super(block);
    }

    @SuppressWarnings("rawtypes")
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
        if (itemStack.getTagCompound() != null) {
            int id = itemStack.getTagCompound()
                .getInteger("delayId");
            list.add(LangHelpers.localize(L10NValues.GENERAL_ITEM_ID, id));
        }
        super.addInformation(itemStack, entityPlayer, list, par4);
    }
}
