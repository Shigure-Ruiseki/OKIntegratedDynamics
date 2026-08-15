package ruiseki.integratedtunnels.core.part;

import net.minecraft.entity.player.EntityPlayer;

import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.inventory.container.ContainerPartSettings;
import ruiseki.okcore.helper.ValueNotifierHelpers;

/**
 * @author rubensworks
 */
public class ContainerInterfaceSettings extends ContainerPartSettings {

    private final int lastChannelInterfaceValueId;

    public ContainerInterfaceSettings(EntityPlayer player, PartTarget target, IPartContainer partContainer,
        IPartType partType) {
        super(player, target, partContainer, partType);
        lastChannelInterfaceValueId = getNextValueId();
    }

    @Override
    protected int getPlayerInventoryOffsetY() {
        return 134;
    }

    @Override
    protected void initializeValues() {
        super.initializeValues();
        ValueNotifierHelpers.setValue(
            this,
            lastChannelInterfaceValueId,
            ((PartTypeInterfacePositionedAddon.State) getPartState()).getChannelInterface());
    }

    public int getLastChannelInterfaceValueId() {
        return lastChannelInterfaceValueId;
    }

    public int getLastChannelInterfaceValue() {
        return ValueNotifierHelpers.getValueInt(this, lastChannelInterfaceValueId);
    }

    @Override
    protected void updatePartSettings() {
        super.updatePartSettings();
        ((PartTypeInterfacePositionedAddon.State) getPartState()).setChannelInterface(getLastChannelInterfaceValue());
    }
}
