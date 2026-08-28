package ruiseki.integrateddynamics.core.block;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.core.helper.CableHelpers;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.helper.WrenchHelpers;
import ruiseki.okcore.block.BlockTileGui;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.tileentity.TileEntityOK;

/**
 * A base block with a gui and tile entity that can connect to cables.
 *
 * @author rubensworks
 */
public abstract class BlockContainerGuiCabled extends BlockTileGui {

    /**
     * Make a new block instance.
     *
     * @param tileEntity The tile class
     */
    public BlockContainerGuiCabled(ExtendedConfig<BlockConfig, Block> eConfig,
        Class<? extends TileEntityOK> tileEntity) {
        super(eConfig, Material.anvil, tileEntity);

        setHardness(5.0F);
        setStepSound(soundTypeMetal);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int sideInt, float subX,
        float subY, float subZ) {
        ItemStack heldItem = player.getHeldItem();
        BlockPos pos = new BlockPos(x, y, z);
        ForgeDirection side = ForgeDirection.getOrientation(sideInt);
        if (!world.isRemote && WrenchHelpers.isWrench(player, heldItem, world, pos, side) && player.isSneaking()) {
            destroyBlock(world, x, y, z, true);
            return true;
        }
        return super.onBlockActivated(world, x, y, z, player, sideInt, subX, subY, subZ);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        if (!world.isRemote) {
            CableHelpers.onCableAdded(world, new BlockPos(x, y, z));
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, placer, stack);
        if (!world.isRemote) {
            CableHelpers.onCableAddedByPlayer(world, new BlockPos(x, y, z), placer);
        }
    }

    @Override
    protected void onPreBlockDestroyed(World world, int x, int y, int z) {
        CableHelpers.onCableRemoving(world, new BlockPos(x, y, z), true, false);
        super.onPreBlockDestroyed(world, x, y, z);
    }

    @Override
    protected void onPostBlockDestroyed(World world, int x, int y, int z) {
        super.onPostBlockDestroyed(world, x, y, z);
        BlockPos pos = new BlockPos(x, y, z);
        CableHelpers.onCableRemoved(world, pos, CableHelpers.getExternallyConnectedCables(world, pos));
    }

    @Override
    public void onBlockExploded(World world, int x, int y, int z, Explosion explosion) {
        BlockPos blockPos = new BlockPos(x, y, z);
        CableHelpers.setRemovingCable(true);
        CableHelpers.onCableRemoving(world, blockPos, true, false);
        Collection<ForgeDirection> connectedCables = CableHelpers.getExternallyConnectedCables(world, blockPos);
        super.onBlockExploded(world, x, y, z, explosion);
        CableHelpers.onCableRemoved(world, blockPos, connectedCables);
        CableHelpers.setRemovingCable(false);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock) {
        super.onNeighborBlockChange(world, x, y, z, neighborBlock);
        NetworkHelpers.onElementProviderBlockNeighborChange(world, new BlockPos(x, y, z), neighborBlock, null);
    }

    @Override
    public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
        super.onNeighborChange(world, x, y, z, tileX, tileY, tileZ);
        if (world instanceof World) {
            NetworkHelpers.onElementProviderBlockNeighborChange(
                (World) world,
                new BlockPos(x, y, z),
                world.getBlock(tileX, tileY, tileZ),
                null,
                new BlockPos(tileX, tileY, tileZ));
        }
    }

    protected boolean isPickBlockPersistData() {
        return false;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z,
        @Nullable EntityPlayer player) {
        if (isPickBlockPersistData()) {
            return super.getPickBlock(target, world, x, y, z, player);
        } else {
            return new ItemStack(getItem(world, x, y, z));
        }
    }
}
