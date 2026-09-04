package ruiseki.integratedterminals.client.gui.container;

import net.minecraft.entity.player.EntityPlayer;

import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.integratedterminals.core.client.gui.GuiTerminalStorageCraftingPlanBase;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageCraftingPlanItem;

public class GuiTerminalStorageCraftingPlanItem
    extends GuiTerminalStorageCraftingPlanBase<Integer, ContainerTerminalStorageCraftingPlanItem> {

    public GuiTerminalStorageCraftingPlanItem(EntityPlayer player, int slotIndex,
        CraftingOptionGuiData craftingOptionGuiData) {
        super(new ContainerTerminalStorageCraftingPlanItem(player, slotIndex, craftingOptionGuiData));
    }
}
