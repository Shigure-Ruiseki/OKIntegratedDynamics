package ruiseki.integratednbt.evaluate.variable;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeNbt;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.item.ProxyVariableFacade;
import ruiseki.integrateddynamics.core.item.VariableFacadeBase;
import ruiseki.integratednbt.evaluate.nbt.NbtValueConverter;
import ruiseki.integratednbt.evaluate.nbt.path.SegmentedNbtPath;
import ruiseki.okcore.helper.LangHelpers;

public class NbtExtractedVariableFacade extends VariableFacadeBase {

    private int sourceNbtId;
    private SegmentedNbtPath extractionPath;
    private byte defaultNbtId;
    private boolean isValidatingVariable = false;
    private boolean isGettingVariable = false;

    public NbtExtractedVariableFacade(boolean generateId, int sourceNbtId, @Nullable SegmentedNbtPath extractionPath,
        byte defaultNbtId) {
        super(generateId);
        this.sourceNbtId = sourceNbtId;
        this.extractionPath = extractionPath;
        this.defaultNbtId = defaultNbtId;
    }

    public NbtExtractedVariableFacade(int id, int sourceNbtId, @Nullable SegmentedNbtPath extractionPath,
        byte defaultNbtId) {
        super(id);
        this.sourceNbtId = sourceNbtId;
        this.extractionPath = extractionPath;
        this.defaultNbtId = defaultNbtId;
    }

    public byte getDefaultNbtId() {
        return this.defaultNbtId;
    }

    public int getSourceNbtId() {
        return this.sourceNbtId;
    }

    public SegmentedNbtPath getExtractionPath() {
        return this.extractionPath;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(List<String> list, EntityPlayer entityPlayer) {
        if (!this.isValid()) {
            return;
        }
        list.add(LangHelpers.localize("integratednbt:nbt_extracted_variable.tooltip.source_nbt_id", this.sourceNbtId));
        list.add(
            LangHelpers
                .localize("integratednbt:nbt_extracted_variable.tooltip.path", this.extractionPath.getDisplayText()));
        list.add(
            LangHelpers.localize(
                "integratednbt:nbt_extracted_variable.tooltip.default_value",
                NbtValueConverter.getDefaultValueDisplayText(this.defaultNbtId)));
        super.addInformation(list, entityPlayer);
    }

    @Override
    public boolean isValid() {
        return this.extractionPath != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V extends IValue> IVariable<V> getVariable(INetwork network, IPartNetwork partNetwork) {
        if (isValid()) {
            // Check if we are entering an infinite recursion
            if (this.isGettingVariable) {
                throw new ProxyVariableFacade.VariableRecursionException(
                    "Detected infinite recursion for variable references.");
            }
            this.isGettingVariable = true;
            IVariableFacade sourceNbtVariableFacade = partNetwork.getVariableFacade(this.sourceNbtId);
            this.isGettingVariable = false;
            if (sourceNbtVariableFacade == null || !sourceNbtVariableFacade.isValid()
                || sourceNbtVariableFacade == this) {
                return null;
            }
            IVariable<ValueTypeNbt.ValueNbt> sourceNbtVariable = sourceNbtVariableFacade
                .getVariable(network, partNetwork);
            return (IVariable<V>) new NbtExtractedVariable(sourceNbtVariable, this.extractionPath, this.defaultNbtId);
        }
        return null;
    }

    @Override
    public void validate(INetwork network, IPartNetwork partNetwork, IValidator validator,
        IValueType containingValueType) {
        if (!this.isValid()) {
            return;
        }
        if (this.sourceNbtId < 0) {
            validator.addError(new LangHelpers.UnlocalizedString(L10NValues.VARIABLE_ERROR_INVALIDITEM));
        } else if (!partNetwork.hasVariableFacade(this.sourceNbtId)) {
            validator.addError(
                new LangHelpers.UnlocalizedString(
                    L10NValues.OPERATOR_ERROR_VARIABLENOTINNETWORK,
                    Integer.toString(this.sourceNbtId)));
        } else {
            IVariableFacade sourceVariableFacade = partNetwork.getVariableFacade(this.sourceNbtId);
            if (sourceVariableFacade == this) {
                validator.addError(
                    new LangHelpers.UnlocalizedString(
                        L10NValues.OPERATOR_ERROR_CYCLICREFERENCE,
                        Integer.toString(this.sourceNbtId)));
            } else if (sourceVariableFacade != null) {
                // Check if we are entering an infinite recursion
                if (this.isValidatingVariable) {
                    throw new ProxyVariableFacade.VariableRecursionException(
                        "Detected infinite recursion for variable references.");
                }
                this.isValidatingVariable = true;
                getVariable(network, partNetwork);
                this.isValidatingVariable = false;
            }
        }
    }

    @Override
    public IValueType<?> getOutputType() {
        return ValueTypes.CATEGORY_ANY;
    }
}
