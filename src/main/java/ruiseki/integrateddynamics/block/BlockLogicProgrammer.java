package ruiseki.integrateddynamics.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammer;
import ruiseki.okcore.block.BlockGui;
import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.DirectionProperty;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.inventory.IGuiConstructor;
import ruiseki.okcore.inventory.container.ContainerExtended;

/**
 * A block that can hold defined variables so that they can be referred to elsewhere in the network.
 *
 * @author rubensworks
 */
public class BlockLogicProgrammer extends BlockGui {

    @BlockProperty
    public static final DirectionProperty FACING = DirectionProperty.facing();

    /**
     * Make a new block instance.
     */
    public BlockLogicProgrammer() {
        super(Material.glass);
        setHardness(3.0F);
        setStepSound(soundTypeMetal);
    }

    @Override
    public IGuiConstructor getGuiProvider(BlockState blockState, World world, BlockPos blockPos) {
        return new IGuiConstructor() {

            @Override
            public @Nullable ContainerExtended createContainer(int windowId, InventoryPlayer playerInventory,
                EntityPlayer player) {
                return new ContainerLogicProgrammer(playerInventory);
            }
        };
    }
}
