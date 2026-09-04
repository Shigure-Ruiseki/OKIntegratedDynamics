package ruiseki.integratedterminals.inventory.container;

import net.minecraft.entity.player.EntityPlayer;

import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.integratedterminals.item.ItemTerminalStoragePortableConfig;
import ruiseki.okcore.inventory.IGuiContainerProvider;

/**
 * @author rubensworks
 */
public class ContainerTerminalStorageCraftingOptionAmountItem
    extends ContainerTerminalStorageCraftingOptionAmountBase<Integer> {

    // Based on ItemInventoryContainer

    private final int itemIndex;

    public ContainerTerminalStorageCraftingOptionAmountItem(EntityPlayer player, int itemIndex,
        CraftingOptionGuiData craftingOptionGuiData) {
        super(
            player,
            ((IGuiContainerProvider) ItemTerminalStoragePortableConfig._instance.getInstance()),
            craftingOptionGuiData);
        this.itemIndex = itemIndex;
    }

}
