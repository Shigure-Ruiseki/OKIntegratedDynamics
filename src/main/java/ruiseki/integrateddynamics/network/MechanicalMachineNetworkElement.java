package ruiseki.integrateddynamics.network;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetwork;
import ruiseki.integrateddynamics.core.network.NetworkElementBase;
import ruiseki.okcore.datastructure.DimPos;

/**
 * Network element for mechanical machines.
 *
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class MechanicalMachineNetworkElement extends NetworkElementBase {

    private final DimPos pos;

    @Override
    public void setPriorityAndChannel(INetwork network, int priority, int channel) {

    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public int getChannel() {
        return IPositionedAddonsNetwork.DEFAULT_CHANNEL;
    }

    @Override
    public boolean canRevalidate(INetwork network) {
        return canRevalidatePositioned(network, pos);
    }

    @Override
    public void revalidate(INetwork network) {
        super.revalidate(network);
        revalidatePositioned(network, pos);
    }

    @Override
    public int compareTo(INetworkElement o) {
        if (o instanceof MechanicalMachineNetworkElement) {
            return getPos().compareTo(((MechanicalMachineNetworkElement) o).getPos());
        }
        return this.getClass()
            .getCanonicalName()
            .compareTo(
                o.getClass()
                    .getCanonicalName());
    }

    @Override
    public boolean isLoaded() {
        return INetworkElement.shouldTick(this.getPos());
    }
}
