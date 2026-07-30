package ruiseki.integrateddynamics.part.aspect.read.redstone;

import ruiseki.integrateddynamics.api.block.IDynamicRedstoneBlock;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.okcore.datastructure.DimPos;

/**
 * Interface for redstone reading the component.
 * 
 * @author rubensworks
 */
public interface IReadRedstoneComponent {

    public void setAllowRedstoneInput(PartTarget target, boolean allow);

    public IDynamicRedstoneBlock getDynamicRedstoneBlock(DimPos dimPos);

}
