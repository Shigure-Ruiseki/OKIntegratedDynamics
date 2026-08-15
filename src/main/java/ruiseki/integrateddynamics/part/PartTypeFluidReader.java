package ruiseki.integrateddynamics.part;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.core.part.read.PartStateReaderBase;
import ruiseki.integrateddynamics.core.part.read.PartTypeReadBase;
import ruiseki.integrateddynamics.part.aspect.Aspects;

/**
 * An fluid reader part.
 *
 * @author rubensworks
 */
public class PartTypeFluidReader
    extends PartTypeReadBase<PartTypeFluidReader, PartStateReaderBase<PartTypeFluidReader>> {

    public PartTypeFluidReader(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(
                this,
                Lists.<IAspect>newArrayList(
                    Aspects.Read.Fluid.BOOLEAN_FULL,
                    Aspects.Read.Fluid.BOOLEAN_EMPTY,
                    Aspects.Read.Fluid.BOOLEAN_NONEMPTY,
                    Aspects.Read.Fluid.BOOLEAN_APPLICABLE,
                    Aspects.Read.Fluid.INTEGER_AMOUNT,
                    Aspects.Read.Fluid.INTEGER_AMOUNTTOTAL,
                    Aspects.Read.Fluid.INTEGER_CAPACITY,
                    Aspects.Read.Fluid.INTEGER_CAPACITYTOTAL,
                    Aspects.Read.Fluid.INTEGER_TANKS,
                    Aspects.Read.Fluid.DOUBLE_FILLRATIO,
                    Aspects.Read.Fluid.LIST_TANKFLUIDS,
                    Aspects.Read.Fluid.LIST_TANKCAPACITIES,
                    Aspects.Read.Fluid.FLUIDSTACK,
                    Aspects.Read.Fluid.BLOCK));
    }

    @Override
    public PartStateReaderBase<PartTypeFluidReader> constructDefaultState() {
        return new PartStateReaderBase<PartTypeFluidReader>();
    }

    @Override
    public int getConsumptionRate(PartStateReaderBase<PartTypeFluidReader> state) {
        return GeneralConfig.fluidReaderBaseConsumption;
    }
}
