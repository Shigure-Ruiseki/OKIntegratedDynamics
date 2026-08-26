package ruiseki.integrateddynamics.tileentity;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import lombok.experimental.Delegate;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.block.BlockEnergyBattery;
import ruiseki.integrateddynamics.block.BlockEnergyBatteryBase;
import ruiseki.integrateddynamics.block.BlockEnergyBatteryConfig;
import ruiseki.integrateddynamics.capability.energystorage.IEnergyStorageCapacity;
import ruiseki.integrateddynamics.capability.networkelementprovider.NetworkElementProviderConfig;
import ruiseki.integrateddynamics.capability.networkelementprovider.NetworkElementProviderSingleton;
import ruiseki.integrateddynamics.core.helper.EnergyHelpers;
import ruiseki.integrateddynamics.core.tileentity.TileCableConnectable;
import ruiseki.integrateddynamics.network.EnergyBatteryNetworkElement;
import ruiseki.okcore.capabilities.resolver.BasicCapabilityResolver;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.energy.component.EnergyHandlerComponent;
import ruiseki.okcore.energy.component.IEnergyHandlerExclusion;
import ruiseki.okcore.helper.BlockStateHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.persist.nbt.NBTPersist;

public class TileEnergyBattery extends TileCableConnectable
    implements IEnergyStorageCapacity, IEnergyProvider, IEnergyReceiver {

    @NBTPersist
    private int energy;
    @NBTPersist(useDefaultValue = false)
    private int capacity = BlockEnergyBatteryConfig.capacity;

    @Delegate(excludes = IEnergyHandlerExclusion.class)
    private final EnergyHandlerComponent energyHandlerComponent = new EnergyHandlerComponent(this);

    public TileEnergyBattery() {
        this.capabilityCache.addCapabilityResolver(
            BasicCapabilityResolver
                .create(NetworkElementProviderConfig.CAPABILITY, () -> new NetworkElementProviderSingleton() {

                    @Override
                    @SuppressWarnings({ "unchecked", "rawtypes" })
                    public INetworkElement createNetworkElement(World world, BlockPos blockPos) {
                        return new EnergyBatteryNetworkElement(DimPos.of(world, blockPos));
                    }
                }));

        this.capabilityCache.addCapabilityResolver(BasicCapabilityResolver.create(CapabilityEnergy.ENERGY, () -> this));
    }

    public boolean isCreative() {
        Block block = getBlockType();
        return block instanceof BlockEnergyBatteryBase && ((BlockEnergyBatteryBase) block).isCreative();
    }

    @Override
    public int getEnergyStored() {
        if (isCreative()) return Integer.MAX_VALUE;
        return this.energy;
    }

    @Override
    public int getMaxEnergyStored() {
        if (isCreative()) return Integer.MAX_VALUE;
        return capacity;
    }

    public void updateBlockState() {
        if (!isCreative()) {
            BlockState blockState = BlockStateHelpers.getState(worldObj, pos);
            if (blockState.getBlock() == BlockEnergyBatteryConfig._instance.getInstance()) {
                int fill = Math.max(
                    0,
                    (int) Math.floor(
                        ((float) energy * (BlockEnergyBattery.FILL.getAllowedValues() - 1))
                            / (float) getMaxEnergyStored()));
                if (blockState.getPropertyValue(BlockEnergyBattery.FILL) != fill) {
                    BlockStateHelpers.set(worldObj, pos, BlockEnergyBattery.FILL, fill);
                    sendUpdate();
                }
            }
        }
    }

    protected void setEnergy(int energy) {
        if (!isCreative()) {
            int lastEnergy = this.energy;
            if (lastEnergy != energy) {
                this.energy = energy;
                markDirty();
                sendUpdate();
            }
        }
    }

    @Override
    protected int getUpdateBackoffTicks() {
        return 20;
    }

    @Override
    protected void onSendUpdate() {
        worldObj.notifyBlocksOfNeighborChange(
            xCoord,
            yCoord,
            zCoord,
            getBlockType(),
            MinecraftHelpers.BLOCK_NOTIFY | MinecraftHelpers.BLOCK_NOTIFY_CLIENT
                | MinecraftHelpers.BLOCK_NOTIFY_NO_RERENDER);
    }

    public static int getEnergyPerTick(int capacity) {
        return Math.max(
            capacity / BlockEnergyBatteryConfig.energyRateCapacityFraction,
            BlockEnergyBatteryConfig.minEnergyRate);
    }

    protected int getEnergyPerTick() {
        return getEnergyPerTick(getMaxEnergyStored());
    }

    @Override
    public int receiveEnergy(int energy, boolean simulate) {
        if (!isCreative()) {
            int stored = getEnergyStored();
            int energyReceived = Math.min(getMaxEnergyStored() - stored, energy);
            if (!simulate) {
                setEnergy(stored + energyReceived);
            }
            return energyReceived;
        }
        return 0;
    }

    @Override
    public int extractEnergy(int energy, boolean simulate) {
        if (isCreative()) return energy;
        energy = Math.max(0, Math.min(energy, getEnergyPerTick()));
        int stored = getEnergyStored();
        int newEnergy = Math.max(stored - energy, 0);;
        if (!simulate) {
            setEnergy(newEnergy);
        }
        return stored - newEnergy;
    }

    protected int addEnergy(int energy) {
        int filled = addEnergyFe(energy, false);
        extractEnergy(filled, false);
        return filled;
    }

    protected int addEnergyFe(int energy, boolean simulate) {
        return EnergyHelpers.fillNeigbours(getWorldObj(), getPos(), energy, simulate);
    }

    @Override
    protected void updateTileEntity() {
        super.updateTileEntity();
        if (!getWorldObj().isRemote && getEnergyStored() > 0
            && getWorldObj().isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {
            addEnergy(Math.min(getEnergyPerTick(), getEnergyStored()));
        }
    }

    @Override
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
