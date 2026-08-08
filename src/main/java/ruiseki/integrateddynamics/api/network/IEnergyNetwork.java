package ruiseki.integrateddynamics.api.network;

import java.util.Set;

import ruiseki.integrateddynamics.api.block.IEnergyBattery;
import ruiseki.integrateddynamics.api.part.PartPos;

/**
 * A network capability that holds energy.
 *
 * @author rubensworks
 */
public interface IEnergyNetwork extends IEnergyBattery {

    /**
     * Add the position of a energy storage battery that must be accessible to the network.
     *
     * @param pos The energy battery position.
     * @return If the battery was added to the network.
     */
    public boolean addEnergyBattery(PartPos pos);

    /**
     * Remove the position of a energy storage battery that was accessible to the network.
     *
     * @param pos The energy battery position.
     */
    public void removeEnergyBattery(PartPos pos);

    /**
     * @return The energy batteries in this network.
     */
    public Set<PartPos> getEnergyBatteries();

    /**
     * @return The current network consumption rate.
     */
    public int getConsumptionRate();

}
