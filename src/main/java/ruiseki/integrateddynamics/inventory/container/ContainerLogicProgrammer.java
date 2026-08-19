package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

import ruiseki.integrateddynamics.block.BlockLogicProgrammer;
import ruiseki.integrateddynamics.block.BlockLogicProgrammerConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.inventory.IGuiContainerProvider;

/**
 * Container for the {@link BlockLogicProgrammer}.
 *
 * @author rubensworks
 */
public class ContainerLogicProgrammer extends ContainerLogicProgrammerBase {

    private final World world;
    private final BlockPos blockPos;

    /**
     * Make a new instance.
     *
     * @param inventory The player inventory.
     * @param world     The world.
     * @param blockPos  The position.
     */
    public ContainerLogicProgrammer(InventoryPlayer inventory, World world, BlockPos blockPos) {
        super(inventory, (IGuiContainerProvider) BlockLogicProgrammerConfig._instance.getInstance());
        this.world = world;
        this.blockPos = blockPos;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }
}
