package ruiseki.integratednbt.evaluate.operator;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.operator.IOperator;
import ruiseki.integrateddynamics.api.evaluate.operator.IOperatorSerializer;
import ruiseki.integratednbt.evaluate.nbt.path.SegmentedNbtPath;

public class NbtExtractionOperatorSerializer implements IOperatorSerializer<NbtExtractionOperator> {

    @Override
    public boolean canHandle(IOperator operator) {
        return operator instanceof NbtExtractionOperator;
    }

    @Override
    public ResourceLocation getUniqueName() {
        return NbtExtractionOperator.UNIQUE_NAME;
    }

    @Override
    public NBTBase serialize(NbtExtractionOperator operator) {
        NBTTagCompound data = new NBTTagCompound();
        data.setTag(
            "path",
            operator.getExtractionPath()
                .toNBT());
        data.setByte("defaultNBTId", operator.getDefaultNBTId());
        return data;
    }

    @Override
    public NbtExtractionOperator deserialize(NBTBase nbt) throws EvaluationException {
        try {
            NBTTagCompound tag = (NBTTagCompound) nbt;
            return new NbtExtractionOperator(
                SegmentedNbtPath.fromNBT(tag.getCompoundTag("path"))
                    .orElse(new SegmentedNbtPath()),
                tag.getByte("defaultNBTId"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new EvaluationException(e.getMessage());
        }
    }
}
