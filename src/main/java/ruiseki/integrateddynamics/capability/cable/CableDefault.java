package ruiseki.integrateddynamics.capability.cable;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integrateddynamics.api.block.cable.ICable;
import ruiseki.integrateddynamics.block.BlockCable;
import ruiseki.integrateddynamics.core.helper.CableHelpers;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.EnumFacingMap;

/**
 * Default implementation of {@link ICable}.
 *
 * @author rubensworks
 */
public abstract class CableDefault implements ICable {

    protected abstract boolean isForceDisconnectable();

    protected abstract EnumFacingMap<Boolean> getForceDisconnected();

    protected abstract EnumFacingMap<Boolean> getConnected();

    protected abstract void markDirty();

    protected abstract void sendUpdate();

    protected abstract World getWorld();

    protected abstract BlockPos getPos();

    protected boolean isForceDisconnected(ForgeDirection side) {
        if (!isForceDisconnectable()) return false;
        if (!getForceDisconnected().containsKey(side)) return false;
        return getForceDisconnected().get(side);
    }

    @Override
    public boolean canConnect(ICable connector, ForgeDirection side) {
        return !isForceDisconnected(side);
    }

    @Override
    public void updateConnections() {
        World world = getWorld();
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            boolean cableConnected = CableHelpers.canCableConnectTo(world, getPos(), side, this);
            getConnected().put(side, cableConnected);

            // Remove any already existing force-disconnects for this side.
            if (!cableConnected && isForceDisconnectable() && this.canConnect(this, side)) {
                getForceDisconnected().put(side, false);

            }
        }
        markDirty();
        sendUpdate();
    }

    @Override
    public boolean isConnected(ForgeDirection side) {
        if (getPos() == null) {
            return false;
        }
        if (getConnected().isEmpty()) {
            updateConnections();
        }
        return getConnected().containsKey(side) && getConnected().get(side);
    }

    @Override
    public void disconnect(ForgeDirection side) {
        if (isForceDisconnectable()) {
            getForceDisconnected().put(side, true);
        }
    }

    @Override
    public void reconnect(ForgeDirection side) {
        if (isForceDisconnectable()) {
            getForceDisconnected().remove(side);
        }
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(BlockCable.getInstance());
    }
}
