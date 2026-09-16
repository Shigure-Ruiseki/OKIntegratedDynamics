package ruiseki.integrateddynamics.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.block.BlockDelayConfig;
import ruiseki.integrateddynamics.core.client.gui.GuiActiveVariableBase;
import ruiseki.integrateddynamics.inventory.container.ContainerDelay;
import ruiseki.integrateddynamics.tileentity.TileDelay;
import ruiseki.okcore.client.gui.component.input.GuiNumberField;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.ValueNotifierHelpers;

/**
 * Gui for the delay.
 *
 * @author rubensworks
 */
public class GuiDelay extends GuiActiveVariableBase<ContainerDelay, TileDelay> {

    private static final int ERROR_X = 110;
    private static final int ERROR_Y = 26;

    private GuiNumberField numberFieldUpdateInterval = null;
    private GuiNumberField numberFieldCapacity = null;

    /**
     * Make a new instance.
     *
     * @param inventory The player inventory.
     * @param tile      The part.
     */
    public GuiDelay(InventoryPlayer inventory, TileDelay tile) {
        super(new ContainerDelay(inventory, tile));
    }

    @Override
    protected ResourceLocation constructGuiTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/delay.png");
    }

    @Override
    protected int getBaseYSize() {
        return 227;
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
    public void initGui() {
        super.initGui();

        numberFieldUpdateInterval = new GuiNumberField(
            Minecraft.getMinecraft().fontRenderer,
            guiLeft + 98,
            guiTop + 102,
            73,
            14,
            true,
            true);
        numberFieldUpdateInterval.setPositiveOnly(true);
        numberFieldUpdateInterval.setMaxStringLength(64);
        numberFieldUpdateInterval.setMaxStringLength(15);
        numberFieldUpdateInterval.setVisible(true);
        numberFieldUpdateInterval.setTextColor(16777215);
        numberFieldUpdateInterval.setCanLoseFocus(true);

        numberFieldCapacity = new GuiNumberField(
            Minecraft.getMinecraft().fontRenderer,
            guiLeft + 98,
            guiTop + 126,
            73,
            14,
            true,
            true);
        numberFieldCapacity.setMinValue(1);
        numberFieldCapacity.setMaxValue(BlockDelayConfig.maxHistoryCapacity);
        numberFieldCapacity.setMaxStringLength(64);
        numberFieldCapacity.setMaxStringLength(15);
        numberFieldCapacity.setVisible(true);
        numberFieldCapacity.setTextColor(16777215);
        numberFieldCapacity.setCanLoseFocus(true);
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        if (!this.numberFieldUpdateInterval.charTyped(typedChar, keyCode)
            && !this.numberFieldCapacity.charTyped(typedChar, keyCode)) {
            return super.charTyped(typedChar, keyCode);
        } else {
            onValueChanged();
        }

        return true;
    }

    @Override
    public boolean keyPressed(int typedChar, int keyCode, int modifiers) {
        if (!this.numberFieldUpdateInterval.keyPressed(typedChar, keyCode, modifiers)
            && !this.numberFieldCapacity.keyPressed(typedChar, keyCode, modifiers)) {
            return super.keyPressed(typedChar, keyCode, modifiers);
        } else {
            onValueChanged();
        }

        return true;
    }

    protected void onValueChanged() {
        int updateInterval = 1;
        int capacity = 5;
        try {
            updateInterval = numberFieldUpdateInterval.getInt();
        } catch (NumberFormatException e) {}
        try {
            capacity = numberFieldCapacity.getInt();
        } catch (NumberFormatException e) {}
        ValueNotifierHelpers.setValue(getContainer(), getContainer().getLastUpdateValueId(), updateInterval);
        ValueNotifierHelpers.setValue(getContainer(), getContainer().getLastCapacityValueId(), capacity);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        boolean clicked = false;
        if (this.numberFieldUpdateInterval.mouseClicked(mouseX, mouseY, mouseButton)) {
            onValueChanged();
            clicked = true;
        }
        if (this.numberFieldCapacity.mouseClicked(mouseX, mouseY, mouseButton)) {
            onValueChanged();
            clicked = true;
        }
        return clicked || super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        numberFieldUpdateInterval.drawScreen(mouseX - guiLeft, mouseY - guiTop, partialTicks);
        numberFieldCapacity.drawScreen(mouseX - guiLeft, mouseY - guiTop, partialTicks);
        fontRendererObj.drawString(
            LangHelpers.localize("gui.integrateddynamics.partsettings.update_interval"),
            guiLeft + 8,
            guiTop + 104,
            Helpers.RGBToInt(0, 0, 0));
        fontRendererObj.drawString(
            LangHelpers.localize("gui.integrateddynamics.delay.capacity"),
            guiLeft + 8,
            guiTop + 128,
            Helpers.RGBToInt(0, 0, 0));
    }

    @Override
    public void onUpdate(int valueId, NBTTagCompound value) {
        if (valueId == getContainer().getLastUpdateValueId()) {
            numberFieldUpdateInterval.setText(Integer.toString(getContainer().getLastUpdateValue()));
        }
        if (valueId == ((ContainerDelay) getContainer()).getLastCapacityValueId()) {
            numberFieldCapacity.setText(Integer.toString(((ContainerDelay) getContainer()).getLastCapacityValue()));
        }
    }
}
