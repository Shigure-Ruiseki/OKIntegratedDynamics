package ruiseki.integrateddynamics.core.logicprogrammer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.client.gui.subgui.ISubGuiBox;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeListProxy;
import ruiseki.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElementType;
import ruiseki.integrateddynamics.api.logicprogrammer.IValueTypeLogicProgrammerElement;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammerBase;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeList;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import ruiseki.integrateddynamics.network.packet.LogicProgrammerSetElementInventory;
import ruiseki.integrateddynamics.network.packet.LogicProgrammerValueTypeListValueChangedPacket;
import ruiseki.okcore.client.gui.component.button.GuiButtonArrow;
import ruiseki.okcore.client.gui.component.button.GuiButtonText;
import ruiseki.okcore.client.gui.component.input.GuiArrowedListField;
import ruiseki.okcore.client.gui.component.input.IInputListener;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.helper.RenderHelpers;

/**
 * Element for the list value type.
 *
 * @author rubensworks
 */
public class ValueTypeListLPElement extends ValueTypeLPElementBase {

    private IValueType listValueType;
    private Map<Integer, IValueTypeLogicProgrammerElement> subElements;
    private Map<Integer, RenderPattern> subElementGuis;
    private int length = 0;
    private int activeElement = -1;
    @SideOnly(Side.CLIENT)
    private MasterSubGuiRenderPattern masterGui;

    private ValueTypeList.ValueList serverValue = null;

    public ValueTypeListLPElement() {
        super(ValueTypes.LIST);
    }

    public void setServerValue(ValueTypeList.ValueList serverValue) {
        this.serverValue = serverValue;
    }

    @Override
    public ILogicProgrammerElementType getType() {
        return LogicProgrammerElementTypes.VALUETYPE;
    }

    @Override
    public IConfigRenderPattern getRenderPattern() {
        return IConfigRenderPattern.NONE_CANVAS_WIDE;
    }

    @Override
    public boolean canWriteElementPre() {
        return MinecraftHelpers.isClientSide() ? listValueType != null : serverValue != null;
    }

    protected List<IValue> constructValues() {
        List<IValue> valueList = Lists.newArrayListWithExpectedSize(this.length);
        for (Map.Entry<Integer, IValueTypeLogicProgrammerElement> value : this.subElements.entrySet()) {
            if (value.getValue()
                .validate() == null) {
                valueList.add(
                    value.getKey(),
                    value.getValue()
                        .getValue());
            } else {
                valueList.add(value.getKey(), listValueType.getDefault());
            }
        }
        return valueList;
    }

    @Override
    public IValue getValue() {
        return MinecraftHelpers.isClientSide() ? ValueTypeList.ValueList.ofList(listValueType, constructValues())
            : serverValue;
    }

    @Override
    public void setValue(IValue value) {
        if (!MinecraftHelpers.isClientSide()) {
            serverValue = (ValueTypeList.ValueList) value;
        }

        IValueTypeListProxy list = ((ValueTypeList.ValueList) value).getRawValue();
        if (!list.isInfinite()) {
            setListValueType(list.getValueType());
            try {
                int length = list.getLength();
                this.length = length;
                for (int i = 0; i < length; i++) {
                    initializeElement(i).setValue(list.get(i));
                }
            } catch (EvaluationException e) {
                // Ignore exceptions
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setValueInGui(ISubGuiBox subGui) {
        if (length > 0) {
            masterGui.setSelectedValueType(this.listValueType);
            setActiveElement(0);
        }
    }

    @Override
    public void setValueInContainer(ContainerLogicProgrammerBase container) {
        if (length > 0) {
            IValueTypeLogicProgrammerElement subElement = setActiveElement(0);
            int x = RenderPatternCommon.calculateX(
                ContainerLogicProgrammerBase.BASE_X,
                ContainerLogicProgrammerBase.MAX_WIDTH,
                subElement.getRenderPattern());
            int y = RenderPatternCommon.calculateY(
                ContainerLogicProgrammerBase.BASE_Y,
                ContainerLogicProgrammerBase.MAX_HEIGHT,
                subElement.getRenderPattern());
            container.setElementInventory(subElement, x, y);
            container.getTemporaryInputSlots()
                .removeDirtyMarkListener(container);
            subElement.setValueInContainer(container);
            container.getTemporaryInputSlots()
                .addDirtyMarkListener(container);
        }
    }

    public void setListValueType(IValueType listValueType) {
        this.listValueType = listValueType;
        reset();
    }

    public void reset() {
        this.subElements = Maps.newHashMap();
        this.subElementGuis = Maps.newHashMap();
        setLength(0);
    }

    public void setLength(int length) {
        this.length = length;
        setActiveElement(length - 1);
    }

    public IValueTypeLogicProgrammerElement initializeElement(int index) {
        IValueTypeLogicProgrammerElement subElement = listValueType.createLogicProgrammerElement();
        subElement.activate();
        subElements.put(index, subElement);
        return subElement;
    }

    public IValueTypeLogicProgrammerElement setActiveElement(int index) {
        activeElement = index;
        IValueTypeLogicProgrammerElement subElement;
        if (index >= 0 && !subElements.containsKey(index)) {
            subElement = initializeElement(index);
            subElement.activate();
        } else {
            subElement = subElements.get(index);
        }
        if (MinecraftHelpers.isClientSide()) {
            masterGui.setActiveElement(activeElement);
            masterGui.container.onDirty();
        }
        return subElement;
    }

    public void removeElement(int index) {
        Map<Integer, IValueTypeLogicProgrammerElement> oldSubElements = subElements;
        Map<Integer, RenderPattern> oldSubElementGuis = subElementGuis;
        subElements = Maps.newHashMap();
        subElementGuis = Maps.newHashMap();
        for (Map.Entry<Integer, IValueTypeLogicProgrammerElement> entry : oldSubElements.entrySet()) {
            int i = entry.getKey();
            if (i < index) {
                subElements.put(i, entry.getValue());
                subElementGuis.put(i, oldSubElementGuis.get(i));
            } else if (i > index) {
                subElements.put(i - 1, entry.getValue());
                subElementGuis.put(i - 1, oldSubElementGuis.get(i));
            }
        }
        setLength(length - 1);
    }

    @Override
    public void activate() {
        reset();
    }

    @Override
    public void deactivate() {
        this.activeElement = -1;
    }

    @Override
    public LangHelpers.UnlocalizedString validate() {
        if (!MinecraftHelpers.isClientSide()) {
            return serverValue == null ? new LangHelpers.UnlocalizedString() : null;
        }
        if (MinecraftHelpers.isClientSide()) {
            IntegratedDynamics._instance.getPacketHandler()
                .sendToServer(
                    new LogicProgrammerValueTypeListValueChangedPacket(
                        listValueType == null ? ValueTypes.LIST.getDefault()
                            : ValueTypeList.ValueList.ofList(listValueType, constructValues())));
        }
        if (this.listValueType == null) {
            return new LangHelpers.UnlocalizedString(L10NValues.VALUETYPE_ERROR_INVALIDINPUTITEM);
        }
        for (Map.Entry<Integer, IValueTypeLogicProgrammerElement> entry : subElements.entrySet()) {
            LangHelpers.UnlocalizedString error = entry.getValue()
                .validate();
            if (error != null) {
                return new LangHelpers.UnlocalizedString(
                    L10NValues.VALUETYPE_ERROR_INVALIDLISTELEMENT,
                    entry.getKey(),
                    error);
            }
        }
        return null;
    }

    @Override
    public boolean isItemValidForSlot(int slotId, ItemStack itemStack) {
        return (slotId == 0 && super.isItemValidForSlot(slotId, itemStack))
            || (activeElement >= 0 && subElements.containsKey(activeElement)
                && subElements.get(activeElement)
                    .isItemValidForSlot(slotId, itemStack));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ISubGuiBox createSubGui(int baseX, int baseY, int maxWidth, int maxHeight, GuiLogicProgrammerBase gui,
        ContainerLogicProgrammerBase container) {
        return masterGui = new MasterSubGuiRenderPattern(this, baseX, baseY, maxWidth, maxHeight, gui, container);
    }

    /**
     * Sub gui that holds the list element value type panel and the panel for browsing through the elements.
     */
    @SideOnly(Side.CLIENT)
    protected static class MasterSubGuiRenderPattern
        extends RenderPattern<ValueTypeListLPElement, GuiLogicProgrammerBase, ContainerLogicProgrammerBase>
        implements IRenderPatternValueTypeTooltip {

        private final int baseX;
        private final int baseY;
        private final int maxWidth;
        private final int maxHeight;
        private final GuiLogicProgrammerBase gui;
        private final ContainerLogicProgrammerBase container;
        private final SelectionSubGui selectionGui;

        protected ListElementSubGui elementSubGui = null;
        protected int lastGuiLeft;
        protected int lastGuiTop;
        private boolean renderTooltip = true;

        public MasterSubGuiRenderPattern(ValueTypeListLPElement element, int baseX, int baseY, int maxWidth,
            int maxHeight, GuiLogicProgrammerBase gui, ContainerLogicProgrammerBase container) {
            super(element, baseX, baseY, maxWidth, maxHeight, gui, container);
            subGuiHolder.addSubGui(
                this.selectionGui = new SelectionSubGui(
                    element,
                    baseX,
                    baseY - getHeight() / 4,
                    maxWidth,
                    maxHeight,
                    gui,
                    container));
            this.baseX = baseX;
            this.baseY = baseY;
            this.maxWidth = maxWidth;
            this.maxHeight = maxHeight;
            this.gui = gui;
            this.container = container;
        }

        public void setActiveElement(int index) {
            if (elementSubGui != null) {
                subGuiHolder.removeSubGui(elementSubGui);
            }
            if (index >= 0) {
                subGuiHolder.addSubGui(
                    elementSubGui = new ListElementSubGui(element, baseX, baseY, maxWidth, maxHeight, gui, container));
                elementSubGui.initGui(lastGuiLeft, lastGuiTop);
            }
        }

        public void setSelectedValueType(IValueType valueType) {
            this.selectionGui.setActiveElement(valueType);
        }

        @Override
        public void initGui(int guiLeft, int guiTop) {
            super.initGui(guiLeft, guiTop);
            lastGuiLeft = guiLeft;
            lastGuiTop = guiTop;
        }

        @Override
        public void drawGuiContainerForegroundLayer(int guiLeft, int guiTop, TextureManager textureManager,
            FontRenderer fontRenderer, int mouseX, int mouseY) {
            super.drawGuiContainerForegroundLayer(guiLeft, guiTop, textureManager, fontRenderer, mouseX, mouseY);

            // Output type tooltip
            this.drawTooltipForeground(gui, container, guiLeft, guiTop, mouseX, mouseY, element.getValueType());
        }

        @Override
        public boolean isRenderTooltip() {
            return this.renderTooltip;
        }

        @Override
        public void setRenderTooltip(boolean renderTooltip) {
            this.renderTooltip = renderTooltip;
        }
    }

    /**
     * Selection panel for the list element value type.
     */
    @SideOnly(Side.CLIENT)
    protected static class SelectionSubGui
        extends RenderPattern<ValueTypeListLPElement, GuiLogicProgrammerBase, ContainerLogicProgrammerBase>
        implements IInputListener {

        private GuiArrowedListField<IValueType> valueTypeSelector = null;
        private GuiButton arrowAdd;

        public SelectionSubGui(ValueTypeListLPElement element, int baseX, int baseY, int maxWidth, int maxHeight,
            GuiLogicProgrammerBase gui, ContainerLogicProgrammerBase container) {
            super(element, baseX, baseY, maxWidth, maxHeight, gui, container);
        }

        @Override
        public int getHeight() {
            return super.getHeight() / 4;
        }

        protected static List<IValueType> getValueTypes() {
            List<IValueType> valueTypes = Lists.newArrayList(LogicProgrammerElementTypes.VALUETYPE.getValueTypes());
            valueTypes.remove(ValueTypes.LIST);
            valueTypes.add(ValueTypes.CATEGORY_ANY);
            return valueTypes;
        }

        @Override
        public void initGui(int guiLeft, int guiTop) {
            super.initGui(guiLeft, guiTop);
            valueTypeSelector = new GuiArrowedListField<>(
                0,
                Minecraft.getMinecraft().fontRenderer,
                getX() + guiLeft + getWidth() / 2 - 50,
                getY() + guiTop + 9,
                100,
                15,
                true,
                true,
                getValueTypes());
            valueTypeSelector.setListener(this);
            if (element.activeElement == -1) {
                onChanged();
            }
            int x = guiLeft + getX();
            int y = guiTop + getY();
            buttonList.add(arrowAdd = new GuiButtonText(1, x + getWidth() - 13, y + 10, 12, 12, "+", true));
        }

        @Override
        public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            valueTypeSelector.mouseClicked(mouseX, mouseY, mouseButton);
        }

        @Override
        protected void actionPerformed(GuiButton guibutton) {
            super.actionPerformed(guibutton);
            if (guibutton == arrowAdd) {
                element.setLength(element.length + 1);
            }
        }

        @Override
        protected boolean isDrawBackground() {
            return false;
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

            valueTypeSelector.drawTextBox(Minecraft.getMinecraft(), mouseX, mouseY);

            if (element.activeElement >= 0) {
                int x = guiLeft + getX() + 10;
                int y = guiTop + getY() + 4;
                RenderHelpers.drawScaledCenteredString(
                    fontRenderer,
                    String.valueOf(element.activeElement),
                    x - 6,
                    y + 12,
                    10,
                    Helpers.RGBToInt(20, 20, 20));
            }
        }

        @Override
        public void onChanged() {
            IValueType newType = valueTypeSelector.getActiveElement();
            element.setListValueType(newType);
            if (arrowAdd != null) {
                arrowAdd.enabled = newType != ValueTypes.CATEGORY_ANY;
            }
        }

        public void setActiveElement(IValueType valueType) {
            valueTypeSelector.setListener(null);
            valueTypeSelector.setActiveElement(valueType);
            valueTypeSelector.setListener(this);
        }
    }

    /**
     * Panel for browsing through the list elements and updating them.
     */
    @SideOnly(Side.CLIENT)
    protected static class ListElementSubGui
        extends RenderPattern<ValueTypeListLPElement, GuiLogicProgrammerBase, ContainerLogicProgrammerBase> {

        private GuiButtonArrow arrowLeft;
        private GuiButtonArrow arrowRight;
        private GuiButton arrowRemove;

        private RenderPattern subGui;
        private IValueTypeLogicProgrammerElement subElement;

        public ListElementSubGui(ValueTypeListLPElement element, int baseX, int baseY, int maxWidth, int maxHeight,
            GuiLogicProgrammerBase gui, ContainerLogicProgrammerBase container) {
            super(element, baseX, baseY, maxWidth, maxHeight, gui, container);
            this.subGui = element.subElementGuis.get(element.activeElement);
            this.subElement = element.subElements.get(element.activeElement);
            if (subGui == null) {
                subGui = (RenderPattern) subElement.createSubGui(baseX, baseY, maxWidth, maxHeight, gui, container);
                element.subElementGuis.put(element.activeElement, subGui);
            }
            int x = RenderPatternCommon.calculateX(baseX, maxWidth, subElement.getRenderPattern());
            int y = RenderPatternCommon.calculateY(baseY, maxHeight, subElement.getRenderPattern());
            gui.getContainer()
                .setElementInventory(subElement, x, y);
            subElement.setValueInGui(subGui);
            subGuiHolder.addSubGui(subGui);
            if (subGui instanceof IRenderPatternValueTypeTooltip) {
                ((IRenderPatternValueTypeTooltip) subGui).setRenderTooltip(false);
            }

            // Do the same thing server-side
            IntegratedDynamics._instance.getPacketHandler()
                .sendToServer(new LogicProgrammerSetElementInventory(element.listValueType, x, y));
        }

        @Override
        public void initGui(int guiLeft, int guiTop) {
            super.initGui(guiLeft, guiTop);
            int x = guiLeft + getX();
            int y = guiTop + getY();
            buttonList.add(arrowLeft = new GuiButtonArrow(1, x, y, GuiButtonArrow.Direction.WEST));
            buttonList.add(
                arrowRight = new GuiButtonArrow(
                    1,
                    x + getWidth() - arrowLeft.width - 1,
                    y,
                    GuiButtonArrow.Direction.EAST));
            buttonList.add(
                arrowRemove = new GuiButtonText(
                    2,
                    x + getWidth() - arrowLeft.width - 1,
                    y + getHeight() - 13,
                    10,
                    12,
                    "-",
                    true));
            arrowLeft.enabled = element.activeElement > 0;
            arrowRight.enabled = element.activeElement < element.length - 1;
            arrowRemove.enabled = element.length > 0;
            container.getTemporaryInputSlots()
                .removeDirtyMarkListener(container);
            subElement.setValueInGui(subGui);
            subElement.setValueInContainer(subGui.container);
            container.getTemporaryInputSlots()
                .addDirtyMarkListener(container);
        }

        @Override
        protected void actionPerformed(GuiButton guibutton) {
            super.actionPerformed(guibutton);
            if (guibutton == arrowLeft) {
                element.setActiveElement(element.activeElement - 1);
            } else if (guibutton == arrowRight) {
                element.setActiveElement(element.activeElement + 1);
            } else if (guibutton == arrowRemove) {
                element.removeElement(element.activeElement);
            }
        }
    }

}
