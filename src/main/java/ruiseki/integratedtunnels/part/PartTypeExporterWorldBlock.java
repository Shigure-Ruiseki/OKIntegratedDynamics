package ruiseki.integratedtunnels.part;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.integratedtunnels.GeneralConfig;
import ruiseki.integratedtunnels.core.part.PartTypeTunnelAspectsWorld;
import ruiseki.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can export block to the world.
 * 
 * @author rubensworks
 */
public class PartTypeExporterWorldBlock
    extends PartTypeTunnelAspectsWorld<PartTypeExporterWorldBlock, PartStateWorld<PartTypeExporterWorldBlock>> {

    public PartTypeExporterWorldBlock(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(
                this,
                Lists.<IAspect>newArrayList(
                    TunnelAspects.Write.World.BLOCK_BOOLEAN_EXPORT,
                    TunnelAspects.Write.World.BLOCK_ITEMSTACK_EXPORT,
                    TunnelAspects.Write.World.BLOCK_LISTITEMSTACK_EXPORT,
                    TunnelAspects.Write.World.BLOCK_PREDICATEITEMSTACK_EXPORT,
                    TunnelAspects.Write.World.BLOCK_NBTITEMSTACK_EXPORT,
                    TunnelAspects.Write.World.BLOCK_BLOCK_EXPORT,
                    TunnelAspects.Write.World.BLOCK_LISTBLOCK_EXPORT,
                    TunnelAspects.Write.World.BLOCK_PREDICATEBLOCK_EXPORT));
    }

    @Override
    protected PartStateWorld<PartTypeExporterWorldBlock> constructDefaultState() {
        return new PartStateWorld<PartTypeExporterWorldBlock>(
            Aspects.REGISTRY.getWriteAspects(this)
                .size());
    }

    @Override
    public int getConsumptionRate(PartStateWorld<PartTypeExporterWorldBlock> state) {
        return state.hasVariable() ? GeneralConfig.exporterWorldBlockBaseConsumptionEnabled
            : GeneralConfig.exporterWorldBlockBaseConsumptionDisabled;
    }
}
