package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

import ruiseki.integrateddynamics.block.BlockLogicProgrammer;

/**
 * Container for the {@link BlockLogicProgrammer}.
 *
 * @author rubensworks
 */
public class ContainerLogicProgrammer extends ContainerLogicProgrammerBase {

    public ContainerLogicProgrammer(int id, InventoryPlayer inventory) {
        super(ContainerLogicProgrammerConfig._instance.getInstance(), id, inventory);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }
}
