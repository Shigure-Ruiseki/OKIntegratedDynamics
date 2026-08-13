package ruiseki.integratedtunnels.part;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.integratedtunnels.GeneralConfig;
import ruiseki.integratedtunnels.core.part.PartTypeTunnelAspects;
import ruiseki.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can import fluids.
 * 
 * @author rubensworks
 */
public class PartTypeImporterFluid
    extends PartTypeTunnelAspects<PartTypeImporterFluid, PartStateFluid<PartTypeImporterFluid>> {

    public PartTypeImporterFluid(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(
                this,
                Lists.<IAspect>newArrayList(
                    TunnelAspects.Write.Fluid.BOOLEAN_IMPORT,
                    TunnelAspects.Write.Fluid.INTEGER_IMPORT,
                    TunnelAspects.Write.Fluid.FLUIDSTACK_IMPORT,
                    TunnelAspects.Write.Fluid.LIST_IMPORT,
                    TunnelAspects.Write.Fluid.PREDICATE_IMPORT,
                    TunnelAspects.Write.Fluid.NBT_IMPORT));
    }

    @Override
    protected PartStateFluid<PartTypeImporterFluid> constructDefaultState() {
        return new PartStateFluid<PartTypeImporterFluid>(
            Aspects.REGISTRY.getWriteAspects(this)
                .size(),
            true,
            false);
    }

    @Override
    public int getConsumptionRate(PartStateFluid<PartTypeImporterFluid> state) {
        return GeneralConfig.importerFluidBaseConsumption;
    }
}
