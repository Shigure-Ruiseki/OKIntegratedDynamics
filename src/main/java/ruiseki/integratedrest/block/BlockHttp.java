package ruiseki.integratedrest.block;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import ruiseki.integrateddynamics.core.block.BlockContainerGuiCabled;
import ruiseki.integratedrest.client.gui.GuiHttp;
import ruiseki.integratedrest.inventory.container.ContainerHttp;
import ruiseki.integratedrest.tileentity.TileHttp;
import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.DirectionProperty;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.DirectionHelpers;

/**
 * A block that can listen to HTTP PUTs.
 *
 * @author rubensworks
 */
public class BlockHttp extends BlockContainerGuiCabled {

    @BlockProperty
    public static final DirectionProperty FACING = DirectionProperty.facing();

    /**
     * Make a new block instance.
     *
     * @param eConfig Config for this block.
     */
    public BlockHttp(ExtendedConfig<BlockConfig, Block> eConfig) {
        super(eConfig, TileHttp.class);
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerHttp.class;
    }

    @Override
    public Class<? extends GuiScreen> getGui() {
        return GuiHttp.class;
    }

    @Override
    public BlockState getStateForPlacement(World world, BlockPos pos, ForgeDirection facing, float hitX, float hitY,
        float hitZ, int meta, EntityLivingBase placer) {
        BlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);
        state.setPropertyValue(FACING, DirectionHelpers.yawToDirection4(placer));
        return state;
    }

    @Override
    public boolean saveNBTToDroppedItem() {
        return false;
    }
}
