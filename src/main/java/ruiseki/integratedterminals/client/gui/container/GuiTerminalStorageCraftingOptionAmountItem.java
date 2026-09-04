package ruiseki.integratedterminals.client.gui.container;

import net.minecraft.entity.player.EntityPlayer;

import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.integratedterminals.core.client.gui.GuiTerminalStorageCraftingOptionAmountBase;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageCraftingOptionAmountItem;

public class GuiTerminalStorageCraftingOptionAmountItem
    extends GuiTerminalStorageCraftingOptionAmountBase<Integer, ContainerTerminalStorageCraftingOptionAmountItem> {

    public GuiTerminalStorageCraftingOptionAmountItem(EntityPlayer player, int slotIndex,
        CraftingOptionGuiData craftingOptionGuiData) {
        super(new ContainerTerminalStorageCraftingOptionAmountItem(player, slotIndex, craftingOptionGuiData));
    }
}
