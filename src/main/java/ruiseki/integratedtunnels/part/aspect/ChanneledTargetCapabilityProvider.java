package ruiseki.integratedtunnels.part.aspect;

import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetwork;
import ruiseki.integratedtunnels.core.part.PartStateRoundRobin;
import ruiseki.okcore.capabilities.ICapabilityProvider;

/**
 * A helper class for movement targets with a certain network type and a capability provider as target.
 *
 * @author rubensworks
 */
public abstract class ChanneledTargetCapabilityProvider<N extends IPositionedAddonsNetwork, T, M>
    extends ChanneledTarget<N, T> {

    private final ICapabilityProvider capabilityProvider;
    private final ForgeDirection side;

    private IIngredientComponentStorage<T, M> storage = null;

    public ChanneledTargetCapabilityProvider(INetwork network, @Nullable ICapabilityProvider capabilityProvider,
        ForgeDirection side, N channeledNetwork, @Nullable PartStateRoundRobin<?> partState, int channel,
        boolean roundRobin, boolean craftIfFailed, boolean passiveIO) {
        super(network, channeledNetwork, partState, channel, roundRobin, craftIfFailed, passiveIO);
        this.capabilityProvider = capabilityProvider;
        this.side = side;
    }

    @Override
    public boolean hasValidTarget() {
        return capabilityProvider != null && getPartState() != null;
    }

    protected abstract IngredientComponent<T, M> getComponent();

    public IIngredientComponentStorage<T, M> getStorage() {
        // Cache the storage
        if (storage == null) {
            storage = getComponent().getStorage(capabilityProvider, side);
        }
        return storage;
    }
}
