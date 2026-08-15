package ruiseki.integrateddynamics.part;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.core.part.write.PartStateWriterBase;
import ruiseki.integrateddynamics.core.part.write.PartTypeWriteBase;
import ruiseki.integrateddynamics.part.aspect.Aspects;

/**
 * A machine writer part.
 *
 * @author josephcsible
 */
public class PartTypeMachineWriter
    extends PartTypeWriteBase<PartTypeMachineWriter, PartStateWriterBase<PartTypeMachineWriter>> {

    public PartTypeMachineWriter(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(this, Lists.<IAspect>newArrayList(

            ));
    }

    @Override
    public PartStateWriterBase<PartTypeMachineWriter> constructDefaultState() {
        return new PartStateWriterBase<PartTypeMachineWriter>(
            Aspects.REGISTRY.getAspects(this)
                .size());
    }

    @Override
    public int getConsumptionRate(PartStateWriterBase<PartTypeMachineWriter> state) {
        return GeneralConfig.machineWriterBaseConsumption;
    }
}
