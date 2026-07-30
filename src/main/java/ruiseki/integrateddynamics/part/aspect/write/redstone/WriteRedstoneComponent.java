package ruiseki.integrateddynamics.part.aspect.write.redstone;

import net.minecraft.block.Block;

import ruiseki.integrateddynamics.api.block.IDynamicRedstoneBlock;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.okcore.datastructure.DimPos;

/**
 * Default component for writing redstone levels.
 * 
 * @author rubensworks
 */
public class WriteRedstoneComponent implements IWriteRedstoneComponent {

    @Override
    public void setRedstoneLevel(PartTarget target, int level) {
        DimPos dimPos = target.getCenter()
            .getPos();
        IDynamicRedstoneBlock block = getDynamicRedstoneBlock(dimPos);
        if (block != null) {
            block.setRedstoneLevel(
                dimPos.getWorld(),
                dimPos.getBlockPos(),
                target.getCenter()
                    .getSide(),
                level);
        }
    }

    @Override
    public void deactivate(PartTarget target) {
        DimPos dimPos = target.getCenter()
            .getPos();
        IDynamicRedstoneBlock block = getDynamicRedstoneBlock(dimPos);
        if (block != null) {
            block.disableRedstoneAt(
                dimPos.getWorld(),
                dimPos.getBlockPos(),
                target.getCenter()
                    .getSide());
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
