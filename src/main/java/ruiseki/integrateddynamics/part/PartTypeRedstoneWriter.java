package ruiseki.integrateddynamics.part;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.core.part.write.PartStateWriterBase;
import ruiseki.integrateddynamics.core.part.write.PartTypeWriteBase;
import ruiseki.integrateddynamics.part.aspect.Aspects;

/**
 * A redstone writer part.
 *
 * @author rubensworks
 */
public class PartTypeRedstoneWriter
    extends PartTypeWriteBase<PartTypeRedstoneWriter, PartStateWriterBase<PartTypeRedstoneWriter>> {

    public PartTypeRedstoneWriter(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(
                this,
                Lists.<IAspect>newArrayList(Aspects.Write.Redstone.BOOLEAN, Aspects.Write.Redstone.INTEGER));
    }

    @Override
    public PartStateWriterBase<PartTypeRedstoneWriter> constructDefaultState() {
        return new PartStateWriterBase<PartTypeRedstoneWriter>(
            Aspects.REGISTRY.getAspects(this)
                .size());
    }

}
