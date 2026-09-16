package ruiseki.integrateddynamics.client.model;

import net.minecraft.nbt.NBTTagCompound;

import lombok.Data;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.okcore.datastructure.EnumFacingMap;

/**
 * @author rubensworks
 */
@Data
public class CableRenderState implements IRenderState {

    private final boolean realCable;
    private final EnumFacingMap<Boolean> connected;
    private final EnumFacingMap<PartHelpers.PartStateHolder<?, ?>> partData;
    private final NBTTagCompound facadeBlockTag;

}
