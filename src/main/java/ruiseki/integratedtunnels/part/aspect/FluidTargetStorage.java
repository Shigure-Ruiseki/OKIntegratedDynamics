package ruiseki.integratedtunnels.part.aspect;

import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectProperties;
import ruiseki.integratedtunnels.api.network.IFluidNetwork;
import ruiseki.integratedtunnels.capability.network.FluidNetworkConfig;
import ruiseki.integratedtunnels.core.part.PartStateRoundRobin;
import ruiseki.integratedtunnels.core.predicate.IngredientPredicate;

/**
 * @author rubensworks
 */
public class FluidTargetStorage extends ChanneledTarget<IFluidNetwork> implements IFluidTarget {

    private final ITunnelConnection connection;
    private final IIngredientComponentStorage<FluidStack, Integer> storage;
    private final IngredientPredicate<FluidStack, Integer> fluidStackMatcher;
    private final PartTarget partTarget;
    private final IAspectProperties properties;

    public FluidTargetStorage(ITunnelTransfer transfer, INetwork network,
        IIngredientComponentStorage<FluidStack, Integer> storage,
        IngredientPredicate<FluidStack, Integer> fluidStackMatcher, PartTarget partTarget, IAspectProperties properties,
        @Nullable PartStateRoundRobin<?> partState) {
        super(
            network,
            network.getCapability(FluidNetworkConfig.CAPABILITY)
                .getOrNull(),
            partState,
            properties.getValue(TunnelAspectWriteBuilders.PROP_CHANNEL)
                .getRawValue(),
            properties.getValue(TunnelAspectWriteBuilders.PROP_ROUNDROBIN)
                .getRawValue(),
            properties.getValue(TunnelAspectWriteBuilders.PROP_CRAFT)
                .getRawValue());
        this.connection = new TunnelConnectionPositionedNetwork(
            network,
            getChannel(),
            partTarget.getTarget(),
            transfer);
        this.storage = storage;
        this.fluidStackMatcher = fluidStackMatcher;
        this.partTarget = partTarget;
        this.properties = properties;
    }

    @Override
    public IIngredientComponentStorage<FluidStack, Integer> getFluidChannel() {
        return getChanneledNetwork().getChannelInternal(getChannel());
    }

    @Override
    public boolean hasValidTarget() {
        return storage != null;
    }

    @Override
    public IIngredientComponentStorage<FluidStack, Integer> getStorage() {
        return storage;
    }

    @Override
    public IngredientPredicate<FluidStack, Integer> getFluidStackMatcher() {
        return fluidStackMatcher;
    }

    @Override
    public PartTarget getPartTarget() {
        return partTarget;
    }

    @Override
    public IAspectProperties getProperties() {
        return properties;
    }

    @Override
    public ITunnelConnection getConnection() {
        return connection;
    }
}
