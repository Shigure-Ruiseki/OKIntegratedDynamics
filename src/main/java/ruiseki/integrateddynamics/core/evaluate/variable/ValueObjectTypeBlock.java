package ruiseki.integrateddynamics.core.evaluate.variable;

import org.apache.commons.lang3.tuple.Pair;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import joptsimple.internal.Strings;
import lombok.ToString;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeNamed;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeNullable;
import ruiseki.okcore.helper.BlockHelpers;

/**
 * Value type with values that are blocks (these are internally stored as blockstates).
 * 
 * @author rubensworks
 */
public class ValueObjectTypeBlock extends ValueObjectTypeBase<ValueObjectTypeBlock.ValueBlock>
    implements IValueTypeNamed<ValueObjectTypeBlock.ValueBlock>, IValueTypeNullable<ValueObjectTypeBlock.ValueBlock> {

    public ValueObjectTypeBlock() {
        super("block");
    }

    @Override
    public ValueBlock getDefault() {
        return ValueBlock.of(null);
    }

    @Override
    public String toCompactString(ValueBlock value) {
        return value.getRawValue()
            .isPresent()
                ? value.getRawValue()
                    .get()
                    .getBlock()
                    .getLocalizedName()
                : "";
    }

    @Override
    public String serialize(ValueBlock value) {
        if (!value.getRawValue()
            .isPresent()) return "";
        Pair<String, Integer> serializedBlockState = BlockHelpers.serializeBlockState(
            value.getRawValue()
                .get());
        return String.format("%s$%s", serializedBlockState.getLeft(), serializedBlockState.getRight());
    }

    @Override
    public ValueBlock deserialize(String value) {
        if (Strings.isNullOrEmpty(value)) return ValueBlock.of(null);
        String[] parts = value.split("\\$");
        try {
            return ValueBlock.of(BlockHelpers.deserializeBlockState(Pair.of(parts[0], Integer.parseInt(parts[1]))));
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException(String.format("Something went wrong while deserializing '%s'.", value));
        }
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
            return a.getBlock() == b.getBlock() && a.getPropertyValue("meta") == b.getPropertyValue("meta");
        }
    }

}
