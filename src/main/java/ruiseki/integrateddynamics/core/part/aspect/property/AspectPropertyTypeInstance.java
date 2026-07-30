package ruiseki.integrateddynamics.core.part.aspect.property;

import lombok.Data;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectPropertyTypeInstance;

/**
 * An instance of a property type with a onLabelPacket.
 * 
 * @author rubensworks
 */
@Data
public class AspectPropertyTypeInstance<T extends IValueType<V>, V extends IValue>
    implements IAspectPropertyTypeInstance<T, V> {

    private final T type;
    private final String unlocalizedName;

}
