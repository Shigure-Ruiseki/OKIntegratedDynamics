package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

import ruiseki.integrateddynamics.block.BlockLogicProgrammer;
import ruiseki.okcore.datastructure.BlockPos;

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
        super(inventory, BlockLogicProgrammer.getInstance());
        this.world = world;
        this.blockPos = blockPos;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.blockPos.getBlock(this.world) == BlockLogicProgrammer.getInstance() && playerIn.getDistanceSq(
            (double) this.blockPos.getX() + 0.5D,
            (double) this.blockPos.getY() + 0.5D,
            (double) this.blockPos.getZ() + 0.5D) <= 64.0D;
    }
}
