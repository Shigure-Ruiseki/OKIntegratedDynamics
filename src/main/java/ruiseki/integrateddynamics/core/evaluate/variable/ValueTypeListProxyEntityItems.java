package ruiseki.integrateddynamics.core.evaluate.variable;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.handler.IItemHandler;
import ruiseki.okcore.persist.nbt.INBTProvider;

/**
 * A list proxy for the item handler items of an entity.
 */
public class ValueTypeListProxyEntityItems extends
    ValueTypeListProxyEntityCapability<IItemHandler, ValueObjectTypeItemStack, ValueObjectTypeItemStack.ValueItemStack>
    implements INBTProvider {

    public ValueTypeListProxyEntityItems(World world, Entity entity, @Nullable ForgeDirection side) {
        super(
            ValueTypeListProxyFactories.ENTITY_CAPABILITY_ITEMS.getName(),
            ValueTypes.OBJECT_ITEMSTACK,
            world,
            entity,
            CapabilityItemHandler.ITEM_HANDLER,
            side);
    }

    public ValueTypeListProxyEntityItems() {
        this(null, null, null);
    }

    @Override
    public int getLength() {
        return getCapability().map(handler -> handler.getSlots())
            .orElse(0);
    }

    @Override
    public ValueObjectTypeItemStack.ValueItemStack get(int index) {
        return ValueObjectTypeItemStack.ValueItemStack.of(
            getCapability().map(handler -> handler.getStackInSlot(index))
                .orElse(null));
    }
}
