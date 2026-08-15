package ruiseki.integratedtunnels.part;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.integratedtunnels.GeneralConfig;
import ruiseki.integratedtunnels.core.part.PartTypeTunnelAspectsWorld;
import ruiseki.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can export energy to the world.
 * 
 * @author rubensworks
 */
public class PartTypeExporterWorldEnergy
    extends PartTypeTunnelAspectsWorld<PartTypeExporterWorldEnergy, PartStateWorld<PartTypeExporterWorldEnergy>> {

    public PartTypeExporterWorldEnergy(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(
                this,
                Lists.<IAspect>newArrayList(
                    TunnelAspects.Write.World.ENTITY_ENERGY_BOOLEAN_EXPORT,
                    TunnelAspects.Write.World.ENTITY_ENERGY_INTEGER_EXPORT));
    }

    @Override
    protected PartStateWorld<PartTypeExporterWorldEnergy> constructDefaultState() {
        return new PartStateWorld<PartTypeExporterWorldEnergy>(
            Aspects.REGISTRY.getWriteAspects(this)
                .size());
    }

    @Override
    public int getConsumptionRate(PartStateWorld<PartTypeExporterWorldEnergy> state) {
        return state.hasVariable() ? GeneralConfig.exporterWorldEnergyBaseConsumptionEnabled
            : GeneralConfig.exporterWorldEnergyBaseConsumptionDisabled;
    }
}
