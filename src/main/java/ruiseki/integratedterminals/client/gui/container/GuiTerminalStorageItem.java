package ruiseki.integratedterminals.client.gui.container;

import net.minecraft.entity.player.EntityPlayer;

import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integratedterminals.core.client.gui.GuiTerminalStorage;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageItem;

public class GuiTerminalStorageItem extends GuiTerminalStorage<Integer, ContainerTerminalStorageItem> {

    public GuiTerminalStorageItem(EntityPlayer player, int slotIndex,
        ContainerTerminalStorageBase.InitTabData initTabData) {
        super(new ContainerTerminalStorageItem(player, slotIndex, initTabData));
    }

    public GuiTerminalStorageItem(EntityPlayer player, int slotIndex) {
        this(player, slotIndex, null);
    }
}
