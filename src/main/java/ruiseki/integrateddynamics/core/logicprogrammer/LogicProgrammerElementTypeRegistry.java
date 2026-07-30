package ruiseki.integrateddynamics.core.logicprogrammer;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElementType;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElementTypeRegistry;

/**
 * Registry for {@link ILogicProgrammerElementType}.
 * 
 * @author rubensworks
 */
public class LogicProgrammerElementTypeRegistry implements ILogicProgrammerElementTypeRegistry {

    private static final LogicProgrammerElementTypeRegistry INSTANCE = new LogicProgrammerElementTypeRegistry();

    private final Map<String, ILogicProgrammerElementType> namedTypes = Maps.newHashMap();
    private final List<ILogicProgrammerElementType> types = Lists.newLinkedList();

    /**
     * @return The unique instance.
     */
    public static LogicProgrammerElementTypeRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public <E extends ILogicProgrammerElementType> E addType(E type) {
        types.add(type);
        namedTypes.put(type.getName(), type);
        return type;
    }

    @Override
    public List<ILogicProgrammerElementType> getTypes() {
        return Collections.unmodifiableList(types);
    }

    @Override
    public ILogicProgrammerElementType getType(String name) {
        return namedTypes.get(name);
    }
}
