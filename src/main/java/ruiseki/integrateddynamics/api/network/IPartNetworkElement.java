package ruiseki.integrateddynamics.api.network;

import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;

/**
 * A part network element.
 *
 * @author rubensworks
 */
public interface IPartNetworkElement<P extends IPartType<P, S>, S extends IPartState<P>>
    extends IEventListenableNetworkElement<IPartNetwork, P> {

    /**
     * @return The part.
     */
    public P getPart();

    /**
     * @return The state for this part.
     */
    public S getPartState();

    /**
     * @return The container in which this part resides.
     */
    public IPartContainer getPartContainer();

    /**
     * @return The target and position of this part.
     */
    public PartTarget getTarget();

}
