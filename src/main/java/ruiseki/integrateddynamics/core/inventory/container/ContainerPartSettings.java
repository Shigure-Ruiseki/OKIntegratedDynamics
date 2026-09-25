package ruiseki.integrateddynamics.core.inventory.container;

import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.api.PartStateException;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.core.network.PartNetworkElement;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.ValueNotifierHelpers;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.inventory.container.InventoryContainer;
import ruiseki.okcore.network.ExtendedBuffer;

/**
 * Container for part settings.
 *
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class ContainerPartSettings extends InventoryContainer {

    public static final String BUTTON_SAVE = "button_save";
    public static final String BUTTON_SETTINGS = "button_settings";
    private static final int PAGE_SIZE = 3;

    private final PartTarget target;
    private final Optional<IPartContainer> partContainer;
    private final IPartType partType;
    private final World world;

    private final int lastUpdateValueId;
    private final int lastPriorityValueId;
    private final int lastChannelValueId;
    private final int lastSideValueId;
    private final int lastMinUpdateValueId;

    public ContainerPartSettings(InventoryPlayer playerInventory, ExtendedBuffer packetBuffer) {
        this(
            playerInventory,
            new SimpleInventory(0),
            PartHelpers.readPartTarget(packetBuffer),
            Optional.empty(),
            PartHelpers.readPart(packetBuffer));
    }

    public ContainerPartSettings(InventoryPlayer playerInventory, IInventory inventory, PartTarget target,
        Optional<IPartContainer> partContainer, IPartType partType) {
        this(
            ContainerPartSettingsConfig._instance.getInstance(),
            playerInventory,
            inventory,
            target,
            partContainer,
            partType);
    }

    public ContainerPartSettings(@Nullable ContainerType<?> type, InventoryPlayer playerInventory, IInventory inventory,
        PartTarget target, Optional<IPartContainer> partContainer, IPartType partType) {
        super(type, playerInventory, inventory);
        this.target = target;
        this.partContainer = partContainer;
        this.partType = partType;
        this.world = player.getEntityWorld();

        addPlayerInventory(player.inventory, 27, getPlayerInventoryOffsetY());

        lastUpdateValueId = getNextValueId();
        lastPriorityValueId = getNextValueId();
        lastChannelValueId = getNextValueId();
        lastSideValueId = getNextValueId();
        lastMinUpdateValueId = getNextValueId();

        putButtonAction(ContainerPartSettings.BUTTON_SAVE, (s, containerExtended) -> {
            if (!world.isRemote) {
                PartHelpers.openContainerPart((EntityPlayerMP) player, target.getCenter(), getPartType());
            }
        });
    }

    protected int getPlayerInventoryOffsetY() {
        return 107;
    }

    @Override
    protected void initializeValues() {
        ValueNotifierHelpers.setValue(this, lastUpdateValueId, getPartType().getUpdateInterval(getPartState()));
        ValueNotifierHelpers.setValue(this, lastPriorityValueId, getPartType().getPriority(getPartState()));
        ValueNotifierHelpers.setValue(this, lastChannelValueId, getPartType().getChannel(getPartState()));
        ForgeDirection targetSide = getPartType().getTargetSideOverride(getPartState());
        ValueNotifierHelpers.setValue(this, lastSideValueId, targetSide == null ? -1 : targetSide.ordinal());
        ValueNotifierHelpers
            .setValue(this, lastMinUpdateValueId, getPartType().getMinimumUpdateInterval(getPartState()));
    }

    public int getLastUpdateValue() {
        return ValueNotifierHelpers.getValueInt(this, lastUpdateValueId);
    }

    public int getLastPriorityValue() {
        return ValueNotifierHelpers.getValueInt(this, lastPriorityValueId);
    }

    public int getLastChannelValue() {
        return ValueNotifierHelpers.getValueInt(this, lastChannelValueId);
    }

    public int getLastSideValue() {
        return ValueNotifierHelpers.getValueInt(this, lastSideValueId);
    }

    public int getLastMinUpdateValue() {
        return ValueNotifierHelpers.getValueInt(this, lastMinUpdateValueId);
    }

    public IPartState getPartState() {
        return partContainer.get()
            .getPartState(
                getTarget().getCenter()
                    .getSide());
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return PartHelpers.canInteractWith(getTarget(), player, this.partContainer.get());
    }

    @Override
    protected int getSizeInventory() {
        return 0;
    }

    @Override
    public void onUpdate(int valueId, NBTTagCompound value) {
        super.onUpdate(valueId, value);
        try {
            if (!world.isRemote) {
                getPartType().setUpdateInterval(getPartState(), getLastUpdateValue());
                DimPos dimPos = getTarget().getCenter()
                    .getPos();
                INetwork network = NetworkHelpers.getNetworkChecked(
                    dimPos.getWorld(),
                    dimPos.getBlockPos(),
                    getTarget().getCenter()
                        .getSide());

                PartTarget target = getTarget();
                updatePartSettings();
                PartNetworkElement networkElement = new PartNetworkElement<>(getPartType(), target.getCenter());
                network.setPriorityAndChannel(networkElement, getLastPriorityValue(), getLastChannelValue());
            }
        } catch (PartStateException e) {
            player.closeScreen();
        }
    }

    protected void updatePartSettings() {
        getPartType().setUpdateInterval(getPartState(), getLastUpdateValue());
        ForgeDirection targetSide = getLastSideValue() >= 0 ? ForgeDirection.VALID_DIRECTIONS[getLastSideValue()]
            : null;
        getPartType().setTargetSideOverride(getPartState(), targetSide);
    }
}
