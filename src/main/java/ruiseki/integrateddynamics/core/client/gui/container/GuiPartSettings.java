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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
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

/**
 * Gui for part settings.
 *
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class GuiPartSettings<C extends ContainerPartSettings> extends GuiContainerExtended<C> {

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
            (C) new ContainerPartSettings(player, target, partContainer, partType),
            player,
            target,
            partContainer,
            partType);
    }

    public GuiPartSettings(C containerPartSettings, EntityPlayer player, PartTarget target,
        IPartContainer partContainer, IPartType partType) {
        super(containerPartSettings);
        this.target = target;
        this.partContainer = partContainer;
        this.partType = partType;
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
                ValueNotifierHelpers.setValue(getContainer(), getContainer().getLastSideValueId(), side);
            }
            if (isFieldUpdateIntervalEnabled()) {
                int updateInterval = numberFieldUpdateInterval.getInt();
                ValueNotifierHelpers.setValue(getContainer(), getContainer().getLastUpdateValueId(), updateInterval);
            }
            if (isFieldPriorityEnabled()) {
                int priority = numberFieldPriority.getInt();
                ValueNotifierHelpers.setValue(getContainer(), getContainer().getLastPriorityValueId(), priority);
            }
            if (isFieldChannelEnabled()) {
                int channel = numberFieldChannel.getInt();
                ValueNotifierHelpers.setValue(getContainer(), getContainer().getLastChannelValueId(), channel);
            }
        } catch (NumberFormatException e) {}
    }

    @Override
    protected ResourceLocation constructGuiTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/part_settings.png");
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
                Minecraft.getMinecraft().fontRenderer,
                guiLeft + 106,
                guiTop + getFieldSideY(),
                70,
                14,
                LangHelpers.localize("gui.integrateddynamics.partsettings.side"),
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
                Minecraft.getMinecraft().fontRenderer,
                guiLeft + 106,
                guiTop + getFieldUpdateIntervalY(),
                70,
                14,
                true,
                LangHelpers.localize("gui.integrateddynamics.partsettings.update_interval"),
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
                Minecraft.getMinecraft().fontRenderer,
                guiLeft + 106,
                guiTop + getFieldPriorityY(),
                70,
                14,
                true,
                LangHelpers.localize("gui.integrateddynamics.partsettings.priority"),
                true);
            numberFieldPriority.setPositiveOnly(false);
            numberFieldPriority.setMaxStringLength(15);
            numberFieldPriority.setVisible(true);
            numberFieldPriority.setTextColor(16777215);
            numberFieldPriority.setCanLoseFocus(true);
        }

        if (isFieldChannelEnabled()) {
            numberFieldChannel = new GuiNumberField(
                Minecraft.getMinecraft().fontRenderer,
                guiLeft + 106,
                guiTop + getFieldChannelY(),
                70,
                14,
                true,
                LangHelpers.localize("gui.integrateddynamics.partsettings.channel"),
                true);
            numberFieldChannel.setPositiveOnly(false);
            numberFieldChannel.setMaxStringLength(15);
            numberFieldChannel.setVisible(true);
            numberFieldChannel.setTextColor(16777215);
            numberFieldChannel.setCanLoseFocus(true);
            numberFieldChannel.setEnabled(isChannelEnabled());
        }

        String save = LangHelpers.localize("gui.integrateddynamics.button.save");
        addRenderableWidget(
            new GuiButtonText(
                this.guiLeft + 178,
                this.guiTop + 8,
                fontRendererObj.getStringWidth(save) + 6,
                16,
                save,
                createServerPressable(ContainerPartSettings.BUTTON_SAVE, b -> onSave()),
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
    public boolean charTyped(char typedChar, int keyCode) {
        if (!(isFieldUpdateIntervalEnabled() && this.numberFieldUpdateInterval.charTyped(typedChar, keyCode))
            && !(isFieldPriorityEnabled() && this.numberFieldPriority.charTyped(typedChar, keyCode))
            && !(isFieldChannelEnabled() && this.numberFieldChannel.charTyped(typedChar, keyCode))
            && !(isFieldSideEnabled() && this.dropdownFieldSide.charTyped(typedChar, keyCode))) {
            return super.charTyped(typedChar, keyCode);
        }
        return true;
    }

    @Override
    public boolean keyPressed(int typedChar, int keyCode, int modifiers) {
        if (typedChar != Keyboard.KEY_ESCAPE) {
            if (isFieldSideEnabled()) {
                if (this.dropdownFieldSide.keyPressed(typedChar, keyCode, modifiers)) {
                    return true;
                }
            }
            if (isFieldUpdateIntervalEnabled()) {
                if (this.numberFieldUpdateInterval.keyPressed(typedChar, keyCode, modifiers)) {
                    return true;
                }
            }
            if (isFieldPriorityEnabled()) {
                if (this.numberFieldPriority.keyPressed(typedChar, keyCode, modifiers)) {
                    return true;
                }
            }
            if (isFieldChannelEnabled()) {
                if (this.numberFieldChannel.keyPressed(typedChar, keyCode, modifiers)) {
                    return true;
                }
            }
            return true;
        } else {
            return super.keyPressed(typedChar, keyCode, modifiers);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (isFieldSideEnabled()) {
            if (this.dropdownFieldSide.mouseClicked(mouseX, mouseY, mouseButton)) {
                return true;
            }
        }
        if (isFieldUpdateIntervalEnabled()) {
            if (this.numberFieldUpdateInterval.mouseClicked(mouseX, mouseY, mouseButton)) {
                return true;
            }
        }
        if (isFieldPriorityEnabled()) {
            if (this.numberFieldPriority.mouseClicked(mouseX, mouseY, mouseButton)) {
                return true;
            }
        }
        if (isFieldChannelEnabled()) {
            if (this.numberFieldChannel.mouseClicked(mouseX, mouseY, mouseButton)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
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
            numberFieldUpdateInterval.drawScreen(mouseX, mouseY, partialTicks);
        }
        if (isFieldPriorityEnabled()) {
            fontRendererObj.drawString(
                LangHelpers.localize("gui.integrateddynamics.partsettings.priority"),
                guiLeft + 8,
                guiTop + getFieldPriorityY() + 3,
                Helpers.RGBToInt(0, 0, 0));
            numberFieldPriority.drawScreen(mouseX, mouseY, partialTicks);;
        }
        if (isFieldChannelEnabled()) {
            fontRendererObj.drawString(
                LangHelpers.localize("gui.integrateddynamics.partsettings.channel"),
                guiLeft + 8,
                guiTop + getFieldChannelY() + 3,
                isChannelEnabled() ? Helpers.RGBToInt(0, 0, 0) : Helpers.RGBToInt(100, 100, 100));
            numberFieldChannel.drawScreen(mouseX, mouseY, partialTicks);
        }
        if (isFieldSideEnabled()) {
            fontRendererObj.drawString(
                LangHelpers.localize("gui.integrateddynamics.partsettings.side"),
                guiLeft + 8,
                guiTop + getFieldSideY() + 3,
                Helpers.RGBToInt(0, 0, 0));
            dropdownFieldSide.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        if (!isChannelEnabled()) {
            GuiHelpers.renderTooltip(
                this,
                8,
                getFieldChannelY() + 3,
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
        if (isFieldSideEnabled() && valueId == getContainer().getLastSideValueId()) {
            int side = getContainer().getLastSideValue();
            setSideInDropdownField(side == -1 ? getDefaultSide() : ForgeDirection.values()[side]);
        }
        if (isFieldUpdateIntervalEnabled() && valueId == getContainer().getLastUpdateValueId()) {
            numberFieldUpdateInterval.setText(Integer.toString(getContainer().getLastUpdateValue()));
        }
        if (isFieldUpdateIntervalEnabled() && valueId == getContainer().getLastMinUpdateValueId()) {
            numberFieldUpdateInterval.setMinValue(getContainer().getLastMinUpdateValue());
        }
        if (isFieldPriorityEnabled() && valueId == getContainer().getLastPriorityValueId()) {
            numberFieldPriority.setText(Integer.toString(getContainer().getLastPriorityValueId()));
        }
        if (isFieldChannelEnabled() && valueId == getContainer().getLastChannelValueId()) {
            numberFieldChannel.setText(Integer.toString(getContainer().getLastChannelValue()));
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
