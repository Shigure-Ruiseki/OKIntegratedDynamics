package ruiseki.integratedtunnels.part.aspect.listproxy;

import java.util.Iterator;
import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Iterators;

import ruiseki.integrateddynamics.api.ingredient.IIngredientPositionsIndex;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeItemStack;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeListProxyPositioned;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integratedtunnels.capability.network.ItemNetworkConfig;
import ruiseki.integratedtunnels.part.aspect.TunnelAspectReadBuilders;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.ingredient.collection.IIngredientCollectionLike;
import ruiseki.okcore.persist.nbt.INBTProvider;
import ruiseki.okcore.persist.nbt.NBTClassType;

/**
 * A list proxy for the items in a network at a certain position.
 */
public class ValueTypeListProxyPositionedItemNetwork
    extends ValueTypeListProxyPositioned<ValueObjectTypeItemStack, ValueObjectTypeItemStack.ValueItemStack>
    implements INBTProvider {

    private int channel;

    public ValueTypeListProxyPositionedItemNetwork(DimPos pos, ForgeDirection side, int channel) {
        super(
            TunnelValueTypeListProxyFactories.POSITIONED_ITEM_NETWORK.getName(),
            ValueTypes.OBJECT_ITEMSTACK,
            pos,
            side);
        this.channel = channel;
    }

    public ValueTypeListProxyPositionedItemNetwork() {
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

    protected Optional<IIngredientPositionsIndex<ItemStack, Integer>> getChannelIndex() {
        return TunnelAspectReadBuilders.Network
            .getChannelIndex(ItemNetworkConfig.CAPABILITY, getPos(), getSide(), channel);
    }

    @Override
    public int getLength() {
        return getChannelIndex().map(IIngredientCollectionLike::size)
            .orElse(0);
    }

    @Override
    public ValueObjectTypeItemStack.ValueItemStack get(int index) {
        return ValueObjectTypeItemStack.ValueItemStack.of(
            getChannelIndex().map(store -> Iterators.get(store.iterator(), index, ItemHelpers.EMPTY))
                .orElse(ItemHelpers.EMPTY));
    }

    @Override
    public Iterator<ValueObjectTypeItemStack.ValueItemStack> iterator() {
        // We use a custom iterator that retrieves the network only once.
        // Because for large networks, the network would have to be retrieved for every single ingredient,
        // which could result in a major performance problem.
        return getChannelIndex().map(
            store -> store.stream()
                .map(ValueObjectTypeItemStack.ValueItemStack::of)
                .iterator())
            .orElse(Iterators.forArray());
    }
}
