package ruiseki.integrateddynamics.core.logicprogrammer;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.item.IValueTypeVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import ruiseki.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElementType;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammer;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammerBase;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeVariableFacade;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;

/**
 * Element for a value type that can be derived from an {@link ItemStack}.
 *
 * @author rubensworks
 */
public class ValueTypeItemStackElement<V extends IValue> extends ValueTypeElement {

    private final IItemStackToValue<V> itemStackToValue;
    private final ILogicProgrammerElementType type;
    private ItemStack itemStack;

    public ValueTypeItemStackElement(IValueType valueType, IItemStackToValue<V> itemStackToValue,
        ILogicProgrammerElementType type) {
        super(valueType);
        this.itemStackToValue = itemStackToValue;
        this.type = type;
    }

    @Override
    public ILogicProgrammerElementType getType() {
        return type;
    }

    @Override
    public IConfigRenderPattern getRenderPattern() {
        return IConfigRenderPattern.SINGLE_SLOT;
    }

    @Override
    public void onInputSlotUpdated(int slotId, ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public boolean canWriteElementPre() {
        return itemStack != null;
    }

    @Override
    public ItemStack writeElement(ItemStack itemStack) {
        IVariableFacadeHandlerRegistry registry = IntegratedDynamics._instance.getRegistryManager()
            .getRegistry(IVariableFacadeHandlerRegistry.class);
        return registry.writeVariableFacadeItem(
            !MinecraftHelpers.isClientSide(),
            itemStack,
            ValueTypes.REGISTRY,
            new ValueTypeVariableFacadeFactory(
                getInnerGuiElement().getValueType(),
                itemStackToValue.getValue(this.itemStack)));
    }

    @Override
    public void activate() {
        this.itemStack = null;
    }

    @Override
    public void deactivate() {

    }

    @Override
    public LangHelpers.UnlocalizedString validate() {
        if (this.itemStack == null) {
            return new LangHelpers.UnlocalizedString(L10NValues.VALUETYPE_ERROR_INVALIDINPUTITEM);
        }
        return itemStackToValue.validate(itemStack);
    }

    @Override
    public boolean isItemValidForSlot(int slotId, ItemStack itemStack) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public SubGuiConfigRenderPattern createSubGui(int baseX, int baseY, int maxWidth, int maxHeight,
        GuiLogicProgrammerBase gui, ContainerLogicProgrammerBase container) {
        return new SubGuiRenderPattern(this, baseX, baseY, maxWidth, maxHeight, gui, container);
    }

    @SideOnly(Side.CLIENT)
    protected static class SubGuiRenderPattern extends
        SubGuiConfigRenderPattern<ValueTypeItemStackElement, GuiLogicProgrammerBase, ContainerLogicProgrammerBase> {

        public SubGuiRenderPattern(ValueTypeItemStackElement element, int baseX, int baseY, int maxWidth, int maxHeight,
            GuiLogicProgrammerBase gui, ContainerLogicProgrammerBase container) {
            super(element, baseX, baseY, maxWidth, maxHeight, gui, container);
        }

        @Override
        public void drawGuiContainerForegroundLayer(int guiLeft, int guiTop, TextureManager textureManager,
            FontRenderer fontRenderer, int mouseX, int mouseY) {
            super.drawGuiContainerForegroundLayer(guiLeft, guiTop, textureManager, fontRenderer, mouseX, mouseY);
            IValueType valueType = element.getInnerGuiElement()
                .getValueType();

            // Output type tooltip
            if (!container.hasWriteItemInSlot()) {
                if (gui.func_146978_c(
                    ContainerLogicProgrammerBase.OUTPUT_X,
                    ContainerLogicProgrammerBase.OUTPUT_Y,
                    GuiLogicProgrammer.BOX_HEIGHT,
                    GuiLogicProgrammer.BOX_HEIGHT,
                    mouseX,
                    mouseY)) {
                    gui.drawTooltip(getValueTypeTooltip(valueType), mouseX - guiLeft, mouseY - guiTop);
                }
            }
        }

    }

    protected static class ValueTypeVariableFacadeFactory
        implements IVariableFacadeHandlerRegistry.IVariableFacadeFactory<IValueTypeVariableFacade> {

        private final IValueType valueType;
        private final IValue value;

        public ValueTypeVariableFacadeFactory(IValueType valueType, IValue value) {
            this.valueType = valueType;
            this.value = value;
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

    public static interface IItemStackToValue<V extends IValue> {

        public LangHelpers.UnlocalizedString validate(ItemStack itemStack);

        public V getValue(ItemStack itemStack);

    }

}
