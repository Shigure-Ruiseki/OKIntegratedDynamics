package ruiseki.integratedtunnels.part.aspect;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectProperties;
import ruiseki.integratedtunnels.api.network.IItemNetwork;
import ruiseki.integratedtunnels.core.part.PartStateRoundRobin;
import ruiseki.integratedtunnels.core.predicate.IngredientPredicate;
import ruiseki.okcore.capabilities.ICapabilityProvider;

/**
 * @author rubensworks
 */
public interface IItemTarget extends IChanneledTarget<IItemNetwork> {

    public IIngredientComponentStorage<ItemStack, Integer> getItemChannel();

    public IIngredientComponentStorage<ItemStack, Integer> getStorage();

    public int getSlot();

    public IngredientPredicate<ItemStack, Integer> getItemStackMatcher();

    public PartTarget getPartTarget();

    public IAspectProperties getProperties();

    public ITunnelConnection getConnection();

    public static IItemTarget ofCapabilityProvider(ITunnelTransfer transfer, PartTarget partTarget,
        IAspectProperties properties, IngredientPredicate<ItemStack, Integer> itemStackMatcher, int slot) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        TileEntity tile = target.getPos()
            .getBlockPos()
            .getTileEntity(
                target.getPos()
                    .getWorld());
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        return new ItemTargetCapabilityProvider(
            transfer,
            network,
            (ICapabilityProvider) tile,
            target.getSide(),
            slot,
            itemStackMatcher,
            partTarget,
            properties,
            partState);
    }

    public static IItemTarget ofEntity(ITunnelTransfer transfer, PartTarget partTarget, @Nullable Entity entity,
        IAspectProperties properties, IngredientPredicate<ItemStack, Integer> itemStackMatcher, int slot) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        return new ItemTargetCapabilityProvider(
            transfer,
            network,
            (ICapabilityProvider) entity,
            target.getSide(),
            slot,
            itemStackMatcher,
            partTarget,
            properties,
            partState);
    }

    public static IItemTarget ofBlock(ITunnelTransfer transfer, PartTarget partTarget, IAspectProperties properties,
        IngredientPredicate<ItemStack, Integer> itemStackMatcher, int slot) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        return new ItemTargetCapabilityProvider(
            transfer,
            network,
            null,
            target.getSide(),
            slot,
            itemStackMatcher,
            partTarget,
            properties,
            partState);
    }

    public static IItemTarget ofStorage(ITunnelTransfer transfer, INetwork network, PartTarget partTarget,
        IAspectProperties properties, IngredientPredicate<ItemStack, Integer> itemStackMatcher,
        IIngredientComponentStorage<ItemStack, Integer> storage, int slot) {
        PartPos center = partTarget.getCenter();
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        return new ItemTargetStorage(
            transfer,
            network,
            storage,
            slot,
            itemStackMatcher,
            partTarget,
            properties,
            partState);
    }

}
