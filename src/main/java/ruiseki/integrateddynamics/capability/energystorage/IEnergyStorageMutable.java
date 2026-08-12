package ruiseki.integrateddynamics.capability.energystorage;

import cofh.api.energy.IEnergyStorage;

/**
 * An energy storage with a mutable energy level.
 * 
 * @author rubensworks
 */
public interface IEnergyStorageMutable extends IEnergyStorage {

    public void setEnergy(int energy);

}
