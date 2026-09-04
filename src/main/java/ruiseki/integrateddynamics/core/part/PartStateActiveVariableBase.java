package ruiseki.integrateddynamics.core.part;

import java.util.List;
import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.block.IVariableContainer;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.capability.valueinterface.ValueInterfaceConfig;
import ruiseki.integrateddynamics.capability.variablecontainer.VariableContainerConfig;
import ruiseki.integrateddynamics.capability.variablecontainer.VariableContainerDefault;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.persist.nbt.NBTClassType;
import ruiseki.okcore.persist.nbt.NBTPersist;

/**
 * An abstract part state with a focus on activatable variables.
 *
 * @author rubensworks
 */
public abstract class PartStateActiveVariableBase<P extends IPartType> extends PartStateBase<P> {

    private boolean checkedForWriteVariable = false;
    protected IVariableFacade currentVariableFacade = null;
    private final IVariableContainer variableContainer;
    @Getter
    @Setter
    private boolean deactivated = false;
    private SimpleInventory inventory;
    @NBTPersist
    private List<LangHelpers.UnlocalizedString> globalErrorMessages = Lists.newLinkedList();
    @Getter
    @Setter
    private boolean retryEvaluation = false;

    public PartStateActiveVariableBase(int inventorySize) {
        this.inventory = new SingularInventory(inventorySize);
        this.inventory.addDirtyMarkListener(this); // No need to remove myself eventually. If I am removed, inv is also
                                                   // removed.
        variableContainer = new VariableContainerDefault();
        addVolatileCapability(VariableContainerConfig.CAPABILITY, LazyOptional.of(() -> variableContainer));
    }

    /**
     * @return The inner inventory
     */
    public SimpleInventory getInventory() {
        return this.inventory;
    }

    protected void validate(INetwork network, IPartNetwork partNetwork) {
        // Note that this is only called server-side, so these errors are sent via NBT to the client(s).
        this.currentVariableFacade.validate(
            network,
            partNetwork,
            new PartStateActiveVariableBase.Validator(this),
            currentVariableFacade.getOutputType());
    }

    protected void onCorruptedState() {
        IntegratedDynamics.clog(Level.ERROR, "A corrupted part state was found at, repairing...");
        Thread.dumpStack();
        this.checkedForWriteVariable = false;
        this.deactivated = true;
    }

    /**
     * @return If there is an active variable present for this state.
     */
    public boolean hasVariable() {
        return (getGlobalErrors().isEmpty() || isRetryEvaluation()) && !getInventory().isEmpty();
    }

    /**
     * Get the active variable in this state.
     *
     * @param <V>         The variable value type.
     * @param network     The network.
     * @param partNetwork The part network.
     * @return The variable.
     */
    public <V extends IValue> IVariable<V> getVariable(INetwork network, IPartNetwork partNetwork) {
        if (!checkedForWriteVariable) {
            if (variableContainer.getVariableCache()
                .isEmpty()) {
                variableContainer.refreshVariables(network, inventory, false);
            }
            for (IVariableFacade facade : variableContainer.getVariableCache()
                .values()) {
                if (facade != null) {
                    currentVariableFacade = facade;
                    validate(network, partNetwork);
                }
            }
            this.checkedForWriteVariable = true;
        }
        if (currentVariableFacade == null) {
            onCorruptedState();
            return null;
        }
        return currentVariableFacade.getVariable(network, partNetwork);
    }

    /**
     * Refresh the current variable to have its current info reset and updated.
     *
     * @param partType The corresponding part type.
     * @param target   The target of the part.
     */
    public void onVariableContentsUpdated(P partType, PartTarget target) {
        // Resets the errors for this aspect
        this.checkedForWriteVariable = false;
        addGlobalError(null);
        this.currentVariableFacade = null;
        // this.deactivated = false; // This *should* not be required anymore, re-activation is handled in
        // AspectWriteBase#update.

        // Refresh any contained variables
        PartPos center = target.getCenter();
        NetworkHelpers.getNetwork(
            center.getPos()
                .getWorld(),
            center.getPos()
                .getBlockPos(),
            center.getSide())
            .ifPresent(network -> variableContainer.refreshVariables(network, inventory, false));
    }

    /**
     * @return All global error messages.
     */
    public List<LangHelpers.UnlocalizedString> getGlobalErrors() {
        return globalErrorMessages;
    }

    /**
     * Add a global error message.
     *
     * @param error The message to add.
     */
    public void addGlobalError(LangHelpers.UnlocalizedString error) {
        setRetryEvaluation(false);
        if (error == null) {
            globalErrorMessages.clear();
        } else {
            globalErrorMessages.add(error);
        }
        onDirty();
        sendUpdate(); // We want this error messages to be sent to the client(s).
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        NBTClassType.writeNbt(List.class, "globalErrorMessages", globalErrorMessages, tag);
        inventory.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        // noinspection unchecked
        this.globalErrorMessages = NBTClassType.readNbt(List.class, "globalErrorMessages", tag);
        inventory.readFromNBT(tag);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> LazyOptional<T> getCapability(Capability<T> capability, INetwork network, IPartNetwork partNetwork,
        PartTarget target) {
        if (capability == ValueInterfaceConfig.CAPABILITY) {
            return LazyOptional.of(() -> {
                if (hasVariable()) {
                    IVariable<IValue> variable = getVariable(network, partNetwork);
                    if (variable != null) {
                        try {
                            return Optional.ofNullable(variable.getValue());
                        } catch (EvaluationException e) {
                            return Optional.empty();
                        }
                    }
                }
                return Optional.empty();
            })
                .cast();
        }
        return super.getCapability(capability, network, partNetwork, target);
    }

    /**
     * An inventory that can only hold one filled slot at a time.
     */
    public static class SingularInventory extends SimpleInventory {

        /**
         * Make a new instance.
         *
         * @param size The amount of slots in the inventory.
         */
        public SingularInventory(int size) {
            super(size, "stateInventory", 1);
        }

        protected boolean canInsert(int slot) {
            for (int i = 0; i < getSizeInventory(); i++) {
                // Only allow insertion if the target slot is the same as the non-empty slot
                if (i != slot && getStackInSlot(i) != null) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean isItemValidForSlot(int i, ItemStack itemstack) {
            return canInsert(i) && super.isItemValidForSlot(i, itemstack);
        }

    }

    public static class Validator implements IVariableFacade.IValidator {

        private final PartStateActiveVariableBase state;

        /**
         * Make a new instance
         *
         * @param state The part state.
         */
        public Validator(PartStateActiveVariableBase state) {
            this.state = state;
        }

        @Override
        public void addError(LangHelpers.UnlocalizedString error) {
            this.state.addGlobalError(error);
        }

    }

}
