package ruiseki.integrateddynamics.core.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.client.gui.ExtendedGuiHandler;
import ruiseki.integrateddynamics.core.client.gui.container.GuiPartSettings;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.MinecraftHelpers;
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

        addPlayerInventory(player.inventory, 8, 31);

        lastUpdateValueId = getNextValueId();

        putButtonAction(GuiPartSettings.BUTTON_SAVE, new IButtonActionServer<InventoryContainer>() {

            @Override
            public void onAction(int buttonId, InventoryContainer container) {
                IntegratedDynamics._instance.getGuiHandler()
                    .setTemporaryData(
                        ExtendedGuiHandler.PART,
                        getTarget().getCenter()
                            .getSide());
                BlockPos pos = getTarget().getCenter()
                    .getPos()
                    .getBlockPos();
                if (!MinecraftHelpers.isClientSide()) {
                    player.openGui(
                        IntegratedDynamics._instance.getModId(),
                        ((IGuiContainerProvider) getPartType()).getGuiID(),
                        world,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ());
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
    }

    public int getLastUpdateValue() {
        return ValueNotifierHelpers.getValueInt(this, lastUpdateValueId);
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
        if (!MinecraftHelpers.isClientSide()) {
            getPartType().setUpdateInterval(getPartState(), getLastUpdateValue());
        }
    }
}
