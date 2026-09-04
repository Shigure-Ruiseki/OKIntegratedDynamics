package ruiseki.integratedcompat.modcompat.waila;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.okcore.datastructure.BlockPos;

/**
 * Waila data provider for parts.
 *
 * @author rubensworks
 *
 */
public class PartDataProvider implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        if (config.getConfig(Waila.getPartConfigId())) {
            BlockPos pos = new BlockPos(accessor.getPosition());
            IPartContainer partContainer = PartHelpers.getPartContainer(accessor.getWorld(), pos, null)
                .getOrNull();
            if (partContainer != null) {
                ForgeDirection side = partContainer.getWatchingSide(accessor.getWorld(), pos, accessor.getPlayer());
                if (side != null && partContainer.hasPart(side)) {
                    IPartType partType = partContainer.getPart(side);
                    IPartState partState = partContainer.getPartState(side);
                    partType.loadTooltip(partState, currenttip);
                }
            }
        }
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
        int y, int z) {
        return tag;
    }
}
