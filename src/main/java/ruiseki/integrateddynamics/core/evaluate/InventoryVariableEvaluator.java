package ruiseki.integrateddynamics.core.evaluate;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.network.event.VariableContentsUpdatedEvent;
import ruiseki.okcore.helper.LangHelpers;

/**
 * A convenience holder class for getting variables from variable cards in a certain inventory slot.
 *
 * @param <V> The variable value type
 * @author rubensworks
 */
public class InventoryVariableEvaluator<V extends IValue> implements IVariableFacade.IValidator {

    private final IVariableFacadeHandlerRegistry handler = IntegratedDynamics._instance.getRegistryManager()
        .getRegistry(IVariableFacadeHandlerRegistry.class);
    private final IInventory inventory;
    private final int slot;
    private final IValueType containingValueType;

    private IVariableFacade variableStored = null;
    private List<LangHelpers.UnlocalizedString> errors = Lists.newLinkedList();

    public InventoryVariableEvaluator(IInventory inventory, int slot, IValueType<V> containingValueType) {
        this.inventory = inventory;
        this.slot = slot;
        this.containingValueType = containingValueType;
    }

    /**
     * @return If the configured slot has an item.
     */
    public boolean hasVariable() {
        return inventory.getStackInSlot(slot) != null;
    }

    /**
     * Refresh the variable reference by checking the inventory,
     * and validating the containing variable.
     *
     * @param network                  The network.
     * @param sendVariablesUpdateEvent If a {@link VariableContentsUpdatedEvent} event must be sent
     *                                 if the variable has changed.
     */
    public void refreshVariable(INetwork network, boolean sendVariablesUpdateEvent) {
        IPartNetwork partNetwork = NetworkHelpers.getPartNetwork(network)
            .getOrNull();

        int lastVariabledId = this.variableStored == null ? -1 : this.variableStored.getId();
        int variableId = -1;
        if (inventory.getStackInSlot(slot) != null && NetworkHelpers.shouldWork()) {
            // Update proxy input
            ItemStack itemStack = inventory.getStackInSlot(slot);
            this.variableStored = handler.handle(itemStack);
            if (this.variableStored != null) {
                variableId = this.variableStored.getId();
            }
        } else {
            this.variableStored = null;
        }

        if (partNetwork == null) {
            addError(new LangHelpers.UnlocalizedString(L10NValues.GENERAL_ERROR_NONETWORK));
        } else if (this.variableStored != null) {
            preValidate();
            try {
                variableStored.validate(partNetwork, this, containingValueType);
            } catch (IllegalArgumentException e) {
                addError(new LangHelpers.UnlocalizedString(e.getMessage()));
            }
        }
        if (sendVariablesUpdateEvent && partNetwork != null && lastVariabledId != variableId) {
            network.getEventBus()
                .post(new VariableContentsUpdatedEvent(network));
        }
    }

    @Nullable
    public IVariable<V> getVariable(INetwork network) {
        return getVariable(NetworkHelpers.getPartNetworkChecked(network));

    }

    @Nullable
    public IVariable<V> getVariable(IPartNetwork network) {
        if (getVariableFacade() == null || !getErrors().isEmpty()) return null;
        try {
            return getVariableFacade().getVariable(network);
        } catch (IllegalArgumentException e) {
            addError(new LangHelpers.UnlocalizedString(e.getMessage()));
            return null;
        }
    }

    public IVariableFacade getVariableFacade() {
        return variableStored;
    }

    protected void preValidate() {

    }

    public void clearErrors() {
        this.errors.clear();
        onErrorsChanged();
    }

    public void setErrors(List<LangHelpers.UnlocalizedString> errors) {
        this.errors = errors;
        onErrorsChanged();
    }

    public List<LangHelpers.UnlocalizedString> getErrors() {
        return errors;
    }

    @Override
    public void addError(LangHelpers.UnlocalizedString error) {
        errors.add(error);
        onErrorsChanged();
    }

    public void onErrorsChanged() {

    }
}
