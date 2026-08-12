package ruiseki.integrateddynamics.core.evaluate.variable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import ruiseki.okcore.persist.nbt.INBTProvider;

/**
 * A list proxy for the inventory of an entity.
 */
public class ValueTypeListProxyEntityArmorInventory
    extends ValueTypeListProxyEntityBase<ValueObjectTypeItemStack, ValueObjectTypeItemStack.ValueItemStack>
    implements INBTProvider {

    public ValueTypeListProxyEntityArmorInventory(World world, Entity entity) {
        super(ValueTypeListProxyFactories.ENTITY_ARMORINVENTORY.getName(), ValueTypes.OBJECT_ITEMSTACK, world, entity);
    }

    public ValueTypeListProxyEntityArmorInventory() {
        this(null, null);
    }

    protected ItemStack[] getInventory() {
        Entity e = getEntity();
        if (e != null) {
            if (e instanceof EntityPlayer) {
                return ((EntityPlayer) e).inventory.armorInventory;
            } else if (e instanceof EntityLivingBase) {
                ItemStack[] lastActiveItems = ((EntityLivingBase) e).getLastActiveItems();
                if (lastActiveItems != null && lastActiveItems.length >= 5) {
                    ItemStack[] armor = new ItemStack[4];
                    System.arraycopy(lastActiveItems, 1, armor, 0, 4);
                    return armor;
                }
            }
        }
        return new ItemStack[0];
    }

    @Override
    public int getLength() {
        return getInventory().length;
    }

    @Override
    public ValueObjectTypeItemStack.ValueItemStack get(int index) {
        return ValueObjectTypeItemStack.ValueItemStack.of(getInventory()[index]);
    }
}
