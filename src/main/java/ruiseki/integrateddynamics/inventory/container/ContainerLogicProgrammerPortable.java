package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.integrateddynamics.item.ItemPortableLogicProgrammer;
import ruiseki.integrateddynamics.item.ItemPortableLogicProgrammerConfig;
import ruiseki.okcore.helper.InventoryHelpers;
import ruiseki.okcore.network.ExtendedBuffer;

/**
 * Container for the {@link ItemPortableLogicProgrammer}.
 *
 * @author rubensworks
 */
public class ContainerLogicProgrammerPortable extends ContainerLogicProgrammerBase {

    private final int itemIndex;

    public ContainerLogicProgrammerPortable(InventoryPlayer playerInventory, ExtendedBuffer packetBuffer) {
        this(playerInventory, packetBuffer.readInt());
    }

    public ContainerLogicProgrammerPortable(InventoryPlayer inventoryPlayer, int itemIndex) {
        super(ContainerLogicProgrammerPortableConfig._instance.getInstance(), inventoryPlayer);
        this.itemIndex = itemIndex;
    }

    public ItemStack getItemStack(EntityPlayer player) {
        return InventoryHelpers.getItemFromIndex(player, itemIndex);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        ItemStack item = getItemStack(player);
        return item != null && item.getItem() == ItemPortableLogicProgrammerConfig._instance.getInstance();
    }

}
