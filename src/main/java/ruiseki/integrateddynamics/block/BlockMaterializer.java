package ruiseki.integrateddynamics.block;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import ruiseki.integrateddynamics.client.gui.GuiMaterializer;
import ruiseki.integrateddynamics.core.block.BlockContainerGuiCabled;
import ruiseki.integrateddynamics.inventory.container.ContainerMaterializer;
import ruiseki.integrateddynamics.tileentity.TileMaterializer;
import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.DirectionProperty;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.DirectionHelpers;

/**
 * A block that can materialize any variable to its raw value.
 *
 * @author rubensworks
 */
public class BlockMaterializer extends BlockContainerGuiCabled {

    @BlockProperty
    public static final DirectionProperty FACING = DirectionProperty.facing();

    private static BlockMaterializer _instance = null;

    /**
     * Get the unique instance.
     *
     * @return The instance.
     */
    public static BlockMaterializer getInstance() {
        return _instance;
    }

    /**
     * Make a new block instance.
     *
     * @param eConfig Config for this block.
     */
    public BlockMaterializer(ExtendedConfig<BlockConfig, Block> eConfig) {
        super(eConfig, TileMaterializer.class);
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerMaterializer.class;
    }

    @Override
    public Class<? extends GuiScreen> getGui() {
        return GuiMaterializer.class;
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
