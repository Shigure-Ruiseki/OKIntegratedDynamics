package ruiseki.integratedtunnels.part;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.integratedtunnels.GeneralConfig;
import ruiseki.integratedtunnels.core.part.PartTypeTunnelAspects;
import ruiseki.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can import energy.
 * 
 * @author rubensworks
 */
public class PartTypeImporterEnergy
    extends PartTypeTunnelAspects<PartTypeImporterEnergy, PartStateEnergy<PartTypeImporterEnergy>> {

    public PartTypeImporterEnergy(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(
                this,
                Lists.<IAspect>newArrayList(
                    TunnelAspects.Write.Energy.BOOLEAN_IMPORT,
                    TunnelAspects.Write.Energy.INTEGER_IMPORT));
    }

    @Override
    protected PartStateEnergy<PartTypeImporterEnergy> constructDefaultState() {
        return new PartStateEnergy<PartTypeImporterEnergy>(
            Aspects.REGISTRY.getWriteAspects(this)
                .size(),
            true,
            false);
    }

    @Override
    public int getConsumptionRate(PartStateEnergy<PartTypeImporterEnergy> state) {
        return GeneralConfig.importerEnergyBaseConsumption;
    }
}
