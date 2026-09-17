package ruiseki.integratedtunnels.core.part;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.client.gui.container.GuiPartSettings;
import ruiseki.integrateddynamics.core.inventory.container.ContainerMultipartAspects;
import ruiseki.integratedtunnels.Reference;
import ruiseki.okcore.client.gui.component.button.GuiButtonImage;
import ruiseki.okcore.client.gui.component.input.GuiNumberField;
import ruiseki.okcore.client.gui.image.IImage;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.ValueNotifierHelpers;

/**
 * @author rubensworks
 */
public class GuiInterfaceSettings extends GuiPartSettings<ContainerInterfaceSettings> {

    private GuiNumberField numberFieldChannelInterface = null;

    public GuiInterfaceSettings(EntityPlayer player, PartTarget target, IPartContainer partContainer,
        IPartType partType) {
        super(
            new ContainerInterfaceSettings(player, target, partContainer, partType),
            player,
            target,
            partContainer,
            partType);
    }

    @Override
    protected ResourceLocation constructGuiTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/part_interface_settings.png");
    }

    @Override
    protected void onSave() {
        super.onSave();
        try {
            int channelInterface = numberFieldChannelInterface.getInt();
            ValueNotifierHelpers
                .setValue(getContainer(), getContainer().getLastChannelInterfaceValueId(), channelInterface);
        } catch (NumberFormatException e) {}
    }

    @Override
    public void initGui() {
        super.initGui();

        numberFieldChannelInterface = new GuiNumberField(
            Minecraft.getMinecraft().fontRenderer,
            guiLeft + 106,
            guiTop + 109,
            70,
            14,
            true,
            true);
        numberFieldChannelInterface.setPositiveOnly(false);
        numberFieldChannelInterface.setMaxStringLength(15);
        numberFieldChannelInterface.setVisible(true);
        numberFieldChannelInterface.setTextColor(16777215);
        numberFieldChannelInterface.setCanLoseFocus(true);

        addRenderableWidget(
            new GuiButtonImage(
                this.guiLeft - 20,
                this.guiTop + 0,
                18,
                18,
                LangHelpers.localize("gui.integrateddynamics.part_offsets"),
                createServerPressable(ContainerMultipartAspects.BUTTON_OFFSETS, (button) -> onSave()),
                new IImage[] { ruiseki.integrateddynamics.client.gui.image.Images.BUTTON_BACKGROUND_INACTIVE,
                    ruiseki.integrateddynamics.client.gui.image.Images.BUTTON_MIDDLE_OFFSET },
                false,
                0,
                0));

        this.refreshValues();
    }

    @Override
    public void onGuiClosed() {
        // Auto-save the settings when the gui is closed,
        // so that the save button becomes optional.
        onSave();
        super.onGuiClosed();
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        if (!this.numberFieldChannelInterface.charTyped(typedChar, keyCode)) {
            return super.charTyped(typedChar, keyCode);
        }
        return true;
    }

    @Override
    public boolean keyPressed(int typedChar, int keyCode, int modifiers) {
        if (typedChar != Keyboard.KEY_ESCAPE) {
            if (this.numberFieldChannelInterface.keyPressed(typedChar, keyCode, modifiers)) {
                return true;
            }
        }
        return super.keyPressed(typedChar, keyCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (this.numberFieldChannelInterface.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        numberFieldChannelInterface.drawScreen(mouseX, mouseY, partialTicks);
        fontRendererObj.drawString(
            LangHelpers.localize("gui.integratedtunnels.partsettings.channel.interface"),
            guiLeft + 8,
            guiTop + 112,
            Helpers.RGBToInt(0, 0, 0));
        numberFieldChannelInterface.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        if (isPointInRegion(-20, 0, 18, 18, mouseX, mouseY)) {
            drawTooltip(
                Lists.newArrayList(LangHelpers.localize("gui.integrateddynamics.part_offsets")),
                mouseX - guiLeft,
                mouseY - guiTop);
        }
    }

    @Override
    protected int getBaseYSize() {
        return 216;
    }

    @Override
    public void onUpdate(int valueId, NBTTagCompound value) {
        super.onUpdate(valueId, value);
        if (valueId == getContainer().getLastChannelInterfaceValueId()) {
            numberFieldChannelInterface.setText(Integer.toString(getContainer().getLastChannelInterfaceValue()));
        }
    }
}
