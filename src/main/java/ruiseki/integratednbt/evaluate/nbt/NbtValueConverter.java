package ruiseki.integratednbt.evaluate.nbt;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeDouble;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeInteger;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeList;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeLong;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeNbt;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeString;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;

/**
 * Converts NBT tags to and from values.
 */
public abstract class NbtValueConverter {

    public static IValueType<? extends IValue> mapNBTToValueType(NBTBase nbt) {
        return mapNBTIDToValueType(nbt.getId());
    }

    public static IValueType<?> mapNBTIDToValueType(int nbtId) {
        switch (nbtId) {
            case 1: // Byte
            case 2: // Short
            case 3: // Int
                return ValueTypes.INTEGER;
            case 4: // Long
                return ValueTypes.LONG;
            case 5: // Float
            case 6: // Double
                return ValueTypes.DOUBLE;
            case 7: // Byte Array
            case 9: // List
            case 11: // Int Array
            case 12: // Long Array (1.7.10 chưa có nhưng giữ để tương thích ID)
                return ValueTypes.LIST;
            case 8: // String
                return ValueTypes.STRING;
            case 10: // Compound
                return ValueTypes.NBT;
            default:
                throw new RuntimeException("Unexpected NBT id:" + nbtId);
        }
    }

    public static String getDefaultValueDisplayText(int nbtId) {
        String formatCode = getDefaultValue(nbtId).getType()
            .getDisplayColorFormat()
            .toString();
        switch (nbtId) {
            case 1: // Byte
            case 2: // Short
            case 3: // Int
            case 4: // Long
            case 5: // Float
            case 6: // Double
            default:
                return formatCode + "0";
            case 7: // Byte Array
            case 9: // List
            case 11: // Int Array
            case 12: // Long Array
                return formatCode + "[]";
            case 8: // String
                return formatCode + "\"\"";
            case 10: // Compound
                return formatCode + "{}";
        }
    }

    public static IValue getDefaultValue(int nbtId) {
        switch (nbtId) {
            case 1: // Byte
            case 2: // Short
            case 3: // Int
                return ValueTypeInteger.ValueInteger.of(0);
            case 4: // Long
                return ValueTypeLong.ValueLong.of(0L);
            case 5: // Float
            case 6: // Double
                return ValueTypeDouble.ValueDouble.of(0.0D);
            case 7: // Byte Array
            case 11: // Int Array
            case 12: // Long Array
                return ValueTypeList.ValueList.ofList(ValueTypes.INTEGER, new ArrayList<>());
            case 8: // String
                return ValueTypeString.ValueString.of("");
            case 9: // List
                return ValueTypeList.ValueList.ofList(ValueTypes.CATEGORY_ANY, new ArrayList<>());
            case 10: // Compound
            default:
                return ValueTypeNbt.ValueNbt.of(new NBTTagCompound());
        }
    }

    public static IValue mapNBTToValue(NBTBase nbt) {
        if (nbt == null) {
            return null;
        }

        switch (nbt.getId()) {
            case 1: // Byte
                return ValueTypeInteger.ValueInteger.of(((NBTTagByte) nbt).func_150287_d());
            case 2: // Short
                return ValueTypeInteger.ValueInteger.of(((NBTTagShort) nbt).func_150287_d());
            case 3: // Int
                return ValueTypeInteger.ValueInteger.of(((NBTTagInt) nbt).func_150287_d());
            case 4: // Long
                return ValueTypeLong.ValueLong.of(((NBTTagLong) nbt).func_150291_c());
            case 5: // Float
                return ValueTypeDouble.ValueDouble.of(((NBTTagFloat) nbt).func_150288_h());
            case 6: // Double
                return ValueTypeDouble.ValueDouble.of(((NBTTagDouble) nbt).func_150286_g());
            case 7: { // Byte Array
                byte[] bytes = ((NBTTagByteArray) nbt).func_150292_c();
                List<ValueTypeInteger.ValueInteger> list = new ArrayList<>(bytes.length);
                for (byte b : bytes) {
                    list.add(ValueTypeInteger.ValueInteger.of(b));
                }
                return ValueTypeList.ValueList.ofList(ValueTypes.INTEGER, list);
            }
            case 8: // String
                return ValueTypeString.ValueString.of(((NBTTagString) nbt).func_150285_a_());
            case 9: { // List
                NBTTagList tagList = (NBTTagList) nbt;
                int count = tagList.tagCount();
                List<IValue> values = new ArrayList<>(count);
                for (int i = 0; i < count; i++) {
                    NBTBase tag = tagList.getCompoundTagAt(i);
                    values.add(mapNBTToValue(tag));
                }
                return ValueTypeList.ValueList.ofList(ValueTypes.CATEGORY_ANY, values);
            }
            case 10: // Compound
                return ValueTypeNbt.ValueNbt.of((NBTTagCompound) nbt);
            case 11: { // Int Array
                int[] ints = ((NBTTagIntArray) nbt).func_150302_c();
                List<ValueTypeInteger.ValueInteger> list = new ArrayList<>(ints.length);
                for (int i : ints) {
                    list.add(ValueTypeInteger.ValueInteger.of(i));
                }
                return ValueTypeList.ValueList.ofList(ValueTypes.INTEGER, list);
            }
            default:
                return null;
        }
    }
}
