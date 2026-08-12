package ruiseki.integrateddynamics.part;

import java.util.Collections;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Sets;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartRenderPosition;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.path.IPathElement;
import ruiseki.integrateddynamics.api.path.ISidedPathElement;
import ruiseki.integrateddynamics.capability.path.PathElementConfig;
import ruiseki.integrateddynamics.capability.path.SidedPathElement;
import ruiseki.integrateddynamics.core.block.IgnoredBlock;
import ruiseki.integrateddynamics.core.block.IgnoredBlockStatus;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.BlockStateHelpers;
import ruiseki.okcore.helper.CapabilityHelpers;

/**
 * A monodirectional wireless connector part that can connect to
 * at most one other monodirectional connector in a straight line.
 *
 * @author rubensworks
 */
public class PartTypeConnectorMonoDirectional
    extends PartTypeConnector<PartTypeConnectorMonoDirectional, PartTypeConnectorMonoDirectional.State> {

    public PartTypeConnectorMonoDirectional(String name) {
        super(name, new PartRenderPosition(0.25F, 0.3125F, 0.5F, 0.5F));
    }

    @Override
    public int getConsumptionRate(State state) {
        return 32;
    }

    @Override
    public PartTypeConnectorMonoDirectional.State constructDefaultState() {
        return new PartTypeConnectorMonoDirectional.State();
    }

    @Override
    public Class<? super PartTypeConnectorMonoDirectional> getPartTypeClass() {
        return PartTypeConnectorMonoDirectional.class;
    }

    @Override
    public void onNetworkAddition(INetwork network, IPartNetwork partNetwork, PartTarget target, State state) {
        super.onNetworkAddition(network, partNetwork, target, state);

        // Find and link two parts
        if (!state.hasTarget()) {
            int offset = findTargetOffset(target.getCenter());
            if (offset > 0) {
                state.setTarget(offset);
                state.getTargetState(target.getCenter())
                    .setTarget(offset);

                // Re-init network at the two disconnected connectors
                DimPos originPos = target.getCenter()
                    .getPos();
                DimPos targetPos = PartTypeConnectorMonoDirectional.State
                    .getTargetPos(target.getCenter(), state.getOffset());
                NetworkHelpers.initNetwork(
                    originPos.getWorld(),
                    originPos.getBlockPos(),
                    target.getCenter()
                        .getSide());
                NetworkHelpers.initNetwork(
                    targetPos.getWorld(),
                    targetPos.getBlockPos(),
                    target.getCenter()
                        .getSide()
                        .getOpposite());
            }
        }
    }

    @Override
    public void onPostRemoved(INetwork network, IPartNetwork partNetwork, PartTarget target, State state) {
        super.onPostRemoved(network, partNetwork, target, state);

        if (state.hasTarget()) {
            // Remove target information in both linked parts
            PartTypeConnectorMonoDirectional.State targetState = state.getTargetState(target.getCenter());
            DimPos originPos = target.getCenter()
                .getPos();
            DimPos targetPos = null;
            if (targetState != null) {
                targetState.removeTarget();
                targetPos = PartTypeConnectorMonoDirectional.State.getTargetPos(target.getCenter(), state.getOffset());
            }
            state.removeTarget();

            // Re-init network at the two disconnected connectors
            NetworkHelpers.initNetwork(
                originPos.getWorld(),
                originPos.getBlockPos(),
                target.getCenter()
                    .getSide());
            if (targetPos != null) {
                NetworkHelpers.initNetwork(
                    targetPos.getWorld(),
                    targetPos.getBlockPos(),
                    target.getCenter()
                        .getSide()
                        .getOpposite());
            }
        }
    }

    @Override
    public ItemStack getItemStack(State state, boolean saveState) {
        // Set offset to 0 to make sure it is not stored in the item
        int offset = state.getOffset();
        state.setOffset(0);

        // Serialize to item
        ItemStack itemStack = super.getItemStack(state, saveState);

        // Set original offset back
        state.setOffset(offset);

        return itemStack;
    }

    /**
     * Look in the part's direction for an unbound monodirectional connector.
     *
     * @param origin The origin position to start looking from.
     * @return The other connector's distance, or 0 if not found.
     */
    protected int findTargetOffset(PartPos origin) {
        int offset = 0;
        PartTypeConnectorMonoDirectional.State state = null;
        while (++offset < GeneralConfig.maxDirectionalConnectorOffset
            && (state = PartTypeConnectorMonoDirectional.State.getUnboundTargetState(origin, offset)) == null);
        if (state != null) {
            return offset;
        }
        return 0;
    }

    protected IgnoredBlockStatus.Status getStatus(PartTypeConnectorMonoDirectional.State state) {
        return state != null && state.hasTarget() ? IgnoredBlockStatus.Status.ACTIVE
            : IgnoredBlockStatus.Status.INACTIVE;
    }

    @Override
    public BlockState getBlockState(IPartContainer partContainer, ForgeDirection side) {
        BlockState state = BlockStateHelpers.getState(getBlock(), 0);
        IgnoredBlockStatus.Status status = getStatus(
            partContainer != null ? (PartTypeConnectorMonoDirectional.State) partContainer.getPartState(side) : null);
        state.setPropertyValue(IgnoredBlock.FACING, side);
        state.setPropertyValue(IgnoredBlockStatus.STATUS, status);
        return state;
    }

    public static class State extends PartTypeConnector.State<PartTypeConnectorMonoDirectional> {

        private int offset = 0;

        @Override
        public Set<ISidedPathElement> getReachableElements() {
            if (getPartPos() != null) {
                ForgeDirection targetSide = getPartPos().getSide()
                    .getOpposite();
                IPathElement pathElement = CapabilityHelpers
                    .getCapability(State.getTargetPos(getPartPos(), offset), PathElementConfig.CAPABILITY, targetSide)
                    .getOrNull();
                if (pathElement != null) {
                    return Sets.newHashSet(SidedPathElement.of(pathElement, targetSide));
                }
            }
            return Collections.emptySet();
        }

        public void setTarget(int offset) {
            setOffset(offset);
            sendUpdate();

            DimPos dimPos = getPosition();
            if (dimPos != null && this.offset > 0) {
                World world = dimPos.getWorld();

                if (world instanceof WorldServer worldServer) {

                    int x = dimPos.getX();
                    int y = dimPos.getY();
                    int z = dimPos.getZ();

                    ForgeDirection side = getPartPos().getSide();

                    for (int i = 1; i < this.offset; i++) {
                        x += side.offsetX;
                        y += side.offsetY;
                        z += side.offsetZ;
                        worldServer.spawnParticle("reddust", x, y, z, 1, 0.0D, 0.0D);
                    }
                }
            }
        }

        public boolean hasTarget() {
            return this.offset > 0;
        }

        public int getOffset() {
            return this.offset;
        }

        /**
         * Set the raw offset.
         * Prefer {@link #setTarget(int)}.
         *
         * @param offset The new offset.
         */
        public void setOffset(int offset) {
            this.offset = offset;
        }

        public void removeTarget() {
            setTarget(0);
        }

        protected PartTypeConnectorMonoDirectional.State getTargetState(PartPos origin) {
            return getTargetState(origin, offset);
        }

        @Override
        public void writeToNBT(NBTTagCompound tag) {
            super.writeToNBT(tag);
            if (offset > 0) {
                tag.setInteger("connect_offset", offset);
            }
        }

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            super.readFromNBT(tag);
            if (tag.hasKey("connect_offset")) {
                this.offset = tag.getInteger("connect_offset");
            }
        }

        protected static PartTypeConnectorMonoDirectional.State getUnboundTargetState(PartPos origin, int offset) {
            PartTypeConnectorMonoDirectional.State state = getTargetState(origin, offset);
            if (state != null && !state.hasTarget()) {
                return state;
            }
            return null;
        }

        protected static PartTypeConnectorMonoDirectional.State getTargetState(PartPos origin, int offset) {
            PartPos targetPos = PartPos.of(
                getTargetPos(origin, offset),
                origin.getSide()
                    .getOpposite());
            PartHelpers.PartStateHolder partStateHolder = PartHelpers.getPart(targetPos);
            if (partStateHolder != null && partStateHolder.getPart() instanceof PartTypeConnectorMonoDirectional) {
                return (State) partStateHolder.getState();
            }
            return null;
        }

        protected static DimPos getTargetPos(PartPos origin, int offset) {
            return DimPos.of(
                origin.getPos()
                    .getWorld(),
                origin.getPos()
                    .getBlockPos()
                    .offset(origin.getSide(), offset));
        }
    }

}
