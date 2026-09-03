package ruiseki.integratedtunnels.part.aspect.operator;

import java.util.Optional;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integrateddynamics.api.evaluate.operator.IOperator;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.ingredient.IIngredientPositionsIndex;
import ruiseki.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import ruiseki.integrateddynamics.core.evaluate.operator.PositionedOperator;
import ruiseki.integratedtunnels.part.aspect.TunnelAspectReadBuilders;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.persist.nbt.NBTClassType;

/**
 * @author rubensworks
 */
public abstract class PositionedOperatorIngredientIndex<T, M> extends PositionedOperator {

    private int channel;

    public PositionedOperatorIngredientIndex(String name, PositionedOperatorIngredientIndex.Function<T, M> function,
        IValueType input, IValueType output, DimPos pos, ForgeDirection side, int channel) {
        super(name, name, new IValueType[] { input }, output, function, IConfigRenderPattern.PREFIX_1, pos, side);
        this.channel = channel;
        ((PositionedOperatorIngredientIndex.Function) this.getFunction()).setOperator(this);
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

    @Override
    protected String getUnlocalizedType() {
        return "virtual";
    }

    @Override
    public IOperator materialize() {
        return this;
    }

    protected Optional<IIngredientPositionsIndex<T, M>> getChannelIndex() {
        return TunnelAspectReadBuilders.Network.getChannelIndex(getNetworkCapability(), getPos(), getSide(), channel);
    }

    protected abstract Capability<? extends IPositionedAddonsNetworkIngredients<T, M>> getNetworkCapability();

    public static abstract class Function<T, M> implements IFunction {

        private PositionedOperatorIngredientIndex<T, M> operator;

        public void setOperator(PositionedOperatorIngredientIndex<T, M> operator) {
            this.operator = operator;
        }

        public PositionedOperatorIngredientIndex<T, M> getOperator() {
            return operator;
        }
    }
}
