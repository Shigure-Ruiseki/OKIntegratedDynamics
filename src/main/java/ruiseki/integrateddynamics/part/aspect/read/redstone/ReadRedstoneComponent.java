package ruiseki.integrateddynamics.part.aspect.read.redstone;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integrateddynamics.api.block.IDynamicRedstone;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.capability.dynamicredstone.DynamicRedstoneConfig;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.CapabilityHelpers;

/**
 * Default component for writing redstone levels.
 *
 * @author rubensworks
 */
public class ReadRedstoneComponent implements IReadRedstoneComponent {

    @Override
    public void setAllowRedstoneInput(PartTarget target, boolean allow) {
        DimPos dimPos = target.getCenter()
            .getPos();
        IDynamicRedstone block = getDynamicRedstoneBlock(
            dimPos,
            target.getCenter()
                .getSide());
        if (block != null) {
            block.setAllowRedstoneInput(allow);
        }
    }

    @Override
    public IDynamicRedstone getDynamicRedstoneBlock(DimPos dimPos, ForgeDirection side) {
        return CapabilityHelpers.getCapability(dimPos, DynamicRedstoneConfig.CAPABILITY, side)
            .getOrNull();
    }
}
