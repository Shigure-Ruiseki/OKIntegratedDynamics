package ruiseki.integrateddynamics.core.logicprogrammer;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.client.gui.subgui.ISubGuiBox;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElementType;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammerBase;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Element for a value type that can be derived from an {@link ItemStack}.
 *
 * @author rubensworks
 */
public class ValueTypeItemStackLPElement<V extends IValue> extends ValueTypeLPElementBase {

    private final IItemStackToValue<V> itemStackToValue;
    private ItemStack itemStack;

    public ValueTypeItemStackLPElement(IValueType valueType, IItemStackToValue<V> itemStackToValue) {
        super(valueType);
        this.itemStackToValue = itemStackToValue;
    }

    @Override
    public ILogicProgrammerElementType getType() {
        return LogicProgrammerElementTypes.VALUETYPE;
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
        return this.itemStackToValue.isNullable() || this.itemStack != null;
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
        if (!this.itemStackToValue.isNullable() && this.itemStack == null) {
            return new LangHelpers.UnlocalizedString(L10NValues.VALUETYPE_ERROR_INVALIDINPUTITEM);
        }
        return itemStackToValue.validate(itemStack);
    }

    @Override
    public boolean isItemValidForSlot(int slotId, ItemStack itemStack) {
        return true;
    }

    @Override
    public IValue getValue() {
        return this.itemStackToValue.getValue(this.itemStack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ISubGuiBox createSubGui(int baseX, int baseY, int maxWidth, int maxHeight, GuiLogicProgrammerBase gui,
        ContainerLogicProgrammerBase container) {
        return new SubGuiRenderPattern(this, baseX, baseY, maxWidth, maxHeight, gui, container);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setValueInGui(ISubGuiBox subGui) {
        ((ValueTypeItemStackLPElement.SubGuiRenderPattern) subGui).container.getTemporaryInputSlots()
            .setInventorySlotContents(0, this.itemStack);
    }

    @SideOnly(Side.CLIENT)
    protected static class SubGuiRenderPattern
        extends RenderPattern<ValueTypeItemStackLPElement, GuiLogicProgrammerBase, ContainerLogicProgrammerBase> {

        public SubGuiRenderPattern(ValueTypeItemStackLPElement element, int baseX, int baseY, int maxWidth,
            int maxHeight, GuiLogicProgrammerBase gui, ContainerLogicProgrammerBase container) {
            super(element, baseX, baseY, maxWidth, maxHeight, gui, container);
        }

        @Override
        public void drawGuiContainerForegroundLayer(int guiLeft, int guiTop, TextureManager textureManager,
            FontRenderer fontRenderer, int mouseX, int mouseY) {
            super.drawGuiContainerForegroundLayer(guiLeft, guiTop, textureManager, fontRenderer, mouseX, mouseY);
            IValueType valueType = element.getValueType();

            // Output type tooltip
            if (!container.hasWriteItemInSlot()) {
                if (gui.func_146978_c(
                    ContainerLogicProgrammerBase.OUTPUT_X,
                    ContainerLogicProgrammerBase.OUTPUT_Y,
                    GuiLogicProgrammerBase.BOX_HEIGHT,
                    GuiLogicProgrammerBase.BOX_HEIGHT,
                    mouseX,
                    mouseY)) {
                    gui.drawTooltip(getValueTypeTooltip(valueType), mouseX - guiLeft, mouseY - guiTop);
                }
            }
        }

    }

    public static interface IItemStackToValue<V extends IValue> {

        public boolean isNullable();

        public LangHelpers.UnlocalizedString validate(ItemStack itemStack);

        public V getValue(ItemStack itemStack);

    }

}
