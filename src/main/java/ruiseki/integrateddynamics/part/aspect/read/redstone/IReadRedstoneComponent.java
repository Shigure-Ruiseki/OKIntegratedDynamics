package ruiseki.integrateddynamics.part.aspect.read.redstone;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integrateddynamics.api.block.IDynamicRedstone;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.okcore.datastructure.DimPos;

/**
 * Interface for redstone reading the component.
 *
 * @author rubensworks
 */
public interface IReadRedstoneComponent {

    public void setAllowRedstoneInput(PartTarget target, boolean allow);

    public IDynamicRedstone getDynamicRedstoneBlock(DimPos dimPos, ForgeDirection side);

}
