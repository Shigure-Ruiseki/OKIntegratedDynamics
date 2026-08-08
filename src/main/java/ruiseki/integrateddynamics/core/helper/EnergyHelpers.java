package ruiseki.integrateddynamics.core.helper;

import java.util.List;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import cofh.api.energy.IEnergyStorage;
import ruiseki.okcore.datastructure.BlockPos;
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

    public static IEnergyStorage getEnergyStorage(IBlockAccess world, BlockPos pos, ForgeDirection facing) {
        IEnergyStorage energyStorage = CapabilityHelpers.getCapability(world, pos, CapabilityEnergy.ENERGY, facing)
            .getOrNull();
        if (energyStorage == null) {
            for (IEnergyStorageProxy energyStorageProxy : ENERGY_STORAGE_PROXIES) {
                energyStorage = energyStorageProxy.getEnergyStorageProxy(world, pos, facing);
                if (energyStorage != null) {
                    return energyStorage;
                }
            }

        }
        return energyStorage;
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
            IEnergyStorage energyStorage = getEnergyStorage(world, pos.offset(side), side.getOpposite());
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

        public @Nullable IEnergyStorage getEnergyStorageProxy(IBlockAccess world, BlockPos pos, ForgeDirection facing);
    }

}
