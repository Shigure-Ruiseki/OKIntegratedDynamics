package ruiseki.integrateddynamics.core.client.gui.container;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.inventory.container.ContainerPartOffset;
import ruiseki.okcore.client.gui.component.button.GuiButtonText;
import ruiseki.okcore.client.gui.component.input.GuiNumberField;
import ruiseki.okcore.client.gui.container.GuiContainerExtended;
import ruiseki.okcore.client.gui.image.IImage;
import ruiseki.okcore.client.gui.image.Images;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.GuiHelpers;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.ValueNotifierHelpers;
import ruiseki.okcore.init.ModBase;

/**
 * Gui for part offsets.
 *
 * @author rubensworks
 */
public class GuiPartOffset<T extends ContainerPartOffset> extends GuiContainerExtended {

    public static final int BUTTON_SAVE = 0;

    private GuiNumberField numberFieldX = null;
    private GuiNumberField numberFieldY = null;
    private GuiNumberField numberFieldZ = null;

    /**
     * Make a new instance.
     *
     * @param target        The target.
     * @param player        The player.
     * @param partContainer The part container.
     * @param partType      The part type.
     */
    public GuiPartOffset(EntityPlayer player, PartTarget target, IPartContainer partContainer, IPartType partType) {
        this(new ContainerPartOffset(player, target, partContainer, partType), player, target, partContainer, partType);
    }

    public GuiPartOffset(ContainerPartOffset containerPartOffset, EntityPlayer player, PartTarget target,
        IPartContainer partContainer, IPartType partType) {
        super(containerPartOffset);

        putButtonAction(BUTTON_SAVE, (buttonId, gui, container) -> onSave());
    }

    @Override
    protected ContainerPartOffset getContainer() {
        return (ContainerPartOffset) super.getContainer();
    }

    @Override
    public String getGuiTexture() {
        return IntegratedDynamics._instance.getReferenceValue(ModBase.REFKEY_TEXTURE_PATH_GUI) + "part_offsets.png";
    }

    protected void onSave() {
        try {
            ValueNotifierHelpers.setValue(getContainer(), getContainer().getLastXValueId(), numberFieldX.getInt());
            ValueNotifierHelpers.setValue(getContainer(), getContainer().getLastYValueId(), numberFieldY.getInt());
            ValueNotifierHelpers.setValue(getContainer(), getContainer().getLastZValueId(), numberFieldZ.getInt());
        } catch (NumberFormatException e) {}
    }

    @Override
    public void initGui() {
        super.initGui();

        numberFieldX = new GuiNumberField(
            0,
            fontRendererObj,
            guiLeft + 107 - 54 - 7 - 18,
            guiTop + 33,
            46,
            14,
            true,
            true);
        numberFieldX.setMaxStringLength(4);
        numberFieldX.setVisible(true);
        numberFieldX.setTextColor(16777215);
        numberFieldX.setCanLoseFocus(true);

        numberFieldY = new GuiNumberField(
            1,
            fontRendererObj,
            guiLeft + 107 - 54 + 36 - 7,
            guiTop + 33,
            46,
            14,
            true,
            true);
        numberFieldY.setMaxStringLength(4);
        numberFieldY.setVisible(true);
        numberFieldY.setTextColor(16777215);
        numberFieldY.setCanLoseFocus(true);

        numberFieldZ = new GuiNumberField(
            2,
            fontRendererObj,
            guiLeft + 107 - 54 + 72 - 7 + 18,
            guiTop + 33,
            46,
            14,
            true,
            true);
        numberFieldZ.setMaxStringLength(4);
        numberFieldZ.setVisible(true);
        numberFieldZ.setTextColor(16777215);
        numberFieldZ.setCanLoseFocus(true);

        String save = LangHelpers.localize("gui.integrateddynamics.button.save");
        this.buttonList.add(
            new GuiButtonText(
                BUTTON_SAVE,
                this.guiLeft + 178,
                this.guiTop + 6,
                this.fontRendererObj.getStringWidth(save) + 6,
                16,
                save,
                true));

        this.refreshValues();
    }

    @Override
    public void onGuiClosed() {
        // Auto-save the offsets when the gui is closed,
        // so that players don't have to explicitly confirm their changes.
        onSave();
        super.onGuiClosed();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (!this.numberFieldX.textboxKeyTyped(typedChar, keyCode)
            && !this.numberFieldY.textboxKeyTyped(typedChar, keyCode)
            && !this.numberFieldZ.textboxKeyTyped(typedChar, keyCode)) {
            onSave();
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.numberFieldX.mouseClicked(mouseX, mouseY, mouseButton);
        this.numberFieldY.mouseClicked(mouseX, mouseY, mouseButton);
        this.numberFieldZ.mouseClicked(mouseX, mouseY, mouseButton);
        onSave();
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        fontRendererObj.drawString(
            LangHelpers.localize("gui.integrateddynamics.partoffset.offsets"),
            guiLeft + 8,
            guiTop + 19,
            Helpers.RGBToInt(0, 0, 0));
        numberFieldX.drawTextBox(Minecraft.getMinecraft(), mouseX, mouseY);
        numberFieldY.drawTextBox(Minecraft.getMinecraft(), mouseX, mouseY);
        numberFieldZ.drawTextBox(Minecraft.getMinecraft(), mouseX, mouseY);

        GlStateManager.color(1, 1, 1, 1);
        for (int i = 0; i < 3; i++) {
            int x = guiLeft + 64 + i * 54;
            if (getContainer().isOffsetVariableFilled(i)) {
                IImage image = getContainer().getOffsetVariableError(i) == null ? Images.OK : Images.ERROR;
                image.draw(this, x, guiTop + 52);
            }
        }

        if (getContainer().getMaxOffset() == 0) {
            Images.ERROR.draw(this, guiLeft + 74, guiTop + 3);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        this.fontRendererObj.drawString(LangHelpers.localize("gui.integrateddynamics.part_offsets"), 8, 6, 4210752);

        if (func_146978_c(0, 0, 90, 18, mouseX, mouseY)) {
            List<String> lines = Lists.newArrayList(
                LangHelpers.localize("gui.integrateddynamics.partoffset.offsets"),
                EnumChatFormatting.GRAY + LangHelpers
                    .localize("gui.integrateddynamics.partoffset.offsets.max", getContainer().getMaxOffset())

            );
            if (getContainer().getMaxOffset() == 0) {
                lines.add(
                    EnumChatFormatting.RED.toString() + EnumChatFormatting.BOLD
                        + LangHelpers.localize(
                            "gui.integrateddynamics.partoffset.offsets.max.howtoincrease",
                            getContainer().getMaxOffset()));
            }
            drawTooltip(lines, mouseX - guiLeft, mouseY - guiTop);
        }

        for (int i = 0; i < 3; i++) {
            int x = 64 + i * 54;
            int slot = i;
            GuiHelpers.renderTooltipOptional(this, x, 52, 14, 13, mouseX, mouseY, () -> {
                String unlocalizedMessage = getContainer().getOffsetVariableError(slot);
                if (unlocalizedMessage != null) {
                    return Optional.of(Collections.singletonList(unlocalizedMessage));
                }
                return Optional.empty();
            });
        }
    }

    @Override
    protected int getBaseXSize() {
        return 214;
    }

    @Override
    protected int getBaseYSize() {
        return 155;
    }

    @Override
    public void onUpdate(int valueId, NBTTagCompound value) {
        if (valueId == getContainer().getLastXValueId()) {
            numberFieldX.setText(Integer.toString(getContainer().getLastXValue()));
        }
        if (valueId == getContainer().getLastYValueId()) {
            numberFieldY.setText(Integer.toString(getContainer().getLastYValue()));
        }
        if (valueId == getContainer().getLastZValueId()) {
            numberFieldZ.setText(Integer.toString(getContainer().getLastZValue()));
        }

        numberFieldX.setEnabled(!getContainer().isOffsetVariableFilled(0));
        numberFieldY.setEnabled(!getContainer().isOffsetVariableFilled(1));
        numberFieldZ.setEnabled(!getContainer().isOffsetVariableFilled(2));

        if (valueId == getContainer().getMaxOffsetId()) {
            int max = getContainer().getMaxOffset();
            numberFieldX.setMaxValue(max);
            numberFieldX.setMinValue(-max);
            numberFieldY.setMaxValue(max);
            numberFieldY.setMinValue(-max);
            numberFieldZ.setMaxValue(max);
            numberFieldZ.setMinValue(-max);
        }
    }

}
