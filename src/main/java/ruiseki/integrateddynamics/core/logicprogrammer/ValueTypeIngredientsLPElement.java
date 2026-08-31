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
import ruiseki.commoncapabilities.api.ingredient.IMixedIngredients;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.MixedIngredients;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.client.gui.subgui.ISubGuiBox;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.ingredient.IIngredientComponentHandler;
import ruiseki.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElementType;
import ruiseki.integrateddynamics.api.logicprogrammer.IValueTypeLogicProgrammerElement;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammerBase;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeIngredients;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.ingredient.IngredientComponentHandlers;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import ruiseki.integrateddynamics.network.packet.LogicProgrammerSetElementInventory;
import ruiseki.integrateddynamics.network.packet.LogicProgrammerValueTypeIngredientsValueChangedPacket;
import ruiseki.okcore.client.gui.component.button.GuiButtonArrow;
import ruiseki.okcore.client.gui.component.button.GuiButtonText;
import ruiseki.okcore.client.gui.component.input.GuiArrowedListField;
import ruiseki.okcore.client.gui.component.input.IInputListener;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.helper.RenderHelpers;

/**
 * Element for the ingredients value type.
 *
 * @author rubensworks
 */
public class ValueTypeIngredientsLPElement extends ValueTypeLPElementBase {

    protected static final int OFFSET_X = 31;
    protected static final int OFFSET_Y = 21;

    private IngredientComponent currentType = IngredientComponent.ITEMSTACK;
    private Map<IngredientComponent, Integer> lengths = Maps.newHashMap();
    private Map<IngredientComponent, Map<Integer, IValueTypeLogicProgrammerElement>> subElements = Maps.newHashMap();
    private Map<IngredientComponent, Map<Integer, RenderPattern>> subElementGuis = Maps.newHashMap();
    private int activeElement = -1;
    @SideOnly(Side.CLIENT)
    private MasterSubGuiRenderPattern masterGui;

    private ValueObjectTypeIngredients.ValueIngredients serverValue = null;

    public ValueTypeIngredientsLPElement() {
        super(ValueTypes.OBJECT_INGREDIENTS);
    }

    public void setServerValue(ValueObjectTypeIngredients.ValueIngredients serverValue) {
        this.serverValue = serverValue;
    }

    @Override
    public ILogicProgrammerElementType getType() {
        return LogicProgrammerElementTypes.VALUETYPE;
    }

    @Override
    public IConfigRenderPattern getRenderPattern() {
        return IConfigRenderPattern.NONE_CANVAS;
    }

    @Override
    public boolean canWriteElementPre() {
        return true;
    }

    protected IMixedIngredients constructValues() {
        Map<IngredientComponent<?, ?>, List<?>> lists = Maps.newIdentityHashMap();
        for (IngredientComponent<?, ?> component : IngredientComponentHandlers.REGISTRY.getComponents()) {
            List values = Lists.newArrayListWithExpectedSize(lengths.get(component));
            subElements.get(component)
                .entrySet()
                .forEach(entry -> {
                    IIngredientComponentHandler componentHandler = IngredientComponentHandlers.REGISTRY
                        .getComponentHandler(component);
                    try {
                        values.add(
                            componentHandler.toInstance(
                                entry.getValue()
                                    .getValue()));
                    } catch (Exception e) {
                        values.add(
                            component.getMatcher()
                                .getEmptyInstance());
                    }
                });
            if (!values.isEmpty()) {
                lists.put(component, values);
            }
        }
        return new MixedIngredients(lists);
    }

    @Override
    public IValue getValue() {
        return MinecraftHelpers.isClientSide() ? ValueObjectTypeIngredients.ValueIngredients.of(constructValues())
            : serverValue;
    }

    @Override
    public void setValue(IValue value) {
        ValueObjectTypeIngredients.ValueIngredients valueIngredients = (ValueObjectTypeIngredients.ValueIngredients) value;
        if (!MinecraftHelpers.isClientSide()) {
            setServerValue(valueIngredients);
        }

        valueIngredients.getRawValue()
            .ifPresent(ingredients -> {
                // Select itemstack by default if it has instances
                if (ingredients.getComponents()
                    .contains(IngredientComponent.ITEMSTACK)) {
                    currentType = IngredientComponent.ITEMSTACK;
                } else {
                    currentType = null;
                }

                for (IngredientComponent<?, ?> ingredientComponent : ingredients.getComponents()) {
                    IIngredientComponentHandler handler = IngredientComponentHandlers.REGISTRY
                        .getComponentHandler(ingredientComponent);

                    // If no itemstacks in ingredient, select any other
                    if (currentType == null) {
                        currentType = ingredientComponent;
                    }

                    // Save length per ingredient component
                    lengths.put(
                        ingredientComponent,
                        ingredients.getInstances(ingredientComponent)
                            .size());

                    // Initialize LP elements for all instances of this ingredient component
                    Map<Integer, IValueTypeLogicProgrammerElement> entries = Maps.newHashMap();
                    List<?> instances = ingredients.getInstances(ingredientComponent);
                    for (int i = 0; i < instances.size(); i++) {
                        initializeElementFromInstanceValue(entries, handler, instances.get(i), i);
                    }
                    subElements.put(ingredientComponent, entries);
                }
            });
    }

    protected <VT extends IValueType<V>, V extends IValue, T, M> void initializeElementFromInstanceValue(
        Map<Integer, IValueTypeLogicProgrammerElement> entries, IIngredientComponentHandler<VT, V, T, M> handler,
        T instance, int instanceIndex) {
        IValue instanceValue = handler.toValue(instance);
        IValueTypeLogicProgrammerElement lpElement = instanceValue.getType()
            .createLogicProgrammerElement();
        lpElement.setValue(instanceValue);
        entries.put(instanceIndex, lpElement);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setValueInGui(ISubGuiBox subGui) {
        if (!subElements.get(currentType)
            .isEmpty()) {
            setActiveElement(0);
        }
    }

    @Override
    public void setValueInContainer(ContainerLogicProgrammerBase container) {
        if (!subElements.get(currentType)
            .isEmpty()) {
            IValueTypeLogicProgrammerElement subElement = setActiveElement(0);
            int x = RenderPattern.calculateX(
                ContainerLogicProgrammerBase.BASE_X,
                ContainerLogicProgrammerBase.MAX_WIDTH,
                subElement.getRenderPattern()) + ContainerLogicProgrammerBase.BASE_X - OFFSET_X;
            int y = RenderPattern.calculateY(
                ContainerLogicProgrammerBase.BASE_Y,
                ContainerLogicProgrammerBase.MAX_HEIGHT,
                subElement.getRenderPattern()) + ContainerLogicProgrammerBase.BASE_Y - OFFSET_Y;
            container.setElementInventory(subElement, x, y);
            subElement.setValueInContainer(container);
        }
    }

    public int getLength() {
        return lengths.get(currentType);
    }

    public void setLength(int length) {
        lengths.put(currentType, length);
        setActiveElement(getLength() - 1);
    }

    public void setCurrentType(IngredientComponent currentType) {
        this.currentType = currentType;
        setActiveElement(
            subElements.get(currentType)
                .size() - 1);
    }

    public IValueTypeLogicProgrammerElement setActiveElement(int index) {
        activeElement = index;
        IValueTypeLogicProgrammerElement subElement = null;
        if (index >= 0) {
            if (!subElements.get(currentType)
                .containsKey(index)) {
                subElements.get(currentType)
                    .put(
                        index,
                        subElement = IngredientComponentHandlers.REGISTRY.getComponentHandler(currentType)
                            .getValueType()
                            .createLogicProgrammerElement());
            } else {
                subElement = subElements.get(currentType)
                    .get(index);
            }
        }
        if (masterGui != null) {
            masterGui.setActiveElement(activeElement);
            masterGui.container.onDirty();
        }
        return subElement;

    }

    public void removeElement(int index) {
        Map<Integer, IValueTypeLogicProgrammerElement> oldSubElements = subElements.get(currentType);
        Map<Integer, RenderPattern> oldSubElementGuis = subElementGuis.get(currentType);
        subElements.put(currentType, Maps.newHashMap());
        subElementGuis.put(currentType, Maps.newHashMap());
        for (Map.Entry<Integer, IValueTypeLogicProgrammerElement> entry : oldSubElements.entrySet()) {
            int i = entry.getKey();
            if (i < index) {
                subElements.get(currentType)
                    .put(i, entry.getValue());
                subElementGuis.get(currentType)
                    .put(i, oldSubElementGuis.get(i));
            } else if (i > index) {
                subElements.get(currentType)
                    .put(i - 1, entry.getValue());
                subElementGuis.get(currentType)
                    .put(i - 1, oldSubElementGuis.get(i));
            }
        }
        setLength(getLength() - 1);
    }

    @Override
    public void activate() {
        for (IngredientComponent recipeComponent : IngredientComponentHandlers.REGISTRY.getComponents()) {
            subElements.put(recipeComponent, Maps.newHashMap());
            subElementGuis.put(recipeComponent, Maps.newHashMap());
            lengths.put(recipeComponent, 0);
        }
    }

    @Override
    public void deactivate() {

    }

    @Override
    public LangHelpers.UnlocalizedString validate() {
        if (!MinecraftHelpers.isClientSide()) {
            return serverValue == null ? new LangHelpers.UnlocalizedString() : null;
        }
        if (MinecraftHelpers.isClientSide()) {
            IntegratedDynamics._instance.getPacketHandler()
                .sendToServer(
                    new LogicProgrammerValueTypeIngredientsValueChangedPacket(
                        ValueObjectTypeIngredients.ValueIngredients.of(constructValues())));
        }
        for (Map<Integer, IValueTypeLogicProgrammerElement> componentValues : subElements.values()) {
            for (Map.Entry<Integer, IValueTypeLogicProgrammerElement> entry : componentValues.entrySet()) {
                LangHelpers.UnlocalizedString error = entry.getValue()
                    .validate();
                if (error != null) {
                    return new LangHelpers.UnlocalizedString(
                        L10NValues.VALUETYPE_ERROR_INVALIDLISTELEMENT,
                        entry.getKey(),
                        error);
                }
            }
        }
        return null;
    }

    @Override
    public boolean isItemValidForSlot(int slotId, ItemStack itemStack) {
        return (slotId == 0 && super.isItemValidForSlot(slotId, itemStack)) || (activeElement >= 0
            && subElements.get(currentType)
                .containsKey(activeElement)
            && subElements.get(currentType)
                .get(activeElement)
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
        extends RenderPattern<ValueTypeIngredientsLPElement, GuiLogicProgrammerBase, ContainerLogicProgrammerBase>
        implements IRenderPatternValueTypeTooltip {

        private final int baseX;
        private final int baseY;
        private final int maxWidth;
        private final int maxHeight;
        private final GuiLogicProgrammerBase gui;
        private final ContainerLogicProgrammerBase container;

        protected ListElementSubGui elementSubGui = null;
        protected int lastGuiLeft;
        protected int lastGuiTop;
        private boolean renderTooltip = true;

        public MasterSubGuiRenderPattern(ValueTypeIngredientsLPElement element, int baseX, int baseY, int maxWidth,
            int maxHeight, GuiLogicProgrammerBase gui, ContainerLogicProgrammerBase container) {
            super(element, baseX, baseY, maxWidth, maxHeight, gui, container);
            subGuiHolder.addSubGui(new SelectionSubGui(element, baseX, baseY, maxWidth, maxHeight, gui, container));
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
                (gui.getContainer()).setElementInventory(null, 0, 0);
            }
            if (index >= 0) {
                subGuiHolder.addSubGui(
                    elementSubGui = new ListElementSubGui(
                        element,
                        baseX,
                        baseY + (getHeight() / 4),
                        maxWidth,
                        maxHeight,
                        gui,
                        container));
                elementSubGui.initGui(lastGuiLeft, lastGuiTop);
            }
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
     * Selection panel for the type.
     */
    @SideOnly(Side.CLIENT)
    protected static class SelectionSubGui
        extends RenderPattern<ValueTypeIngredientsLPElement, GuiLogicProgrammerBase, ContainerLogicProgrammerBase>
        implements IInputListener {

        private GuiArrowedListField<IngredientComponent> valueTypeSelector = null;
        private GuiButton arrowAdd;

        public SelectionSubGui(ValueTypeIngredientsLPElement element, int baseX, int baseY, int maxWidth, int maxHeight,
            GuiLogicProgrammerBase gui, ContainerLogicProgrammerBase container) {
            super(element, baseX, baseY, maxWidth, maxHeight, gui, container);
        }

        @Override
        public int getHeight() {
            return super.getHeight() / 4;
        }

        protected static List<IngredientComponent> getValueTypes() {
            return Lists.newArrayList(IngredientComponentHandlers.REGISTRY.getComponents());
        }

        @Override
        public void initGui(int guiLeft, int guiTop) {
            super.initGui(guiLeft, guiTop);
            valueTypeSelector = new GuiArrowedListField<IngredientComponent>(
                0,
                Minecraft.getMinecraft().fontRenderer,
                getX() + guiLeft + getWidth() / 2 - 50,
                getY() + guiTop + 2,
                100,
                15,
                true,
                true,
                getValueTypes()) {

                @Override
                protected String activeElementToString(IngredientComponent element) {
                    return LangHelpers.localize(
                        element.getName()
                            .toString());
                }
            };
            valueTypeSelector.setListener(this);
            // onChanged();
            int x = guiLeft + getX();
            int y = guiTop + getY();
            buttonList
                .add(arrowAdd = new GuiButtonText(1, x + getWidth() - 13, y + getHeight() - 13, 12, 12, "+", true));
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
                element.setLength(element.getLength() + 1);
            }
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
        }

        @Override
        public void onChanged() {
            element.setCurrentType(valueTypeSelector.getActiveElement());
        }
    }

    /**
     * Panel for browsing through the list elements and updating them.
     */
    @SideOnly(Side.CLIENT)
    protected static class ListElementSubGui
        extends RenderPattern<ValueTypeIngredientsLPElement, GuiLogicProgrammerBase, ContainerLogicProgrammerBase> {

        private GuiButtonArrow arrowLeft;
        private GuiButtonArrow arrowRight;
        private GuiButton arrowRemove;

        private RenderPattern subGui;
        private IValueTypeLogicProgrammerElement subElement;

        public ListElementSubGui(ValueTypeIngredientsLPElement element, int baseX, int baseY, int maxWidth,
            int maxHeight, GuiLogicProgrammerBase gui, ContainerLogicProgrammerBase container) {
            super(element, baseX, baseY, maxWidth, maxHeight, gui, container);
            this.subGui = element.subElementGuis.get(element.currentType)
                .get(element.activeElement);
            this.subElement = element.subElements.get(element.currentType)
                .get(element.activeElement);
            if (subGui == null) {
                subGui = (RenderPattern) subElement
                    .createSubGui(baseX, baseY, maxWidth, maxHeight / 3 * 2, gui, container);
                element.subElementGuis.get(element.currentType)
                    .put(element.activeElement, subGui);
            }
            int x = getX() + baseX - OFFSET_X;
            int y = getY() + baseY - OFFSET_Y;
            gui.getContainer()
                .setElementInventory(subElement, x, y);
            subElement.setValueInGui(subGui);
            subGuiHolder.addSubGui(subGui);

            // Do the same thing server-side
            IntegratedDynamics._instance.getPacketHandler()
                .sendToServer(
                    new LogicProgrammerSetElementInventory(
                        IngredientComponentHandlers.REGISTRY.getComponentHandler(element.currentType)
                            .getValueType(),
                        x,
                        y));
        }

        @Override
        public int getHeight() {
            return (super.getHeight() / 4) * 3;
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
                    x + (getWidth() / 2) - (arrowLeft.width / 2),
                    y + getHeight() - 13,
                    12,
                    12,
                    "-",
                    true));
            arrowLeft.enabled = element.activeElement > 0;
            arrowRight.enabled = element.activeElement < element.getLength() - 1;
            arrowRemove.enabled = element.getLength() > 0;
            subElement.setValueInGui(subGui);
            subElement.setValueInContainer(subGui.container);
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
            int x = guiLeft + getX() + (getWidth() / 2);
            int y = guiTop + getY() + 4;
            RenderHelpers.drawScaledCenteredString(
                fontRenderer,
                String.valueOf(element.activeElement),
                x - 4,
                y + 2,
                10,
                Helpers.RGBToInt(20, 20, 20));
        }
    }

}
