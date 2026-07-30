package ruiseki.integrateddynamics.part.aspect.read.redstone;

import net.minecraft.block.Block;

import ruiseki.integrateddynamics.api.block.IDynamicRedstoneBlock;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.okcore.datastructure.DimPos;

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
        IDynamicRedstoneBlock block = getDynamicRedstoneBlock(dimPos);
        if (block != null) {
            block.setAllowRedstoneInput(
                dimPos.getWorld(),
                dimPos.getBlockPos(),
                target.getCenter()
                    .getSide(),
                allow);
        }
    }

    @Override
    public IDynamicRedstoneBlock getDynamicRedstoneBlock(DimPos dimPos) {
        Block block = dimPos.getBlockPos()
            .getBlock(dimPos.getWorld());
        if (block instanceof IDynamicRedstoneBlock) {
            return (IDynamicRedstoneBlock) block;
        }
        return null;
    }
}
