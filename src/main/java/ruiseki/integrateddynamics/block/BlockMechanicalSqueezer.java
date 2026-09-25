package ruiseki.integrateddynamics.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integrateddynamics.core.block.BlockContainerGuiCabled;
import ruiseki.integrateddynamics.tileentity.TileMechanicalSqueezer;
import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.BooleanProperty;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.FluidHelpers;
import ruiseki.okcore.helper.TileHelpers;

public class BlockMechanicalSqueezer extends BlockContainerGuiCabled {

    @BlockProperty
    public static final BooleanProperty ON = BooleanProperty.construct("lit", false, (world, x, y, z) -> {
        TileMechanicalSqueezer tile = TileHelpers.getSafeTile(world, x, y, z, TileMechanicalSqueezer.class);
        return tile != null && tile.wasWorking();
    }, (world, x, y, z, value) -> {
        TileMechanicalSqueezer tile = TileHelpers.getSafeTile(world, x, y, z, TileMechanicalSqueezer.class);
        if (tile != null) tile.setWorking(value);
    });

    public BlockMechanicalSqueezer() {
        super(TileMechanicalSqueezer.class);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int sideInt, float subX,
        float subY, float subZ) {
        return FluidHelpers
            .interactWithFluidHandler(player, world, new BlockPos(x, y, z), ForgeDirection.getOrientation(sideInt))
            || super.onBlockActivated(world, x, y, z, player, sideInt, subX, subY, subZ);
    }

    @Override
    protected boolean isPickBlockPersistData() {
        return true;
    }
}
