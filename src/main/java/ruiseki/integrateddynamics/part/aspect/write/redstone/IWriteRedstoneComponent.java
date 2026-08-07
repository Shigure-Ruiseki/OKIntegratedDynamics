package ruiseki.integrateddynamics.part.aspect.write.redstone;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integrateddynamics.api.block.IDynamicRedstone;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.okcore.datastructure.DimPos;

/**
 * Interface for redstone writing the component.
 *
 * @author rubensworks
 */
public interface IWriteRedstoneComponent {

    public void setRedstoneLevel(PartTarget target, int level);

    public void deactivate(PartTarget target);

    public IDynamicRedstone getDynamicRedstoneBlock(DimPos dimPos, ForgeDirection side);

}
