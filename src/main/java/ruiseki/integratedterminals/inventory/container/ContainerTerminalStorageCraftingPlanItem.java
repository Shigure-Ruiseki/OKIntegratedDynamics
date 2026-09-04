package ruiseki.integratedterminals.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.integratedterminals.item.ItemTerminalStoragePortableConfig;
import ruiseki.okcore.helper.InventoryHelpers;
import ruiseki.okcore.inventory.IGuiContainerProvider;

/**
 * @author rubensworks
 */
public class ContainerTerminalStorageCraftingPlanItem extends ContainerTerminalStorageCraftingPlanBase<Integer> {

    // Based on ItemInventoryContainer

    private final int itemIndex;

    public ContainerTerminalStorageCraftingPlanItem(EntityPlayer player, int itemIndex,
        CraftingOptionGuiData craftingOptionGuiData) {
        super(
            player,
            ((IGuiContainerProvider) ItemTerminalStoragePortableConfig._instance.getInstance()),
            craftingOptionGuiData);
        this.itemIndex = itemIndex;
    }

    public ItemStack getItemStack(EntityPlayer player) {
        return InventoryHelpers.getItemFromIndex(player, itemIndex);
    }

    @Override
    public INetwork getNetwork() {
        return ContainerTerminalStorageItem.getNetworkFromItem(getItemStack(player));
    }
}
