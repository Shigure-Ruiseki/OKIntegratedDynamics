package ruiseki.integratedtunnels.part.aspect.listproxy;

import java.util.Iterator;
import java.util.Optional;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.Iterators;

import ruiseki.integrateddynamics.api.ingredient.IIngredientPositionsIndex;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeFluidStack;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeListProxyPositioned;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integratedtunnels.capability.network.FluidNetworkConfig;
import ruiseki.integratedtunnels.part.aspect.TunnelAspectReadBuilders;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.FluidHelpers;
import ruiseki.okcore.ingredient.collection.IIngredientCollectionLike;
import ruiseki.okcore.persist.nbt.INBTProvider;
import ruiseki.okcore.persist.nbt.NBTClassType;

/**
 * A list proxy for the fluids in a network at a certain position.
 */
public class ValueTypeListProxyPositionedFluidNetwork
    extends ValueTypeListProxyPositioned<ValueObjectTypeFluidStack, ValueObjectTypeFluidStack.ValueFluidStack>
    implements INBTProvider {

    private int channel;

    public ValueTypeListProxyPositionedFluidNetwork(DimPos pos, ForgeDirection side, int channel) {
        super(
            TunnelValueTypeListProxyFactories.POSITIONED_FLUID_NETWORK.getName(),
            ValueTypes.OBJECT_FLUIDSTACK,
            pos,
            side);
        this.channel = channel;
    }

    public ValueTypeListProxyPositionedFluidNetwork() {
        this(null, null, 0);
    }

    @Override
    public void writeGeneratedFieldsToNBT(NBTTagCompound tag) {
        super.writeGeneratedFieldsToNBT(tag);
        NBTClassType.writeNbt(Integer.class, "channel", this.channel, tag);
    }

    @Override
    public void readGeneratedFieldsFromNBT(NBTTagCompound tag) {
        super.readGeneratedFieldsFromNBT(tag);
        this.channel = NBTClassType.readNbt(Integer.class, "channel", tag);
    }

    protected Optional<IIngredientPositionsIndex<FluidStack, Integer>> getChannelIndex() {
        return TunnelAspectReadBuilders.Network
            .getChannelIndex(FluidNetworkConfig.CAPABILITY, getPos(), getSide(), channel);
    }

    @Override
    public int getLength() {
        return getChannelIndex().map(IIngredientCollectionLike::size)
            .orElse(0);
    }

    @Override
    public ValueObjectTypeFluidStack.ValueFluidStack get(int index) {
        return ValueObjectTypeFluidStack.ValueFluidStack.of(
            getChannelIndex().map(store -> Iterators.get(store.iterator(), index, FluidHelpers.EMPTY))
                .orElse(FluidHelpers.EMPTY));
    }

    @Override
    public Iterator<ValueObjectTypeFluidStack.ValueFluidStack> iterator() {
        // We use a custom iterator that retrieves the network only once.
        // Because for large networks, the network would have to be retrieved for every single ingredient,
        // which could result in a major performance problem.
        return getChannelIndex().map(
            store -> store.stream()
                .map(ValueObjectTypeFluidStack.ValueFluidStack::of)
                .iterator())
            .orElse(Iterators.forArray());
    }
}
