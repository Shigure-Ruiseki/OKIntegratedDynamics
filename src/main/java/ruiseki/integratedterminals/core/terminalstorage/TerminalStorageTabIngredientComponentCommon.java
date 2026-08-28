package ruiseki.integratedterminals.core.terminalstorage;

import java.util.List;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.core.evaluate.InventoryVariableEvaluator;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeOperator;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.inventory.container.slot.SlotVariable;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabCommon;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.persist.nbt.NBTClassType;

/**
 * A common storage terminal ingredient tab.
 *
 * @param <T> The instance type.
 * @param <M> The matching condition parameter.
 * @author rubensworks
 */
public class TerminalStorageTabIngredientComponentCommon<T, M>
    implements ITerminalStorageTabCommon, IVariableFacade.IValidator {

    private final ContainerTerminalStorageBase containerTerminalStorage;
    private final ResourceLocation name;
    protected final IngredientComponent<T, M> ingredientComponent;

    private final int errorsValueId;

    private SimpleInventory inventory = null;
    private boolean dirtyInv;
    private final List<InventoryVariableEvaluator<ValueTypeOperator.ValueOperator>> variableEvaluators = Lists
        .newArrayList();
    private final List<IVariable<ValueTypeOperator.ValueOperator>> variables = Lists.newArrayList();

    private int variableSlotNumberStart;
    private int variableSlotNumberEnd;

    public TerminalStorageTabIngredientComponentCommon(ContainerTerminalStorageBase<?> containerTerminalStorage,
        ResourceLocation name, IngredientComponent<T, M> ingredientComponent) {
        this.containerTerminalStorage = containerTerminalStorage;
        this.name = name;
        this.ingredientComponent = ingredientComponent;

        this.errorsValueId = containerTerminalStorage.getNextValueId();
    }

    @Override
    public ResourceLocation getName() {
        return this.name;
    }

    @Override
    public List<Pair<Slot, ISlotPositionCallback>> loadSlots(Container container, int startIndex, EntityPlayer player,
        Optional<IVariableInventory> variableInventoryOptional) {
        List<Pair<Slot, ITerminalStorageTabCommon.ISlotPositionCallback>> slots = Lists.newArrayList();

        variableSlotNumberStart = startIndex;
        inventory = new SimpleInventory(3, "inv", 1);

        if (variableInventoryOptional.isPresent()) {
            variableInventoryOptional.get()
                .loadNamedInventory(
                    this.getName()
                        .toString(),
                    inventory);
        }

        variableEvaluators.clear();
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            int slot = i;
            variableEvaluators.add(
                new InventoryVariableEvaluator<ValueTypeOperator.ValueOperator>(inventory, slot, ValueTypes.OPERATOR) {

                    @Override
                    public void onErrorsChanged() {
                        super.onErrorsChanged();
                        setLocalErrors(slot, getErrors());
                    }
                });
        }
        variableSlotNumberEnd = startIndex + inventory.getSizeInventory();

        inventory.addDirtyMarkListener(() -> dirtyInv = true);

        slots.add(
            Pair.of(
                new SlotVariable(inventory, 0, 0, 0),
                factors -> Pair.of(
                    factors.offsetX() + (factors.gridXSize() / 2) + 139,
                    factors.offsetY() + factors.gridYSize() + factors.playerInventoryOffsetY() + 63)));
        slots.add(
            Pair.of(
                new SlotVariable(inventory, 1, 0, 0),
                factors -> Pair.of(
                    factors.offsetX() + (factors.gridXSize() / 2) + 139,
                    factors.offsetY() + factors.gridYSize() + factors.playerInventoryOffsetY() + 81)));
        slots.add(
            Pair.of(
                new SlotVariable(inventory, 2, 0, 0),
                factors -> Pair.of(
                    factors.offsetX() + (factors.gridXSize() / 2) + 139,
                    factors.offsetY() + factors.gridYSize() + factors.playerInventoryOffsetY() + 99)));

        dirtyInv = true;

        return slots;
    }

    public int getVariableSlotNumberStart() {
        return variableSlotNumberStart;
    }

    public int getVariableSlotNumberEnd() {
        return variableSlotNumberEnd;
    }

    @Override
    public void onUpdate(Container container, EntityPlayer player, Optional<IVariableInventory> variableInventory) {
        if (this.dirtyInv && !player.worldObj.isRemote) {
            this.dirtyInv = false;

            ContainerTerminalStorageBase<?> containerTerminalStorage = (ContainerTerminalStorageBase<?>) container;

            if (variableInventory.isPresent()) {
                variableInventory.get()
                    .saveNamedInventory(
                        this.getName()
                            .toString(),
                        inventory);
            }

            // Update variable facades
            INetwork network = containerTerminalStorage.getNetwork()
                .orElse(null);

            clearGlobalErrors();
            this.variables.clear();
            if (network == null) {
                addError(new LangHelpers.UnlocalizedString(L10NValues.GENERAL_ERROR_NONETWORK));
            } else {
                for (int i = 0; i < inventory.getSizeInventory(); i++) {
                    InventoryVariableEvaluator<ValueTypeOperator.ValueOperator> evaluator = variableEvaluators.get(i);
                    evaluator.refreshVariable(network, false);
                    IVariable<ValueTypeOperator.ValueOperator> variable = evaluator.getVariable(network);
                    if (variable != null) {
                        // Refresh filter when variable is invalidated
                        variable.addInvalidationListener(() -> inventory.markDirty());
                        this.variables.add(variable);
                    }

                    containerTerminalStorage.onVariableContentsUpdated(network, variable);
                }
            }

            // Tell the container that our filter may have changed
            TerminalStorageTabIngredientComponentServer tabServer = (TerminalStorageTabIngredientComponentServer) containerTerminalStorage
                .getTabServer(getName().toString());
            if (tabServer != null) {
                tabServer.updateFilter(this.variables, this);
                tabServer.reApplyFilter();
            }
        }
    }

    @Override
    public void addError(LangHelpers.UnlocalizedString error) {
        List<LangHelpers.UnlocalizedString> errors = getGlobalErrors();
        errors.add(error);
        NBTTagCompound tag = this.containerTerminalStorage.getValue(this.errorsValueId);
        if (tag == null) {
            tag = new NBTTagCompound();
        } else {
            tag = (NBTTagCompound) tag.copy();
        }
        NBTClassType.writeNbt(List.class, getName().toString() + ":globalErrors", errors, tag);
        this.containerTerminalStorage.setValue(this.errorsValueId, tag);
    }

    public List<LangHelpers.UnlocalizedString> getGlobalErrors() {
        NBTTagCompound tag = this.containerTerminalStorage.getValue(this.errorsValueId);
        if (tag == null) {
            return Lists.newArrayList();
        } else {
            return NBTClassType.readNbt(List.class, getName().toString() + ":globalErrors", tag);
        }
    }

    public void clearGlobalErrors() {
        NBTTagCompound tag = this.containerTerminalStorage.getValue(this.errorsValueId);
        if (tag == null) {
            tag = new NBTTagCompound();
        } else {
            tag = (NBTTagCompound) tag.copy();
        }
        NBTClassType.writeNbt(List.class, getName().toString() + ":globalErrors", Lists.newArrayList(), tag);
        this.containerTerminalStorage.setValue(this.errorsValueId, tag);
    }

    public void setLocalErrors(int slot, List<LangHelpers.UnlocalizedString> errors) {
        NBTTagCompound tag = this.containerTerminalStorage.getValue(this.errorsValueId);
        if (tag == null) {
            tag = new NBTTagCompound();
        } else {
            tag = (NBTTagCompound) tag.copy();
        }
        NBTClassType.writeNbt(List.class, getName().toString() + ":localErrors" + slot, errors, tag);
        this.containerTerminalStorage.setValue(this.errorsValueId, tag);
    }

    public List<LangHelpers.UnlocalizedString> getLocalErrors(int slot) {
        NBTTagCompound tag = this.containerTerminalStorage.getValue(this.errorsValueId);
        if (tag == null) {
            return Lists.newArrayList();
        } else {
            return NBTClassType.readNbt(List.class, getName().toString() + ":localErrors" + slot, tag);
        }
    }

    public boolean hasErrors() {
        return !getGlobalErrors().isEmpty();
    }
}
