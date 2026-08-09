package ruiseki.integrateddynamics.core.logicprogrammer;

import java.util.List;
import java.util.Set;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.inventory.Container;

import com.google.common.collect.Sets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Setter;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.client.gui.subgui.ISubGuiBox;
import ruiseki.integrateddynamics.api.evaluate.operator.IOperator;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElementType;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammerBase;
import ruiseki.integrateddynamics.core.client.gui.IDropdownEntry;
import ruiseki.integrateddynamics.core.client.gui.IDropdownEntryListener;
import ruiseki.integrateddynamics.core.evaluate.operator.Operators;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeOperator;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import ruiseki.integrateddynamics.network.packet.LogicProgrammerValueTypeOperatorValueChangedPacket;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;

/**
 * Element for the operator value type.
 *
 * @author rubensworks
 */
public class ValueTypeOperatorLPElement extends ValueTypeLPElementBase implements IDropdownEntryListener {

    @Setter
    private IOperator selectedOperator = null;

    public ValueTypeOperatorLPElement() {
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
        return LogicProgrammerElementTypes.VALUETYPE;
    }

    @Override
    public LangHelpers.UnlocalizedString validate() {
        return selectedOperator != null ? null : getValueType().canDeserialize(getInnerGuiElement().getInputString());
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
    public IValue getValue() {
        return ValueTypeOperator.ValueOperator.of(selectedOperator);
    }

    @Override
    public void activate() {

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
    public ISubGuiBox createSubGui(int baseX, int baseY, int maxWidth, int maxHeight, GuiLogicProgrammerBase gui,
        ContainerLogicProgrammerBase container) {
        return new RenderPattern(this, baseX, baseY, maxWidth, maxHeight, gui, container);
    }

    public static class RenderPattern<S extends ISubGuiBox, G extends Gui, C extends Container>
        extends ValueTypeLPElementRenderPattern {

        private final ValueTypeOperatorLPElement element;

        public RenderPattern(ValueTypeOperatorLPElement element, int baseX, int baseY, int maxWidth, int maxHeight,
            GuiLogicProgrammerBase gui, ContainerLogicProgrammerBase container) {
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
