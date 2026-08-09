package ruiseki.integrateddynamics.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.block.cable.ICableFakeable;
import ruiseki.integrateddynamics.block.BlockCable;
import ruiseki.integrateddynamics.core.helper.CableHelpers;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.item.ItemBlockMetadata;

/**
 * The item for the cable.
 *
 * @author rubensworks
 */
public class ItemBlockCable extends ItemBlockMetadata {

    private static final List<IUseAction> USE_ACTIONS = Lists.newArrayList();

    public ItemBlockCable(Block block) {
        super(block);
    }

    public static void addUseAction(IUseAction useAction) {
        USE_ACTIONS.add(useAction);
    }

    protected boolean checkCableAt(World world, BlockPos pos) {
        if (!CableHelpers.isNoFakeCable(world, pos) && CableHelpers.getCable(world, pos) != null) {
            return true;
        }
        for (IUseAction useAction : USE_ACTIONS) {
            if (useAction.canPlaceAt(world, pos)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean func_150936_a(World world, int x, int y, int z, int sideInt, EntityPlayer player, ItemStack stack) {
        BlockPos pos = new BlockPos(x, y, z);
        ForgeDirection side = ForgeDirection.getOrientation(sideInt);
        BlockPos target = pos.offset(side);

        // First check if pos is an unreal cable.
        if (checkCableAt(world, pos)) return true;
        // Check if target is an unreal cable.
        if (checkCableAt(world, target)) return true;

        // FIX: Check if either the clicked position OR target offset position is replaceable!
        Block blockAtPos = pos.getBlock(world);
        if (blockAtPos.isReplaceable(world, pos.getX(), pos.getY(), pos.getZ())) {
            return true;
        }

        Block blockAtTarget = target.getBlock(world);
        return blockAtTarget.isReplaceable(world, target.getX(), target.getY(), target.getZ());
    }

    protected boolean attempItemUseTarget(ItemStack stack, World world, BlockPos pos, BlockCable blockCable,
        EntityLivingBase placer, boolean offsetAdded) {
        Block block = pos.getBlock(world);
        if (!block.isAir(world, pos.getX(), pos.getY(), pos.getZ())) {
            ICableFakeable cable = CableHelpers.getCableFakeable(world, pos);
            if (cable != null && !cable.isRealCable()) {
                if (!world.isRemote) {
                    cable.setRealCable(true);
                    CableHelpers.updateConnections(world, pos);
                    CableHelpers.onCableAdded(world, pos, placer);
                }
                return true;
            }
            if (!offsetAdded) {
                for (IUseAction useAction : USE_ACTIONS) {
                    if (useAction.attempItemUseTarget(stack, world, pos, blockCable)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected void afterItemUse(ItemStack stack, World world, BlockPos pos, BlockCable blockCable,
        boolean calledSuper) {
        if (!calledSuper) {
            playPlaceSound(world, pos);
            --stack.stackSize;
        }
        blockCable.setDisableCollisionBox(false);
    }

    public static void playPlaceSound(World world, BlockPos pos) {
        Block block = BlockCable.getInstance();
        Block.SoundType stepSound = block.stepSound;
        world.playSoundEffect(
            (double) pos.getX() + 0.5D,
            (double) pos.getY() + 0.5D,
            (double) pos.getZ() + 0.5D,
            stepSound.func_150496_b(),
            (stepSound.getVolume() + 1.0F) / 2.0F,
            stepSound.getPitch() * 0.8F);
    }

    public static void playBreakSound(World world, BlockPos pos) {
        world.playBroadcastSound(2001, pos.getX(), pos.getY(), pos.getZ(), Block.getIdFromBlock(pos.getBlock(world)));
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, int x, int y, int z, int sideInt,
        float hitX, float hitY, float hitZ) {
        BlockPos pos = new BlockPos(x, y, z);
        ForgeDirection side = ForgeDirection.getOrientation(sideInt);

        BlockCable blockCable = (BlockCable) field_150939_a;
        blockCable.setDisableCollisionBox(true);

        // 1. Try placing inside fake cable at clicked position
        if (attempItemUseTarget(stack, worldIn, pos, blockCable, playerIn, false)) {
            afterItemUse(stack, worldIn, pos, blockCable, false);
            return true;
        }

        // 2. Try placing inside fake cable at target offset position
        BlockPos targetPos = pos.offset(side);
        if (attempItemUseTarget(stack, worldIn, targetPos, blockCable, playerIn, true)) {
            // FIX: Pass targetPos instead of pos!
            afterItemUse(stack, worldIn, pos.offset(side), blockCable, false);
            return true;
        }

        // 3. Normal Vanilla ItemBlock placement
        boolean ret = super.onItemUse(stack, playerIn, worldIn, x, y, z, sideInt, hitX, hitY, hitZ);
        afterItemUse(stack, worldIn, pos, blockCable, true);

        return ret;
    }

    public static interface IUseAction {

        /**
         * Attempt to use the given item.
         *
         * @param itemStack  The item stack that is being used.
         * @param world      The world.
         * @param pos        The position.
         * @param blockCable The cable block instance.
         * @return If the use action was applied.
         */
        public boolean attempItemUseTarget(ItemStack itemStack, World world, BlockPos pos, BlockCable blockCable);

        /**
         * If the block can be placed at the given position.
         *
         * @param world The world.
         * @param pos   The position.
         * @return If the block can be placed.
         */
        public boolean canPlaceAt(World world, BlockPos pos);
    }
}
