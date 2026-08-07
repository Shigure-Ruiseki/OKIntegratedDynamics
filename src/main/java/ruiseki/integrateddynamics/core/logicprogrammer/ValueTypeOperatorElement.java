package ruiseki.integrateddynamics.core.logicprogrammer;

import java.util.List;
import java.util.Set;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Sets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Setter;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.client.gui.subgui.ISubGuiBox;
import ruiseki.integrateddynamics.api.evaluate.operator.IOperator;
import ruiseki.integrateddynamics.api.item.IValueTypeVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import ruiseki.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElementType;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammer;
import ruiseki.integrateddynamics.core.client.gui.IDropdownEntry;
import ruiseki.integrateddynamics.core.client.gui.IDropdownEntryListener;
import ruiseki.integrateddynamics.core.evaluate.operator.Operators;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeOperator;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeVariableFacade;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammer;
import ruiseki.integrateddynamics.network.packet.LogicProgrammerValueTypeOperatorValueChangedPacket;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;

/**
 * Element for the operator value type.
 *
 * @author rubensworks
 */
public class ValueTypeOperatorElement extends ValueTypeElement implements IDropdownEntryListener {

    @Setter
    private IOperator selectedOperator = null;

    public ValueTypeOperatorElement() {
        super(ValueTypes.OPERATOR);
        Set<IDropdownEntry<?>> operatorEntries = Sets.newLinkedHashSet();
        for (IOperator operator : Operators.REGISTRY.getOperators()) {
            operatorEntries.add(new OperatorDropdownEntry(operator));
        }
        getInnerGuiElement().setDropdownPossibilities(operatorEntries);
        getInnerGuiElement().setDropdownEntryListener(this);
        getInnerGuiElement().setDefaultInputString(""); // We don't want the default one to show up by default.
    }

    @Override
    public ILogicProgrammerElementType getType() {
        return LogicProgrammerElementTypes.OPERATOR_ELEMENT_TYPE;
    }

    @Override
    public LangHelpers.UnlocalizedString validate() {
        return selectedOperator != null ? null : super.validate();
    }

    @Override
    public IConfigRenderPattern getRenderPattern() {
        return IConfigRenderPattern.NONE_CANVAS;
    }

    @Override
    public boolean canWriteElementPre() {
        return selectedOperator != null;
    }

    @Override
    public void activate() {

    }

    @Override
    public ItemStack writeElement(ItemStack itemStack) {
        IVariableFacadeHandlerRegistry registry = IntegratedDynamics._instance.getRegistryManager()
            .getRegistry(IVariableFacadeHandlerRegistry.class);
        return registry.writeVariableFacadeItem(
            !MinecraftHelpers.isClientSide(),
            itemStack,
            ValueTypes.REGISTRY,
            new ValueTypeVariableFacadeFactory(ValueTypeOperator.ValueOperator.of(selectedOperator)));
    }

    @Override
    public void onSetDropdownPossiblity(IDropdownEntry<?> dropdownEntry) {
        OperatorDropdownEntry operatorDropdownEntry = (OperatorDropdownEntry) dropdownEntry;
        selectedOperator = operatorDropdownEntry == null ? null : operatorDropdownEntry.getValue();
        if (MinecraftHelpers.isClientSide()) {
            IntegratedDynamics._instance.getPacketHandler()
                .sendToServer(
                    new LogicProgrammerValueTypeOperatorValueChangedPacket(
                        ValueTypeOperator.ValueOperator.of(selectedOperator)));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public SubGuiConfigRenderPattern createSubGui(int baseX, int baseY, int maxWidth, int maxHeight,
        GuiLogicProgrammer gui, ContainerLogicProgrammer container) {
        return new SubGuiRenderPattern(this, baseX, baseY, maxWidth, maxHeight, gui, container);
    }

    public static class SubGuiRenderPattern<S extends ISubGuiBox, G extends Gui, C extends Container>
        extends ValueTypeElementSubGuiRenderPattern {

        private final ValueTypeOperatorElement element;

        public SubGuiRenderPattern(ValueTypeOperatorElement element, int baseX, int baseY, int maxWidth, int maxHeight,
            GuiLogicProgrammer gui, ContainerLogicProgrammer container) {
            super(element, baseX, baseY, maxWidth, maxHeight, gui, container);
            this.element = element;
        }

        @Override
        public void drawGuiContainerBackgroundLayer(int guiLeft, int guiTop, TextureManager textureManager,
            FontRenderer fontRenderer, float partialTicks, int mouseX, int mouseY) {
            super.drawGuiContainerBackgroundLayer(
                guiLeft,
                guiTop,
                textureManager,
                fontRenderer,
                partialTicks,
                mouseX,
                mouseY);
            IOperator operator = element.selectedOperator;
            if (operator != null) {
                int offsetY = 0;
                for (String line : ValueTypeOperator.getSignatureLines(operator, true)) {
                    fontRenderer.drawString(
                        line,
                        getX() + guiLeft + 10,
                        getY() + guiTop + 25 + offsetY,
                        Helpers.RGBToInt(10, 10, 10));
                    offsetY += fontRenderer.FONT_HEIGHT;
                }
            }
        }
    }

    protected static class ValueTypeVariableFacadeFactory
        implements IVariableFacadeHandlerRegistry.IVariableFacadeFactory<IValueTypeVariableFacade> {

        private final ValueTypeOperator.ValueOperator value;

        public ValueTypeVariableFacadeFactory(ValueTypeOperator.ValueOperator value) {
            this.value = value;
        }

        @Override
        public IValueTypeVariableFacade create(boolean generateId) {
            return new ValueTypeVariableFacade<>(generateId, ValueTypes.OPERATOR, value);
        }

        @Override
        public IValueTypeVariableFacade create(int id) {
            return new ValueTypeVariableFacade<>(id, ValueTypes.OPERATOR, value);
        }
    }

    public static class OperatorDropdownEntry implements IDropdownEntry<IOperator> {

        private final IOperator operator;

        public OperatorDropdownEntry(IOperator operator) {
            this.operator = operator;
        }

        @Override
        public String getMatchString() {
            return operator.getLocalizedNameFull();
        }

        @Override
        public String getDisplayString() {
            return getMatchString();
        }

        @Override
        public List<String> getTooltip() {
            return ValueTypeOperator.getSignatureLines(operator, true);
        }

        @Override
        public IOperator getValue() {
            return operator;
        }
    }

}
