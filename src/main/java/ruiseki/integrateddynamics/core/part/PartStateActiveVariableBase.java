package ruiseki.integrateddynamics.core.part;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.item.ItemVariable;
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
    @Getter
    @Setter
    private boolean deactivated = false;
    private SimpleInventory inventory;
    @NBTPersist
    private List<LangHelpers.UnlocalizedString> globalErrorMessages = Lists.newLinkedList();

    public PartStateActiveVariableBase(int inventorySize) {
        this.inventory = new SingularInventory(inventorySize);
        this.inventory.addDirtyMarkListener(this); // No need to remove myself eventually. If I am removed, inv is also
                                                   // removed.
    }

    /**
     * @return The inner inventory
     */
    public SimpleInventory getInventory() {
        return this.inventory;
    }

    protected void validate(IPartNetwork network) {
        // Note that this is only called server-side, so these errors are sent via NBT to the client(s).
        this.currentVariableFacade
            .validate(network, new PartStateActiveVariableBase.Validator(this), currentVariableFacade.getOutputType());
    }

    protected void onCorruptedState() {
        IntegratedDynamics.clog(Level.WARN, "A corrupted part state was found at, repairing...");
        this.checkedForWriteVariable = false;
        this.deactivated = true;
    }

    /**
     * @return If there is an active variable present for this state.
     */
    public boolean hasVariable() {
        return getGlobalErrors().isEmpty() && !getInventory().isEmpty();
    }

    /**
     * Get the active variable in this state.
     *
     * @param <V>     The variable value type.
     * @param network The network.
     * @return The variable.
     */
    public <V extends IValue> IVariable<V> getVariable(IPartNetwork network) {
        if (!checkedForWriteVariable) {
            for (int slot = 0; slot < getInventory().getSizeInventory(); slot++) {
                ItemStack itemStack = getInventory().getStackInSlot(slot);
                if (itemStack != null) {
                    this.currentVariableFacade = ItemVariable.getInstance()
                        .getVariableFacade(itemStack);
                    validate(network);
                }
            }
            this.checkedForWriteVariable = true;
        }
        if (currentVariableFacade == null) {
            onCorruptedState();
            return null;
        }
        return currentVariableFacade.getVariable(network);
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

        protected boolean canInsert() {
            for (int i = 0; i < getSizeInventory(); i++) {
                if (getStackInSlot(i) != null) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean isItemValidForSlot(int i, ItemStack itemstack) {
            return canInsert() && super.isItemValidForSlot(i, itemstack);
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
