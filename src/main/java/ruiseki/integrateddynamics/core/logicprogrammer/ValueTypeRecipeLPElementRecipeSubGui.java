package ruiseki.integrateddynamics.core.logicprogrammer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.inventory.Slot;
import net.minecraft.util.EnumChatFormatting;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Getter;
import lombok.Setter;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammerBase;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import ruiseki.integrateddynamics.network.packet.LogicProgrammerValueTypeRecipeValueChangedPacket;
import ruiseki.okcore.client.gui.component.input.GuiTextFieldExtended;
import ruiseki.okcore.helper.LangHelpers;

/**
 * @author rubensworks
 */
@SideOnly(Side.CLIENT)
public class ValueTypeRecipeLPElementRecipeSubGui
    extends RenderPattern<ValueTypeRecipeLPElement, GuiLogicProgrammerBase, ContainerLogicProgrammerBase>
    implements IRenderPatternValueTypeTooltip {

    @Getter
    @Setter
    private boolean renderTooltip = true;
    @Getter
    private GuiTextFieldExtended inputFluidAmountBox = null;
    @Getter
    private GuiTextFieldExtended inputEnergyBox = null;
    @Getter
    private GuiTextFieldExtended outputFluidAmountBox = null;
    @Getter
    private GuiTextFieldExtended outputEnergyBox = null;

    public ValueTypeRecipeLPElementRecipeSubGui(ValueTypeRecipeLPElement element, int baseX, int baseY, int maxWidth,
        int maxHeight, GuiLogicProgrammerBase gui, ContainerLogicProgrammerBase container) {
        super(element, baseX, baseY, maxWidth, maxHeight, gui, container);
    }

    protected static GuiTextFieldExtended makeTextBox(int x, int y, String text) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        int searchWidth = 35;

        GuiTextFieldExtended box = new GuiTextFieldExtended(
            fontRenderer,
            x,
            y,
            searchWidth,
            fontRenderer.FONT_HEIGHT + 3,
            LangHelpers.localize("gui.okcore.search"),
            true);
        box.setMaxStringLength(10);
        box.setEnableBackgroundDrawing(false);
        box.setVisible(true);
        box.setTextColor(16777215);
        box.setCanLoseFocus(true);
        box.setText(text);
        box.width = searchWidth;
        return box;
    }

    @Override
    public void initGui(int guiLeft, int guiTop) {
        super.initGui(guiLeft, guiTop);

        this.inputFluidAmountBox = makeTextBox(
            guiLeft + getX() + 21,
            guiTop + getY() + 59,
            element.getInputFluidAmount());
        this.inputEnergyBox = makeTextBox(guiLeft + getX() + 21, guiTop + getY() + 77, element.getInputEnergy());
        this.outputFluidAmountBox = makeTextBox(
            guiLeft + getX() + 101,
            guiTop + getY() + 59,
            element.getOutputFluidAmount());
        this.outputEnergyBox = makeTextBox(guiLeft + getX() + 101, guiTop + getY() + 77, element.getOutputEnergy());
    }

    @Override
    public void drawGuiContainerForegroundLayer(int guiLeft, int guiTop, TextureManager textureManager,
        FontRenderer fontRenderer, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(guiLeft, guiTop, textureManager, fontRenderer, mouseX, mouseY);

        // Output type tooltip
        this.drawTooltipForeground(gui, container, guiLeft, guiTop, mouseX, mouseY, element.getValueType());

        // Render the info tooltip when hovering the input item slots
        for (int slotId = 0; slotId < this.container.inventorySlots.size(); ++slotId) {
            Slot slot = this.gui.inventorySlots.inventorySlots.get(slotId);
            if (slotId >= ValueTypeRecipeLPElement.SLOT_OFFSET && slotId < 9 + ValueTypeRecipeLPElement.SLOT_OFFSET) {
                int slotX = slot.xDisplayPosition;
                int slotY = slot.yDisplayPosition;

                // Draw tooltips
                if (gui.func_146978_c(slotX, slotY, 16, 16, mouseX, mouseY)) {
                    gui.drawTooltip(
                        Lists.newArrayList(
                            EnumChatFormatting.ITALIC + LangHelpers
                                .localize("valuetype.valuetypes.integrateddynamics.ingredients.slot.info")),
                        mouseX - guiLeft,
                        mouseY - guiTop - (slot.getStack() == null ? 0 : 15));
                }
            }
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

        // Draw crafting arrow
        this.drawTexturedModalRect(guiLeft + getX() + 66, guiTop + getY() + 21, 0, 38, 22, 15);

        inputFluidAmountBox.drawScreen(mouseX, mouseY, partialTicks);
        fontRenderer.drawString(
            LangHelpers.localize(L10NValues.GENERAL_ENERGY_UNIT) + ":",
            guiLeft + getX() + 2,
            guiTop + getY() + 78,
            0);
        inputEnergyBox.drawScreen(mouseX, mouseY, partialTicks);
        outputFluidAmountBox.drawScreen(mouseX, mouseY, partialTicks);
        fontRenderer.drawString(
            LangHelpers.localize(L10NValues.GENERAL_ENERGY_UNIT) + ":",
            guiLeft + getX() + 84,
            guiTop + getY() + 78,
            0);
        outputEnergyBox.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        if (inputFluidAmountBox.charTyped(typedChar, keyCode)) {
            element.setInputFluidAmount(inputFluidAmountBox.getText());
            container.onDirty();
            IntegratedDynamics._instance.getPacketHandler()
                .sendToServer(
                    new LogicProgrammerValueTypeRecipeValueChangedPacket(
                        element.getInputFluidAmount(),
                        LogicProgrammerValueTypeRecipeValueChangedPacket.Type.INPUT_FLUID));
            return true;
        }
        if (inputEnergyBox.charTyped(typedChar, keyCode)) {
            element.setInputEnergy(inputEnergyBox.getText());
            container.onDirty();
            IntegratedDynamics._instance.getPacketHandler()
                .sendToServer(
                    new LogicProgrammerValueTypeRecipeValueChangedPacket(
                        element.getInputEnergy(),
                        LogicProgrammerValueTypeRecipeValueChangedPacket.Type.INPUT_ENERGY));
            return true;
        }
        if (outputFluidAmountBox.charTyped(typedChar, keyCode)) {
            element.setOutputFluidAmount(outputFluidAmountBox.getText());
            container.onDirty();
            IntegratedDynamics._instance.getPacketHandler()
                .sendToServer(
                    new LogicProgrammerValueTypeRecipeValueChangedPacket(
                        element.getOutputFluidAmount(),
                        LogicProgrammerValueTypeRecipeValueChangedPacket.Type.OUTPUT_FLUID));
            return true;
        }
        if (outputEnergyBox.charTyped(typedChar, keyCode)) {
            element.setOutputEnergy(outputEnergyBox.getText());
            container.onDirty();
            IntegratedDynamics._instance.getPacketHandler()
                .sendToServer(
                    new LogicProgrammerValueTypeRecipeValueChangedPacket(
                        element.getOutputEnergy(),
                        LogicProgrammerValueTypeRecipeValueChangedPacket.Type.OUTPUT_ENERGY));
            return true;
        }
        return super.charTyped(typedChar, keyCode);
    }

    @Override
    public boolean keyPressed(int typedChar, int keyCode, int modifiers) {
        if (inputFluidAmountBox.keyPressed(typedChar, keyCode, modifiers)) {
            element.setInputFluidAmount(inputFluidAmountBox.getText());
            container.onDirty();
            IntegratedDynamics._instance.getPacketHandler()
                .sendToServer(
                    new LogicProgrammerValueTypeRecipeValueChangedPacket(
                        element.getInputFluidAmount(),
                        LogicProgrammerValueTypeRecipeValueChangedPacket.Type.INPUT_FLUID));
            return true;
        }
        if (inputEnergyBox.keyPressed(typedChar, keyCode, modifiers)) {
            element.setInputEnergy(inputEnergyBox.getText());
            container.onDirty();
            IntegratedDynamics._instance.getPacketHandler()
                .sendToServer(
                    new LogicProgrammerValueTypeRecipeValueChangedPacket(
                        element.getInputEnergy(),
                        LogicProgrammerValueTypeRecipeValueChangedPacket.Type.INPUT_ENERGY));
            return true;
        }
        if (outputFluidAmountBox.keyPressed(typedChar, keyCode, modifiers)) {
            element.setOutputFluidAmount(outputFluidAmountBox.getText());
            container.onDirty();
            IntegratedDynamics._instance.getPacketHandler()
                .sendToServer(
                    new LogicProgrammerValueTypeRecipeValueChangedPacket(
                        element.getOutputFluidAmount(),
                        LogicProgrammerValueTypeRecipeValueChangedPacket.Type.OUTPUT_FLUID));
            return true;
        }
        if (outputEnergyBox.keyPressed(typedChar, keyCode, modifiers)) {
            element.setOutputEnergy(outputEnergyBox.getText());
            container.onDirty();
            IntegratedDynamics._instance.getPacketHandler()
                .sendToServer(
                    new LogicProgrammerValueTypeRecipeValueChangedPacket(
                        element.getOutputEnergy(),
                        LogicProgrammerValueTypeRecipeValueChangedPacket.Type.OUTPUT_ENERGY));
            return true;
        }
        return super.keyPressed(typedChar, keyCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return inputFluidAmountBox.mouseClicked(mouseX, mouseY, mouseButton)
            || inputEnergyBox.mouseClicked(mouseX, mouseY, mouseButton)
            || outputFluidAmountBox.mouseClicked(mouseX, mouseY, mouseButton)
            || outputEnergyBox.mouseClicked(mouseX, mouseY, mouseButton)
            || super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
