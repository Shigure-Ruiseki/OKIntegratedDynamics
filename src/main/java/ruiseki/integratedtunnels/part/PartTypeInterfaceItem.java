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
import ruiseki.integratedtunnels.core.part.IPartTypeInterfacePositionedAddon;
import ruiseki.integratedtunnels.core.part.PartTypeInterfacePositionedAddon;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.helper.ItemHelpers;
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
    public Capability<IItemNetwork> getNetworkCapability() {
        return ItemNetworkConfig.CAPABILITY;
    }

    @Override
    public Capability<IItemHandler> getTargetCapability() {
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

    public static class State extends
        PartTypeInterfacePositionedAddon.State<IItemNetwork, IItemHandler, PartTypeInterfaceItem, PartTypeInterfaceItem.State> {

        @Override
        public Capability<IItemHandler> getTargetCapability() {
            return CapabilityItemHandler.ITEM_HANDLER;
        }

        @Override
        public IItemHandler getCapabilityInstance() {
            return new PartTypeInterfaceItem.ItemHandler(this);
        }

        @Override
        public <T2> LazyOptional<T2> getCapability(Capability<T2> capability, INetwork network,
            IPartNetwork partNetwork, PartTarget target) {
            if (isNetworkAndPositionValid() && capability == SlotlessItemHandlerConfig.CAPABILITY) {
                return LazyOptional.of(this::getCapabilityInstance)
                    .cast();
            }
            return super.getCapability(capability, network, partNetwork, target);
        }
    }

    public static class ItemHandler implements IItemHandler, ISlotlessItemHandler {

        private final IPartTypeInterfacePositionedAddon.IState<IItemNetwork, IItemHandler, ?, ?> state;

        public ItemHandler(IPartTypeInterfacePositionedAddon.IState<IItemNetwork, IItemHandler, ?, ?> state) {
            this.state = state;
        }

        protected IItemHandler getItemHandler() {
            return state.getPositionedAddonsNetwork()
                .getChannelExternal(CapabilityItemHandler.ITEM_HANDLER, state.getChannel());
        }

        @Override
        public int getSlots() {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            int ret = getItemHandler().getSlots();
            state.enablePosition();
            return ret;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            if (!state.isNetworkAndPositionValid()) {
                return ItemHelpers.EMPTY;
            }
            state.disablePosition();
            ItemStack ret = getItemHandler().getStackInSlot(slot);
            state.enablePosition();
            return ret;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!state.isNetworkAndPositionValid()) {
                return stack;
            }
            state.disablePosition();
            ItemStack ret = getItemHandler().insertItem(slot, stack, simulate);
            state.enablePosition();
            return ret;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!state.isNetworkAndPositionValid()) {
                return ItemHelpers.EMPTY;
            }
            state.disablePosition();
            ItemStack ret = getItemHandler().extractItem(slot, amount, simulate);
            state.enablePosition();
            return ret;
        }

        @Override
        public int getSlotLimit(int slot) {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            int ret = getItemHandler().getSlotLimit(slot);
            state.enablePosition();
            return ret;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (!state.isNetworkAndPositionValid()) {
                return false;
            }
            state.disablePosition();
            boolean ret = getItemHandler().isItemValid(slot, stack);
            state.enablePosition();
            return ret;
        }

        @Override
        public Iterator<ItemStack> getItems() {
            if (!state.isNetworkAndPositionValid()) {
                return Iterators.forArray();
            }
            state.disablePosition();
            Iterator<ItemStack> ret = state.getPositionedAddonsNetwork()
                .getChannel(state.getChannelInterface())
                .iterator();
            state.enablePosition();
            return ret;
        }

        @Override
        public Iterator<ItemStack> findItems(ItemStack stack, int matchFlags) {
            if (!state.isNetworkAndPositionValid()) {
                return Iterators.forArray();
            }
            state.disablePosition();
            Iterator<ItemStack> ret = state.getPositionedAddonsNetwork()
                .getChannel(state.getChannelInterface())
                .iterator(stack, matchFlags);
            state.enablePosition();
            return ret;
        }

        @Override
        public ItemStack insertItem(ItemStack stack, boolean simulate) {
            if (!state.isNetworkAndPositionValid()) {
                return stack;
            }
            state.disablePosition();
            ItemStack ret = state.getPositionedAddonsNetwork()
                .getChannel(state.getChannelInterface())
                .insert(stack, simulate);
            state.enablePosition();
            return ret;
        }

        @Override
        public ItemStack extractItem(int amount, boolean simulate) {
            if (!state.isNetworkAndPositionValid()) {
                return ItemHelpers.EMPTY;
            }
            state.disablePosition();
            ItemStack ret = state.getPositionedAddonsNetwork()
                .getChannel(state.getChannelInterface())
                .extract(amount, simulate);
            state.enablePosition();
            return ret;
        }

        @Override
        public ItemStack extractItem(ItemStack matchStack, int matchFlags, boolean simulate) {
            if (!state.isNetworkAndPositionValid()) {
                return ItemHelpers.EMPTY;
            }
            state.disablePosition();
            ItemStack ret = state.getPositionedAddonsNetwork()
                .getChannel(state.getChannelInterface())
                .extract(matchStack, matchFlags, simulate);
            state.enablePosition();
            return ret;
        }

        @Override
        public int getLimit() {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            int limit = 0;
            IItemHandler itemHandler = getItemHandler();
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                limit += itemHandler.getSlotLimit(i);
            }
            state.enablePosition();
            return limit;
        }
    }
}
