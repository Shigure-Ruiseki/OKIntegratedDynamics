package ruiseki.integrateddynamics.core.inventory.container;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.PartStateException;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.client.gui.ExtendedGuiHandler;
import ruiseki.integrateddynamics.core.client.gui.container.GuiPartOffset;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.core.inventory.container.slot.SlotVariable;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.ValueNotifierHelpers;
import ruiseki.okcore.inventory.IGuiContainerProvider;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.inventory.container.ExtendedInventoryContainer;
import ruiseki.okcore.inventory.container.InventoryContainer;
import ruiseki.okcore.inventory.container.button.IButtonActionServer;

/**
 * Container for part offsets.
 *
 * @author rubensworks
 */
public class ContainerPartOffset extends ExtendedInventoryContainer {

    private final PartTarget target;
    private final IPartContainer partContainer;
    private final IPartType partType;
    private final World world;

    private final int lastXValueId;
    private final int lastYValueId;
    private final int lastZValueId;
    private final List<Integer> offsetVariableSlotErrorIds;
    private final List<Integer> offsetVariableSlotFilled;
    private final int maxOffsetId;

    private final SimpleInventory offsetVariablesInventory;
    private boolean dirtyInv = false;

    public ContainerPartOffset(final EntityPlayer player, PartTarget target, IPartContainer partContainer,
        IPartType partType) {
        super(player.inventory, (IGuiContainerProvider) partType);
        this.target = target;
        this.partContainer = partContainer;
        this.partType = partType;
        this.world = player.getEntityWorld();

        addPlayerInventory(player.inventory, 27, getPlayerInventoryOffsetY());

        lastXValueId = getNextValueId();
        lastYValueId = getNextValueId();
        lastZValueId = getNextValueId();
        this.offsetVariableSlotErrorIds = Lists.newArrayList();
        this.offsetVariableSlotFilled = Lists.newArrayList();
        for (int i = 0; i < 3; i++) {
            this.offsetVariableSlotErrorIds.add(getNextValueId());
            this.offsetVariableSlotFilled.add(getNextValueId());
        }
        this.maxOffsetId = getNextValueId();

        putButtonAction(GuiPartOffset.BUTTON_SAVE, new IButtonActionServer<InventoryContainer>() {

            @Override
            public void onAction(int buttonId, InventoryContainer container) {
                if (!(getPartType() instanceof IGuiContainerProvider)
                    || ((IGuiContainerProvider) getPartType()).getContainer() != ContainerPartOffset.this.getClass()) {
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

        offsetVariablesInventory = new SimpleInventory(3, "", 1);
        offsetVariablesInventory.addDirtyMarkListener(() -> dirtyInv = true);
        if (!player.worldObj.isRemote) {
            getPartState().loadInventoryNamed("offsetVariablesInventory", offsetVariablesInventory);
        }
        addSlotToContainer(new SlotVariable(offsetVariablesInventory, 0, 45, 51));
        addSlotToContainer(new SlotVariable(offsetVariablesInventory, 1, 99, 51));
        addSlotToContainer(new SlotVariable(offsetVariablesInventory, 2, 153, 51));
    }

    public IPartType getPartType() {
        return partType;
    }

    public PartTarget getTarget() {
        return target;
    }

    public int getLastXValueId() {
        return lastXValueId;
    }

    public int getLastYValueId() {
        return lastYValueId;
    }

    public int getLastZValueId() {
        return lastZValueId;
    }

    public int getMaxOffsetId() {
        return maxOffsetId;
    }

    protected int getPlayerInventoryOffsetY() {
        return 73;
    }

    @Override
    protected void initializeValues() {
        Vector3i offset = getPartType().getTargetOffset(getPartState());
        ValueNotifierHelpers.setValue(this, lastXValueId, offset.x());
        ValueNotifierHelpers.setValue(this, lastYValueId, offset.y());
        ValueNotifierHelpers.setValue(this, lastZValueId, offset.z());
        ValueNotifierHelpers.setValue(this, maxOffsetId, getPartState().getMaxOffset());
    }

    public int getLastXValue() {
        return ValueNotifierHelpers.getValueInt(this, lastXValueId);
    }

    public int getLastYValue() {
        return ValueNotifierHelpers.getValueInt(this, lastYValueId);
    }

    public int getLastZValue() {
        return ValueNotifierHelpers.getValueInt(this, lastZValueId);
    }

    public int getMaxOffset() {
        return ValueNotifierHelpers.getValueInt(this, maxOffsetId);
    }

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
                updatePartOffset();
            }
        } catch (PartStateException e) {
            player.closeScreen();
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        if (!player.worldObj.isRemote) {
            IPartState partState = getPartState();

            if (this.dirtyInv) {
                this.dirtyInv = false;
                partState.saveInventoryNamed("offsetVariablesInventory", offsetVariablesInventory);
                getPartType().onOffsetVariablesChanged(getTarget(), partState);
            }

            for (int i = 0; i < 3; i++) {
                ValueNotifierHelpers
                    .setValue(this, this.offsetVariableSlotErrorIds.get(i), partState.getOffsetVariableError(i));
                ValueNotifierHelpers.setValue(
                    this,
                    this.offsetVariableSlotFilled.get(i),
                    offsetVariablesInventory.getStackInSlot(i) != null);
            }

            Vector3i offset = getPartType().getTargetOffset(getPartState());
            ValueNotifierHelpers.setValue(this, lastXValueId, offset.x());
            ValueNotifierHelpers.setValue(this, lastYValueId, offset.y());
            ValueNotifierHelpers.setValue(this, lastZValueId, offset.z());
        }
    }

    @Nullable
    public String getOffsetVariableError(int slot) {
        return ValueNotifierHelpers.getValueString(this, this.offsetVariableSlotErrorIds.get(slot));
    }

    public boolean isOffsetVariableFilled(int slot) {
        return ValueNotifierHelpers.getValueBoolean(this, this.offsetVariableSlotFilled.get(slot));
    }

    protected void updatePartOffset() {
        Vector3i offset = new Vector3i(getLastXValue(), getLastYValue(), getLastZValue());
        getPartType().setTargetOffset(getPartState(), offset);
    }
}
