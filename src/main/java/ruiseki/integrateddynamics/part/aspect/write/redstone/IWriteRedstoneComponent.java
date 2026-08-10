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

    public void setRedstoneLevel(PartTarget target, int level, boolean strongPower);

    public void setLastPulseValue(PartTarget target, int value);

    public int getLastPulseValue(PartTarget target);

    public void deactivate(PartTarget target);

    public IDynamicRedstone getDynamicRedstoneBlock(DimPos dimPos, ForgeDirection side);

}
