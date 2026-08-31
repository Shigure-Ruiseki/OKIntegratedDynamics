package ruiseki.integrateddynamics.core.logicprogrammer;

import java.util.List;

import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Getter;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.client.gui.subgui.IGuiInputElementValueType;
import ruiseki.integrateddynamics.api.client.gui.subgui.ISubGuiBox;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.item.IValueTypeVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import ruiseki.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElementType;
import ruiseki.integrateddynamics.api.logicprogrammer.IValueTypeLogicProgrammerElement;
import ruiseki.integrateddynamics.block.BlockLogicProgrammerConfig;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammerBase;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeVariableFacade;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import ruiseki.integrateddynamics.item.ItemVariableConfig;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;

/**
 * Element for value type.
 *
 * @author rubensworks
 */
public abstract class ValueTypeLPElementBase
    implements IValueTypeLogicProgrammerElement<ISubGuiBox, GuiLogicProgrammerBase, ContainerLogicProgrammerBase> {

    @Getter
    private final IValueType<?> valueType;

    public ValueTypeLPElementBase(IValueType<?> valueType) {
        this.valueType = valueType;
    }

    @Nullable
    @Override
    public <G2 extends Gui, C2 extends Container> IGuiInputElementValueType<?, G2, C2> createInnerGuiElement() {
        return null;
    }

    @Nullable
    public IGuiInputElementValueType<?, GuiLogicProgrammerBase, ContainerLogicProgrammerBase> getInnerGuiElement() {
        return null;
    }

    @Override
    public void loadTooltip(List<String> lines) {
        getValueType().loadTooltip(lines, true, null);
    }

    @Override
    public ILogicProgrammerElementType getType() {
        return LogicProgrammerElementTypes.VALUETYPE;
    }

    @Override
    public String getMatchString() {
        return getLocalizedNameFull().toLowerCase();
    }

    @Override
    public boolean matchesInput(IValueType<?> valueType) {
        return false;
    }

    @Override
    public boolean matchesOutput(IValueType<?> valueType) {
        return ValueHelpers.correspondsTo(valueType, valueType);
    }

    @Override
    public String getLocalizedNameFull() {
        return LangHelpers.localize(valueType.getUnlocalizedName());
    }

    @Override
    public IConfigRenderPattern getRenderPattern() {
        return IConfigRenderPattern.NONE;
    }

    @Override
    public void onInputSlotUpdated(int slotId, ItemStack itemStack) {

    }

    @Override
    public boolean isFor(IVariableFacade variableFacade) {
        if (variableFacade instanceof IValueTypeVariableFacade) {
            IValueTypeVariableFacade valueTypeFacade = (IValueTypeVariableFacade) variableFacade;
            if (valueTypeFacade.isValid()) {
                return getValueType() == valueTypeFacade.getValueType();
            }
        }
        return false;
    }

    @Override
    public boolean canWriteElementPre() {
        return true;
    }

    @Override
    public ItemStack writeElement(EntityPlayer player, ItemStack itemStack) {
        IVariableFacadeHandlerRegistry registry = IntegratedDynamics._instance.getRegistryManager()
            .getRegistry(IVariableFacadeHandlerRegistry.class);
        return registry.writeVariableFacadeItem(
            !MinecraftHelpers.isClientSide(),
            itemStack,
            ValueTypes.REGISTRY,
            new ValueTypeVariableFacadeFactory(getValueType(), getValue()),
            player,
            BlockLogicProgrammerConfig._instance.getInstance());
    }

    @Override
    public void loadElement(IVariableFacade variableFacade) {
        if (variableFacade instanceof IValueTypeVariableFacade valueTypeVariableFacade) {
            setValue(valueTypeVariableFacade.getValue());
        }
    }

    @Override
    public boolean canCurrentlyReadFromOtherItem() {
        return true;
    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public int getColor() {
        return valueType.getDisplayColor();
    }

    @Override
    public String getSymbol() {
        return LangHelpers.localize(valueType.getUnlocalizedName());
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
    @SideOnly(Side.CLIENT)
    public boolean isFocused(ISubGuiBox subGui) {
        if (subGui instanceof ValueTypeStringLPElementRenderPattern) {
            return ((ValueTypeStringLPElementRenderPattern) subGui).getSearchField()
                .isFocused();
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setFocused(ISubGuiBox subGui, boolean focused) {
        if (subGui instanceof ValueTypeStringLPElementRenderPattern) {
            ((ValueTypeStringLPElementRenderPattern) subGui).getSearchField()
                .setFocused(focused);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public abstract ISubGuiBox createSubGui(int baseX, int baseY, int maxWidth, int maxHeight,
        GuiLogicProgrammerBase gui, ContainerLogicProgrammerBase container);

    @Override
    public void setValue(IValue value) {
        if (getInnerGuiElement() != null) {
            getInnerGuiElement().setValue(value);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setValueInGui(ISubGuiBox subGui) {
        if (getInnerGuiElement() != null) {
            ((IGuiInputElementValueType) getInnerGuiElement()).setValueInGui(subGui, true);
        }
    }

    @Override
    public void setValueInContainer(ContainerLogicProgrammerBase container) {

    }

    protected static class ValueTypeVariableFacadeFactory
        implements IVariableFacadeHandlerRegistry.IVariableFacadeFactory<IValueTypeVariableFacade> {

        private final IValueType valueType;
        private final IValue value;

        public ValueTypeVariableFacadeFactory(IValueType valueType, IValue value) {
            this.valueType = valueType;
            this.value = value;
        }

        public ValueTypeVariableFacadeFactory(IValue value) {
            this(value.getType(), value);
        }

        @Override
        public IValueTypeVariableFacade create(boolean generateId) {
            return new ValueTypeVariableFacade(generateId, valueType, value);
        }

        @Override
        public IValueTypeVariableFacade create(int id) {
            return new ValueTypeVariableFacade(id, valueType, value);
        }
    }

}
