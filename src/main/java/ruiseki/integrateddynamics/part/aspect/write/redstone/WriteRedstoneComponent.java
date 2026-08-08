package ruiseki.integrateddynamics.part.aspect.write.redstone;

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
public class WriteRedstoneComponent implements IWriteRedstoneComponent {

    @Override
    public void setRedstoneLevel(PartTarget target, int level, boolean strongPower) {
        DimPos dimPos = target.getCenter()
            .getPos();
        IDynamicRedstone block = getDynamicRedstoneBlock(
            dimPos,
            target.getCenter()
                .getSide());
        if (block != null) {
            block.setRedstoneLevel(level, strongPower);
        }
    }

    @Override
    public void deactivate(PartTarget target) {
        DimPos dimPos = target.getCenter()
            .getPos();
        IDynamicRedstone block = getDynamicRedstoneBlock(
            dimPos,
            target.getCenter()
                .getSide());
        if (block != null && !dimPos.getWorld().isRemote) {
            block.setRedstoneLevel(-1, false);
        }
    }

    @Override
    public IDynamicRedstone getDynamicRedstoneBlock(DimPos dimPos, ForgeDirection side) {
        return CapabilityHelpers.getCapability(dimPos, DynamicRedstoneConfig.CAPABILITY, side)
            .getOrNull();
    }
}
