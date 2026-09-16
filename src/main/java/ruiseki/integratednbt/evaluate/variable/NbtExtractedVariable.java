package ruiseki.integratednbt.evaluate.variable;

import net.minecraft.nbt.NBTBase;

import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.expression.VariableAdapter;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeNbt;
import ruiseki.integratednbt.evaluate.nbt.NbtValueConverter;
import ruiseki.integratednbt.evaluate.nbt.path.SegmentedNbtPath;

public class NbtExtractedVariable extends VariableAdapter<IValue> {

    private IVariable<ValueTypeNbt.ValueNbt> sourceNBTVariable;
    private SegmentedNbtPath extractionPath;
    private NBTBase cachedValue;
    private byte defaultNBTId;

    public NbtExtractedVariable(IVariable<ValueTypeNbt.ValueNbt> sourceNBTVariable, SegmentedNbtPath extractionPath,
        byte defaultNBTId) {
        this.sourceNBTVariable = sourceNBTVariable;
        this.extractionPath = extractionPath;
        this.defaultNBTId = defaultNBTId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IValueType<IValue> getType() {
        try {
            this.ensureCachedValue();
            if (this.cachedValue == null) {
                return NbtValueConverter.getDefaultValue(this.defaultNBTId)
                    .getType();
            }
            return (IValueType<IValue>) NbtValueConverter.mapNBTToValueType(this.cachedValue);
        } catch (EvaluationException ex) {
            return NbtValueConverter.getDefaultValue(this.defaultNBTId)
                .getType();
        }
    }

    private void ensureCachedValue() throws EvaluationException {
        if (this.cachedValue == null) {
            this.sourceNBTVariable.addInvalidationListener(this);
            this.cachedValue = this.extractionPath.extract(
                this.sourceNBTVariable.getValue()
                    .getRawValue()
                    .orElse(null));
        }
    }

    @Override
    public IValue getValue() throws EvaluationException {
        this.ensureCachedValue();
        if (this.cachedValue == null) {
            return NbtValueConverter.getDefaultValue(this.defaultNBTId);
        }
        return NbtValueConverter.mapNBTToValue(this.cachedValue);
    }

    @Override
    public void invalidate() {
        this.cachedValue = null;
        super.invalidate();
    }
}
