package ruiseki.integrateddynamics.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.commoncapabilities.api.capability.wrench.DefaultWrench;
import ruiseki.commoncapabilities.capability.wrench.WrenchConfig;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.item.ItemBase;
import ruiseki.okcore.modcompat.capabilities.DefaultCapabilityProvider;

/**
 * The default wrench for this mod.
 *
 * @author rubensworks
 */
public class ItemWrench extends ItemBase {

    /**
     * Make a new item instance.
     *
     */
    public ItemWrench() {
        super();
    }

    @Override
    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
        return true;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
        return new DefaultCapabilityProvider<>(() -> WrenchConfig.CAPABILITY, new DefaultWrench());
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(x, y, z);
        if (!world.isRemote || player.isSneaking()) {
            return false;
        } else if (block.rotateBlock(world, x, y, z, ForgeDirection.getOrientation(side))) {
            player.swingItem();
            return true;
        }
        return true;
    }
}
