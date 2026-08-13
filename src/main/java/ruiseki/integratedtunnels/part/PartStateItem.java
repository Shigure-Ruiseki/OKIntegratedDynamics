package ruiseki.integratedtunnels.part;

import net.minecraft.item.ItemStack;

import ruiseki.integrateddynamics.api.part.write.IPartTypeWriter;
import ruiseki.integratedtunnels.api.network.IItemNetwork;
import ruiseki.integratedtunnels.core.part.PartStatePositionedAddon;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.handler.IItemHandler;

/**
 * A part state for handling item import and export.
 * It also acts as an item capability that can be added to itself.
 * 
 * @author rubensworks
 */
public class PartStateItem<P extends IPartTypeWriter> extends PartStatePositionedAddon<P, IItemNetwork>
    implements IItemHandler {

    public PartStateItem(int inventorySize, boolean canReceive, boolean canExtract) {
        super(inventorySize, canReceive, canExtract);
    }

    protected IItemHandler getItemHandler() {
        return getPositionedAddonsNetwork().getChannelExternal(CapabilityItemHandler.ITEM_HANDLER, getChannel());
    }

    @Override
    public int getSlots() {
        return getPositionedAddonsNetwork() != null ? getItemHandler().getSlots() : 0;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return getPositionedAddonsNetwork() != null ? getItemHandler().getStackInSlot(slot) : null;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return canReceive() && getPositionedAddonsNetwork() != null ? getItemHandler().insertItem(slot, stack, simulate)
            : stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return canExtract() && getPositionedAddonsNetwork() != null
            ? getItemHandler().extractItem(slot, amount, simulate)
            : null;
    }

    @Override
    public int getSlotLimit(int slot) {
        return getPositionedAddonsNetwork() != null ? getItemHandler().getSlotLimit(slot) : 0;
    }
}
