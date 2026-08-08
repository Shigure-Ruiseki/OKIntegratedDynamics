package ruiseki.integrateddynamics.core.part;

import java.util.Set;

import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartRenderPosition;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.part.aspect.Aspects;

/**
 * An abstract {@link IPartType} that can hold aspects.
 *
 * @author rubensworks
 */
public abstract class PartTypeAspects<P extends IPartType<P, S>, S extends IPartState<P>>
    extends PartTypeConfigurable<P, S> {

    private Set<IAspect> aspects = null;

    public PartTypeAspects(String name, PartRenderPosition partRenderPosition) {
        super(name, partRenderPosition);
    }

    /**
     * @return All possible aspects that can be used in this part type.
     */
    public Set<IAspect> getAspects() {
        if (aspects == null) {
            aspects = Aspects.REGISTRY.getAspects(this);
        }
        return aspects;
    }

    @Override
    public boolean isUpdate(S state) {
        return !getAspects().isEmpty();
    }

    @Override
    public int getConsumptionRate(S state) {
        return 1;
    }

}
