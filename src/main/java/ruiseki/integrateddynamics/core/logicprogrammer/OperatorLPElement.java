package ruiseki.integrateddynamics.core.logicprogrammer;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Iterables;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Data;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.evaluate.operator.IOperator;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeCategory;
import ruiseki.integrateddynamics.api.item.IOperatorVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandler;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import ruiseki.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElement;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElementType;
import ruiseki.integrateddynamics.block.BlockLogicProgrammerConfig;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammerBase;
import ruiseki.integrateddynamics.core.evaluate.operator.Operators;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeVariableFacade;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.item.OperatorVariableFacade;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import ruiseki.integrateddynamics.item.ItemVariableConfig;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.inventory.SimpleInventory;

/**
 * Element for operator.
 *
 * @author rubensworks
 */
@Data
public class OperatorLPElement
    implements ILogicProgrammerElement<RenderPattern, GuiLogicProgrammerBase, ContainerLogicProgrammerBase> {

    private final IOperator operator;
    private IVariableFacade[] inputVariables;
    private boolean focused = false;

    @Override
    public ILogicProgrammerElementType getType() {
        return LogicProgrammerElementTypes.OPERATOR;
    }

    @Override
    public String getMatchString() {
        return getOperator().getLocalizedNameFull()
            .toLowerCase();
    }

    @Override
    public boolean matchesInput(IValueType valueType) {
        for (IValueType operatorIn : getOperator().getInputTypes()) {
            if (ValueHelpers.correspondsTo(operatorIn, valueType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean matchesOutput(IValueType valueType) {
        return ValueHelpers.correspondsTo(getOperator().getOutputType(), valueType);
    }

    @Override
    public String getLocalizedNameFull() {
        return getOperator().getLocalizedNameFull();
    }

    @Override
    public void loadTooltip(List<String> lines) {
        getOperator().loadTooltip(lines, true);
    }

    @Override
    public IConfigRenderPattern getRenderPattern() {
        if (getOperator().getRenderPattern() == null) {
            throw new IllegalStateException(
                "Tried to render a (possibly virtual) operator with a null render pattern: "
                    + getOperator().getUniqueName());
        }
        return getOperator().getRenderPattern();
    }

    @Override
    public void onInputSlotUpdated(int slotId, ItemStack itemStack) {
        IVariableFacade variableFacade = IntegratedDynamics._instance.getRegistryManager()
            .getRegistry(IVariableFacadeHandlerRegistry.class)
            .handle(itemStack);
        inputVariables[slotId] = variableFacade;
    }

    @Override
    public boolean canWriteElementPre() {
        for (IVariableFacade inputVariable : inputVariables) {
            if (inputVariable == null || !inputVariable.isValid()) {
                return false;
            }
        }
        return true;
    }

    protected int[] getVariableIds(IVariableFacade[] inputVariables) {
        int[] variableIds = new int[inputVariables.length];
        for (int i = 0; i < inputVariables.length; i++) {
            variableIds[i] = inputVariables[i].getId();
        }
        return variableIds;
    }

    @Override
    public ItemStack writeElement(EntityPlayer player, ItemStack itemStack) {
        IVariableFacadeHandlerRegistry registry = IntegratedDynamics._instance.getRegistryManager()
            .getRegistry(IVariableFacadeHandlerRegistry.class);
        int[] variableIds = getVariableIds(inputVariables);
        return registry.writeVariableFacadeItem(
            !MinecraftHelpers.isClientSide(),
            itemStack,
            Operators.REGISTRY,
            new OperatorVariableFacadeFactory(operator, variableIds),
            player,
            BlockLogicProgrammerConfig._instance.getInstance());
    }

    @Override
    public void loadElement(IVariableFacade variableFacade) {
        if (variableFacade instanceof OperatorVariableFacade operatorVariableFacade) {
            int[] variableIds = operatorVariableFacade.getVariableIds();
            this.inputVariables = new IVariableFacade[variableIds.length];
            for (int i = 0; i < variableIds.length; i++) {
                IValueType valueType = operator.getInputTypes()[i];
                if (valueType instanceof IValueTypeCategory<?>valueTypeCategory) {
                    valueType = Iterables.getFirst(valueTypeCategory.getElements(), valueType);
                }
                this.inputVariables[i] = new ValueTypeVariableFacade<>(
                    variableIds[i],
                    valueType,
                    valueType.getDefault());
            }
        }
    }

    @Override
    public void setValueInGui(RenderPattern subGui) {
        setValueInContainer((ContainerLogicProgrammerBase) subGui.container);
    }

    @Override
    public void setValueInContainer(ContainerLogicProgrammerBase container) {
        SimpleInventory inputSlots = container.getTemporaryInputSlots();
        for (int i = 0; i < inputSlots.getSizeInventory(); i++) {
            ItemStack itemStack = IntegratedDynamics._instance.getRegistryManager()
                .getRegistry(IVariableFacadeHandlerRegistry.class)
                .writeVariableFacadeItem(
                    new ItemStack(ItemVariableConfig._instance.getInstance()),
                    inputVariables[i],
                    (IVariableFacadeHandler) ValueTypes.REGISTRY);
            inputSlots.setInventorySlotContents(i, itemStack);
        }
    }

    @Override
    public boolean canCurrentlyReadFromOtherItem() {
        return true;
    }

    @Override
    public void activate() {
        this.inputVariables = new IVariableFacade[getRenderPattern().getSlotPositions().length];
    }

    @Override
    public void deactivate() {
        this.inputVariables = null;
    }

    @Override
    public LangHelpers.UnlocalizedString validate() {
        return getOperator().validateTypes(ValueHelpers.from(inputVariables));
    }

    @Override
    public int getColor() {
        return getOperator().getOutputType()
            .getDisplayColor();
    }

    @Override
    public String getSymbol() {
        return getOperator().getSymbol();
    }

    @Override
    public boolean isFor(IVariableFacade variableFacade) {
        if (variableFacade instanceof IOperatorVariableFacade) {
            IOperatorVariableFacade operatorFacade = (IOperatorVariableFacade) variableFacade;
            if (operatorFacade.isValid()) {
                return getOperator() == operatorFacade.getOperator();
            }
        }
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int slotId, ItemStack itemStack) {
        return itemStack.getItem() == ItemVariableConfig._instance.getInstance();
    }

    @Override
    public boolean slotClick(int slotId, Slot slot, int mouseButton, int clickType, EntityPlayer player) {
        return false;
    }

    @Override
    public int getItemStackSizeLimit() {
        return 1;
    }

    @Override
    public boolean isFocused(RenderPattern subGui) {
        return focused;
    }

    @Override
    public void setFocused(RenderPattern subGui, boolean focused) {
        this.focused = focused;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderPattern createSubGui(int baseX, int baseY, int maxWidth, int maxHeight, GuiLogicProgrammerBase gui,
        ContainerLogicProgrammerBase container) {
        return new OperatorLPElementRenderPattern(this, baseX, baseY, maxWidth, maxHeight, gui, container);
    }

    protected static class OperatorVariableFacadeFactory
        implements IVariableFacadeHandlerRegistry.IVariableFacadeFactory<IOperatorVariableFacade> {

        private final IOperator operator;
        private final int[] variableIds;

        public OperatorVariableFacadeFactory(IOperator operator, int[] variableIds) {
            this.operator = operator;
            this.variableIds = variableIds;
        }

        @Override
        public IOperatorVariableFacade create(boolean generateId) {
            return new OperatorVariableFacade(generateId, operator, variableIds);
        }

        @Override
        public IOperatorVariableFacade create(int id) {
            return new OperatorVariableFacade(id, operator, variableIds);
        }
    }

}
