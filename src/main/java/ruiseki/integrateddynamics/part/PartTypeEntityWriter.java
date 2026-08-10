package ruiseki.integrateddynamics.part;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.core.part.write.PartStateWriterBase;
import ruiseki.integrateddynamics.core.part.write.PartTypeWriteBase;
import ruiseki.integrateddynamics.part.aspect.Aspects;

/**
 * An entity writer part.
 * 
 * @author josephcsible
 */
public class PartTypeEntityWriter
    extends PartTypeWriteBase<PartTypeEntityWriter, PartStateWriterBase<PartTypeEntityWriter>> {

    public PartTypeEntityWriter(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(this, Lists.<IAspect>newArrayList(

            ));
    }

    @Override
    public PartStateWriterBase<PartTypeEntityWriter> constructDefaultState() {
        return new PartStateWriterBase<PartTypeEntityWriter>(
            Aspects.REGISTRY.getAspects(this)
                .size());
    }

}
