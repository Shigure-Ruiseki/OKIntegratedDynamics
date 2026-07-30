package ruiseki.integrateddynamics.part.aspect.write.redstone;

import ruiseki.integrateddynamics.api.block.IDynamicRedstoneBlock;
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

    public IDynamicRedstoneBlock getDynamicRedstoneBlock(DimPos dimPos);

}
