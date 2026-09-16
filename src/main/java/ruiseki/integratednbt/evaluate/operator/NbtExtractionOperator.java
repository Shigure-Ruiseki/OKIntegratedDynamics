package ruiseki.integratednbt.evaluate.operator;

import java.util.List;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.ResourceLocation;

import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.operator.IOperator;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeNbt;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integratednbt.Reference;
import ruiseki.integratednbt.evaluate.nbt.NbtValueConverter;
import ruiseki.integratednbt.evaluate.nbt.path.SegmentedNbtPath;
import ruiseki.okcore.helper.LangHelpers;

public class NbtExtractionOperator implements IOperator {

    public static ResourceLocation UNIQUE_NAME = new ResourceLocation(Reference.MOD_ID, "nbt_extraction");
    private SegmentedNbtPath extractionPath;
    private byte defaultNBTId;

    public NbtExtractionOperator(SegmentedNbtPath extractionPath, byte defaultNBTId) {
        this.extractionPath = extractionPath;
        this.defaultNBTId = defaultNBTId;
    }

    public SegmentedNbtPath getExtractionPath() {
        return this.extractionPath;
    }

    public byte getDefaultNBTId() {
        return this.defaultNBTId;
    }

    @Override
    public ResourceLocation getUniqueName() {
        return UNIQUE_NAME;
    }

    @Override
    public String getUnlocalizedName() {
        return "integratednbt:nbt_extraction_operator.full_name";
    }

    @Override
    public void loadTooltip(List<String> lines, boolean appendOptionalInfo) {
        String operatorName = LangHelpers.localize(this.getUnlocalizedName());
        String categoryName = LangHelpers.localize(this.getUnlocalizedCategoryName());
        String symbol = this.getSymbol();
        String outputTypeName = LangHelpers.localize(
            this.getOutputType()
                .getUnlocalizedName());
        lines.add(LangHelpers.localize(L10NValues.OPERATOR_TOOLTIP_OPERATORNAME, operatorName, symbol));
        lines.add(LangHelpers.localize(L10NValues.OPERATOR_TOOLTIP_OPERATORCATEGORY, categoryName));
        lines.add(
            LangHelpers.localize(
                L10NValues.OPERATOR_TOOLTIP_INPUTTYPENAME,
                1,

                ValueTypes.NBT.getDisplayColorFormat() + LangHelpers.localize(ValueTypes.NBT.getUnlocalizedName())));
        lines.add(
            LangHelpers.localize(
                L10NValues.OPERATOR_TOOLTIP_OUTPUTTYPENAME,
                this.getOutputType()
                    .getDisplayColorFormat() + outputTypeName));
    }

    @Override
    public String getUnlocalizedCategoryName() {
        return "integratednbt:nbt_extraction_operator.category_name";
    }

    @Override
    public String getLocalizedNameFull() {
        return LangHelpers.localize("integratednbt:nbt_extraction_operator.category_name");
    }

    @Override
    public String getSymbol() {
        return this.extractionPath.getCompactDisplayText();
    }

    @Override
    public IValueType<?> getOutputType() {
        return ValueTypes.CATEGORY_ANY;
    }

    @Override
    public IValueType<?>[] getInputTypes() {
        return new IValueType[] { ValueTypes.NBT };
    }

    @Override
    public IValueType<?> getConditionalOutputType(IVariable[] input) {
        try {
            if (input.length == 1) {
                IValue value = input[0].getValue();
                if (value instanceof ValueTypeNbt.ValueNbt) {
                    NBTBase extracted = this.extractionPath.extract(
                        ((ValueTypeNbt.ValueNbt) value).getRawValue()
                            .orElse(null));
                    if (extracted != null) {
                        return NbtValueConverter.mapNBTToValueType(extracted);
                    }
                }
            }
        } catch (EvaluationException ignored) {}
        return NbtValueConverter.mapNBTIDToValueType(this.defaultNBTId);
    }

    @Override
    public IValue evaluate(IVariable... input) throws EvaluationException {
        if (input.length == 1) {
            IValue value = input[0].getValue();
            if (value instanceof ValueTypeNbt.ValueNbt) {
                NBTBase extracted = this.extractionPath.extract(
                    ((ValueTypeNbt.ValueNbt) value).getRawValue()
                        .orElse(null));
                if (extracted != null) {
                    return NbtValueConverter.mapNBTToValue(extracted);
                }
            }
        }
        return NbtValueConverter.getDefaultValue(this.defaultNBTId);
    }

    @Override
    public int getRequiredInputLength() {
        return 1;
    }

    @Override
    public LangHelpers.UnlocalizedString validateTypes(IValueType[] input) {
        if (input.length != 1) {
            return new LangHelpers.UnlocalizedString(
                L10NValues.OPERATOR_ERROR_WRONGINPUTLENGTH,
                LangHelpers.localize("integratednbt:nbt_extraction_operator.full_name"),
                input.length,
                1);
        }
        IValueType<?> inputType = input[0];
        if (inputType == null) {
            return new LangHelpers.UnlocalizedString(
                L10NValues.OPERATOR_ERROR_NULLTYPE,
                LangHelpers.localize("integratednbt:nbt_extraction_operator.full_name"),
                "0");
        }
        if (!ValueHelpers.correspondsTo(ValueTypes.NBT, inputType)) {
            return new LangHelpers.UnlocalizedString(
                L10NValues.OPERATOR_ERROR_WRONGTYPE,
                LangHelpers.localize("integratednbt:nbt_extraction_operator.full_name"),
                LangHelpers.localize(inputType.getUnlocalizedName()),
                "1",
                LangHelpers.localize(ValueTypes.NBT.getUnlocalizedName()));
        }
        return null;
    }

    @Override
    public IConfigRenderPattern getRenderPattern() {
        return null;
    }

    @Override
    public IOperator materialize() {
        return this;
    }
}
