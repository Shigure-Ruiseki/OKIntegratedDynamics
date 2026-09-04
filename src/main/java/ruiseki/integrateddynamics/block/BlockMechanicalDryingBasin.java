package ruiseki.integrateddynamics.block;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integrateddynamics.client.gui.GuiMechanicalDryingBasin;
import ruiseki.integrateddynamics.core.block.BlockContainerGuiCabled;
import ruiseki.integrateddynamics.inventory.container.ContainerMechanicalDryingBasin;
import ruiseki.integrateddynamics.tileentity.TileMechanicalDryingBasin;
import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.BooleanProperty;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.FluidHelpers;
import ruiseki.okcore.helper.TileHelpers;

/**
 * A block that can expose variables.
 *
 * @author rubensworks
 */
public class BlockMechanicalDryingBasin extends BlockContainerGuiCabled {

    @BlockProperty
    public static final BooleanProperty ON = BooleanProperty.construct("lit", false, (world, x, y, z) -> {
        TileMechanicalDryingBasin tile = TileHelpers.getSafeTile(world, x, y, z, TileMechanicalDryingBasin.class);
        return tile != null && tile.wasWorking();
    }, (world, x, y, z, value) -> {
        TileMechanicalDryingBasin tile = TileHelpers.getSafeTile(world, x, y, z, TileMechanicalDryingBasin.class);
        if (tile != null) tile.setWorking(value);
    });

    /**
     * Make a new block instance.
     *
     * @param eConfig Config for this block.
     */
    public BlockMechanicalDryingBasin(ExtendedConfig<BlockConfig, Block> eConfig) {
        super(eConfig, TileMechanicalDryingBasin.class);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int sideInt, float subX,
        float subY, float subZ) {
        BlockPos blockPos = new BlockPos(x, y, z);
        return FluidHelpers.interactWithFluidHandler(player, world, blockPos, ForgeDirection.UP)
            || FluidHelpers.interactWithFluidHandler(player, world, blockPos, ForgeDirection.DOWN)
            || super.onBlockActivated(world, x, y, z, player, sideInt, subX, subY, subZ);
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerMechanicalDryingBasin.class;
    }

    @Override
    public Class<? extends GuiScreen> getGui() {
        return GuiMechanicalDryingBasin.class;
    }

    @Override
    protected boolean isPickBlockPersistData() {
        return true;
    }
}
