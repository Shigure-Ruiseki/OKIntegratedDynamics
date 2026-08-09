package ruiseki.integrateddynamics.capability.energystorage;

import cofh.api.energy.IEnergyStorage;

/**
 * An energy storage with a mutable capacity.
 * 
 * @author rubensworks
 */
public interface IEnergyStorageCapacity extends IEnergyStorage {

    public void setCapacity(int capacity);

}
