package ruiseki.integratedtunnels.part;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.integratedtunnels.GeneralConfig;
import ruiseki.integratedtunnels.core.part.PartTypeTunnelAspects;
import ruiseki.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can export fluids.
 * 
 * @author rubensworks
 */
public class PartTypeExporterFluid
    extends PartTypeTunnelAspects<PartTypeExporterFluid, PartStateFluid<PartTypeExporterFluid>> {

    public PartTypeExporterFluid(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(
                this,
                Lists.<IAspect>newArrayList(
                    TunnelAspects.Write.Fluid.BOOLEAN_EXPORT,
                    TunnelAspects.Write.Fluid.INTEGER_EXPORT,
                    TunnelAspects.Write.Fluid.FLUIDSTACK_EXPORT,
                    TunnelAspects.Write.Fluid.LIST_EXPORT,
                    TunnelAspects.Write.Fluid.PREDICATE_EXPORT,
                    TunnelAspects.Write.Fluid.NBT_EXPORT));
    }

    @Override
    protected PartStateFluid<PartTypeExporterFluid> constructDefaultState() {
        return new PartStateFluid<PartTypeExporterFluid>(
            Aspects.REGISTRY.getWriteAspects(this)
                .size(),
            false,
            true);
    }

    @Override
    public int getConsumptionRate(PartStateFluid<PartTypeExporterFluid> state) {
        return GeneralConfig.exporterFluidBaseConsumption;
    }
}
