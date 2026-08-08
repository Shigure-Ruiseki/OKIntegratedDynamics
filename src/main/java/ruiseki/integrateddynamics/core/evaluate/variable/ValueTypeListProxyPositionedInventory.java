package ruiseki.integrateddynamics.core.evaluate.variable;

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
}
