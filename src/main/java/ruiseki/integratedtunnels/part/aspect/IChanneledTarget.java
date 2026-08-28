package ruiseki.integratedtunnels.part.aspect;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.PartStateException;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetwork;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integratedtunnels.core.part.PartStateRoundRobin;

/**
 * A helper class for movement targets with a certain network type.
 *
 * @author rubensworks
 */
public interface IChanneledTarget<N extends IPositionedAddonsNetwork> {

    public INetwork getNetwork();

    public N getChanneledNetwork();

    public boolean hasValidTarget();

    public PartStateRoundRobin<?> getPartState();

    public int getChannel();

    public boolean isRoundRobin();

    public boolean isCraftIfFailed();

    public void preTransfer();

    public void postTransfer();

    public static INetwork getNetworkChecked(PartPos pos) throws PartStateException {
        INetwork network = NetworkHelpers.getNetwork(
            pos.getPos()
                .getWorld(),
            pos.getPos()
                .getBlockPos(),
            pos.getSide())
            .getOrNull();
        if (network == null) {
            IntegratedDynamics.clog(Level.ERROR, "Could not get the network for transfer as no network was found.");
            throw new PartStateException(pos.getPos(), pos.getSide());
        }
        return network;
    }

    @Nullable
    public static PartStateRoundRobin<?> getPartState(PartPos center) {
        PartHelpers.PartStateHolder<?, ?> partStateHolder = PartHelpers.getPart(center);
        if (partStateHolder == null) {
            return null;
        }
        return (PartStateRoundRobin<?>) partStateHolder.getState();
    }

}
