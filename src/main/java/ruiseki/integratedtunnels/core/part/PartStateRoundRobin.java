package ruiseki.integratedtunnels.core.part;

import ruiseki.integrateddynamics.api.network.IPartPosIteratorHandler;
import ruiseki.integrateddynamics.api.part.write.IPartTypeWriter;
import ruiseki.integrateddynamics.core.part.write.PartStateWriterBase;

/**
 * A writer part state that maintains an iterator for round-robin iteration over interfaces.
 *
 * @author rubensworks
 */
public class PartStateRoundRobin<P extends IPartTypeWriter> extends PartStateWriterBase<P> {

    private IPartPosIteratorHandler partPosIteratorHandler = null;

    public PartStateRoundRobin(int inventorySize) {
        super(inventorySize);
    }

    public void setPartPosIteratorHandler(IPartPosIteratorHandler partPosIteratorHandler) {
        this.partPosIteratorHandler = partPosIteratorHandler;
    }

    public IPartPosIteratorHandler getPartPosIteratorHandler() {
        return partPosIteratorHandler;
    }
}
