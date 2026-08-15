package ruiseki.integrateddynamics.core.part;

import ruiseki.integrateddynamics.api.part.IPartType;

/**
 * Dummy part state for parts that should not persist their state.
 *
 * @author rubensworks
 */
public class PartStateEmpty<P extends IPartType> extends PartStateBase<P> {

}
