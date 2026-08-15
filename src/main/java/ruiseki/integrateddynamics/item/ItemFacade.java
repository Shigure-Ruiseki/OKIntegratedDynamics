package ruiseki.integrateddynamics.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.Pair;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.api.block.IFacadeable;
import ruiseki.integrateddynamics.capability.facadeable.FacadeableConfig;
import ruiseki.okcore.config.configurable.ConfigurableItem;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.config.extendedconfig.ItemConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.BlockHelpers;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.ItemNBTHelpers;
import ruiseki.okcore.helper.LangHelpers;

/**
 * An item that represents a facade of a certain type.
 *
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class ItemFacade extends ConfigurableItem {

    private static ItemFacade _instance = null;

    /**
     * Get the unique instance.
     *
     * @return The instance.
     */
    public static ItemFacade getInstance() {
        return _instance;
    }

    /**
     * Make a new item instance.
     *
     * @param eConfig Config for this blockState.
     */
    public ItemFacade(ExtendedConfig<ItemConfig> eConfig) {
        super(eConfig);
    }

    public BlockState getFacadeBlock(ItemStack itemStack) {
        if (itemStack.hasTagCompound()) {
            NBTTagCompound tag = itemStack.getTagCompound();
            String blockName = tag.getString("blockName");
            int meta = tag.getInteger("meta");
            return BlockHelpers.deserializeBlockState(Pair.of(blockName, meta));
        }
        return null;
    }

    public ItemStack getFacadeBlockItem(ItemStack itemStack) {
        BlockState blockState = getFacadeBlock(itemStack);
        if (blockState != null) {
            return BlockHelpers.getItemStackFromBlockState(blockState);
        }
        return null;
    }

    public void writeFacadeBlock(ItemStack itemStack, BlockState blockState) {
        NBTTagCompound tag = ItemNBTHelpers.getNBT(itemStack);
        Pair<String, Integer> serializedBlockState = BlockHelpers.serializeBlockState(blockState);
        tag.setString("blockName", serializedBlockState.getLeft());
        tag.setInteger("meta", serializedBlockState.getRight());
    }

    @Override
    public String getItemStackDisplayName(ItemStack itemStack) {
        String suffix = EnumChatFormatting.ITALIC + LangHelpers.localize("general.integrateddynamics.info.none");
        ItemStack itemStackInner = getFacadeBlockItem(itemStack);
        if (itemStackInner != null) {
            suffix = getFacadeBlockItem(itemStack).getDisplayName();
        }
        return super.getItemStackDisplayName(itemStack) + " - " + suffix;
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer playerIn, World world, int x, int y, int z, int sideInt,
        float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            BlockPos pos = new BlockPos(x, y, z);
            IFacadeable facadeable = CapabilityHelpers.getCapability(world, pos, FacadeableConfig.CAPABILITY, null)
                .getOrNull();
            BlockState blockState = getFacadeBlock(itemStack);
            if (facadeable != null && blockState != null) {
                // Add facade to existing cable
                if (!facadeable.hasFacade()) {
                    facadeable.setFacade(blockState);
                    ItemBlockCable.playPlaceSound(world, pos);
                    itemStack.stackSize--;
                }
            }
            return true;
        }
        return super.onItemUse(itemStack, playerIn, world, x, y, z, sideInt, hitX, hitY, hitZ);
    }

}
