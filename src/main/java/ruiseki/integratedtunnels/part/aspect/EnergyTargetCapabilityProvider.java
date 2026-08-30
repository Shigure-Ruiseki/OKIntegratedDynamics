package ruiseki.integratedtunnels.part.aspect;

import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.integrateddynamics.api.network.IEnergyNetwork;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectProperties;
import ruiseki.integrateddynamics.capability.network.EnergyNetworkConfig;
import ruiseki.integratedtunnels.core.part.PartStateRoundRobin;
import ruiseki.okcore.capabilities.ICapabilityProvider;

/**
 * @author rubensworks
 */
public class EnergyTargetCapabilityProvider extends ChanneledTargetCapabilityProvider<IEnergyNetwork, Long, Boolean>
    implements IEnergyTarget {

    private final long amount;
    private final boolean exactAmount;

    public EnergyTargetCapabilityProvider(@Nullable ICapabilityProvider capabilityProvider, ForgeDirection side,
        INetwork network, IAspectProperties properties, long amount, @Nullable PartStateRoundRobin<?> partStateEnergy) {
        super(
            network,
            capabilityProvider,
            side,
            network.getCapability(EnergyNetworkConfig.CAPABILITY)
                .getOrNull(),
            partStateEnergy,
            properties.getValue(TunnelAspectWriteBuilders.PROP_CHANNEL)
                .getRawValue(),
            properties.getValue(TunnelAspectWriteBuilders.PROP_ROUNDROBIN)
                .getRawValue(),
            properties.getValue(TunnelAspectWriteBuilders.PROP_CRAFT)
                .getRawValue());
        this.amount = amount;
        this.exactAmount = properties.getValue(TunnelAspectWriteBuilders.PROP_EXACTAMOUNT)
            .getRawValue();
    }

    @Override
    public IIngredientComponentStorage<Long, Boolean> getEnergyChannel() {
        return getChanneledNetwork().getChannelInternal(getChannel());
    }

    @Override
    public long getAmount() {
        return amount;
    }

    @Override
    public boolean isExactAmount() {
        return exactAmount;
    }

    @Override
    protected IngredientComponent<Long, Boolean> getComponent() {
        return IngredientComponent.ENERGY;
    }
}
