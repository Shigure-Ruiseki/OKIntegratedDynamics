package ruiseki.integrateddynamics.core.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.PartStateException;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.client.gui.ExtendedGuiHandler;
import ruiseki.integrateddynamics.core.client.gui.container.GuiPartSettings;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.core.network.PartNetworkElement;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.ValueNotifierHelpers;
import ruiseki.okcore.inventory.IGuiContainerProvider;
import ruiseki.okcore.inventory.container.ExtendedInventoryContainer;
import ruiseki.okcore.inventory.container.InventoryContainer;
import ruiseki.okcore.inventory.container.button.IButtonActionServer;

/**
 * Container for part settings.
 *
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class ContainerPartSettings extends ExtendedInventoryContainer {

    public static final int BUTTON_SETTINGS = 1;
    private static final int PAGE_SIZE = 3;

    private final PartTarget target;
    private final IPartContainer partContainer;
    private final IPartType partType;
    private final World world;
    private final BlockPos pos;

    private final int lastUpdateValueId;
    private final int lastPriorityValueId;

    /**
     * Make a new instance.
     *
     * @param target        The target.
     * @param player        The player.
     * @param partContainer The part container.
     * @param partType      The part type.
     */
    public ContainerPartSettings(final EntityPlayer player, PartTarget target, IPartContainer partContainer,
        IPartType partType) {
        super(player.inventory, (IGuiContainerProvider) partType);
        this.target = target;
        this.partContainer = partContainer;
        this.partType = partType;
        this.world = player.getEntityWorld();
        ChunkCoordinates coordinates = player.getPlayerCoordinates();
        this.pos = new BlockPos(coordinates.posX, coordinates.posY, coordinates.posZ);

        addPlayerInventory(player.inventory, 8, 57);

        lastUpdateValueId = getNextValueId();
        lastPriorityValueId = getNextValueId();

        putButtonAction(GuiPartSettings.BUTTON_SAVE, new IButtonActionServer<InventoryContainer>() {

            @Override
            public void onAction(int buttonId, InventoryContainer container) {
                if (!(getPartType() instanceof IGuiContainerProvider)
                    || ((IGuiContainerProvider) getPartType()).getContainer()
                        != ContainerPartSettings.this.getClass()) {
                    if (!world.isRemote) {
                        IntegratedDynamics._instance.getGuiHandler()
                            .setTemporaryData(
                                ExtendedGuiHandler.PART,
                                getTarget().getCenter()
                                    .getSide());
                        BlockPos pos = getTarget().getCenter()
                            .getPos()
                            .getBlockPos();
                        player.openGui(
                            IntegratedDynamics._instance.getModId(),
                            ((IGuiContainerProvider) getPartType()).getGuiID(),
                            world,
                            pos.getX(),
                            pos.getY(),
                            pos.getZ());
                    }
                } else {
                    player.closeScreen();
                }
            }
        });
    }

    public int getLastUpdateValueId() {
        return lastUpdateValueId;
    }

    @Override
    protected void initializeValues() {
        ValueNotifierHelpers.setValue(this, lastUpdateValueId, getPartType().getUpdateInterval(getPartState()));
        ValueNotifierHelpers.setValue(this, lastPriorityValueId, getPartType().getPriority(getPartState()));
    }

    public int getLastUpdateValue() {
        return ValueNotifierHelpers.getValueInt(this, lastUpdateValueId);
    }

    public int getLastPriorityValue() {
        return ValueNotifierHelpers.getValueInt(this, lastPriorityValueId);
    }

    @SuppressWarnings("unchecked")
    public IPartState getPartState() {
        return partContainer.getPartState(
            getTarget().getCenter()
                .getSide());
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return PartHelpers.canInteractWith(getTarget(), player, this.partContainer);
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
                INetwork network = NetworkHelpers.getNetwork(dimPos.getWorld(), dimPos.getBlockPos());
                PartNetworkElement networkElement = new PartNetworkElement(getPartType(), getTarget());
                network.setPriority(networkElement, getLastPriorityValue());
            }
        } catch (PartStateException e) {
            player.closeScreen();
        }
    }
}
