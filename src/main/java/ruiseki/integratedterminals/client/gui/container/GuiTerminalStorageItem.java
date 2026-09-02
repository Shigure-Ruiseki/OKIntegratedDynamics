package ruiseki.integratedterminals.client.gui.container;

import net.minecraft.entity.player.EntityPlayer;

import ruiseki.integratedterminals.core.client.gui.GuiTerminalStorage;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageItem;
import ruiseki.integratedterminals.inventory.container.TerminalStorageState;

public class GuiTerminalStorageItem extends GuiTerminalStorage<Integer, ContainerTerminalStorageItem> {

    public GuiTerminalStorageItem(EntityPlayer player, int slotIndex,
        ContainerTerminalStorageBase.InitTabData initTabData, TerminalStorageState terminalStorageState) {
        super(new ContainerTerminalStorageItem(player, slotIndex, initTabData, terminalStorageState));
    }
}
