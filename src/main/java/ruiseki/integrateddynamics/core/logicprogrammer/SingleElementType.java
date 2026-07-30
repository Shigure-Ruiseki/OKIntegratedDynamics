package ruiseki.integrateddynamics.core.logicprogrammer;

import java.util.List;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElement;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElementType;

/**
 * Element type that provides exactly one element.
 * 
 * @author rubensworks
 */
public class SingleElementType<E extends ILogicProgrammerElement> implements ILogicProgrammerElementType<E> {

    private final ILogicProgrammerElementConstructor<E> constructor;
    private final String id;

    public SingleElementType(ILogicProgrammerElementConstructor<E> constructor, String id) {
        this.constructor = constructor;
        this.id = id;
    }

    @Override
    public E getByName(String name) {
        return constructor.construct();
    }

    @Override
    public String getName(E element) {
        return "";
    }

    @Override
    public String getName() {
        return "single:" + id;
    }

    @Override
    public List<E> createElements() {
        return Lists.newArrayList(constructor.construct());
    }

    public static interface ILogicProgrammerElementConstructor<E extends ILogicProgrammerElement> {

        public E construct();

    }
}
