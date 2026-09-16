package ruiseki.integrateddynamics.core.evaluate.variable;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import cpw.mods.fml.common.registry.GameData;
import lombok.ToString;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeNamed;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeNullable;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeUniquelyNamed;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeItemStackLPElement;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeLPElementBase;
import ruiseki.okcore.helper.BlockHelpers;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Value type with values that are blocks (these are internally stored as blockstates).
 *
 * @author rubensworks
 */
public class ValueObjectTypeBlock extends ValueObjectTypeBase<ValueObjectTypeBlock.ValueBlock>
    implements IValueTypeNamed<ValueObjectTypeBlock.ValueBlock>,
    IValueTypeUniquelyNamed<ValueObjectTypeBlock.ValueBlock>, IValueTypeNullable<ValueObjectTypeBlock.ValueBlock> {

    public ValueObjectTypeBlock() {
        super("block", ValueObjectTypeBlock.ValueBlock.class);
    }

    public static String getBlockDisplayNameUsSafe(BlockState blockState) throws NoSuchMethodException {
        return blockState.getBlock()
            .getLocalizedName();
    }

    public static String getBlockkDisplayNameSafe(BlockState blockState) {
        // Certain mods may call client-side only methods,
        // so call a server-side-safe fallback method if that fails.
        try {
            return getBlockDisplayNameUsSafe(blockState);
        } catch (NoSuchMethodException e) {
            return LangHelpers.localize(
                blockState.getBlock()
                    .getUnlocalizedName() + ".name");
        }
    }

    @Override
    public ValueBlock getDefault() {
        return ValueBlock.of(null);
    }

    @Override
    public String toCompactString(ValueBlock value) {
        if (value.getRawValue()
            .isPresent()) {
            BlockState blockState = value.getRawValue()
                .get();
            ItemStack itemStack = BlockHelpers.getItemStackFromBlockState(blockState);
            if (itemStack != null) {
                return ValueObjectTypeItemStack.getItemStackDisplayNameSafe(itemStack);
            }
            return ValueObjectTypeBlock.getBlockkDisplayNameSafe(blockState);
        }
        return "";
    }

    @Override
    public NBTBase serialize(ValueBlock value) {
        if (!value.getRawValue()
            .isPresent()) return new NBTTagCompound();
        return BlockHelpers.serializeBlockState(
            value.getRawValue()
                .get());
    }

    @Override
    public ValueBlock deserialize(NBTBase value) {
        if (value.getId() == Constants.NBT.TAG_END
            || (value.getId() == Constants.NBT.TAG_COMPOUND && ((NBTTagCompound) value).hasNoTags())) {
            return ValueBlock.of(null);
        }
        return ValueBlock.of(BlockHelpers.deserializeBlockState((NBTTagCompound) value));
    }

    @Override
    public String getName(ValueBlock a) {
        return toCompactString(a);
    }

    @Override
    public boolean isNull(ValueBlock a) {
        return !a.getRawValue()
            .isPresent();
    }

    @Override
    public ValueTypeLPElementBase createLogicProgrammerElement() {
        return new ValueTypeItemStackLPElement<>(
            this,
            new ValueTypeItemStackLPElement.IItemStackToValue<ValueObjectTypeBlock.ValueBlock>() {

                @Override
                public boolean isNullable() {
                    return true;
                }

                @Override
                public LangHelpers.UnlocalizedString validate(ItemStack itemStack) {
                    if (itemStack != null && !(itemStack.getItem() instanceof ItemBlock)) {
                        return new LangHelpers.UnlocalizedString(L10NValues.VALUETYPE_OBJECT_BLOCK_ERROR_NOBLOCK);
                    }
                    return null;
                }

                @Override
                public ValueObjectTypeBlock.ValueBlock getValue(ItemStack itemStack) {
                    return ValueObjectTypeBlock.ValueBlock
                        .of(itemStack == null ? null : BlockHelpers.getBlockStateFromItemStack(itemStack));
                }

                @Override
                public ItemStack getValueAsItemStack(ValueBlock value) {
                    return value.getRawValue()
                        .map(BlockHelpers::getItemStackFromBlockState)
                        .orElse(ItemHelpers.EMPTY);
                }
            });
    }

    @Override
    public String getUniqueName(ValueBlock value) {
        if (value.getRawValue()
            .isPresent()) {
            BlockState blockState = value.getRawValue()
                .get();
            int meta = blockState.getBlockMeta(0);
            return GameData.getBlockRegistry()
                .getNameForObject(blockState.getBlock()) + (meta > 0 ? " " + meta : "");
        }
        return "";
    }

    @ToString
    public static class ValueBlock extends ValueOptionalBase<BlockState> {

        private ValueBlock(BlockState blockState) {
            super(ValueTypes.OBJECT_BLOCK, blockState);
        }

        public static ValueBlock of(BlockState blockState) {
            return new ValueBlock(blockState);
        }

        @Override
        protected boolean isEqual(BlockState a, BlockState b) {
            return a.getBlock() == b.getBlock() && a.getBlockMeta(0) == b.getBlockMeta(0);
        }
    }

}
