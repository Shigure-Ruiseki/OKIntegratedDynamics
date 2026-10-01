package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.integrateddynamics.item.ItemPortableLogicProgrammer;
import ruiseki.integrateddynamics.item.ItemPortableLogicProgrammerConfig;
import ruiseki.okcore.inventory.ItemLocation;
import ruiseki.okcore.network.ExtendedBuffer;

/**
 * Container for the {@link ItemPortableLogicProgrammer}.
 *
 * @author rubensworks
 */
public class ContainerLogicProgrammerPortable extends ContainerLogicProgrammerBase {

    private final ItemLocation itemLocation;

    public ContainerLogicProgrammerPortable(int id, InventoryPlayer playerInventory, ExtendedBuffer packetBuffer) {
        this(id, playerInventory, ItemLocation.readFromPacketBuffer(packetBuffer));
    }

    public ContainerLogicProgrammerPortable(int id, InventoryPlayer inventoryPlayer, ItemLocation itemLocation) {
        super(ContainerLogicProgrammerPortableConfig._instance.getInstance(), id, inventoryPlayer);
        this.itemLocation = itemLocation;
    }

    public ItemStack getItemStack(EntityPlayer player) {
        return itemLocation.getItemStack(player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        ItemStack item = getItemStack(player);
        return item != null && item.getItem() == ItemPortableLogicProgrammerConfig._instance.getInstance();
    }

}
