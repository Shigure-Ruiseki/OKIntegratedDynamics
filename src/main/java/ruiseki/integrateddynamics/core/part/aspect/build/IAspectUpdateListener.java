package ruiseki.integrateddynamics.core.part.aspect.build;

import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;

/**
 * Listens to calls to
 * {@link ruiseki.integrateddynamics.api.part.aspect.IAspect#update(IPartNetwork, IPartType, PartTarget, IPartState)}.
 * 
 * @author rubensworks
 */
public interface IAspectUpdateListener {

    public <P extends IPartType<P, S>, S extends IPartState<P>> void onUpdate(IPartNetwork network, P partType,
        PartTarget target, S state);

    /**
     * Before the update is called.
     */
    public static interface Before extends IAspectUpdateListener {
    }

    /**
     * After the update was called.
     */
    public static interface After extends IAspectUpdateListener {
    }

}
