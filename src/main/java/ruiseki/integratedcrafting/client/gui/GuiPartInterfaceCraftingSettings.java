package ruiseki.integratedcrafting.client.gui;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integratedcrafting.Reference;
import ruiseki.integratedcrafting.inventory.container.ContainerPartInterfaceCraftingSettings;
import ruiseki.integrateddynamics.core.client.gui.GuiTextFieldDropdown;
import ruiseki.integrateddynamics.core.client.gui.container.GuiPartSettings;
import ruiseki.okcore.client.gui.component.button.GuiButtonCheckbox;
import ruiseki.okcore.client.gui.component.input.GuiArrowedListField;
import ruiseki.okcore.client.gui.component.input.GuiNumberField;
import ruiseki.okcore.client.gui.component.input.IInputListener;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.ValueNotifierHelpers;

/**
 * @author rubensworks
 */
public class GuiPartInterfaceCraftingSettings extends GuiPartSettings<ContainerPartInterfaceCraftingSettings>
    implements IInputListener {

    private GuiArrowedListField<IngredientComponent<?, ?>> ingredientComponentSideSelector = null;
    private GuiTextFieldDropdown<ForgeDirection> dropdownFieldSide = null;
    private List<SideDropdownEntry> dropdownEntries;
    private IngredientComponent<?, ?> selectedIngredientComponent = null;
    private GuiNumberField numberFieldChannelInterfaceCrafting = null;
    private GuiButtonCheckbox checkboxFieldDisabledCraftingCheck = null;
    private GuiButtonCheckbox checkboxFieldBlockingMode = null;

    public GuiPartInterfaceCraftingSettings(ContainerPartInterfaceCraftingSettings container) {
        super(container);
    }

    @Override
    protected ResourceLocation constructGuiTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/part_interface_settings.png");
    }

    @Override
    protected boolean isFieldSideEnabled() {
        return false;
    }

    protected int getFieldUpdateIntervalY() {
        return 59;
    }

    protected int getFieldPriorityY() {
        return 84;
    }

    protected int getFieldChannelY() {
        return 109;
    }

    @Override
    protected void onSave() {
        super.onSave();
        try {
            ForgeDirection selectedSide = dropdownFieldSide.getSelectedDropdownPossibility() == null ? null
                : dropdownFieldSide.getSelectedDropdownPossibility()
                    .getValue();
            int side = selectedSide != null && selectedSide != getDefaultSide() ? selectedSide.ordinal() : -1;
            ValueNotifierHelpers.setValue(
                getContainer(),
                getContainer().getTargetSideOverrideValueId(selectedIngredientComponent),
                side);

            int channelInterface = numberFieldChannelInterfaceCrafting.getInt();
            ValueNotifierHelpers
                .setValue(getContainer(), getContainer().getLastChannelInterfaceCraftingValueId(), channelInterface);
            getContainer().setLastDisableCraftingCheckValue(checkboxFieldDisabledCraftingCheck.isChecked());
            getContainer().setLastBlockingModeValue(checkboxFieldBlockingMode.isChecked());
        } catch (NumberFormatException e) {}
    }

    @Override
    public void initGui() {
        super.initGui();

        ingredientComponentSideSelector = new GuiArrowedListField<IngredientComponent<?, ?>>(
            Minecraft.getMinecraft().fontRenderer,
            guiLeft + 106,
            guiTop + 9,
            68,
            15,
            true,
            LangHelpers.localize("gui.integratedcrafting.partsettings.ingredient"),
            true,
            Lists.newArrayList(IngredientComponent.REGISTRY.getValuesCollection())) {

            @Override
            protected String activeElementToString(IngredientComponent<?, ?> element) {
                return LangHelpers.localize(element.getTranslationKey());
            }
        };
        ingredientComponentSideSelector.setListener(this);
        selectedIngredientComponent = ingredientComponentSideSelector.getActiveElement();

        dropdownEntries = Arrays.stream(ForgeDirection.VALID_DIRECTIONS)
            .map(SideDropdownEntry::new)
            .collect(Collectors.toList());
        dropdownFieldSide = new GuiTextFieldDropdown(
            Minecraft.getMinecraft().fontRenderer,
            guiLeft + 106,
            guiTop + 34,
            68,
            14,
            LangHelpers.localize("gui.integrateddynamics.partsettings.side"),
            true,
            Sets.newHashSet(dropdownEntries));
        setSideInDropdownField(
            selectedIngredientComponent,
            container.getTargetSideOverrideValue(selectedIngredientComponent));
        dropdownFieldSide.setMaxStringLength(15);
        dropdownFieldSide.setVisible(true);
        dropdownFieldSide.setTextColor(16777215);
        dropdownFieldSide.setCanLoseFocus(true);

        numberFieldChannelInterfaceCrafting = new GuiNumberField(
            Minecraft.getMinecraft().fontRenderer,
            guiLeft + 106,
            guiTop + 134,
            70,
            14,
            true,
            LangHelpers.localize("gui.integrateddynamics.partsettings.update_interval"),
            true);
        numberFieldChannelInterfaceCrafting.setPositiveOnly(false);
        numberFieldChannelInterfaceCrafting.setMaxStringLength(15);
        numberFieldChannelInterfaceCrafting.setVisible(true);
        numberFieldChannelInterfaceCrafting.setTextColor(16777215);
        numberFieldChannelInterfaceCrafting.setCanLoseFocus(true);

        checkboxFieldDisabledCraftingCheck = new GuiButtonCheckbox(
            guiLeft + 110,
            guiTop + 149,
            110,
            10,
            LangHelpers.localize("gui.integratedcrafting.partsettings.craftingcheckdisabled"),
            (entry) -> {},
            false);

        checkboxFieldBlockingMode = new GuiButtonCheckbox(
            guiLeft + 110,
            guiTop + 159,
            110,
            10,
            LangHelpers.localize("gui.integratedcrafting.partsettings.blockingmode"),
            (entry) -> {},
            false);

        this.refreshValues();
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        if (!this.numberFieldChannelInterfaceCrafting.charTyped(typedChar, keyCode)
            && !this.dropdownFieldSide.charTyped(typedChar, keyCode)) {
            return super.charTyped(typedChar, keyCode);
        }
        return true;
    }

    @Override
    public boolean keyPressed(int typedChar, int keyCode, int modifiers) {
        if (typedChar != Keyboard.KEY_ESCAPE) {
            if (this.numberFieldChannelInterfaceCrafting.keyPressed(typedChar, keyCode, modifiers)) {
                return true;
            }
            if (this.dropdownFieldSide.keyPressed(typedChar, keyCode, modifiers)) {
                return true;
            }
        }
        return super.keyPressed(typedChar, keyCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (this.ingredientComponentSideSelector.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        if (this.dropdownFieldSide.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        if (this.numberFieldChannelInterfaceCrafting.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        if (this.checkboxFieldDisabledCraftingCheck.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        if (this.checkboxFieldBlockingMode.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        fontRendererObj.drawString(
            LangHelpers.localize("gui.integrateddynamics.partsettings.side"),
            guiLeft + 8,
            guiTop + 12,
            Helpers.RGBToInt(0, 0, 0));
        GlStateManager.color(1, 1, 1, 1);
        ingredientComponentSideSelector.drawScreen(mouseX, mouseY, partialTicks);
        dropdownFieldSide.drawScreen(mouseX, mouseY, partialTicks);

        fontRendererObj.drawString(
            LangHelpers.localize("gui.integratedcrafting.partsettings.channel.interface"),
            guiLeft + 8,
            guiTop + 137,
            0);
        numberFieldChannelInterfaceCrafting.drawScreen(mouseX, mouseY, partialTicks);

        fontRendererObj.drawString(
            LangHelpers.localize("gui.integratedcrafting.partsettings.craftingcheckdisabled"),
            guiLeft + 8,
            guiTop + 152,
            0);
        checkboxFieldDisabledCraftingCheck.drawScreen(mouseX, mouseY, partialTicks);

        fontRendererObj.drawString(
            LangHelpers.localize("gui.integratedcrafting.partsettings.blockingmode"),
            guiLeft + 8,
            guiTop + 162,
            0);
        checkboxFieldBlockingMode.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

    }

    @Override
    protected int getBaseYSize() {
        return 256;
    }

    protected void setSideInDropdownField(IngredientComponent<?, ?> ingredientComponent, ForgeDirection side) {
        if (selectedIngredientComponent == ingredientComponent) {
            dropdownFieldSide.selectPossibility(dropdownEntries.get(side.ordinal()));
        }
    }

    @Override
    public void onUpdate(int valueId, NBTTagCompound value) {
        super.onUpdate(valueId, value);
        for (IngredientComponent<?, ?> ingredientComponent : IngredientComponent.REGISTRY.getValuesCollection()) {
            if (valueId == getContainer().getTargetSideOverrideValueId(ingredientComponent)) {
                int side = getContainer().getTargetSideOverrideValue(ingredientComponent)
                    .ordinal();
                setSideInDropdownField(
                    ingredientComponent,
                    side == -1 ? getDefaultSide() : ForgeDirection.VALID_DIRECTIONS[side]);
            }
        }
        if (valueId == getContainer().getLastChannelInterfaceCraftingValueId()) {
            numberFieldChannelInterfaceCrafting
                .setText(Integer.toString(getContainer().getLastChannelInterfaceValue()));
        }
        if (valueId == getContainer().getLastDisableCraftingCheckValueId()) {
            checkboxFieldDisabledCraftingCheck.setChecked(getContainer().getLastDisableCraftingCheckValue());
        }

        if (valueId == getContainer().getLastBlockingModeValueId()) {
            checkboxFieldBlockingMode.setChecked(getContainer().getLastBlockingModeValue());
        }
    }

    @Override
    public void onChanged() {
        this.onSave();
        selectedIngredientComponent = ingredientComponentSideSelector.getActiveElement();
        setSideInDropdownField(
            selectedIngredientComponent,
            ((ContainerPartInterfaceCraftingSettings) container)
                .getTargetSideOverrideValue(selectedIngredientComponent));
    }
}
