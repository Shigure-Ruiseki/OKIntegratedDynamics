package ruiseki.integratedtunnels.part.aspect;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.integrateddynamics.api.network.IEnergyNetwork;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectProperties;
import ruiseki.integratedtunnels.core.part.PartStateRoundRobin;
import ruiseki.okcore.capabilities.ICapabilityProvider;

/**
 * @author rubensworks
 */
public interface IEnergyTarget extends IChanneledTarget<IEnergyNetwork> {

    public IIngredientComponentStorage<Integer, Boolean> getEnergyChannel();

    public IIngredientComponentStorage<Integer, Boolean> getStorage();

    public int getAmount();

    public boolean isExactAmount();

    public static IEnergyTarget ofTile(PartTarget partTarget, IAspectProperties properties, int amount) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        TileEntity tile = target.getPos()
            .getBlockPos()
            .getTileEntity(
                target.getPos()
                    .getWorld());
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        return new EnergyTargetCapabilityProvider(
            (ICapabilityProvider) tile,
            target.getSide(),
            network,
            properties,
            amount,
            partState);
    }

    public static IEnergyTarget ofEntity(PartTarget partTarget, @Nullable Entity entity, IAspectProperties properties,
        int amount) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        return new EnergyTargetCapabilityProvider(
            (ICapabilityProvider) entity,
            target.getSide(),
            network,
            properties,
            amount,
            partState);
    }

}
