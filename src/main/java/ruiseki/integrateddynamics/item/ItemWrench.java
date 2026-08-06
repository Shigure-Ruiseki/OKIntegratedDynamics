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
import ruiseki.okcore.config.configurable.ConfigurableItem;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.modcompat.capabilities.DefaultCapabilityProvider;

/**
 * The default wrench for this mod.
 *
 * @author rubensworks
 */
public class ItemWrench extends ConfigurableItem {

    private static ItemWrench _instance = null;

    /**
     * Get the unique instance.
     *
     * @return The instance.
     */
    public static ItemWrench getInstance() {
        return _instance;
    }

    /**
     * Make a new item instance.
     *
     * @param eConfig Config for this blockState.
     */
    public ItemWrench(ExtendedConfig eConfig) {
        super(eConfig);
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
        if (block == null || player.isSneaking()) {
            return false;
        } else if (block.rotateBlock(world, x, y, z, ForgeDirection.getOrientation(side))) {
            player.swingItem();
            return !world.isRemote;
        }
        return true;
    }
}
