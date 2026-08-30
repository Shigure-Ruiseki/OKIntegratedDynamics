package ruiseki.integrateddynamics.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.okcore.helper.ItemNBTHelpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.item.ItemBase;

/**
 * An enhancement item.
 *
 * @author rubensworks
 */
public class ItemEnhancement extends ItemBase {

    private final Type type;

    public ItemEnhancement(Type type) {
        super();
        this.type = type;
    }

    public <P extends IPartType<P, S>, S extends IPartState<P>> boolean applyEnhancement(IPartType<P, S> partType,
        IPartState<P> partState, ItemStack itemStack, EntityPlayer player) {
        switch (this.type) {
            case OFFSET -> {
                if (partType.supportsOffsets()) {
                    int value = getEnhancementValue(itemStack);
                    int newValue = partState.getMaxOffset() + value;
                    if (newValue < GeneralConfig.maxPartOffset) {
                        if (!player.worldObj.isRemote) {
                            partState.setMaxOffset(newValue);
                        }
                        itemStack.stackSize--;
                        player.addChatComponentMessage(
                            new ChatComponentTranslation(
                                "item.items.integrateddynamics.enhancement_offset.increased",
                                newValue));
                        return true;
                    }
                    player.addChatComponentMessage(
                        new ChatComponentTranslation(
                            "item.items.integrateddynamics.enhancement_offset.limit",
                            GeneralConfig.maxPartOffset));
                    return false;
                }
            }
        }
        return false;
    }

    public int getEnhancementValue(ItemStack itemStack) {
        return ItemNBTHelpers.getNBT(itemStack)
            .getInteger("value");
    }

    public void setEnhancementValue(ItemStack itemStack, int value) {
        ItemNBTHelpers.getNBT(itemStack)
            .setInteger("value", value);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List<String> list, boolean flag) {
        list.add(
            LangHelpers.localize(
                EnumChatFormatting.GRAY + "item.items.integrateddynamics.enhancement_offset.tooltip",
                getEnhancementValue(itemStack)));
        super.addInformation(itemStack, entityPlayer, list, flag);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
        ItemStack itemStack = new ItemStack(this);
        setEnhancementValue(itemStack, 4);
        list.add(itemStack);
    }

    public static enum Type {
        OFFSET
    }

}
