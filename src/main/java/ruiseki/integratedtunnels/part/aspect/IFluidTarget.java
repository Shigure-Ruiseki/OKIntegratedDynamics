package ruiseki.integratedtunnels.part.aspect;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectProperties;
import ruiseki.integratedtunnels.api.network.IFluidNetwork;
import ruiseki.integratedtunnels.core.part.PartStateRoundRobin;
import ruiseki.integratedtunnels.core.predicate.IngredientPredicate;
import ruiseki.okcore.capabilities.ICapabilityProvider;

/**
 * @author rubensworks
 */
public interface IFluidTarget extends IChanneledTarget<IFluidNetwork> {

    public IIngredientComponentStorage<FluidStack, Integer> getFluidChannel();

    public IIngredientComponentStorage<FluidStack, Integer> getStorage();

    public IngredientPredicate<FluidStack, Integer> getFluidStackMatcher();

    public PartTarget getPartTarget();

    public IAspectProperties getProperties();

    public ITunnelConnection getConnection();

    public static IFluidTarget ofCapabilityProvider(ITunnelTransfer transfer, PartTarget partTarget,
        IAspectProperties properties, IngredientPredicate<FluidStack, Integer> fluidStackMatcher) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        TileEntity tile = target.getPos()
            .getBlockPos()
            .getTileEntity(
                target.getPos()
                    .getWorld());
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        return new FluidTargetCapabilityProvider(
            transfer,
            network,
            (ICapabilityProvider) tile,
            target.getSide(),
            fluidStackMatcher,
            partTarget,
            properties,
            partState);
    }

    public static IFluidTarget ofEntity(ITunnelTransfer transfer, PartTarget partTarget, @Nullable Entity entity,
        IAspectProperties properties, IngredientPredicate<FluidStack, Integer> fluidStackMatcher) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        return new FluidTargetCapabilityProvider(
            transfer,
            network,
            (ICapabilityProvider) entity,
            target.getSide(),
            fluidStackMatcher,
            partTarget,
            properties,
            partState);
    }

    public static IFluidTarget ofBlock(ITunnelTransfer transfer, PartTarget partTarget, IAspectProperties properties,
        IngredientPredicate<FluidStack, Integer> fluidStackMatcher) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        return new FluidTargetCapabilityProvider(
            transfer,
            network,
            null,
            target.getSide(),
            fluidStackMatcher,
            partTarget,
            properties,
            partState);
    }

    public static IFluidTarget ofStorage(ITunnelTransfer transfer, INetwork network, PartTarget partTarget,
        IAspectProperties properties, IngredientPredicate<FluidStack, Integer> fluidStackMatcher,
        IIngredientComponentStorage<FluidStack, Integer> storage) {
        PartPos center = partTarget.getCenter();
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        return new FluidTargetStorage(transfer, network, storage, fluidStackMatcher, partTarget, properties, partState);
    }

}
