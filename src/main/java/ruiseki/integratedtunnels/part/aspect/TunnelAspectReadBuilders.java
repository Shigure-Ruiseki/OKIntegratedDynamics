package ruiseki.integratedtunnels.part.aspect;

import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.tuple.Pair;

import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.integrateddynamics.api.ingredient.IIngredientPositionsIndex;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectProperties;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeInteger;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeList;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeLong;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeOperator;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.part.aspect.build.AspectBuilder;
import ruiseki.integrateddynamics.core.part.aspect.build.IAspectValuePropagator;
import ruiseki.integrateddynamics.part.aspect.read.AspectReadBuilders;
import ruiseki.integratedtunnels.IntegratedTunnels;
import ruiseki.integratedtunnels.capability.network.FluidNetworkConfig;
import ruiseki.integratedtunnels.capability.network.ItemNetworkConfig;
import ruiseki.integratedtunnels.part.aspect.listproxy.ValueTypeListProxyPositionedFluidNetwork;
import ruiseki.integratedtunnels.part.aspect.listproxy.ValueTypeListProxyPositionedItemNetwork;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.DimPos;

/**
 * @author rubensworks
 */
public class TunnelAspectReadBuilders {

    public static final class Network {

        public static <T, M> Optional<IIngredientComponentStorage<T, M>> getChannel(
            Capability<? extends IPositionedAddonsNetworkIngredients<T, M>> networkCapability, DimPos dimPos,
            ForgeDirection side, int channel) {
            INetwork network = NetworkHelpers.getNetwork(dimPos.getWorld(), dimPos.getBlockPos(), side)
                .getOrNull();
            return Optional.ofNullable(
                network != null ? network.getCapability(networkCapability)
                    .map(itemNetwork -> {
                        itemNetwork.scheduleObservation();
                        return itemNetwork.getChannel(channel);
                    })
                    .orElse(null) : null);
        }

        public static <T, M> Optional<IIngredientPositionsIndex<T, M>> getChannelIndex(
            Capability<? extends IPositionedAddonsNetworkIngredients<T, M>> networkCapability, DimPos dimPos,
            ForgeDirection side, int channel) {
            INetwork network = NetworkHelpers.getNetwork(dimPos.getWorld(), dimPos.getBlockPos(), side)
                .getOrNull();
            return Optional.ofNullable(
                network != null ? network.getCapability(networkCapability)
                    .map(itemNetwork -> {
                        itemNetwork.scheduleObservation();
                        return itemNetwork.getChannelIndex(channel);
                    })
                    .orElse(null) : null);
        }

        public static final class Item {

            public static final AspectBuilder<ValueTypeList.ValueList, ValueTypeList, Pair<PartTarget, IAspectProperties>> BUILDER_LIST = AspectReadBuilders.BUILDER_LIST
                .byMod(IntegratedTunnels._instance)
                .withProperties(AspectReadBuilders.Network.PROPERTIES)
                .appendKind("itemnetwork");
            public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, Pair<PartTarget, IAspectProperties>> BUILDER_INTEGER = AspectReadBuilders.BUILDER_INTEGER
                .byMod(IntegratedTunnels._instance)
                .withProperties(AspectReadBuilders.Network.PROPERTIES)
                .appendKind("itemnetwork");
            public static final AspectBuilder<ValueTypeLong.ValueLong, ValueTypeLong, Pair<PartTarget, IAspectProperties>> BUILDER_LONG = AspectReadBuilders.BUILDER_LONG
                .byMod(IntegratedTunnels._instance)
                .withProperties(AspectReadBuilders.Network.PROPERTIES)
                .appendKind("itemnetwork");
            public static final AspectBuilder<ValueTypeOperator.ValueOperator, ValueTypeOperator, Pair<PartTarget, IAspectProperties>> BUILDER_OPERATOR = AspectReadBuilders.BUILDER_OPERATOR
                .byMod(IntegratedTunnels._instance)
                .withProperties(AspectReadBuilders.Network.PROPERTIES)
                .appendKind("itemnetwork");

            public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, IIngredientComponentStorage<ItemStack, Integer>> PROP_GET_CHANNEL = input -> {
                int channel = input.getRight()
                    .getValue(AspectReadBuilders.Network.PROPERTY_CHANNEL)
                    .getRawValue();
                return getChannel(
                    ItemNetworkConfig.CAPABILITY,
                    input.getLeft()
                        .getTarget()
                        .getPos(),
                    input.getLeft()
                        .getTarget()
                        .getSide(),
                    channel).orElse(null);
            };
            public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, IIngredientPositionsIndex<ItemStack, Integer>> PROP_GET_CHANNELINDEX = input -> {
                int channel = input.getRight()
                    .getValue(AspectReadBuilders.Network.PROPERTY_CHANNEL)
                    .getRawValue();
                return getChannelIndex(
                    ItemNetworkConfig.CAPABILITY,
                    input.getLeft()
                        .getTarget()
                        .getPos(),
                    input.getLeft()
                        .getTarget()
                        .getSide(),
                    channel).orElse(null);
            };

            public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, ValueTypeList.ValueList> PROP_GET_LIST = input -> ValueTypeList.ValueList
                .ofFactory(
                    new ValueTypeListProxyPositionedItemNetwork(
                        input.getLeft()
                            .getTarget()
                            .getPos(),
                        input.getLeft()
                            .getTarget()
                            .getSide(),
                        input.getRight()
                            .getValue(AspectReadBuilders.Network.PROPERTY_CHANNEL)
                            .getRawValue()));
        }

        public static final class Fluid {

            public static final AspectBuilder<ValueTypeList.ValueList, ValueTypeList, Pair<PartTarget, IAspectProperties>> BUILDER_LIST = AspectReadBuilders.BUILDER_LIST
                .byMod(IntegratedTunnels._instance)
                .withProperties(AspectReadBuilders.Network.PROPERTIES)
                .appendKind("fluidnetwork");
            public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, Pair<PartTarget, IAspectProperties>> BUILDER_INTEGER = AspectReadBuilders.BUILDER_INTEGER
                .byMod(IntegratedTunnels._instance)
                .withProperties(AspectReadBuilders.Network.PROPERTIES)
                .appendKind("fluidnetwork");
            public static final AspectBuilder<ValueTypeLong.ValueLong, ValueTypeLong, Pair<PartTarget, IAspectProperties>> BUILDER_LONG = AspectReadBuilders.BUILDER_LONG
                .byMod(IntegratedTunnels._instance)
                .withProperties(AspectReadBuilders.Network.PROPERTIES)
                .appendKind("fluidnetwork");
            public static final AspectBuilder<ValueTypeOperator.ValueOperator, ValueTypeOperator, Pair<PartTarget, IAspectProperties>> BUILDER_OPERATOR = AspectReadBuilders.BUILDER_OPERATOR
                .byMod(IntegratedTunnels._instance)
                .withProperties(AspectReadBuilders.Network.PROPERTIES)
                .appendKind("fluidnetwork");

            public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, IIngredientComponentStorage<FluidStack, Integer>> PROP_GET_CHANNEL = input -> {
                int channel = input.getRight()
                    .getValue(AspectReadBuilders.Network.PROPERTY_CHANNEL)
                    .getRawValue();
                return getChannel(
                    FluidNetworkConfig.CAPABILITY,
                    input.getLeft()
                        .getTarget()
                        .getPos(),
                    input.getLeft()
                        .getTarget()
                        .getSide(),
                    channel).orElse(null);
            };
            public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, IIngredientPositionsIndex<FluidStack, Integer>> PROP_GET_CHANNELINDEX = input -> {
                int channel = input.getRight()
                    .getValue(AspectReadBuilders.Network.PROPERTY_CHANNEL)
                    .getRawValue();
                return getChannelIndex(
                    FluidNetworkConfig.CAPABILITY,
                    input.getLeft()
                        .getTarget()
                        .getPos(),
                    input.getLeft()
                        .getTarget()
                        .getSide(),
                    channel).orElse(null);
            };

            public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, ValueTypeList.ValueList> PROP_GET_LIST = input -> ValueTypeList.ValueList
                .ofFactory(
                    new ValueTypeListProxyPositionedFluidNetwork(
                        input.getLeft()
                            .getTarget()
                            .getPos(),
                        input.getLeft()
                            .getTarget()
                            .getSide(),
                        input.getRight()
                            .getValue(AspectReadBuilders.Network.PROPERTY_CHANNEL)
                            .getRawValue()));
        }
    }

}
