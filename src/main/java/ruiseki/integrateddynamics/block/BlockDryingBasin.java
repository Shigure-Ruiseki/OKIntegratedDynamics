package ruiseki.integrateddynamics.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

import ruiseki.integrateddynamics.tileentity.TileDryingBasin;
import ruiseki.okcore.block.BlockTile;
import ruiseki.okcore.fluid.FluidActionResult;
import ruiseki.okcore.fluid.FluidHelpers;
import ruiseki.okcore.fluid.handler.SmartTank;
import ruiseki.okcore.helper.InventoryHelpers;
import ruiseki.okcore.helper.TileHelpers;

public class BlockDryingBasin extends BlockTile {

    public BlockDryingBasin() {
        super(Material.wood, TileDryingBasin.class);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int sideInt, float subX,
        float subY, float subZ) {

        TileDryingBasin tile = TileHelpers.getSafeTile(world, x, y, z, TileDryingBasin.class);
        if (tile == null) {
            return false;
        }

        if (world.isRemote) {
            return true;
        }

        ForgeDirection side = ForgeDirection.getOrientation(sideInt);
        ItemStack itemStack = player.inventory.getCurrentItem();
        SmartTank tank = tile.getTank();
        ItemStack tileStack = tile.getStackInSlot(0);

        if (itemStack != null) {
            IFluidHandler itemFluidHandler = FluidHelpers.getFluidHandler(itemStack)
                .getOrNull();
            if (itemFluidHandler != null) {
                if (tank.isEmpty()
                    && FluidHelpers.tryEmptyContainer(itemStack, tank, Integer.MAX_VALUE, player, side, false)
                        .isSuccess()) {
                    FluidActionResult fluidAction = FluidHelpers
                        .tryEmptyContainer(itemStack, tank, Integer.MAX_VALUE, player, side, true);
                    if (fluidAction.isSuccess()) {
                        InventoryHelpers.tryReAddToStack(player, itemStack, fluidAction.getResult());
                        tile.sendUpdate();
                    }
                    return true;
                }

                if (!tank.isEmpty()
                    && FluidHelpers.tryFillContainer(itemStack, tank, Integer.MAX_VALUE, player, side, false)
                        .isSuccess()) {
                    FluidActionResult fluidAction = FluidHelpers
                        .tryFillContainer(itemStack, tank, Integer.MAX_VALUE, player, side, true);
                    if (fluidAction.isSuccess()) {
                        InventoryHelpers.tryReAddToStack(player, itemStack, fluidAction.getResult());
                        tile.sendUpdate();
                    }
                    return true;
                }
            }
        }

        if (tileStack != null) {
            if (itemStack == null) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, tileStack);
                tile.setInventorySlotContents(0, null);
            } else if (player.inventory.addItemStackToInventory(tileStack)) {
                tile.setInventorySlotContents(0, null);
            } else {
                return false;
            }

            player.inventoryContainer.detectAndSendChanges();
            tile.sendUpdate();
            return true;

        } else if (itemStack != null) {
            tile.setInventorySlotContents(0, itemStack.splitStack(1));

            if (itemStack.stackSize <= 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }

            player.inventoryContainer.detectAndSendChanges();
            tile.sendUpdate();
            return true;
        }

        return false;
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask,
        List<AxisAlignedBB> list, Entity collider) {
        float f = 0.125F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.3125F, 1.0F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return side != ForgeDirection.UP && side != ForgeDirection.DOWN && super.isSideSolid(world, x, y, z, side);
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        TileDryingBasin tile = TileHelpers.getSafeTile(world, x, y, z, TileDryingBasin.class);
        if (tile == null) return 0;
        return tile.getStackInSlot(0) != null ? 15 : 0;
    }
}
