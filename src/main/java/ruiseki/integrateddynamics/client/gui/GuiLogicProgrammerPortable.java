package ruiseki.integrateddynamics.client.gui;

import net.minecraft.entity.player.InventoryPlayer;

import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerPortable;

/**
 * Gui for the {@link ruiseki.integrateddynamics.item.ItemPortableLogicProgrammer}.
 *
 * @author rubensworks
 */
public class GuiLogicProgrammerPortable extends GuiLogicProgrammerBase<ContainerLogicProgrammerPortable> {

    public GuiLogicProgrammerPortable(ContainerLogicProgrammerPortable container, InventoryPlayer inventoryPlayer) {
        super(container, inventoryPlayer);
    }

}
