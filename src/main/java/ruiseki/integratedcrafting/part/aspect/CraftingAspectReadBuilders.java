package ruiseki.integratedcrafting.part.aspect;

import org.apache.commons.lang3.tuple.Pair;

import ruiseki.integratedcrafting.IntegratedCrafting;
import ruiseki.integratedcrafting.api.network.ICraftingNetwork;
import ruiseki.integratedcrafting.capability.network.CraftingNetworkConfig;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectProperties;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeList;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.part.aspect.build.AspectBuilder;
import ruiseki.integrateddynamics.core.part.aspect.build.IAspectValuePropagator;
import ruiseki.integrateddynamics.part.aspect.read.AspectReadBuilders;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.datastructure.LazyOptional;

/**
 * @author rubensworks
 */
public class CraftingAspectReadBuilders {

    public static final class CraftingNetwork {

        public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, Pair<IAspectProperties, LazyOptional<ICraftingNetwork>>> PROP_GET_CRAFTING_NETWORK = input -> {
            DimPos dimPos = input.getLeft()
                .getTarget()
                .getPos();
            INetwork network = NetworkHelpers.getNetwork(
                dimPos.getWorld(),
                dimPos.getBlockPos(),
                input.getLeft()
                    .getTarget()
                    .getSide());
            return Pair.of(
                input.getRight(),
                network != null ? network.getCapability(CraftingNetworkConfig.CAPABILITY) : LazyOptional.empty());
        };

        public static final AspectBuilder<ValueTypeList.ValueList, ValueTypeList, Pair<IAspectProperties, LazyOptional<ICraftingNetwork>>> BUILDER_LIST = AspectReadBuilders.BUILDER_LIST
            .byMod(IntegratedCrafting._instance)
            .withProperties(AspectReadBuilders.Network.PROPERTIES)
            .handle(PROP_GET_CRAFTING_NETWORK, "network");

    }

}
