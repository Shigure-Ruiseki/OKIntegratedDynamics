package ruiseki.integratedtunnels.core.part;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetwork;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.part.PartStateBase;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.LazyOptional;

/**
 * Interface for positioned network addons that do not have a filter.
 *
 * @author rubensworks
 */
public abstract class PartTypeInterfacePositionedAddon<N extends IPositionedAddonsNetwork, T, P extends PartTypeInterfacePositionedAddon<N, T, P, S>, S extends IPartTypeInterfacePositionedAddon.IState<N, T, P, S>>
    extends PartTypeTunnel<P, S> implements IPartTypeInterfacePositionedAddon<N, T, P, S> {

    public PartTypeInterfacePositionedAddon(String name) {
        super(name);
    }

    @Override
    public Class<? extends GuiScreen> getGui() {
        return GuiInterfaceSettings.class;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerInterfaceSettings.class;
    }

    @Override
    public boolean isUpdate(S state) {
        return getConsumptionRate(state) > 0 && GeneralConfig.energyConsumptionMultiplier > 0;
    }

    @Override
    public void onAddingPositionToNetwork(N networkCapability, INetwork network, PartPos pos, int priority,
        int channelInterface, S state) {
        networkCapability.addPosition(pos, priority, channelInterface);
    }

    @Override
    public void onRemovingPositionFromNetwork(N networkCapability, INetwork network, PartPos pos, S state) {
        networkCapability.removePosition(pos);
    }

    // Methods below copied to PartTypeInterfacePositionedAddonFiltering

    @Override
    public void afterNetworkReAlive(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        super.afterNetworkReAlive(network, partNetwork, target, state);
        addTargetToNetwork(network, target, state.getPriority(), state.getChannelInterface(), state);
    }

    @Override
    public void onNetworkRemoval(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        super.onNetworkRemoval(network, partNetwork, target, state);
        scheduleNetworkObservation(target, state);
        removeTargetFromNetwork(network, state);
    }

    @Override
    public void onNetworkAddition(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        super.onNetworkAddition(network, partNetwork, target, state);
        addTargetToNetwork(network, target, state.getPriority(), state.getChannelInterface(), state);
        scheduleNetworkObservation(target, state);
    }

    @Override
    public void onBlockNeighborChange(INetwork network, IPartNetwork partNetwork, PartTarget target, S state,
        IBlockAccess world, Block neighbourBlock, BlockPos neighbourBlockPos) {
        super.onBlockNeighborChange(network, partNetwork, target, state, world, neighbourBlock, neighbourBlockPos);
        if (network != null) {
            updateTargetInNetwork(network, target, state.getPriority(), state.getChannelInterface(), state);
        }
    }

    @Override
    public void setPriorityAndChannel(INetwork network, IPartNetwork partNetwork, PartTarget target, S state,
        int priority, int channel) {
        // We need to do this because the energy network is not automagically aware of the priority changes,
        // so we have to re-add it.
        removeTargetFromNetwork(network, state);
        super.setPriorityAndChannel(network, partNetwork, target, state, priority, channel);
        addTargetToNetwork(network, target, priority, state.getChannelInterface(), state);
    }

    @Override
    public boolean setTargetOffset(S state, PartPos center, Vector3i offset) {
        // Remove interface before changing offset, and re-add after,
        // because the target offset might change the interface.
        INetwork network = state.getNetwork();
        if (network != null) {
            removeTargetFromNetwork(network, state);
        }
        boolean ret = super.setTargetOffset(state, center, offset);
        if (network != null) {
            PartTarget target = getTarget(center, state);
            addTargetToNetwork(network, target, state.getPriority(), state.getChannelInterface(), state);
            // Force an observation, so that the network index does not linger on the old target
            scheduleNetworkObservation(target, state);
        }
        return ret;
    }

    @Override
    public void setTargetSideOverride(S state, @Nullable ForgeDirection side) {
        // Remove interface before changing the target side, and re-add after,
        // because the target side determines the position of this interface in the network.
        INetwork network = state.getNetwork();
        PartPos center = state.getCenter();
        if (network != null && center != null) {
            removeTargetFromNetwork(network, state);
        }
        super.setTargetSideOverride(state, side);
        if (network != null && center != null) {
            PartTarget target = getTarget(center, state);
            addTargetToNetwork(network, target, state.getPriority(), state.getChannelInterface(), state);
            // Force an observation, so that the network index does not linger on the old target side
            scheduleNetworkObservation(target, state);
        }
    }

    public static abstract class State<N extends IPositionedAddonsNetwork, T, P extends PartTypeInterfacePositionedAddon<N, T, P, S>, S extends State<N, T, P, S>>
        extends PartStateBase<P> implements IPartTypeInterfacePositionedAddon.IState<N, T, P, S> {

        private N positionedAddonsNetwork = null;
        private PartPos pos = null;
        private PartPos center = null;
        private boolean validTargetCapability = false;
        private int channelInterface = 0;

        private INetwork network;
        private IPartNetwork partNetwork;

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            super.readFromNBT(tag);
            if (tag.hasKey("channelInterface", Constants.NBT.TAG_INT)) {
                this.channelInterface = tag.getInteger("channelInterface");
            } else {
                this.channelInterface = getChannel();
            }
        }

        @Override
        public void writeToNBT(NBTTagCompound tag) {
            super.writeToNBT(tag);
            tag.setInteger("channelInterface", channelInterface);
        }

        @Override
        public void setChannelInterface(int channelInterface) {
            this.channelInterface = channelInterface;
            sendUpdate();
        }

        @Override
        public int getChannelInterface() {
            return channelInterface;
        }

        @Override
        @Nullable
        public N getPositionedAddonsNetwork() {
            return positionedAddonsNetwork;
        }

        @Override
        public void setPositionedAddonsNetwork(N positionedAddonsNetwork) {
            this.positionedAddonsNetwork = positionedAddonsNetwork;
        }

        @Override
        public boolean isValidTargetCapability() {
            return validTargetCapability;
        }

        @Override
        public void setValidTargetCapability(boolean validTargetCapability) {
            this.validTargetCapability = validTargetCapability;
        }

        @Override
        public PartPos getPos() {
            return pos;
        }

        @Override
        public void setPos(PartPos pos) {
            this.pos = pos;
        }

        @Nullable
        @Override
        public PartPos getCenter() {
            return center;
        }

        @Override
        public void setCenter(@Nullable PartPos center) {
            this.center = center;
        }

        @Override
        public void setNetworks(@Nullable INetwork network, @Nullable IPartNetwork partNetwork) {
            this.network = network;
            this.partNetwork = partNetwork;
        }

        @Override
        public @Nullable INetwork getNetwork() {
            return network;
        }

        @Override
        public @Nullable IPartNetwork getPartNetwork() {
            return partNetwork;
        }

        @Override
        public <T2> LazyOptional<T2> getCapability(Capability<T2> capability, INetwork network,
            IPartNetwork partNetwork, PartTarget target) {
            if (isNetworkAndPositionValid() && capability == getTargetCapability()) {
                return LazyOptional.of(() -> this)
                    .cast();
            }
            return super.getCapability(capability, network, partNetwork, target);
        }
    }

}
