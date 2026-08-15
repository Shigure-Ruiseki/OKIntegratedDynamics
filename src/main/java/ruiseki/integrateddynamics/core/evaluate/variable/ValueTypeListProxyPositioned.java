package ruiseki.integrateddynamics.core.evaluate.variable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.persist.nbt.INBTProvider;
import ruiseki.okcore.persist.nbt.NBTClassType;

/**
 * A list proxy for a certain position.
 */
public abstract class ValueTypeListProxyPositioned<T extends IValueType<V>, V extends IValue>
    extends ValueTypeListProxyBase<T, V> implements INBTProvider {

    private DimPos pos;
    private ForgeDirection side;

    public ValueTypeListProxyPositioned(String name, T valueType, DimPos pos, ForgeDirection side) {
        super(name, valueType);
        this.pos = pos;
        this.side = side;
    }

    @Override
    public void writeGeneratedFieldsToNBT(NBTTagCompound tag) {
        NBTClassType.writeNbt(DimPos.class, "pos", pos, tag);
        NBTClassType.writeNbt(ForgeDirection.class, "side", side, tag);
    }

    @Override
    public void readGeneratedFieldsFromNBT(NBTTagCompound tag) {
        this.pos = NBTClassType.readNbt(DimPos.class, "pos", tag);
        this.side = NBTClassType.readNbt(ForgeDirection.class, "side", tag);
    }

    protected DimPos getPos() {
        return pos;
    }

    protected void setPos(DimPos pos) {
        this.pos = pos;
    }

    protected ForgeDirection getSide() {
        return side;
    }

    protected void setSide(ForgeDirection side) {
        this.side = side;
    }
}
