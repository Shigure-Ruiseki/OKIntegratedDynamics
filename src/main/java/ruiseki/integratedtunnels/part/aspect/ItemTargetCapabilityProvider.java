package ruiseki.integratedtunnels.part.aspect;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectProperties;
import ruiseki.integratedtunnels.api.network.IItemNetwork;
import ruiseki.integratedtunnels.capability.network.ItemNetworkConfig;
import ruiseki.integratedtunnels.core.part.PartStateRoundRobin;
import ruiseki.integratedtunnels.core.predicate.IngredientPredicate;
import ruiseki.okcore.capabilities.ICapabilityProvider;

/**
 * @author rubensworks
 */
public class ItemTargetCapabilityProvider extends ChanneledTargetCapabilityProvider<IItemNetwork, ItemStack, Integer>
    implements IItemTarget {

    private final ITunnelConnection connection;
    private final int slot;
    private final IngredientPredicate<ItemStack, Integer> itemStackMatcher;
    private final PartTarget partTarget;
    private final IAspectProperties properties;

    public ItemTargetCapabilityProvider(ITunnelTransfer transfer, INetwork network,
        @Nullable ICapabilityProvider capabilityProvider, ForgeDirection side, int slot,
        IngredientPredicate<ItemStack, Integer> itemStackMatcher, PartTarget partTarget, IAspectProperties properties,
        @Nullable PartStateRoundRobin<?> partState) {
        super(
            network,
            capabilityProvider,
            side,
            network.getCapability(ItemNetworkConfig.CAPABILITY)
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
        this.slot = slot;
        this.itemStackMatcher = itemStackMatcher;
        this.partTarget = partTarget;
        this.properties = properties;
    }

    @Override
    public IIngredientComponentStorage<ItemStack, Integer> getItemChannel() {
        return getChanneledNetwork().getChannel(getChannel());
    }

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public IngredientPredicate<ItemStack, Integer> getItemStackMatcher() {
        return itemStackMatcher;
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

    @Override
    protected IngredientComponent<ItemStack, Integer> getComponent() {
        return IngredientComponent.ITEMSTACK;
    }
}
