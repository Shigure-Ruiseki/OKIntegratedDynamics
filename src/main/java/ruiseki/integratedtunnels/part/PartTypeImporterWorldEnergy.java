package ruiseki.integratedtunnels.part;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.integratedtunnels.GeneralConfig;
import ruiseki.integratedtunnels.core.part.PartTypeTunnelAspectsWorld;
import ruiseki.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can import energy from the world.
 * 
 * @author rubensworks
 */
public class PartTypeImporterWorldEnergy
    extends PartTypeTunnelAspectsWorld<PartTypeImporterWorldEnergy, PartStateWorld<PartTypeImporterWorldEnergy>> {

    public PartTypeImporterWorldEnergy(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(
                this,
                Lists.<IAspect>newArrayList(
                    TunnelAspects.Write.World.ENTITY_ENERGY_BOOLEAN_IMPORT,
                    TunnelAspects.Write.World.ENTITY_ENERGY_INTEGER_IMPORT));
    }

    @Override
    protected PartStateWorld<PartTypeImporterWorldEnergy> constructDefaultState() {
        return new PartStateWorld<PartTypeImporterWorldEnergy>(
            Aspects.REGISTRY.getWriteAspects(this)
                .size());
    }

    @Override
    public int getConsumptionRate(PartStateWorld<PartTypeImporterWorldEnergy> state) {
        return state.hasVariable() ? GeneralConfig.importerWorldEnergyBaseConsumptionEnabled
            : GeneralConfig.importerWorldEnergyBaseConsumptionDisabled;
    }
}
