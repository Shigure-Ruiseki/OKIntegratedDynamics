package ruiseki.integrateddynamics.core.part;

import java.util.List;
import java.util.Map;

import net.minecraftforge.common.MinecraftForge;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.evaluate.InventoryVariableEvaluator;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeInteger;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.part.event.PartVariableDrivenVariableContentsUpdatedEvent;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.inventory.SimpleInventory;

/**
 * Handles dynamic offsets inside part states.
 *
 * @author rubensworks
 */
public class PartStateOffsetHandler<P extends IPartType> {

    public final List<InventoryVariableEvaluator<ValueTypeInteger.ValueInteger>> offsetVariableEvaluators = Lists
        .newArrayList();
    public final Int2ObjectMap<String> offsetVariablesSlotMessages = new Int2ObjectArrayMap<>();
    public boolean offsetVariablesDirty = true;
    public final IntSet offsetVariableSlotDirty = new IntArraySet();
    public final Map<IVariable, Boolean> offsetVariableListeners = new MapMaker().weakKeys()
        .makeMap();
    protected final Int2ObjectMap<IVariable> slotVariables = new Int2ObjectArrayMap<>();

    public void initializeVariableEvaluators(SimpleInventory offsetVariablesInventory) {
        offsetVariableEvaluators.clear();
        for (int i = 0; i < 3; i++) {
            int slot = i;
            offsetVariableEvaluators
                .add(new InventoryVariableEvaluator<>(offsetVariablesInventory, slot, ValueTypes.INTEGER) {

                    @Override
                    public void onErrorsChanged() {
                        super.onErrorsChanged();
                        setOffsetVariableErrors(slot, getErrors());
                    }
                });
        }
    }

    public void updateOffsetVariables(P partType, IPartState<P> partState, INetwork network, IPartNetwork partNetwork,
        PartTarget target) {
        // Reload offset variables if needed
        if (offsetVariablesDirty) {
            offsetVariablesDirty = false;
            reloadOffsetVariables(partType, partState, network, partNetwork, target);
        }

        // Only update single slots if needed
        if (!offsetVariableSlotDirty.isEmpty()) {
            IntArraySet offsetVariableSlotDirtyCopy = new IntArraySet(offsetVariableSlotDirty);
            offsetVariableSlotDirty.clear();
            for (Integer slot : offsetVariableSlotDirtyCopy) {
                this.reloadOffsetVariable(partType, partState, network, partNetwork, target, slot);
            }
        }
    }

    public void markOffsetVariablesChanged() {
        this.offsetVariablesDirty = true;
    }

    public SimpleInventory getOffsetVariablesInventory(IPartState<P> partState) {
        SimpleInventory offsetVariablesInventory = new SimpleInventory(3, "", 1);
        partState.loadInventoryNamed("offsetVariablesInventory", offsetVariablesInventory);
        return offsetVariablesInventory;
    }

    public void reloadOffsetVariables(P partType, IPartState<P> partState, INetwork network, IPartNetwork partNetwork,
        PartTarget target) {
        offsetVariableSlotDirty.clear();
        SimpleInventory offsetVariablesInventory = getOffsetVariablesInventory(partState);
        initializeVariableEvaluators(offsetVariablesInventory);
        for (int i = 0; i < offsetVariablesInventory.getSizeInventory(); i++) {
            reloadOffsetVariable(partType, partState, network, partNetwork, target, i);
        }
    }

    private void setOffsetVariableErrors(int slot, List<LangHelpers.UnlocalizedString> errors) {
        if (errors.isEmpty()) {
            if (this.offsetVariablesSlotMessages.size() > slot) {
                this.offsetVariablesSlotMessages.remove(slot);
            }
        } else {
            this.offsetVariablesSlotMessages.put(
                slot,
                errors.get(0)
                    .localize());
        }
    }

    @Nullable
    public String getOffsetVariableError(int slot) {
        return this.offsetVariablesSlotMessages.get(slot);
    }

    protected void reloadOffsetVariable(P partType, IPartState<P> partState, INetwork network, IPartNetwork partNetwork,
        PartTarget target, int slot) {
        if (this.offsetVariablesSlotMessages.size() > slot) {
            this.offsetVariablesSlotMessages.remove(slot);
        }
        IVariable lastVariable = slotVariables.get(slot);
        if (lastVariable != null) {
            lastVariable.invalidate();
        }

        InventoryVariableEvaluator<ValueTypeInteger.ValueInteger> evaluator = offsetVariableEvaluators.get(slot);
        evaluator.refreshVariable(network, false);
        IVariable<ValueTypeInteger.ValueInteger> variable = evaluator.getVariable(network);
        if (variable != null) {
            slotVariables.put(slot, variable);
            try {
                // Refresh the recipe if variable is changed
                // The map is needed because we only want to register the listener once for each variable
                if (!this.offsetVariableListeners.containsKey(variable)) {
                    variable.addInvalidationListener(() -> {
                        this.offsetVariableListeners.remove(variable);
                        this.offsetVariableSlotDirty.add(slot);
                    });
                    this.offsetVariableListeners.put(variable, true);
                }

                IValue value = variable.getValue();
                if (value.getType() == ValueTypes.INTEGER) {
                    int valueRaw = ((ValueTypeInteger.ValueInteger) value).getRawValue();
                    Vector3i offset = partState.getTargetOffset();
                    if (slot == 0) {
                        offset = new Vector3i(valueRaw, offset.y(), offset.z());
                    }
                    if (slot == 1) {
                        offset = new Vector3i(offset.x(), valueRaw, offset.z());
                    }
                    if (slot == 2) {
                        offset = new Vector3i(offset.x(), offset.y(), valueRaw);
                    }
                    boolean valid = partType.setTargetOffset(partState, target.getCenter(), offset);
                    if (!valid) {
                        this.offsetVariablesSlotMessages.put(
                            slot,
                            LangHelpers.localize("gui.integrateddynamics.partoffset.slot.message.outofrange"));
                        partState.markDirty();
                    }
                } else {
                    this.offsetVariablesSlotMessages
                        .put(slot, LangHelpers.localize("gui.integrateddynamics.partoffset.slot.message.noint"));
                    partState.markDirty();
                }
            } catch (EvaluationException e) {
                this.offsetVariablesSlotMessages.put(slot, e.getMessage());
                partState.markDirty();
            }
        } else if (evaluator.hasVariable()) {
            this.offsetVariableSlotDirty.add(slot);
        }

        try {
            MinecraftForge.EVENT_BUS.post(
                new PartVariableDrivenVariableContentsUpdatedEvent<>(
                    network,
                    partNetwork,
                    target,
                    partType,
                    partState,
                    null,
                    variable,
                    variable != null ? variable.getValue() : null));
        } catch (EvaluationException e) {
            // Ignore error
        }
    }

}
