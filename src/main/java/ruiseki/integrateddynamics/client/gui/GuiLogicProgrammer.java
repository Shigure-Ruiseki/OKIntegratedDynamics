package ruiseki.integrateddynamics.client.gui;

import net.minecraft.entity.player.InventoryPlayer;

import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammer;

/**
 * Gui for the {@link ruiseki.integrateddynamics.block.BlockLogicProgrammer}.
 *
 * @author rubensworks
 */
public class GuiLogicProgrammer extends GuiLogicProgrammerBase<ContainerLogicProgrammer> {

    public GuiLogicProgrammer(ContainerLogicProgrammer container, InventoryPlayer inventoryPlayer) {
        super(container, inventoryPlayer);
    }
}
