package ruiseki.integratedtunnels.part;

import ruiseki.integrateddynamics.api.part.write.IPartTypeWriter;
import ruiseki.integratedtunnels.core.part.PartStateRoundRobin;

/**
 * A base world part state.
 * 
 * @author rubensworks
 */
public class PartStateWorld<P extends IPartTypeWriter> extends PartStateRoundRobin<P> {

    public PartStateWorld(int inventorySize) {
        super(inventorySize);
    }

    @Override
    protected int getDefaultUpdateInterval() {
        return 10;
    }
}
