package ruiseki.integrateddynamics.network;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.api.network.IEnergyNetwork;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetwork;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.network.NetworkElementBase;
import ruiseki.integrateddynamics.tileentity.TileEnergyBattery;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.TileHelpers;

/**
 * Network element for variable stores.
 *
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class EnergyBatteryNetworkElement extends NetworkElementBase {

    private final DimPos pos;

    protected TileEnergyBattery getTile() {
        return TileHelpers.getSafeTile(getPos().getWorld(), getPos().getBlockPos(), TileEnergyBattery.class);
    }

    @Override
    public int getUpdateInterval() {
        return 0;
    }

    @Override
    public boolean isUpdate() {
        return false;
    }

    @Override
    public void update(INetwork network) {

    }

    @Override
    public void beforeNetworkKill(INetwork network) {

    }

    @Override
    public void afterNetworkAlive(INetwork network) {

    }

    @Override
    public void addDrops(List<ItemStack> itemStacks, boolean dropMainElement, boolean saveState) {

    }

    @Override
    public boolean onNetworkAddition(INetwork network) {
        IEnergyNetwork energyNetwork = NetworkHelpers.getEnergyNetwork(network);
        if (energyNetwork != null) {
            energyNetwork.addPosition(PartPos.of(getPos(), null), 0, IPositionedAddonsNetwork.DEFAULT_CHANNEL);
            return super.onNetworkAddition(network);
        }
        return false;
    }

    @Override
    public void onNetworkRemoval(INetwork network) {
        IEnergyNetwork energyNetwork = NetworkHelpers.getEnergyNetwork(network);
        if (energyNetwork != null) {
            energyNetwork.removePosition(PartPos.of(getPos(), null));
        }
    }

    @Override
    public void onPreRemoved(INetwork network) {

    }

    @Override
    public void onNeighborBlockChange(INetwork network, IBlockAccess world, Block neighborBlock) {

    }

    @Override
    public void setPriorityAndChannel(INetwork network, int priority, int channel) {

    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public int getChannel() {
        return IPositionedAddonsNetwork.DEFAULT_CHANNEL;
    }

    @Override
    public boolean canRevalidate(INetwork network) {
        return canRevalidatePositioned(network, pos);
    }

    @Override
    public void revalidate(INetwork network) {
        super.revalidate(network);
        revalidatePositioned(network, pos);
    }

    @Override
    public int compareTo(INetworkElement o) {
        if (o instanceof EnergyBatteryNetworkElement) {
            return getPos().compareTo(((EnergyBatteryNetworkElement) o).getPos());
        }
        return this.getClass()
            .getCanonicalName()
            .compareTo(
                o.getClass()
                    .getCanonicalName());
    }

}
