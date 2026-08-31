package ruiseki.integratedtunnels.part.aspect;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectProperties;
import ruiseki.integratedtunnels.api.network.IFluidNetwork;
import ruiseki.integratedtunnels.capability.network.FluidNetworkConfig;
import ruiseki.integratedtunnels.core.part.PartStateRoundRobin;
import ruiseki.integratedtunnels.core.predicate.IngredientPredicate;
import ruiseki.okcore.capabilities.ICapabilityProvider;

/**
 * @author rubensworks
 */
public class FluidTargetCapabilityProvider extends ChanneledTargetCapabilityProvider<IFluidNetwork, FluidStack, Integer>
    implements IFluidTarget {

    private final ITunnelConnection connection;
    private final PartTarget partTarget;
    private final IngredientPredicate<FluidStack, Integer> fluidStackMatcher;
    private final IAspectProperties properties;

    public FluidTargetCapabilityProvider(ITunnelTransfer transfer, INetwork network,
        @Nullable ICapabilityProvider capabilityProvider, ForgeDirection side,
        IngredientPredicate<FluidStack, Integer> fluidStackMatcher, PartTarget partTarget, IAspectProperties properties,
        @Nullable PartStateRoundRobin<?> partState) {
        super(
            network,
            capabilityProvider,
            side,
            network.getCapability(FluidNetworkConfig.CAPABILITY)
                .getOrNull(),
            partState,
            properties.getValue(TunnelAspectWriteBuilders.PROP_CHANNEL)
                .getRawValue(),
            properties.getValue(TunnelAspectWriteBuilders.PROP_ROUNDROBIN)
                .getRawValue(),
            properties.getValue(TunnelAspectWriteBuilders.PROP_CRAFT)
                .getRawValue());
        this.connection = new TunnelConnectionPositionedNetworkCapabilityProvider(
            network,
            getChannel(),
            partTarget.getTarget(),
            transfer,
            capabilityProvider);
        this.fluidStackMatcher = fluidStackMatcher;
        this.partTarget = partTarget;
        this.properties = properties;
    }

    @Override
    public PartTarget getPartTarget() {
        return partTarget;
    }

    @Override
    public IIngredientComponentStorage<FluidStack, Integer> getFluidChannel() {
        return getChanneledNetwork().getChannel(getChannel());
    }

    @Override
    public IngredientPredicate<FluidStack, Integer> getFluidStackMatcher() {
        return fluidStackMatcher;
    }

    @Override
    public IAspectProperties getProperties() {
        return properties;
    }

    @Override
    public ITunnelConnection getConnection() {
        return connection;
    }

    @Override
    protected IngredientComponent<FluidStack, Integer> getComponent() {
        return IngredientComponent.FLUIDSTACK;
    }
}
