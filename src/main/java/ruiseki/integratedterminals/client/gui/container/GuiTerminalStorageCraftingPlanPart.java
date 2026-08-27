package ruiseki.integratedterminals.client.gui.container;

import net.minecraft.entity.player.EntityPlayer;

import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.integratedterminals.core.client.gui.GuiTerminalStorageCraftingPlanBase;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageCraftingPlanPart;
import ruiseki.integratedterminals.part.PartTypeTerminalStorage;

public class GuiTerminalStorageCraftingPlanPart
    extends GuiTerminalStorageCraftingPlanBase<PartPos, ContainerTerminalStorageCraftingPlanPart> {

    public GuiTerminalStorageCraftingPlanPart(EntityPlayer player, PartTarget target, IPartContainer partContainer,
        PartTypeTerminalStorage partType, CraftingOptionGuiData craftingOptionGuiData) {
        super(
            new ContainerTerminalStorageCraftingPlanPart(
                player,
                target,
                partContainer,
                partType,
                craftingOptionGuiData));
    }
}
