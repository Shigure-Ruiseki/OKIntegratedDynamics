package ruiseki.integrateddynamics.core.part.event;

import cpw.mods.fml.common.eventhandler.Event;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;

/**
 * An part event that is posted in the Forge event bus.
 * 
 * @author rubensworks
 */
public class PartEvent<P extends IPartType, S extends IPartState<P>> extends Event {

    private final INetwork network;
    private final IPartNetwork partNetwork;
    private final PartTarget target;
    private final P partType;
    private final S partState;

    public PartEvent(INetwork network, IPartNetwork partNetwork, PartTarget target, P partType, S partState) {
        this.network = network;
        this.partNetwork = partNetwork;
        this.target = target;
        this.partType = partType;
        this.partState = partState;
    }

    public INetwork getNetwork() {
        return network;
    }

    public IPartNetwork getPartNetwork() {
        return partNetwork;
    }

    public PartTarget getTarget() {
        return target;
    }

    public P getPartType() {
        return partType;
    }

    public S getPartState() {
        return partState;
    }

}
