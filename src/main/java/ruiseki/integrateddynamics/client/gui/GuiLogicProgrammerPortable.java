package ruiseki.integrateddynamics.client.gui;

import net.minecraft.entity.player.EntityPlayer;

import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerPortable;

/**
 * Gui for the {@link ruiseki.integrateddynamics.item.ItemPortableLogicProgrammer}.
 *
 * @author rubensworks
 */
public class GuiLogicProgrammerPortable extends GuiLogicProgrammerBase {

    public GuiLogicProgrammerPortable(EntityPlayer player, int itemIndex) {
        super(player.inventory, new ContainerLogicProgrammerPortable(player, itemIndex));
    }

}
