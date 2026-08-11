package ruiseki.integrateddynamics.api.network;

import java.util.Iterator;
import java.util.function.Supplier;

import ruiseki.integrateddynamics.api.part.PartPos;

/**
 * Determines the starting position of an iterator of {@link PartPos}.
 * 
 * @author rubensworks
 */
public interface IPartPosIteratorHandler {

    /**
     * Given an iteraror constructor, determine the starting position of it.
     * 
     * @param iteratorSupplier A constructor of an iterator of {@link PartPos}.
     *                         Each invocation must produce a new iterator.
     * @param channel          The channel in which the iterator is being handled.
     * @return An iterator, may be ended.
     */
    public Iterator<PartPos> handleIterator(Supplier<Iterator<PartPos>> iteratorSupplier, int channel);

    /**
     * Copy this handler.
     * This should be cheap to call, as this can be called multiple times per tick.
     * 
     * @return Create a stateful copy of this handler.
     */
    public IPartPosIteratorHandler clone();

}
