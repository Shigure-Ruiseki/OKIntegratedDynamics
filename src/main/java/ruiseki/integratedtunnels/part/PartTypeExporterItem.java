package ruiseki.integratedtunnels.part;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.integratedtunnels.GeneralConfig;
import ruiseki.integratedtunnels.core.part.PartTypeTunnelAspects;
import ruiseki.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can export items.
 * 
 * @author rubensworks
 */
public class PartTypeExporterItem
    extends PartTypeTunnelAspects<PartTypeExporterItem, PartStateItem<PartTypeExporterItem>> {

    public PartTypeExporterItem(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(
                this,
                Lists.<IAspect>newArrayList(
                    TunnelAspects.Write.Item.BOOLEAN_EXPORT,
                    TunnelAspects.Write.Item.INTEGER_EXPORT,
                    TunnelAspects.Write.Item.INTEGER_SLOT_EXPORT,
                    TunnelAspects.Write.Item.ITEMSTACK_EXPORT,
                    TunnelAspects.Write.Item.LIST_EXPORT,
                    TunnelAspects.Write.Item.PREDICATE_EXPORT,
                    TunnelAspects.Write.Item.NBT_EXPORT));
    }

    @Override
    protected PartStateItem<PartTypeExporterItem> constructDefaultState() {
        return new PartStateItem<PartTypeExporterItem>(
            Aspects.REGISTRY.getWriteAspects(this)
                .size(),
            false,
            true);
    }

    @Override
    public int getConsumptionRate(PartStateItem<PartTypeExporterItem> state) {
        return GeneralConfig.exporterItemBaseConsumption;
    }
}
