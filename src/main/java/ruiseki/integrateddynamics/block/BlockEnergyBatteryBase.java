package ruiseki.integrateddynamics.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import cofh.api.energy.IEnergyStorage;
import ruiseki.integrateddynamics.core.block.BlockContainerCabled;
import ruiseki.integrateddynamics.core.helper.Helpers;
import ruiseki.integrateddynamics.tileentity.TileEnergyBattery;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.helper.TileHelpers;

/**
 * A block that holds energy.
 *
 * @author rubensworks
 */
public abstract class BlockEnergyBatteryBase extends BlockContainerCabled implements IEnergyContainerBlock {

    /**
     * Make a new block instance.
     *
     * @param eConfig Config for this block.
     */
    public BlockEnergyBatteryBase(ExtendedConfig<BlockConfig> eConfig) {
        super(eConfig, TileEnergyBattery.class);
    }

    @Override
    public String getEneryContainerNBTName() {
        return "energy";
    }

    @Override
    public String getEneryContainerCapacityNBTName() {
        return "capacity";
    }

    public abstract boolean isCreative();

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int sideInt, float subX,
        float subY, float subZ) {
        if (super.onBlockActivated(world, x, y, z, player, sideInt, subX, subY, subZ)) {
            return true;
        }
        if (player.getHeldItem() == null) {
            TileEnergyBattery tile = TileHelpers.getSafeTile(world, x, y, z, TileEnergyBattery.class);
            if (tile != null) {
                player.addChatComponentMessage(
                    new ChatComponentTranslation(
                        Helpers.getLocalizedEnergyLevel(tile.getEnergyStored(), tile.getMaxEnergyStored())));
                return true;
            }
        }
        return false;
    }

    /**
     * Fill an IEnergyStorage with all the energy it can hold
     *
     * @param energyStorage IEnergyStorage that is to be filled
     */
    public static void fill(IEnergyStorage energyStorage) {
        int max = energyStorage.getMaxEnergyStored();
        int stored = 1;
        while (stored > 0) {
            stored = energyStorage.receiveEnergy(max, false);
        }
    }
}
