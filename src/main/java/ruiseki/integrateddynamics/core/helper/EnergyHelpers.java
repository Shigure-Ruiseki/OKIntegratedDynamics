package ruiseki.integrateddynamics.core.helper;

import java.util.List;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Lists;

import cofh.api.energy.IEnergyStorage;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.helper.CapabilityHelpers;

/**
 * Helpers related to energy.
 *
 * @author rubensworks
 */
public class EnergyHelpers {

    private static final List<IEnergyStorageProxy> ENERGY_STORAGE_PROXIES = Lists.newArrayList();

    public static void addEnergyStorageProxy(IEnergyStorageProxy energyStorageProxy) {
        ENERGY_STORAGE_PROXIES.add(energyStorageProxy);
    }

    public static LazyOptional<IEnergyStorage> getEnergyStorage(PartPos pos) {
        return getEnergyStorage(pos.getPos(), pos.getSide());
    }

    public static LazyOptional<IEnergyStorage> getEnergyStorage(DimPos pos, ForgeDirection facing) {
        World world = pos.getWorld();
        return world != null ? getEnergyStorage(world, pos.getBlockPos(), facing) : LazyOptional.empty();
    }

    public static LazyOptional<IEnergyStorage> getEnergyStorage(IBlockAccess world, BlockPos pos,
        ForgeDirection facing) {
        IEnergyStorage energyStorage = CapabilityHelpers.getCapability(world, pos, CapabilityEnergy.ENERGY, facing)
            .orElseGet(() -> {
                for (IEnergyStorageProxy energyStorageProxy : ENERGY_STORAGE_PROXIES) {
                    LazyOptional<IEnergyStorage> optionalEnergyStorage = energyStorageProxy
                        .getEnergyStorageProxy(world, pos, facing);
                    if (optionalEnergyStorage.isPresent()) {
                        return optionalEnergyStorage.orElse(null);
                    }
                }
                return null;
            });
        return energyStorage == null ? LazyOptional.empty() : LazyOptional.of(() -> energyStorage);
    }

    /**
     * Attempty to fill the neighbouring tiles with energy.
     * 
     * @param world    The world.
     * @param pos      The filler's position.
     * @param energy   The energy to add.
     * @param simulate If the filling should be simulated.
     * @return The amount of energy that was filled somewhere.
     */
    public static int fillNeigbours(World world, BlockPos pos, int energy, boolean simulate) {
        int toFill = energy;
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            IEnergyStorage energyStorage = getEnergyStorage(world, pos.offset(side), side.getOpposite()).orElse(null);
            if (energyStorage != null) {
                toFill -= energyStorage.receiveEnergy(toFill, simulate);
                if (toFill <= 0) {
                    return energy;
                }
            }
        }
        return energy - toFill;
    }

    public static interface IEnergyStorageProxy {

        public LazyOptional<IEnergyStorage> getEnergyStorageProxy(IBlockAccess world, BlockPos pos,
            ForgeDirection facing);
    }

}
