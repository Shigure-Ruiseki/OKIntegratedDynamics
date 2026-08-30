package ruiseki.integrateddynamics.core.client.gui.container;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.client.gui.ExtendedGuiHandler;
import ruiseki.integrateddynamics.core.client.gui.GuiTextFieldDropdown;
import ruiseki.integrateddynamics.core.client.gui.IDropdownEntry;
import ruiseki.integrateddynamics.core.inventory.container.ContainerPartSettings;
import ruiseki.okcore.client.gui.component.button.GuiButtonText;
import ruiseki.okcore.client.gui.component.input.GuiNumberField;
import ruiseki.okcore.client.gui.container.GuiContainerExtended;
import ruiseki.okcore.helper.GuiHelpers;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.ValueNotifierHelpers;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.inventory.container.ExtendedInventoryContainer;

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

    private GuiNumberField numberFieldUpdateInterval = null;
    private GuiNumberField numberFieldPriority = null;
    private GuiNumberField numberFieldChannel = null;
    private GuiTextFieldDropdown<ForgeDirection> dropdownFieldSide = null;
    private List<SideDropdownEntry> dropdownEntries;

    /**
     * Make a new instance.
     *
     * @param target        The target.
     * @param player        The player.
     * @param partContainer The part container.
     * @param partType      The part type.
     */
    public GuiPartSettings(EntityPlayer player, PartTarget target, IPartContainer partContainer, IPartType partType) {
        this(
            new ContainerPartSettings(player, target, partContainer, partType),
            player,
            target,
            partContainer,
            partType);
    }

    public GuiPartSettings(ContainerPartSettings containerPartSettings, EntityPlayer player, PartTarget target,
        IPartContainer partContainer, IPartType partType) {
        super(containerPartSettings);
        this.target = target;
        this.partContainer = partContainer;
        this.partType = partType;

        putButtonAction(BUTTON_SAVE, (buttonId, gui, container) -> onSave());
    }

    @Override
    protected ExtendedInventoryContainer getContainer() {
        return super.getContainer();
    }

    protected void onSave() {
        IntegratedDynamics._instance.getGuiHandler()
            .setTemporaryData(
                ExtendedGuiHandler.PART,
                getTarget().getCenter()
                    .getSide());
        try {
            if (isFieldSideEnabled()) {
                ForgeDirection selectedSide = dropdownFieldSide.getSelectedDropdownPossibility() == null ? null
                    : dropdownFieldSide.getSelectedDropdownPossibility()
                        .getValue();
                int side = selectedSide != null && selectedSide != getDefaultSide() ? selectedSide.ordinal() : -1;
                ValueNotifierHelpers
                    .setValue(getContainer(), ((ContainerPartSettings) getContainer()).getLastSideValueId(), side);
            }
            if (isFieldUpdateIntervalEnabled()) {
                int updateInterval = numberFieldUpdateInterval.getInt();
                ValueNotifierHelpers.setValue(
                    getContainer(),
                    ((ContainerPartSettings) getContainer()).getLastUpdateValueId(),
                    updateInterval);
            }
            if (isFieldPriorityEnabled()) {
                int priority = numberFieldPriority.getInt();
                ValueNotifierHelpers.setValue(
                    getContainer(),
                    ((ContainerPartSettings) getContainer()).getLastPriorityValueId(),
                    priority);
            }
            if (isFieldChannelEnabled()) {
                int channel = numberFieldChannel.getInt();
                ValueNotifierHelpers.setValue(
                    getContainer(),
                    ((ContainerPartSettings) getContainer()).getLastChannelValueId(),
                    channel);
            }
        } catch (NumberFormatException e) {}
    }

    @Override
    public String getGuiTexture() {
        return getContainer().getGuiProvider()
            .getModGui()
            .getReferenceValue(ModBase.REFKEY_TEXTURE_PATH_GUI) + "part_settings.png";
    }

    protected ForgeDirection getCurrentSide() {
        return getTarget().getTarget()
            .getSide();
    }

    protected ForgeDirection getDefaultSide() {
        return getTarget().getCenter()
            .getSide()
            .getOpposite();
    }

    protected String getSideText(ForgeDirection side) {
        return side.name()
            .toLowerCase(Locale.ENGLISH);
    }

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);

        if (isFieldSideEnabled()) {
            dropdownEntries = Arrays.stream(ForgeDirection.VALID_DIRECTIONS)
                .map(SideDropdownEntry::new)
                .collect(Collectors.toList());
            dropdownFieldSide = new GuiTextFieldDropdown(
                0,
                Minecraft.getMinecraft().fontRenderer,
                guiLeft + 106,
                guiTop + getFieldSideY(),
                70,
                14,
                true,
                Sets.newHashSet(dropdownEntries));
            setSideInDropdownField(getCurrentSide());
            dropdownFieldSide.setMaxStringLength(15);
            dropdownFieldSide.setVisible(true);
            dropdownFieldSide.setTextColor(16777215);
            dropdownFieldSide.setCanLoseFocus(true);
        }

        if (isFieldUpdateIntervalEnabled()) {
            numberFieldUpdateInterval = new GuiNumberField(
                0,
                Minecraft.getMinecraft().fontRenderer,
                guiLeft + 106,
                guiTop + getFieldUpdateIntervalY(),
                70,
                14,
                true,
                true);
            numberFieldUpdateInterval.setMaxStringLength(15);
            numberFieldUpdateInterval.setVisible(true);
            numberFieldUpdateInterval.setTextColor(16777215);
            numberFieldUpdateInterval.setCanLoseFocus(true);

            ContainerPartSettings container = (ContainerPartSettings) getContainer();
            numberFieldUpdateInterval.setMinValue(
                container.getPartType()
                    .getMinimumUpdateInterval(container.getPartState()));
        }

        if (isFieldPriorityEnabled()) {
            numberFieldPriority = new GuiNumberField(
                0,
                Minecraft.getMinecraft().fontRenderer,
                guiLeft + 106,
                guiTop + getFieldPriorityY(),
                70,
                14,
                true,
                true);
            numberFieldPriority.setPositiveOnly(false);
            numberFieldPriority.setMaxStringLength(15);
            numberFieldPriority.setVisible(true);
            numberFieldPriority.setTextColor(16777215);
            numberFieldPriority.setCanLoseFocus(true);
        }

        if (isFieldChannelEnabled()) {
            numberFieldChannel = new GuiNumberField(
                0,
                Minecraft.getMinecraft().fontRenderer,
                guiLeft + 106,
                guiTop + getFieldChannelY(),
                70,
                14,
                true,
                true);
            numberFieldChannel.setPositiveOnly(false);
            numberFieldChannel.setMaxStringLength(15);
            numberFieldChannel.setVisible(true);
            numberFieldChannel.setTextColor(16777215);
            numberFieldChannel.setCanLoseFocus(true);
            numberFieldChannel.setEnabled(isChannelEnabled());
        }

        String save = LangHelpers.localize("gui.integrateddynamics.button.save");
        buttonList.add(
            new GuiButtonText(
                BUTTON_SAVE,
                this.guiLeft + 178,
                this.guiTop + 8,
                fontRendererObj.getStringWidth(save) + 6,
                16,
                save,
                true));

        this.refreshValues();
    }

    protected int getFieldSideY() {
        return 9;
    }

    protected int getFieldUpdateIntervalY() {
        return 34;
    }

    protected int getFieldPriorityY() {
        return 59;
    }

    protected int getFieldChannelY() {
        return 84;
    }

    protected boolean isFieldSideEnabled() {
        return true;
    }

    protected boolean isFieldUpdateIntervalEnabled() {
        return true;
    }

    protected boolean isFieldPriorityEnabled() {
        return true;
    }

    protected boolean isFieldChannelEnabled() {
        return true;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (!this.checkHotbarKeys(keyCode)) {
            if (!(isFieldUpdateIntervalEnabled() && this.numberFieldUpdateInterval != null
                && this.numberFieldUpdateInterval.textboxKeyTyped(typedChar, keyCode))
                && !(isFieldPriorityEnabled() && this.numberFieldPriority != null
                    && this.numberFieldPriority.textboxKeyTyped(typedChar, keyCode))
                && !(isFieldChannelEnabled() && this.numberFieldChannel != null
                    && this.numberFieldChannel.textboxKeyTyped(typedChar, keyCode))
                && !(isFieldSideEnabled() && this.dropdownFieldSide != null
                    && this.dropdownFieldSide.textboxKeyTyped(typedChar, keyCode))) {
                super.keyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isFieldUpdateIntervalEnabled() && this.numberFieldUpdateInterval != null) {
            this.numberFieldUpdateInterval.mouseClicked(mouseX, mouseY, mouseButton);
        }
        if (isFieldPriorityEnabled() && this.numberFieldPriority != null) {
            this.numberFieldPriority.mouseClicked(mouseX, mouseY, mouseButton);
        }
        if (isFieldChannelEnabled() && this.numberFieldChannel != null) {
            this.numberFieldChannel.mouseClicked(mouseX, mouseY, mouseButton);
        }
        if (isFieldSideEnabled() && this.dropdownFieldSide != null) {
            this.dropdownFieldSide.mouseClicked(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        if (isFieldUpdateIntervalEnabled()) {
            fontRendererObj.drawString(
                LangHelpers.localize("gui.integrateddynamics.partsettings.update_interval"),
                guiLeft + 8,
                guiTop + getFieldUpdateIntervalY() + 3,
                Helpers.RGBToInt(0, 0, 0));
            numberFieldUpdateInterval.drawTextBox(Minecraft.getMinecraft(), mouseX, mouseY);
        }
        if (isFieldPriorityEnabled()) {
            fontRendererObj.drawString(
                LangHelpers.localize("gui.integrateddynamics.partsettings.priority"),
                guiLeft + 8,
                guiTop + getFieldPriorityY() + 3,
                Helpers.RGBToInt(0, 0, 0));
            numberFieldPriority.drawTextBox(Minecraft.getMinecraft(), mouseX, mouseY);
        }
        if (isFieldChannelEnabled()) {
            fontRendererObj.drawString(
                LangHelpers.localize("gui.integrateddynamics.partsettings.channel"),
                guiLeft + 8,
                guiTop + getFieldChannelY() + 3,
                isChannelEnabled() ? Helpers.RGBToInt(0, 0, 0) : Helpers.RGBToInt(100, 100, 100));
            numberFieldChannel.drawTextBox(Minecraft.getMinecraft(), mouseX, mouseY);
        }
        if (isFieldSideEnabled()) {
            fontRendererObj.drawString(
                LangHelpers.localize("gui.integrateddynamics.partsettings.side"),
                guiLeft + 8,
                guiTop + getFieldSideY() + 3,
                Helpers.RGBToInt(0, 0, 0));
            dropdownFieldSide.drawTextBox(Minecraft.getMinecraft(), mouseX, mouseY);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        if (!isChannelEnabled()) {
            GuiHelpers.renderTooltip(
                this,
                8,
                87,
                100,
                20,
                mouseX,
                mouseY,
                () -> Lists
                    .newArrayList(LangHelpers.localize("gui.integrateddynamics.partsettings.channel.disabledinfo")));
        }
    }

    protected boolean isChannelEnabled() {
        return GeneralConfig.energyConsumptionMultiplier > 0;
    }

    @Override
    protected int getBaseXSize() {
        return 214;
    }

    @Override
    protected int getBaseYSize() {
        return 191;
    }

    protected void setSideInDropdownField(ForgeDirection side) {
        dropdownFieldSide.selectPossibility(dropdownEntries.get(side.ordinal()));
    }

    @Override
    public void onUpdate(int valueId, NBTTagCompound value) {
        if (isFieldSideEnabled() && valueId == ((ContainerPartSettings) getContainer()).getLastSideValueId()) {
            int side = ((ContainerPartSettings) getContainer()).getLastSideValue();
            setSideInDropdownField(side == -1 ? getDefaultSide() : ForgeDirection.VALID_DIRECTIONS[side]);
        }
        if (isFieldUpdateIntervalEnabled()
            && valueId == ((ContainerPartSettings) getContainer()).getLastUpdateValueId()) {
            numberFieldUpdateInterval
                .setText(Integer.toString(((ContainerPartSettings) getContainer()).getLastUpdateValue()));
        }
        if (isFieldPriorityEnabled() && valueId == ((ContainerPartSettings) getContainer()).getLastPriorityValueId()) {
            numberFieldPriority
                .setText(Integer.toString(((ContainerPartSettings) getContainer()).getLastPriorityValue()));
        }
        if (isFieldChannelEnabled() && valueId == ((ContainerPartSettings) getContainer()).getLastChannelValueId()) {
            numberFieldChannel
                .setText(Integer.toString(((ContainerPartSettings) getContainer()).getLastChannelValue()));
        }
    }

    public class SideDropdownEntry implements IDropdownEntry<ForgeDirection> {

        private final ForgeDirection side;

        public SideDropdownEntry(ForgeDirection side) {
            this.side = side;
        }

        @Override
        public String getMatchString() {
            return getSideText(side);
        }

        @Override
        public String getDisplayString() {
            return (getDefaultSide() == this.side ? EnumChatFormatting.YELLOW : "") + getMatchString();
        }

        @Override
        public List<String> getTooltip() {
            return Collections.emptyList();
        }

        @Override
        public ForgeDirection getValue() {
            return this.side;
        }
    }

}
