package ruiseki.integratedterminals.client.gui.container;

import net.minecraft.entity.player.EntityPlayer;

import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.integratedterminals.core.client.gui.GuiTerminalStorageCraftingOptionAmountBase;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageCraftingOptionAmountPart;
import ruiseki.integratedterminals.part.PartTypeTerminalStorage;

public class GuiTerminalStorageCraftingOptionAmountPart
    extends GuiTerminalStorageCraftingOptionAmountBase<PartPos, ContainerTerminalStorageCraftingOptionAmountPart> {

    public GuiTerminalStorageCraftingOptionAmountPart(EntityPlayer player, PartTarget target,
        IPartContainer partContainer, PartTypeTerminalStorage partType, CraftingOptionGuiData craftingOptionGuiData) {
        super(
            new ContainerTerminalStorageCraftingOptionAmountPart(
                player,
                target,
                partContainer,
                partType,
                craftingOptionGuiData));
    }
}
