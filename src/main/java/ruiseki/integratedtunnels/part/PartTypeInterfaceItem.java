package ruiseki.integratedtunnels.part;

import java.util.Iterator;

import net.minecraft.item.ItemStack;

import com.google.common.collect.Iterators;

import ruiseki.commoncapabilities.api.capability.itemhandler.ISlotlessItemHandler;
import ruiseki.commoncapabilities.capability.itemhandler.SlotlessItemHandlerConfig;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integratedtunnels.GeneralConfig;
import ruiseki.integratedtunnels.api.network.IItemNetwork;
import ruiseki.integratedtunnels.capability.network.ItemNetworkConfig;
import ruiseki.integratedtunnels.core.part.PartTypeInterfacePositionedAddon;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.handler.IItemHandler;

/**
 * Interface for item handlers.
 *
 * @author rubensworks
 */
public class PartTypeInterfaceItem extends
    PartTypeInterfacePositionedAddon<IItemNetwork, IItemHandler, PartTypeInterfaceItem, PartTypeInterfaceItem.State> {

    public PartTypeInterfaceItem(String name) {
        super(name);
    }

    @Override
    protected Capability<IItemNetwork> getNetworkCapability() {
        return ItemNetworkConfig.CAPABILITY;
    }

    @Override
    protected Capability<IItemHandler> getTargetCapability() {
        return CapabilityItemHandler.ITEM_HANDLER;
    }

    @Override
    protected PartTypeInterfaceItem.State constructDefaultState() {
        return new PartTypeInterfaceItem.State();
    }

    @Override
    public int getConsumptionRate(State state) {
        return GeneralConfig.interfaceItemBaseConsumption;
    }

    public static class State
        extends PartTypeInterfacePositionedAddon.State<PartTypeInterfaceItem, IItemNetwork, IItemHandler>
        implements IItemHandler, ISlotlessItemHandler {

        @Override
        protected Capability<IItemHandler> getTargetCapability() {
            return CapabilityItemHandler.ITEM_HANDLER;
        }

        protected IItemHandler getItemHandler() {
            return getPositionedAddonsNetwork().getChannelExternal(CapabilityItemHandler.ITEM_HANDLER, getChannel());
        }

        @Override
        public int getSlots() {
            if (!isNetworkAndPositionValid()) {
                return 0;
            }
            disablePosition();
            int ret = getItemHandler().getSlots();
            enablePosition();
            return ret;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            if (!isNetworkAndPositionValid()) {
                return null;
            }
            disablePosition();
            ItemStack ret = getItemHandler().getStackInSlot(slot);
            enablePosition();
            return ret;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!isNetworkAndPositionValid()) {
                return stack;
            }
            disablePosition();
            ItemStack ret = getItemHandler().insertItem(slot, stack, simulate);
            enablePosition();
            return ret;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!isNetworkAndPositionValid()) {
                return null;
            }
            disablePosition();
            ItemStack ret = getItemHandler().extractItem(slot, amount, simulate);
            enablePosition();
            return ret;
        }

        @Override
        public int getSlotLimit(int slot) {
            if (!isNetworkAndPositionValid()) {
                return 0;
            }
            disablePosition();
            int ret = getItemHandler().getSlotLimit(slot);
            enablePosition();
            return ret;
        }

        @Override
        public Iterator<ItemStack> getItems() {
            if (!isNetworkAndPositionValid()) {
                return Iterators.forArray();
            }
            disablePosition();
            Iterator<ItemStack> ret = getPositionedAddonsNetwork().getChannel(getChannelInterface())
                .iterator();
            enablePosition();
            return ret;
        }

        @Override
        public Iterator<ItemStack> findItems(ItemStack stack, int matchFlags) {
            if (!isNetworkAndPositionValid()) {
                return Iterators.forArray();
            }
            disablePosition();
            Iterator<ItemStack> ret = getPositionedAddonsNetwork().getChannel(getChannelInterface())
                .iterator(stack, matchFlags);
            enablePosition();
            return ret;
        }

        @Override
        public ItemStack insertItem(ItemStack stack, boolean simulate) {
            if (!isNetworkAndPositionValid()) {
                return stack;
            }
            disablePosition();
            ItemStack ret = getPositionedAddonsNetwork().getChannel(getChannelInterface())
                .insert(stack, simulate);
            enablePosition();
            return ret;
        }

        @Override
        public ItemStack extractItem(int amount, boolean simulate) {
            if (!isNetworkAndPositionValid()) {
                return null;
            }
            disablePosition();
            ItemStack ret = getPositionedAddonsNetwork().getChannel(getChannelInterface())
                .extract(amount, simulate);
            enablePosition();
            return ret;
        }

        @Override
        public ItemStack extractItem(ItemStack matchStack, int matchFlags, boolean simulate) {
            if (!isNetworkAndPositionValid()) {
                return null;
            }
            disablePosition();
            ItemStack ret = getPositionedAddonsNetwork().getChannel(getChannelInterface())
                .extract(matchStack, matchFlags, simulate);
            enablePosition();
            return ret;
        }

        @Override
        public int getLimit() {
            if (!isNetworkAndPositionValid()) {
                return 0;
            }
            disablePosition();
            int limit = 0;
            IItemHandler itemHandler = getItemHandler();
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                limit += itemHandler.getSlotLimit(i);
            }
            enablePosition();
            return limit;
        }

        @Override
        public <T2> LazyOptional<T2> getCapability(Capability<T2> capability, INetwork network,
            IPartNetwork partNetwork, PartTarget target) {
            if (isNetworkAndPositionValid() && capability == SlotlessItemHandlerConfig.CAPABILITY) {
                return LazyOptional.of(() -> this)
                    .cast();
            }
            return super.getCapability(capability, network, partNetwork, target);
        }
    }
}
