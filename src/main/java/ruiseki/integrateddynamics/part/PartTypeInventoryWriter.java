package ruiseki.integrateddynamics.part;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.core.part.write.PartStateWriterBase;
import ruiseki.integrateddynamics.core.part.write.PartTypeWriteBase;
import ruiseki.integrateddynamics.part.aspect.Aspects;

/**
 * An inventory writer part.
 *
 * @author rubensworks
 */
public class PartTypeInventoryWriter
    extends PartTypeWriteBase<PartTypeInventoryWriter, PartStateWriterBase<PartTypeInventoryWriter>> {

    public PartTypeInventoryWriter(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(this, Lists.<IAspect>newArrayList(

            ));
    }

    @Override
    public PartStateWriterBase<PartTypeInventoryWriter> constructDefaultState() {
        return new PartStateWriterBase<PartTypeInventoryWriter>(
            Aspects.REGISTRY.getAspects(this)
                .size());
    }

    @Override
    public int getConsumptionRate(PartStateWriterBase<PartTypeInventoryWriter> state) {
        return GeneralConfig.inventoryWriterBaseConsumption;
    }
}
