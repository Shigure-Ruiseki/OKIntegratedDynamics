package ruiseki.integrateddynamics.core.client.gui.container;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import org.lwjgl.input.Keyboard;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.inventory.container.ContainerPartSettings;
import ruiseki.okcore.client.gui.component.button.GuiButtonText;
import ruiseki.okcore.client.gui.component.input.GuiNumberField;
import ruiseki.okcore.client.gui.container.GuiContainerExtended;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.ValueNotifierHelpers;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.inventory.container.ExtendedInventoryContainer;
import ruiseki.okcore.inventory.container.button.IButtonActionClient;

/**
 * Gui for part settings.
 *
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class GuiPartSettings extends GuiContainerExtended {

    public static final int BUTTON_SAVE = 0;

    private final PartTarget target;
    private final IPartContainer partContainer;
    private final IPartType partType;

    private GuiNumberField numberField = null;

    /**
     * Make a new instance.
     *
     * @param target        The target.
     * @param player        The player.
     * @param partContainer The part container.
     * @param partType      The part type.
     */
    public GuiPartSettings(EntityPlayer player, PartTarget target, IPartContainer partContainer, IPartType partType) {
        super(new ContainerPartSettings(player, target, partContainer, partType));
        this.target = target;
        this.partContainer = partContainer;
        this.partType = partType;

        putButtonAction(BUTTON_SAVE, new IButtonActionClient<GuiContainerExtended, ExtendedInventoryContainer>() {

            @Override
            public void onAction(int buttonId, GuiContainerExtended gui, ExtendedInventoryContainer container) {
                try {
                    int updateInterval = numberField.getInt();
                    ValueNotifierHelpers.setValue(
                        getContainer(),
                        ((ContainerPartSettings) getContainer()).getLastUpdateValueId(),
                        updateInterval);
                } catch (NumberFormatException e) {}
            }
        });
    }

    @Override
    public String getGuiTexture() {
        return getContainer().getGuiProvider()
            .getModGui()
            .getReferenceValue(ModBase.REFKEY_TEXTURE_PATH_GUI) + "partSettings.png";
    }

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);

        numberField = new GuiNumberField(
            0,
            Minecraft.getMinecraft().fontRenderer,
            guiLeft + 38,
            guiTop + 9,
            100,
            14,
            true,
            true);
        numberField.setMaxStringLength(64);
        numberField.setMaxStringLength(15);
        numberField.setVisible(true);
        numberField.setTextColor(16777215);
        numberField.setCanLoseFocus(true);

        String save = LangHelpers.localize("gui.integrateddynamics.button.save");
        buttonList.add(
            new GuiButtonText(
                BUTTON_SAVE,
                this.guiLeft + 140,
                this.guiTop + 8,
                fontRendererObj.getStringWidth(save) + 6,
                16,
                save,
                true));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (!this.checkHotbarKeys(keyCode)) {
            if (!this.numberField.textboxKeyTyped(typedChar, keyCode)) {
                super.keyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.numberField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        numberField.drawTextBox(Minecraft.getMinecraft(), mouseX - guiLeft, mouseY - guiTop);
        fontRendererObj.drawString(
            LangHelpers.localize("gui.integrateddynamics.partsettings.updateInterval"),
            guiLeft + 8,
            guiTop + 12,
            Helpers.RGBToInt(0, 0, 0));
    }

    @Override
    public void onUpdate(int valueId, NBTTagCompound value) {
        numberField.setText(Integer.toString(((ContainerPartSettings) getContainer()).getLastUpdateValue()));
    }

}
