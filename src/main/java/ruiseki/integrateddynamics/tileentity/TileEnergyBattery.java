package ruiseki.integrateddynamics.tileentity;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;
import com.gtnewhorizon.gtnhlib.blockstate.registry.BlockPropertyRegistry;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import ruiseki.integrateddynamics.api.block.IEnergyBattery;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.block.BlockEnergyBattery;
import ruiseki.integrateddynamics.block.BlockEnergyBatteryBase;
import ruiseki.integrateddynamics.block.BlockEnergyBatteryConfig;
import ruiseki.integrateddynamics.capability.energybattery.EnergyBatteryConfig;
import ruiseki.integrateddynamics.capability.networkelementprovider.NetworkElementProviderConfig;
import ruiseki.integrateddynamics.capability.networkelementprovider.NetworkElementProviderSingleton;
import ruiseki.integrateddynamics.core.tileentity.TileCableConnectable;
import ruiseki.integrateddynamics.network.EnergyBatteryNetworkElement;
import ruiseki.okcore.capabilities.resolver.BasicCapabilityResolver;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.BlockStateHelpers;
import ruiseki.okcore.persist.nbt.NBTPersist;

public class TileEnergyBattery extends TileCableConnectable
    implements IEnergyBattery, IEnergyProvider, IEnergyReceiver {

    @NBTPersist
    private int energy;

    public TileEnergyBattery() {

        this.capabilityCache.addCapabilityResolver(
            BasicCapabilityResolver.create(
                NetworkElementProviderConfig.CAPABILITY,
                () -> new NetworkElementProviderSingleton<IPartNetwork>() {

                    @Override
                    @SuppressWarnings({ "unchecked", "rawtypes" })
                    public INetworkElement<IPartNetwork> createNetworkElement(World world, BlockPos blockPos) {
                        return (INetworkElement) new EnergyBatteryNetworkElement(DimPos.of(world, blockPos));
                    }
                }));

        this.capabilityCache
            .addCapabilityResolver(BasicCapabilityResolver.create(EnergyBatteryConfig.CAPABILITY, () -> this));
    }

    protected boolean isCreative() {
        return ((BlockEnergyBatteryBase) getBlock()).isCreative();
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return consume(maxExtract, simulate);
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        return addEnergy(maxReceive, simulate);
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return getStoredEnergy();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return getMaxStoredEnergy();
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    @Override
    public int getStoredEnergy() {
        if (isCreative()) return Integer.MAX_VALUE;
        return this.energy;
    }

    @Override
    public int getMaxStoredEnergy() {
        if (isCreative()) return Integer.MAX_VALUE;
        return BlockEnergyBatteryConfig.capacity;
    }

    @Override
    public int addEnergy(int energy, boolean simulate) {
        if (!isCreative()) {
            int stored = getStoredEnergy();
            int newEnergy = Math.min(stored + energy, getMaxStoredEnergy());
            if (!simulate) {
                setEnergy(newEnergy);
            }
            return newEnergy - stored;
        }
        return 0;
    }

    @Override
    public int consume(int energy, boolean simulate) {
        if (isCreative()) return energy;
        int stored = getStoredEnergy();
        int newEnergy = Math.max(stored - energy, 0);
        if (!simulate) {
            setEnergy(newEnergy);
        }
        return stored - newEnergy;
    }

    public void updateBlockState() {
        if (!isCreative()) {
            BlockState blockState = BlockPropertyRegistry.getBlockState(worldObj, xCoord, yCoord, zCoord);
            if (blockState.getBlock() == BlockEnergyBattery.getInstance()) {
                int fill = (int) Math.floor(
                    ((float) energy * (BlockEnergyBattery.FILL.getAllowedValues() - 1)) / (float) getMaxStoredEnergy());
                BlockStateHelpers.set(worldObj, xCoord, yCoord, zCoord, BlockEnergyBattery.FILL, fill);
            }
        }
    }

    protected void setEnergy(int energy) {
        if (!isCreative()) {
            this.energy = energy;
            updateBlockState();
            sendUpdate();
        }
    }

}
