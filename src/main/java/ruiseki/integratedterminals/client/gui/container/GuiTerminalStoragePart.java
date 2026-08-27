package ruiseki.integratedterminals.client.gui.container;

import net.minecraft.entity.player.EntityPlayer;

import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integratedterminals.core.client.gui.GuiTerminalStorage;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStoragePart;

public class GuiTerminalStoragePart extends GuiTerminalStorage<PartPos, ContainerTerminalStoragePart> {

    public GuiTerminalStoragePart(EntityPlayer player, PartTarget target, IPartContainer partContainer,
        IPartType partType, ContainerTerminalStorageBase.InitTabData initTabData) {
        super(new ContainerTerminalStoragePart(player, target, partContainer, partType, initTabData));
    }

    public GuiTerminalStoragePart(EntityPlayer player, PartTarget target, IPartContainer partContainer,
        IPartType partType) {
        this(player, target, partContainer, partType, null);
    }
}
