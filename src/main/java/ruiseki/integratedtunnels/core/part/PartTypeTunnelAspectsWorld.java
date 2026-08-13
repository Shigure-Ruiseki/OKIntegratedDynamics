package ruiseki.integratedtunnels.core.part;

import ruiseki.integrateddynamics.api.part.PartRenderPosition;
import ruiseki.integrateddynamics.api.part.write.IPartStateWriter;
import ruiseki.integrateddynamics.api.part.write.IPartTypeWriter;

/**
 * Base part for a tunnels with aspects.
 * 
 * @author rubensworks
 */
public abstract class PartTypeTunnelAspectsWorld<P extends IPartTypeWriter<P, S>, S extends IPartStateWriter<P>>
    extends PartTypeTunnelAspects<P, S> {

    public PartTypeTunnelAspectsWorld(String name) {
        super(name, new PartRenderPosition(0.1875F, 0.1875F, 0.625F, 0.625F));
    }

    @Override
    public int getConsumptionRate(S state) {
        return state.hasVariable() ? 32 : super.getConsumptionRate(state);
    }

}
