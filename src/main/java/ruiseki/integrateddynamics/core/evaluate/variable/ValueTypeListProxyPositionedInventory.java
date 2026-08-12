package ruiseki.integrateddynamics.core.evaluate.variable;

import java.util.Iterator;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.handler.IItemHandler;
import ruiseki.okcore.persist.nbt.INBTProvider;

/**
 * A list proxy for an inventory at a certain position.
 */
public class ValueTypeListProxyPositionedInventory
    extends ValueTypeListProxyPositioned<ValueObjectTypeItemStack, ValueObjectTypeItemStack.ValueItemStack>
    implements INBTProvider {

    public ValueTypeListProxyPositionedInventory(DimPos pos, ForgeDirection side) {
        super(ValueTypeListProxyFactories.POSITIONED_INVENTORY.getName(), ValueTypes.OBJECT_ITEMSTACK, pos, side);
    }

    public ValueTypeListProxyPositionedInventory() {
        this(null, null);
    }

    protected IItemHandler getInventory() {
        return CapabilityHelpers.getCapability(getPos(), CapabilityItemHandler.ITEM_HANDLER, getSide())
            .getOrNull();
    }

    @Override
    public int getLength() {
        IItemHandler inventory = getInventory();
        if (inventory == null) {
            return 0;
        }
        return inventory.getSlots();
    }

    @Override
    public ValueObjectTypeItemStack.ValueItemStack get(int index) {
        return ValueObjectTypeItemStack.ValueItemStack.of(getInventory().getStackInSlot(index));
    }

    @Override
    public Iterator<ValueObjectTypeItemStack.ValueItemStack> iterator() {
        // We use a custom iterator that retrieves the itemhandler capability only once.
        // Because for large inventories, the capability would have to be retrieved for every single slot,
        // which could result in a major performance problem.
        return new ValueTypeListProxyPositionedInventory.ListFactoryIterator(getInventory());
    }

    public static class ListFactoryIterator implements Iterator<ValueObjectTypeItemStack.ValueItemStack> {

        private final IItemHandler itemHandler;
        private int index = 0;

        public ListFactoryIterator(IItemHandler itemHandler) {
            this.itemHandler = itemHandler;
        }

        @Override
        public boolean hasNext() {
            return itemHandler != null && index < itemHandler.getSlots();
        }

        @Override
        public ValueObjectTypeItemStack.ValueItemStack next() {
            return ValueObjectTypeItemStack.ValueItemStack.of(this.itemHandler.getStackInSlot(index++));
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }
}
