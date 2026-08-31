package ruiseki.integratedtunnels.core;

import java.util.Iterator;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.integratedtunnels.GeneralConfig;
import ruiseki.integratedtunnels.IntegratedTunnels;
import ruiseki.integratedtunnels.api.world.IBlockBreakHandler;
import ruiseki.integratedtunnels.api.world.IBlockBreakHandlerRegistry;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.capability.wrapper.BlockLiquidWrapper;
import ruiseki.okcore.fluid.capability.wrapper.BlockWrapper;
import ruiseki.okcore.fluid.capability.wrapper.FluidBlockWrapper;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.helper.FluidHelpers;

/**
 * An item storage for world block placement.
 *
 * @author rubensworks
 */
public class FluidStorageBlockWrapper implements IIngredientComponentStorage<FluidStack, Integer> {

    private final WorldServer world;
    private final BlockPos pos;
    private final ForgeDirection side;
    private final boolean blockUpdate;

    private final IIngredientComponentStorage<FluidStack, Integer> targetStorage;

    public FluidStorageBlockWrapper(WorldServer world, BlockPos pos, ForgeDirection side, boolean blockUpdate) {
        this.world = world;
        this.pos = pos;
        this.side = side;
        this.blockUpdate = blockUpdate;

        IFluidHandler fluidHandler = FluidHelpers.getFluidHandler(world, pos, side)
            .getOrNull();
        this.targetStorage = fluidHandler != null
            ? getComponent().getStorageWrapperHandler(CapabilityFluidHandler.FLUID_HANDLER)
                .wrapComponentStorage(fluidHandler)
            : null;
    }

    protected void sendBlockUpdate() {
        world.notifyBlocksOfNeighborChange(pos.getX(), pos.getY(), pos.getZ(), Blocks.air);
    }

    protected IBlockBreakHandler getBlockBreakHandler(BlockState blockState, World world, BlockPos pos,
        EntityPlayer player) {
        return IntegratedTunnels._instance.getRegistryManager()
            .getRegistry(IBlockBreakHandlerRegistry.class)
            .getHandler(blockState, world, pos, player);
    }

    protected void postInsert(FluidStack moved) {
        if (moved != null && GeneralConfig.worldInteractionEvents) {
            world.playSoundEffect(
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D,
                "game.neutral.swim.splash",
                1.0F,
                1.0F);
        }
        if (blockUpdate) {
            sendBlockUpdate();
        }
    }

    protected void postExtract(FluidStack moved) {
        if (moved != null && GeneralConfig.worldInteractionEvents) {
            world.playSoundEffect(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, "random.drink", 1.0F, 1.0F);
        }
    }

    @Override
    public IngredientComponent<FluidStack, Integer> getComponent() {
        return IngredientComponent.FLUIDSTACK;
    }

    @Override
    public Iterator<FluidStack> iterator() {
        return this.targetStorage.iterator();
    }

    @Override
    public Iterator<FluidStack> iterator(@Nonnull FluidStack prototype, Integer matchCondition) {
        return this.targetStorage.iterator(prototype, matchCondition);
    }

    @Override
    public long getMaxQuantity() {
        return FluidHelpers.BUCKET_VOLUME;
    }

    @Override
    public FluidStack insert(@Nonnull FluidStack stack, boolean simulate) {
        if (targetStorage != null) {
            return stack;
        }

        Fluid fluid = stack.getFluid();
        if (world.provider.isHellWorld && fluid == FluidRegistry.WATER) {
            return null;
        }

        Block block = fluid.getBlock();
        IFluidHandler handler;
        if (block == null) {
            return stack;
        } else if (block instanceof IFluidBlock) {
            handler = new FluidBlockWrapper((IFluidBlock) block, world, pos);
        } else if (block instanceof BlockLiquid) {
            handler = new BlockLiquidWrapper((BlockLiquid) block, world, pos);
        } else {
            handler = new BlockWrapper(block, world, pos);
        }

        int filled = handler.fill(stack, !simulate);
        int remaining = FluidHelpers.getAmount(stack) - filled;
        if (!simulate && filled > 0) {
            postInsert(stack);
        }

        if (remaining == 0) {
            return null;
        } else {
            return new FluidStack(stack, remaining);
        }
    }

    @Override
    public FluidStack extract(@Nonnull FluidStack prototype, Integer matchCondition, boolean simulate) {
        FluidStack extracted = targetStorage.extract(prototype, matchCondition, simulate);
        if (!simulate) {
            postExtract(extracted);
        }
        return extracted;
    }

    @Override
    public FluidStack extract(long maxQuantity, boolean simulate) {
        FluidStack extracted = targetStorage.extract(maxQuantity, simulate);
        if (!simulate) {
            postExtract(extracted);
        }
        return extracted;
    }

}
