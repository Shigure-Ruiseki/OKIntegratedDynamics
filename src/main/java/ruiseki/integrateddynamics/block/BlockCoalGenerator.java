package ruiseki.integrateddynamics.block;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import ruiseki.integrateddynamics.client.gui.GuiCoalGenerator;
import ruiseki.integrateddynamics.core.block.BlockContainerGuiCabled;
import ruiseki.integrateddynamics.inventory.container.ContainerCoalGenerator;
import ruiseki.integrateddynamics.tileentity.TileCoalGenerator;
import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.BooleanProperty;
import ruiseki.okcore.block.property.DirectionProperty;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.DirectionHelpers;
import ruiseki.okcore.helper.TileHelpers;

/**
 * A block that can generate energy from coal.
 *
 * @author rubensworks
 */
public class BlockCoalGenerator extends BlockContainerGuiCabled {

    @BlockProperty
    public static final DirectionProperty FACING = DirectionProperty.facing();
    @BlockProperty
    public static final BooleanProperty ON = BooleanProperty.construct("lit", false, (world, x, y, z) -> {
        TileCoalGenerator tile = TileHelpers.getSafeTile(world, x, y, z, TileCoalGenerator.class);
        return tile != null && tile.isLit();
    }, (world, x, y, z, value) -> {
        TileCoalGenerator tile = TileHelpers.getSafeTile(world, x, y, z, TileCoalGenerator.class);
        if (tile != null) tile.setLit(value);
    });

    /**
     * Make a new block instance.
     */
    public BlockCoalGenerator(ExtendedConfig<BlockConfig, Block> eConfig) {
        super(eConfig, TileCoalGenerator.class);
    }

    @Override
    public BlockState getStateForPlacement(World world, BlockPos pos, ForgeDirection facing, float hitX, float hitY,
        float hitZ, int meta, EntityLivingBase placer) {
        BlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);
        state.setPropertyValue(FACING, DirectionHelpers.yawToDirection4(placer));
        return state;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerCoalGenerator.class;
    }

    @Override
    public Class<? extends GuiScreen> getGui() {
        return GuiCoalGenerator.class;
    }

    @Override
    protected boolean isPickBlockPersistData() {
        return true;
    }
}
