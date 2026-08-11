package ruiseki.integrateddynamics.core.network;

import java.util.Iterator;
import java.util.function.Supplier;

import ruiseki.integrateddynamics.api.network.IPartPosIteratorHandler;
import ruiseki.integrateddynamics.api.part.PartPos;

/**
 * An {@link IPartPosIteratorHandler} that returns the given iterator unchanged.
 * 
 * @author rubensworks
 */
public class PartPosIteratorHandlerDummy implements IPartPosIteratorHandler {

    public static final PartPosIteratorHandlerDummy INSTANCE = new PartPosIteratorHandlerDummy();

    @Override
    public Iterator<PartPos> handleIterator(Supplier<Iterator<PartPos>> iteratorSupplier, int channel) {
        return iteratorSupplier.get();
    }

    @Override
    public IPartPosIteratorHandler clone() {
        return this;
    }
}
