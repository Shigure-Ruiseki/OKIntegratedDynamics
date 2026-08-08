package ruiseki.integrateddynamics.network;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkElement;
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
    public void addDrops(List<ItemStack> itemStacks, boolean dropMainElement) {

    }

    @Override
    public boolean onNetworkAddition(INetwork network) {
        return NetworkHelpers.getEnergyNetwork(network)
            .addEnergyBattery(getPos());
    }

    @Override
    public void onNetworkRemoval(INetwork network) {
        NetworkHelpers.getEnergyNetwork(network)
            .removeEnergyBattery(getPos());
    }

    @Override
    public void onPreRemoved(INetwork network) {

    }

    @Override
    public void onNeighborBlockChange(INetwork network, IBlockAccess world, Block neighborBlock) {

    }

    @Override
    public int compareTo(INetworkElement o) {
        if (o instanceof EnergyBatteryNetworkElement) {
            return getPos().compareTo(((EnergyBatteryNetworkElement) o).getPos());
        }
        return Integer.compare(hashCode(), o.hashCode());
    }

}
