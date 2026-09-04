package ruiseki.integratedtunnels.core.part;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetwork;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import ruiseki.integrateddynamics.api.network.PositionedAddonsNetworkIngredientsFilter;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.part.write.PartStateWriterBase;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.LazyOptional;

/**
 * Interface for positioned network addons that have a filter.
 *
 * @author rubensworks
 */
public abstract class PartTypeInterfacePositionedAddonFiltering<N extends IPositionedAddonsNetwork, T, P extends PartTypeInterfacePositionedAddonFiltering<N, T, P, S>, S extends PartTypeInterfacePositionedAddonFiltering.State<N, T, P, S>>
    extends PartTypeTunnelAspects<P, S> implements IPartTypeInterfacePositionedAddon<N, T, P, S> {

    public PartTypeInterfacePositionedAddonFiltering(String name) {
        super(name);
    }

    @Override
    public void update(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        if (state.isRequireAspectUpdateAndReset()) {
            // For filter interfaces, we assume that targetFilters are set upon each aspect exec, which we only need to
            // do once.
            super.update(network, partNetwork, target, state);
        }
    }

    @Override
    protected void onVariableContentsUpdated(IPartNetwork network, PartTarget target, S state) {
        super.onVariableContentsUpdated(network, target, state);
        state.requireAspectUpdate();
    }

    @Override
    public void onAddingPositionToNetwork(N networkCapability, INetwork network, PartPos pos, int priority,
        int channelInterface, S state) {
        if (state.getTargetFilter() != null) {
            networkCapability.addPosition(pos, priority, channelInterface);
            ((IPositionedAddonsNetworkIngredients<T, ?>) state.getPositionedAddonsNetwork())
                .setPositionedStorageFilter(pos, state.getTargetFilter());
        }
    }

    @Override
    public void onRemovingPositionFromNetwork(N networkCapability, INetwork network, PartPos pos, S state) {
        networkCapability.removePosition(pos);
        N addonsNetwork = state.getPositionedAddonsNetwork();
        if (addonsNetwork != null) {
            ((IPositionedAddonsNetworkIngredients<T, ?>) addonsNetwork).setPositionedStorageFilter(pos, null);
        }
    }

    // Methods below copied from PartTypeInterfacePositionedAddon

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

    public static abstract class State<N extends IPositionedAddonsNetwork, T, P extends PartTypeInterfacePositionedAddonFiltering<N, T, P, S>, S extends PartTypeInterfacePositionedAddonFiltering.State<N, T, P, S>>
        extends PartStateWriterBase<P> implements IPartTypeInterfacePositionedAddon.IState<N, T, P, S> {

        private N positionedAddonsNetwork = null;
        private PartPos pos = null;
        private PartPos center = null;
        private boolean validTargetCapability = false;
        private int channelInterface = 0;

        private PositionedAddonsNetworkIngredientsFilter<T> targetFilter = null;
        private INetwork network;
        private IPartNetwork partNetwork;
        private boolean requireAspectUpdate = true;

        public State(int inventorySize) {
            super(inventorySize);
        }

        @Override
        protected int getDefaultUpdateInterval() {
            return 10;
        }

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            super.readFromNBT(tag);
            if (tag.hasKey("channelInterface", Constants.NBT.TAG_INT)) {
                this.channelInterface = tag.getInteger("channelInterface");
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

        public boolean isRequireAspectUpdateAndReset() {
            boolean ret = this.requireAspectUpdate;
            this.requireAspectUpdate = false;
            return ret;
        }

        @Nullable
        public PositionedAddonsNetworkIngredientsFilter<T> getTargetFilter() {
            return this.targetFilter;
        }

        public void setTargetFilter(@Nullable PositionedAddonsNetworkIngredientsFilter<T> targetFilter) {
            this.targetFilter = targetFilter;

            // Trigger aspect re-execution if needed.
            // Our networks are unset while this part is detached from its network, in which case we retry later.
            if (targetFilter == null || network == null || partNetwork == null) {
                this.requireAspectUpdate();
            } else {
                getVariable(network, partNetwork).addInvalidationListener(this::requireAspectUpdate);
            }
        }

        public void requireAspectUpdate() {
            this.requireAspectUpdate = true;
        }

        @Override
        public void setNetworks(@Nullable INetwork network, @Nullable IPartNetwork partNetwork) {
            this.network = network;
            this.partNetwork = partNetwork;
        }

        @Override
        @Nullable
        public INetwork getNetwork() {
            return network;
        }

        @Override
        @Nullable
        public IPartNetwork getPartNetwork() {
            return partNetwork;
        }

        @Override
        public <T2> LazyOptional<T2> getCapability(Capability<T2> capability, INetwork network,
            IPartNetwork partNetwork, PartTarget target) {
            if (isNetworkAndPositionValid() && capability == getTargetCapability()) {
                return LazyOptional.of(this::getCapabilityInstance)
                    .cast();
            }
            return super.getCapability(capability, network, partNetwork, target);
        }
    }

}
