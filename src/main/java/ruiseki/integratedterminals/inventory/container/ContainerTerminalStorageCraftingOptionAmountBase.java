package ruiseki.integratedterminals.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

import org.jetbrains.annotations.Nullable;

import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.inventory.container.InventoryContainer;

/**
 * A container for setting the amount for a given crafting option.
 *
 * @author rubensworks
 */
public class ContainerTerminalStorageCraftingOptionAmountBase<L> extends InventoryContainer {

    private final CraftingOptionGuiData<?, ?, L> craftingOptionGuiData;

    public ContainerTerminalStorageCraftingOptionAmountBase(@Nullable ContainerType<?> type,
        InventoryPlayer playerInventory, CraftingOptionGuiData craftingOptionGuiData) {
        super(type, playerInventory, new SimpleInventory());

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
