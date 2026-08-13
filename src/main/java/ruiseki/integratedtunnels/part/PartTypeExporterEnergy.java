package ruiseki.integratedtunnels.part;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.integratedtunnels.GeneralConfig;
import ruiseki.integratedtunnels.core.part.PartTypeTunnelAspects;
import ruiseki.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can export energy.
 * 
 * @author rubensworks
 */
public class PartTypeExporterEnergy
    extends PartTypeTunnelAspects<PartTypeExporterEnergy, PartStateEnergy<PartTypeExporterEnergy>> {

    public PartTypeExporterEnergy(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(
                this,
                Lists.<IAspect>newArrayList(
                    TunnelAspects.Write.Energy.BOOLEAN_EXPORT,
                    TunnelAspects.Write.Energy.INTEGER_EXPORT));
    }

    @Override
    protected PartStateEnergy<PartTypeExporterEnergy> constructDefaultState() {
        return new PartStateEnergy<PartTypeExporterEnergy>(
            Aspects.REGISTRY.getWriteAspects(this)
                .size(),
            false,
            true);
    }

    @Override
    public int getConsumptionRate(PartStateEnergy<PartTypeExporterEnergy> state) {
        return GeneralConfig.exporterEnergyBaseConsumption;
    }
}
