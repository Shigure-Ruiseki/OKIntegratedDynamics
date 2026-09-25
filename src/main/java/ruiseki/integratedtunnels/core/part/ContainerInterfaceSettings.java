package ruiseki.integratedtunnels.core.part;

import java.util.Optional;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.core.inventory.container.ContainerMultipartAspects;
import ruiseki.integrateddynamics.core.inventory.container.ContainerPartSettings;
import ruiseki.okcore.helper.ValueNotifierHelpers;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.network.ExtendedBuffer;

/**
 * @author rubensworks
 */
public class ContainerInterfaceSettings extends ContainerPartSettings {

    private final int lastChannelInterfaceValueId;

    public ContainerInterfaceSettings(InventoryPlayer playerInventory, ExtendedBuffer packetBuffer) {
        this(
            playerInventory,
            new SimpleInventory(0),
            PartHelpers.readPartTarget(packetBuffer),
            Optional.empty(),
            PartHelpers.readPart(packetBuffer));
    }

    public ContainerInterfaceSettings(InventoryPlayer playerInventory, IInventory inventory, PartTarget target,
        Optional<IPartContainer> partContainer, IPartType partType) {
        super(
            ContainerInterfaceSettingsConfig._instance.getInstance(),
            playerInventory,
            inventory,
            target,
            partContainer,
            partType);
        lastChannelInterfaceValueId = getNextValueId();

        putButtonAction(ContainerMultipartAspects.BUTTON_OFFSETS, (s, containerExtended) -> {
            if (!player.worldObj.isRemote) {
                PartHelpers.openContainerPartOffsets((EntityPlayerMP) player, target.getCenter(), partType);
            }
        });
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
            ((IPartTypeInterfacePositionedAddon.IState) getPartState()).getChannelInterface());
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
        ((IPartTypeInterfacePositionedAddon.IState) getPartState()).setChannelInterface(getLastChannelInterfaceValue());
    }
}
