package ruiseki.integratedtunnels.part;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.integratedtunnels.GeneralConfig;
import ruiseki.integratedtunnels.core.part.PartTypeTunnelAspectsWorld;
import ruiseki.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can export items to the world.
 *
 * @author rubensworks
 */
public class PartTypeExporterWorldItem
    extends PartTypeTunnelAspectsWorld<PartTypeExporterWorldItem, PartStateWorld<PartTypeExporterWorldItem>> {

    public PartTypeExporterWorldItem(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(
                this,
                Lists.<IAspect>newArrayList(
                    TunnelAspects.Write.World.ENTITYITEM_BOOLEAN_EXPORT,
                    TunnelAspects.Write.World.ENTITYITEM_INTEGER_EXPORT,
                    TunnelAspects.Write.World.ENTITYITEM_ITEMSTACK_EXPORT,
                    TunnelAspects.Write.World.ENTITYITEM_LISTITEMSTACK_EXPORT,
                    TunnelAspects.Write.World.ENTITYITEM_PREDICATEITEMSTACK_EXPORT,
                    TunnelAspects.Write.World.ENTITYITEM_NBT_EXPORT,

                    TunnelAspects.Write.World.ENTITY_ITEM_BOOLEAN_EXPORT,
                    TunnelAspects.Write.World.ENTITY_ITEM_INTEGER_EXPORT,
                    TunnelAspects.Write.World.ENTITY_ITEM_INTEGER_SLOT_EXPORT,
                    TunnelAspects.Write.World.ENTITY_ITEM_ITEMSTACK_EXPORT,
                    TunnelAspects.Write.World.ENTITY_ITEM_LISTITEMSTACK_EXPORT,
                    TunnelAspects.Write.World.ENTITY_ITEM_PREDICATEITEMSTACK_EXPORT,
                    TunnelAspects.Write.World.ENTITY_ITEM_NBT_EXPORT));
    }

    @Override
    protected PartStateWorld<PartTypeExporterWorldItem> constructDefaultState() {
        return new PartStateWorld<PartTypeExporterWorldItem>(
            Aspects.REGISTRY.getWriteAspects(this)
                .size());
    }

    @Override
    public int getConsumptionRate(PartStateWorld<PartTypeExporterWorldItem> state) {
        return state.hasVariable() ? GeneralConfig.exporterWorldItemBaseConsumptionEnabled
            : GeneralConfig.exporterWorldItemBaseConsumptionDisabled;
    }
}
