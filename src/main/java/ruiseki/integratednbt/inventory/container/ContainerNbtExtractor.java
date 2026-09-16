package ruiseki.integratednbt.inventory.container;

import java.util.Objects;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.world.World;

import ruiseki.integrateddynamics.api.PartStateException;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeNbt;
import ruiseki.integratednbt.IntegratedNbt;
import ruiseki.integratednbt.evaluate.NbtExtractorOutputMode;
import ruiseki.integratednbt.evaluate.nbt.path.SegmentedNbtPath;
import ruiseki.integratednbt.helpers.VariableHelpers;
import ruiseki.integratednbt.network.packet.UpdateClientNbtExtractorPacket;
import ruiseki.integratednbt.tileentity.TileNbtExtractor;
import ruiseki.okcore.datastructure.Wrapper;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.inventory.container.TileInventoryContainerConfigurable;
import ruiseki.okcore.inventory.slot.SlotExtended;

public class ContainerNbtExtractor extends TileInventoryContainerConfigurable<TileNbtExtractor> {

    /**
     * A slot whose position is determined later.
     */
    private static class ResponsiveSlot extends SlotExtended {

        private int baseX;
        private int baseY;

        public ResponsiveSlot(IInventory inventoryIn, int index, int baseX, int baseY) {
            super(inventoryIn, index, baseX, baseY);
            this.baseX = baseX;
            this.baseY = baseY;
        }

        public void setOffset(int xPos, int yPos) {
            this.xDisplayPosition = this.baseX + xPos;
            this.yDisplayPosition = this.baseY + yPos;
        }
    }

    private static class VariableSlot extends ResponsiveSlot {

        public VariableSlot(IInventory inventoryIn, int index, int baseX, int baseY) {
            super(inventoryIn, index, baseX, baseY);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return VariableHelpers.isVariable(stack);
        }

        @Override
        public int getItemStackLimit(ItemStack stack) {
            return 1;
        }
    }

    private static class VarOutSlot extends VariableSlot {

        public VarOutSlot(IInventory inventoryIn, int index, int baseX, int baseY) {
            super(inventoryIn, index, baseX, baseY);
        }
    }

    private static class SrcNBTSlot extends VariableSlot {

        public SrcNBTSlot(IInventory inventoryIn, int index, int baseX, int baseY) {
            super(inventoryIn, index, baseX, baseY);
        }
    }

    private static final int SRC_NBT = 0;
    private static final int VAR_OUT = 1;
    private static final int INVENTORY_START = 2;
    private static final int INVENTORY_END = 38; // Exclusive
    private UpdateClientNbtExtractorPacket.ErrorCode clientErrorCode = null;
    private Wrapper<NBTBase> clientNBT = null;
    private SegmentedNbtPath clientPath = null;
    private NbtExtractorOutputMode clientOutputMode = null;
    private LangHelpers.UnlocalizedString clientErrorMessage = null;
    private Boolean clientAutoRefresh = null;

    /**
     * Make a new instance.
     *
     * @param inventory The player inventory.
     * @param tile      The part.
     */
    public ContainerNbtExtractor(InventoryPlayer inventory, TileNbtExtractor tile) {
        super(inventory, tile);
        tile.setLastPlayer(inventory.player);
        this.addSlotToContainer(new SrcNBTSlot(tile.getInventory(), SRC_NBT, 9, 6));
        this.addSlotToContainer(new VarOutSlot(tile.getInventory(), VAR_OUT, 153, 6));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new ResponsiveSlot(inventory, j + i * 9 + 9, 9 + j * 18, 28 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new ResponsiveSlot(inventory, i, 9 + i * 18, 86));
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        World world = this.tile.getWorldObj();
        if (world != null && !world.isRemote) {
            UpdateClientNbtExtractorPacket.ErrorCode errorCode;
            Wrapper<NBTBase> newNBT = this.clientNBT;
            LangHelpers.UnlocalizedString errorMessage = null;
            if (!this.getSlot(SRC_NBT)
                .getHasStack()) {
                // Client will do this automatically
                errorCode = this.clientErrorCode = null;
            } else {
                try {
                    Wrapper<NBTBase> frozenValue = this.tile.getFrozenValue();
                    if (frozenValue == null) {
                        // If there is no frozen value (either frozen mode is not on, or it is
                        // on, but a value has not been evaluated yet.)
                        IVariable<?> variable = this.tile.getSrcNBTVariable();
                        if (variable == null) {
                            errorCode = UpdateClientNbtExtractorPacket.ErrorCode.EVAL_ERROR;
                            errorMessage = this.tile.getFirstErrorMessage();
                        } else {
                            IValue value = variable.getValue();
                            if (value instanceof ValueTypeNbt.ValueNbt) {
                                newNBT = new Wrapper<>(
                                    ((ValueTypeNbt.ValueNbt) value).getRawValue()
                                        .orElse(null));
                                errorCode = UpdateClientNbtExtractorPacket.ErrorCode.NO_ERROR;
                            } else {
                                errorCode = UpdateClientNbtExtractorPacket.ErrorCode.TYPE_ERROR;
                            }
                        }
                    } else {
                        errorCode = UpdateClientNbtExtractorPacket.ErrorCode.NO_ERROR;
                        newNBT = frozenValue;
                    }
                } catch (EvaluationException | PartStateException exception) {
                    exception.printStackTrace();
                    errorCode = UpdateClientNbtExtractorPacket.ErrorCode.EVAL_ERROR;
                    errorMessage = new LangHelpers.UnlocalizedString(exception.getMessage());
                } catch (Exception exception) {
                    errorCode = UpdateClientNbtExtractorPacket.ErrorCode.UNEXPECTED_ERROR;
                    exception.printStackTrace();
                    IntegratedNbt.clog("Unexpected error occurred while evaluating variable.");
                }
            }
            UpdateClientNbtExtractorPacket message = new UpdateClientNbtExtractorPacket();
            if (!Objects.equals(this.clientNBT, newNBT)) {
                message.updateNBT(newNBT.get());
                this.clientNBT = newNBT;
            }
            if (this.clientErrorCode != errorCode) {
                message.updateErrorCode(errorCode);
                this.clientErrorCode = errorCode;
            }
            SegmentedNbtPath nbtPath = this.tile.getExtractionPath();
            if (this.clientPath != nbtPath) {
                message.updateExtractionPath(nbtPath);
                this.clientPath = nbtPath;
            }
            NbtExtractorOutputMode outputMode = this.tile.getOutputMode();
            if (this.clientOutputMode != outputMode) {
                message.updateOutputMode(outputMode);
                this.clientOutputMode = outputMode;
            }
            if (!Objects.equals(errorMessage, this.clientErrorMessage)) {
                message.updateErrorMessage(errorMessage);
                this.clientErrorMessage = errorMessage;
            }
            if (this.clientAutoRefresh == null || this.tile.isAutoRefresh() != this.clientAutoRefresh) {
                message.updateAutoRefresh(this.tile.isAutoRefresh());
                this.clientAutoRefresh = this.tile.isAutoRefresh();
            }
            if (!message.isEmpty()) {
                EntityPlayerMP playerMP = (EntityPlayerMP) this.player;
                IntegratedNbt._instance.getPacketHandler()
                    .sendToPlayer(message, playerMP);
            }
            if (errorCode == UpdateClientNbtExtractorPacket.ErrorCode.NO_ERROR) {
                this.tile.updateLastEvaluatedNBT(newNBT.get());
            } else {
                this.tile.updateLastEvaluatedNBT(null);
            }
        }
    }

    public void setSlotOffset(int x, int y) {
        for (Slot slot : this.inventorySlots) {
            ((ResponsiveSlot) slot).setOffset(x, y);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return true;
    }

    @Nonnull
    public Slot getSrcNBTSlot() {
        return this.getSlot(SRC_NBT);
    }

    @Nonnull
    public Slot getVarOutSlot() {
        return this.getSlot(VAR_OUT);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack leftOver = ItemHelpers.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack fromSlot = slot.getStack();
            leftOver = fromSlot.copy();

            if (index == SRC_NBT || index == VAR_OUT) {
                if (!this.mergeItemStack(fromSlot, INVENTORY_START, INVENTORY_END, true)) {
                    return ItemHelpers.EMPTY;
                }
            } else {
                if (VariableHelpers.isVariable(fromSlot)) {
                    if ((this.inventorySlots.get(SRC_NBT)).getHasStack()) {
                        if ((this.inventorySlots.get(VAR_OUT)).getHasStack()) {
                            return ItemHelpers.EMPTY;
                        } else {
                            Slot varOutSlot = this.getSlot(VAR_OUT);
                            varOutSlot.putStack(fromSlot.splitStack(1));
                            varOutSlot.onSlotChanged();
                        }
                    } else {
                        Slot srcNBTSlot = this.getSlot(SRC_NBT);
                        srcNBTSlot.putStack(fromSlot.splitStack(1));
                        srcNBTSlot.onSlotChanged();
                    }
                } else {
                    return ItemHelpers.EMPTY;
                }
            }

            if (fromSlot.stackSize == 0) {
                slot.putStack(ItemHelpers.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (fromSlot.stackSize == leftOver.stackSize) {
                return ItemHelpers.EMPTY;
            }

            slot.onPickupFromSlot(playerIn, fromSlot);
        }

        return leftOver;
    }
}
