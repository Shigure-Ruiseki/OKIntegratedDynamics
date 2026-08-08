package ruiseki.integrateddynamics.part;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.core.part.read.PartStateReaderBase;
import ruiseki.integrateddynamics.core.part.read.PartTypeReadBase;
import ruiseki.integrateddynamics.part.aspect.Aspects;

/**
 * An entity reader part.
 *
 * @author rubensworks
 */
public class PartTypeEntityReader
    extends PartTypeReadBase<PartTypeEntityReader, PartStateReaderBase<PartTypeEntityReader>> {

    public PartTypeEntityReader(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(
                this,
                Lists.<IAspect>newArrayList(
                    Aspects.Read.Entity.INTEGER_ITEMFRAMEROTATION,
                    Aspects.Read.Entity.LIST_ENTITIES,
                    Aspects.Read.Entity.LIST_PLAYERS,
                    Aspects.Read.Entity.ENTITY,
                    Aspects.Read.Entity.ITEMSTACK_ITEMFRAMECONTENTS));
    }

    @Override
    public PartStateReaderBase<PartTypeEntityReader> constructDefaultState() {
        return new PartStateReaderBase<PartTypeEntityReader>();
    }

}
