package ruiseki.integrateddynamics.core.tileentity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.Getter;
import ruiseki.integrateddynamics.api.evaluate.IValueInterface;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkEventListener;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.network.event.INetworkEvent;
import ruiseki.integrateddynamics.capability.valueinterface.ValueInterfaceConfig;
import ruiseki.integrateddynamics.core.evaluate.InventoryVariableEvaluator;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.network.event.VariableContentsUpdatedEvent;
import ruiseki.okcore.capabilities.resolver.BasicCapabilityResolver;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.persist.IDirtyMarkListener;
import ruiseki.okcore.persist.nbt.NBTClassType;
import ruiseki.okcore.persist.nbt.NBTPersist;

/**
 * Base part entity that can hold variables.
 *
 * @param <E> The type of event listener
 * @author rubensworks
 */
public abstract class TileActiveVariableBase<E> extends TileCableConnectableInventory
    implements IDirtyMarkListener, INetworkEventListener<E> {

    private final InventoryVariableEvaluator<IValue> evaluator;

    protected IVariableFacade variableStored = null;
    @NBTPersist
    @Getter
    private List<LangHelpers.UnlocalizedString> errors = Lists.newLinkedList();

    public TileActiveVariableBase(int inventorySize, String inventoryName) {
        super(inventorySize, inventoryName, 1);
        inventory.addDirtyMarkListener(this);
        IValueInterface valueInterface = () -> {
            INetwork network = getNetwork();
            IPartNetwork partNetwork = NetworkHelpers.getPartNetworkChecked(network);
            if (hasVariable()) {
                IVariable<?> variable = getVariable(partNetwork);
                if (variable != null) {
                    return Optional.of(variable.getValue());
                }
            }
            return Optional.empty();
        };
        this.capabilityCache.addCapabilityResolver(
            BasicCapabilityResolver.create(ValueInterfaceConfig.CAPABILITY, () -> valueInterface));
        this.evaluator = createEvaluator();
    }

    protected InventoryVariableEvaluator<IValue> createEvaluator() {
        return new InventoryVariableEvaluator<>(this, getSlotRead(), ValueTypes.CATEGORY_ANY);
    }

    public InventoryVariableEvaluator getEvaluator() {
        return evaluator;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        List<LangHelpers.UnlocalizedString> errors = evaluator.getErrors();
        NBTClassType.writeNbt(List.class, "errors", errors, tag);
        super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        evaluator.setErrors(NBTClassType.readNbt(List.class, "errors", tag));
        super.readFromNBT(tag);
    }

    public abstract int getSlotRead();

    public boolean hasVariable() {
        return getStackInSlot(getSlotRead()) != null;
    }

    protected void updateReadVariable(boolean sendVariablesUpdateEvent) {
        evaluator.refreshVariable(getNetwork(), sendVariablesUpdateEvent);
        sendUpdate();
    }

    protected void preValidate(IVariableFacade variableStored) {

    }

    @Override
    public void onDirty() {
        if (!worldObj.isRemote) {
            updateReadVariable(true);
        }
    }

    @Nullable
    public IVariable<?> getVariable(IPartNetwork network) {
        return evaluator.getVariable(network);
    }

    @Override
    public boolean hasEventSubscriptions() {
        return true;
    }

    @Override
    public Set<Class<? extends INetworkEvent>> getSubscribedEvents() {
        return Sets.<Class<? extends INetworkEvent>>newHashSet(VariableContentsUpdatedEvent.class);
    }

    @Override
    public void onEvent(INetworkEvent event, E networkElement) {
        if (event instanceof VariableContentsUpdatedEvent) {
            updateReadVariable(false);
        }
    }

    @Override
    public void afterNetworkReAlive() {
        super.afterNetworkReAlive();
        updateReadVariable(true);
    }
}
