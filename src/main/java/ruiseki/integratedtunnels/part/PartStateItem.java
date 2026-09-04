package ruiseki.integratedtunnels.part;

import net.minecraft.item.ItemStack;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.network.PositionedAddonsNetworkIngredientsFilter;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.write.IPartTypeWriter;
import ruiseki.integratedtunnels.api.network.IItemNetwork;
import ruiseki.integratedtunnels.core.TunnelHelpers;
import ruiseki.integratedtunnels.core.part.PartStatePositionedAddon;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.handler.IItemHandler;

/**
 * A part state for handling item import and export.
 * It also acts as an item capability that can be added to itself.
 *
 * @author rubensworks
 */
public class PartStateItem<P extends IPartTypeWriter> extends PartStatePositionedAddon<P, IItemNetwork, ItemStack>
    implements IItemHandler {

    public PartStateItem(int inventorySize, boolean canReceive, boolean canExtract) {
        super(inventorySize, canReceive, canExtract);
    }

    @Override
    public <T2> LazyOptional<T2> getCapability(Capability<T2> capability, INetwork network, IPartNetwork partNetwork,
        PartTarget target) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER) {
            return LazyOptional.of(() -> this)
                .cast();
        }
        return super.getCapability(capability, network, partNetwork, target);
    }

    protected IItemHandler getItemHandler() {
        return getPositionedAddonsNetwork()
            .getChannelExternal(CapabilityItemHandler.ITEM_HANDLER, TunnelHelpers.getPassiveInteractionChannel(this));
    }

    @Override
    public int getSlots() {
        return getPositionedAddonsNetwork() != null && getStorageFilter() != null ? getItemHandler().getSlots() : 0;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        ItemStack ret = getPositionedAddonsNetwork() != null && getStorageFilter() != null
            ? getItemHandler().getStackInSlot(slot)
            : ItemHelpers.EMPTY;
        if (!ItemHelpers.isEmpty(ret) && !getStorageFilter().testView(ret)) {
            return ItemHelpers.EMPTY;
        }
        return ret;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (canReceive() && getPositionedAddonsNetwork() != null && getStorageFilter() != null) {
            if (!getStorageFilter().testInsertion(stack)) {
                return stack;
            }
            return getItemHandler().insertItem(slot, stack, simulate);
        }
        return stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (canExtract() && getPositionedAddonsNetwork() != null && getStorageFilter() != null) {
            PositionedAddonsNetworkIngredientsFilter<ItemStack> filter = getStorageFilter();

            // If we do an effective extraction, first simulate to check if it matches the filter
            if (!simulate) {
                ItemStack extractedSimulated = getItemHandler().extractItem(slot, amount, true);
                if (!filter.testExtraction(extractedSimulated)) {
                    return ItemHelpers.EMPTY;
                }
            }

            ItemStack extracted = getItemHandler().extractItem(slot, amount, simulate);

            // If simulating, just check the output
            if (simulate && !filter.testExtraction(extracted)) {
                return ItemHelpers.EMPTY;
            }

            return extracted;
        }
        return ItemHelpers.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return getPositionedAddonsNetwork() != null ? getItemHandler().getSlotLimit(slot) : 0;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return getPositionedAddonsNetwork() != null && getStorageFilter() != null
            && getItemHandler().isItemValid(slot, stack)
            && !getStorageFilter().testInsertion(stack);
    }
}
