package ruiseki.integratedtunnels.part;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.integratedtunnels.GeneralConfig;
import ruiseki.integratedtunnels.core.part.PartTypeTunnelAspects;
import ruiseki.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can import items.
 * 
 * @author rubensworks
 */
public class PartTypeImporterItem
    extends PartTypeTunnelAspects<PartTypeImporterItem, PartStateItem<PartTypeImporterItem>> {

    public PartTypeImporterItem(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(
                this,
                Lists.<IAspect>newArrayList(
                    TunnelAspects.Write.Item.BOOLEAN_IMPORT,
                    TunnelAspects.Write.Item.INTEGER_IMPORT,
                    TunnelAspects.Write.Item.INTEGER_SLOT_IMPORT,
                    TunnelAspects.Write.Item.ITEMSTACK_IMPORT,
                    TunnelAspects.Write.Item.LIST_IMPORT,
                    TunnelAspects.Write.Item.PREDICATE_IMPORT,
                    TunnelAspects.Write.Item.NBT_IMPORT));
    }

    @Override
    protected PartStateItem<PartTypeImporterItem> constructDefaultState() {
        return new PartStateItem<PartTypeImporterItem>(
            Aspects.REGISTRY.getWriteAspects(this)
                .size(),
            true,
            false);
    }

    @Override
    public int getConsumptionRate(PartStateItem<PartTypeImporterItem> state) {
        return GeneralConfig.importerItemBaseConsumption;
    }
}
