package ruiseki.integratedrest.client.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.core.client.gui.GuiActiveVariableBase;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.logicprogrammer.LogicProgrammerElementTypes;
import ruiseki.integratedrest.inventory.container.ContainerHttp;
import ruiseki.integratedrest.tileentity.TileHttp;
import ruiseki.okcore.client.gui.component.input.GuiArrowedListField;
import ruiseki.okcore.helper.ValueNotifierHelpers;

public class GuiHttp extends GuiActiveVariableBase<ContainerHttp, TileHttp> {

    private static final int ERROR_X = 140;
    private static final int ERROR_Y = 36;

    private GuiArrowedListField<IValueType> valueTypeSelector = null;

    public GuiHttp(InventoryPlayer inventory, TileHttp tile) {
        super(new ContainerHttp(inventory, tile));
    }

    @Override
    protected int getBaseYSize() {
        return 173;
    }

    @Override
    protected int getErrorX() {
        return ERROR_X;
    }

    @Override
    protected int getErrorY() {
        return ERROR_Y;
    }

    @Override
    protected int getValueY() {
        return 42;
    }

    @Override
    public void initGui() {
        super.initGui();

        List<IValueType> valueTypes = Lists.newArrayList(LogicProgrammerElementTypes.VALUETYPE.getValueTypes());
        valueTypes.add(ValueTypes.CATEGORY_ANY);
        valueTypeSelector = new GuiArrowedListField<>(
            0,
            Minecraft.getMinecraft().fontRenderer,
            guiLeft + 38,
            guiTop + 18,
            105,
            14,
            true,
            true,
            valueTypes);
        valueTypeSelector.setListener(
            () -> ValueNotifierHelpers.setValue(
                getContainer(),
                getContainer().getValueTypeId(),
                valueTypeSelector.getActiveElement()
                    .getUniqueName()
                    .toString()));
        getContainer().getValueType()
            .ifPresent(vt -> valueTypeSelector.setActiveElement(vt));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);
        valueTypeSelector.drawTextBox(Minecraft.getMinecraft(), mouseX, mouseY);
    }

    @Override
    public void onUpdate(int valueId, NBTTagCompound value) {
        if (valueId == getContainer().getValueTypeId()) {
            getContainer().getValueType()
                .ifPresent(vt -> valueTypeSelector.setActiveElement(vt));
        }
    }

}
