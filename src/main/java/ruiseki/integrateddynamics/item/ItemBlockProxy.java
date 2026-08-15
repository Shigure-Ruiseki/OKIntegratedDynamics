package ruiseki.integrateddynamics.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.item.ItemBlockNBT;

/**
 * The item for the proxy.
 * 
 * @author rubensworks
 */
public class ItemBlockProxy extends ItemBlockNBT {

    /**
     * Make a new instance.
     *
     * @param block The blockState instance.
     */
    public ItemBlockProxy(Block block) {
        super(block);
        this.setMaxStackSize(64);
    }

    @SuppressWarnings("rawtypes")
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
        if (itemStack.getTagCompound() != null) {
            int id = itemStack.getTagCompound()
                .getInteger("proxyId");
            list.add(LangHelpers.localize(L10NValues.GENERAL_ITEM_ID, id));
        }
        super.addInformation(itemStack, entityPlayer, list, par4);
    }
}
