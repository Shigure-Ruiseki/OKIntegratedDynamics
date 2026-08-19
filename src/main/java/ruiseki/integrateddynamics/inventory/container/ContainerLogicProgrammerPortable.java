package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.integrateddynamics.item.ItemPortableLogicProgrammer;
import ruiseki.integrateddynamics.item.ItemPortableLogicProgrammerConfig;
import ruiseki.okcore.helper.InventoryHelpers;
import ruiseki.okcore.inventory.IGuiContainerProvider;

/**
 * Container for the {@link ItemPortableLogicProgrammer}.
 *
 * @author rubensworks
 */
public class ContainerLogicProgrammerPortable extends ContainerLogicProgrammerBase {

    private final int itemIndex;

    public ContainerLogicProgrammerPortable(EntityPlayer player, int itemIndex) {
        super(player.inventory, (IGuiContainerProvider) ItemPortableLogicProgrammerConfig._instance.getInstance());
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
