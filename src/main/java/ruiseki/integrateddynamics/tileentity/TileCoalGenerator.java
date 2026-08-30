package ruiseki.integrateddynamics.tileentity;

import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Lists;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyStorage;
import lombok.experimental.Delegate;
import ruiseki.integrateddynamics.api.network.IEnergyNetwork;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetwork;
import ruiseki.integrateddynamics.capability.networkelementprovider.NetworkElementProviderConfig;
import ruiseki.integrateddynamics.capability.networkelementprovider.NetworkElementProviderSingleton;
import ruiseki.integrateddynamics.core.helper.EnergyHelpers;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.tileentity.TileCableConnectableInventory;
import ruiseki.integrateddynamics.network.CoalGeneratorNetworkElement;
import ruiseki.okcore.capabilities.resolver.BasicCapabilityResolver;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.energy.component.EnergyProviderComponent;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.persist.nbt.NBTPersist;

/**
 * A part entity for the coal energy generator.
 *
 * @author rubensworks
 */
public class TileCoalGenerator extends TileCableConnectableInventory implements IEnergyProvider, IEnergyStorage {

    public static final int MAX_PROGRESS = 13;
    public static final int ENERGY_PER_TICK = 20;
    public static final int SLOT_FUEL = 0;

    @NBTPersist
    private int currentlyBurningMax;
    @NBTPersist
    private int currentlyBurning;

    @NBTPersist
    private boolean lit;

    @Delegate
    private final EnergyProviderComponent energyProvider = new EnergyProviderComponent(this);

    public TileCoalGenerator() {
        super(1, "fuel", 64);
        this.capabilityCache.addCapabilityResolver(
            BasicCapabilityResolver
                .create(NetworkElementProviderConfig.CAPABILITY, () -> new NetworkElementProviderSingleton() {

                    @Override
                    public INetworkElement createNetworkElement(World world, BlockPos blockPos) {
                        return new CoalGeneratorNetworkElement(DimPos.of(world, blockPos));
                    }
                }));
        this.capabilityCache.addCapabilityResolver(BasicCapabilityResolver.create(CapabilityEnergy.ENERGY, () -> this));

        Collection<Integer> allSlots = Lists.newArrayList(SLOT_FUEL);
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            addSlotsToSide(side, allSlots);
        }
    }

    public LazyOptional<IEnergyNetwork> getEnergyNetwork() {
        return NetworkHelpers.getEnergyNetwork(getNetwork());
    }

    public boolean isLit() {
        return lit;
    }

    public void setLit(boolean lit) {
        this.lit = lit;
        this.markDirty();
        this.onSendUpdate();
    }

    public int getProgress() {
        float current = currentlyBurning;
        float max = currentlyBurningMax;
        if (max == 0) {
            return -1;
        }
        return Math.round((current / max) * (float) MAX_PROGRESS);
    }

    public boolean isBurning() {
        return currentlyBurning < currentlyBurningMax;
    }

    public boolean canAddEnergy(int energy) {
        IEnergyNetwork network = getEnergyNetwork().getOrNull();
        if (network != null && network.getChannelInternal(IPositionedAddonsNetwork.DEFAULT_CHANNEL)
            .insert((long) energy, true) == 0) {
            return true;
        }
        return addEnergyFe(energy, true) == energy;
    }

    protected int addEnergy(int energy) {
        IEnergyNetwork network = getEnergyNetwork().getOrNull();
        int toFill = energy;
        if (network != null) {
            toFill = Helpers.castSafe(
                network.getChannelInternal(IPositionedAddonsNetwork.DEFAULT_CHANNEL)
                    .insert((long) toFill, false));
        }
        if (toFill > 0) {
            toFill -= addEnergyFe(toFill, false);
        }
        return energy - toFill;
    }

    protected int addEnergyFe(int energy, boolean simulate) {
        return EnergyHelpers.fillNeigbours(getWorldObj(), getPos(), energy, simulate);
    }

    @Override
    protected void updateTileEntity() {
        super.updateTileEntity();
        if (!getWorldObj().isRemote) {
            boolean wasBurning = isBurning();

            if ((getStackInSlot(SLOT_FUEL) != null || isBurning()) && canAddEnergy(ENERGY_PER_TICK)) {
                if (isBurning()) {
                    if (++currentlyBurning >= currentlyBurningMax) {
                        currentlyBurning = 0;
                        currentlyBurningMax = 0;
                    }
                    int toFill = ENERGY_PER_TICK;
                    addEnergy(toFill);
                    markDirty();
                }
                if (!isBurning()) {
                    ItemStack fuel;
                    if (TileEntityFurnace.isItemFuel(getStackInSlot(SLOT_FUEL))
                        && (fuel = decrStackSize(SLOT_FUEL, 1)) != null) {
                        if (getStackInSlot(SLOT_FUEL) == null) {
                            setInventorySlotContents(
                                SLOT_FUEL,
                                fuel.getItem()
                                    .getContainerItem(fuel));
                        }
                        currentlyBurningMax = TileEntityFurnace.getItemBurnTime(fuel);
                        currentlyBurning = 0;
                        markDirty();
                    }
                }
            }

            if (wasBurning != isBurning()) {
                setLit(!wasBurning);
            }
        }
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return 0;
    }
}
