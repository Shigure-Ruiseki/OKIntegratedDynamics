package ruiseki.integratednbt.block;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import ruiseki.integrateddynamics.core.block.BlockContainerGuiCabled;
import ruiseki.integratednbt.client.gui.container.GuiNbtExtractor;
import ruiseki.integratednbt.inventory.container.ContainerNbtExtractor;
import ruiseki.integratednbt.item.ItemNbtExtractorRemoteConfig;
import ruiseki.integratednbt.tileentity.TileNbtExtractor;
import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.DirectionProperty;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.DirectionHelpers;

public class BlockNbtExtractor extends BlockContainerGuiCabled {

    @BlockProperty
    public static final DirectionProperty FACING = DirectionProperty.facing();

    /**
     * Make a new block instance.
     *
     * @param eConfig Config for this block.
     */
    public BlockNbtExtractor(ExtendedConfig<BlockConfig, Block> eConfig) {
        super(eConfig, TileNbtExtractor.class);
    }

    @Override
    public BlockState getStateForPlacement(World world, BlockPos pos, ForgeDirection facing, float hitX, float hitY,
        float hitZ, int meta, EntityLivingBase placer) {
        BlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);
        state.setPropertyValue(FACING, DirectionHelpers.yawToDirection4(placer));
        return state;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int sideInt, float subX,
        float subY, float subZ) {
        if (!world.isRemote && player.getItemInUse()
            .getItem() == ItemNbtExtractorRemoteConfig._instance.getInstance()) {
            return false;
        }
        return super.onBlockActivated(world, x, y, z, player, sideInt, subX, subY, subZ);
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerNbtExtractor.class;
    }

    @Override
    public Class<? extends GuiScreen> getGui() {
        return GuiNbtExtractor.class;
    }
}
