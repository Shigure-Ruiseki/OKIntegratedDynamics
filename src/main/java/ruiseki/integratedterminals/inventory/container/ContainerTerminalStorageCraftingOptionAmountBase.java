package ruiseki.integratedterminals.inventory.container;

import net.minecraft.entity.player.EntityPlayer;

import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.integratedterminals.proxy.guiprovider.GuiProviders;
import ruiseki.okcore.inventory.container.ExtendedInventoryContainer;

/**
 * A container for setting the amount for a given crafting option.
 * 
 * @author rubensworks
 */
public class ContainerTerminalStorageCraftingOptionAmountBase<L> extends ExtendedInventoryContainer {

    private final CraftingOptionGuiData<?, ?, L> craftingOptionGuiData;

    public ContainerTerminalStorageCraftingOptionAmountBase(final EntityPlayer player,
        CraftingOptionGuiData craftingOptionGuiData) {
        super(player.inventory, GuiProviders.GUI_TERMINAL_STORAGE_CRAFTNG_OPTION_AMOUNT);

        addPlayerInventory(player.inventory, 9, 80);

        this.craftingOptionGuiData = craftingOptionGuiData;
    }

    public <T, M> CraftingOptionGuiData<T, M, L> getCraftingOptionGuiData() {
        return (CraftingOptionGuiData<T, M, L>) craftingOptionGuiData;
    }

    @Override
    protected int getSizeInventory() {
        return 0;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }
}
