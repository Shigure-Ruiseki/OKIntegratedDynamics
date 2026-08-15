package ruiseki.integratedtunnels.part;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.integratedtunnels.GeneralConfig;
import ruiseki.integratedtunnels.core.part.PartTypeTunnelAspectsWorld;
import ruiseki.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can export fluids to the world.
 * 
 * @author rubensworks
 */
public class PartTypeExporterWorldFluid
    extends PartTypeTunnelAspectsWorld<PartTypeExporterWorldFluid, PartStateWorld<PartTypeExporterWorldFluid>> {

    public PartTypeExporterWorldFluid(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(
                this,
                Lists.<IAspect>newArrayList(
                    TunnelAspects.Write.World.FLUID_BOOLEAN_EXPORT,
                    TunnelAspects.Write.World.FLUID_FLUIDSTACK_EXPORT,
                    TunnelAspects.Write.World.FLUID_LIST_EXPORT,
                    TunnelAspects.Write.World.FLUID_PREDICATE_EXPORT,
                    TunnelAspects.Write.World.FLUID_NBT_EXPORT,

                    TunnelAspects.Write.World.ENTITY_FLUID_BOOLEAN_EXPORT,
                    TunnelAspects.Write.World.ENTITY_FLUID_INTEGER_EXPORT,
                    TunnelAspects.Write.World.ENTITY_FLUID_FLUIDSTACK_EXPORT,
                    TunnelAspects.Write.World.ENTITY_FLUID_LISTFLUIDSTACK_EXPORT,
                    TunnelAspects.Write.World.ENTITY_FLUID_PREDICATEFLUIDSTACK_EXPORT,
                    TunnelAspects.Write.World.ENTITY_FLUID_NBT_EXPORT));
    }

    @Override
    protected PartStateWorld<PartTypeExporterWorldFluid> constructDefaultState() {
        return new PartStateWorld<PartTypeExporterWorldFluid>(
            Aspects.REGISTRY.getWriteAspects(this)
                .size());
    }

    @Override
    public int getConsumptionRate(PartStateWorld<PartTypeExporterWorldFluid> state) {
        return state.hasVariable() ? GeneralConfig.exporterWorldFluidBaseConsumptionEnabled
            : GeneralConfig.exporterWorldFluidBaseConsumptionDisabled;
    }
}
