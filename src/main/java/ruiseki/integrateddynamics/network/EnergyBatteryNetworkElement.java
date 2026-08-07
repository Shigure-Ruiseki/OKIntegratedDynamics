package ruiseki.integrateddynamics.network;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.api.network.IEnergyNetwork;
import ruiseki.integrateddynamics.api.network.INetworkElement;
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
public class EnergyBatteryNetworkElement extends NetworkElementBase<IEnergyNetwork> {

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
    public void update(IEnergyNetwork network) {

    }

    @Override
    public void beforeNetworkKill(IEnergyNetwork network) {

    }

    @Override
    public void afterNetworkAlive(IEnergyNetwork network) {

    }

    @Override
    public void addDrops(List<ItemStack> itemStacks, boolean dropMainElement) {

    }

    @Override
    public boolean onNetworkAddition(IEnergyNetwork network) {
        return network.addEnergyBattery(getPos());
    }

    @Override
    public void onNetworkRemoval(IEnergyNetwork network) {
        network.removeEnergyBattery(getPos());
    }

    @Override
    public void onPreRemoved(IEnergyNetwork network) {

    }

    @Override
    public void onNeighborBlockChange(IEnergyNetwork network, IBlockAccess world, Block neighborBlock) {

    }

    @Override
    public int compareTo(INetworkElement o) {
        if (o instanceof EnergyBatteryNetworkElement) {
            return getPos().compareTo(((EnergyBatteryNetworkElement) o).getPos());
        }
        return Integer.compare(hashCode(), o.hashCode());
    }

}
