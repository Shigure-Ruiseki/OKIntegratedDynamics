package ruiseki.integratedcrafting.client.gui;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integratedcrafting.IntegratedCrafting;
import ruiseki.integratedcrafting.Reference;
import ruiseki.integratedcrafting.inventory.container.ContainerPartInterfaceCraftingSettings;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.client.gui.GuiTextFieldDropdown;
import ruiseki.integrateddynamics.core.client.gui.container.GuiPartSettings;
import ruiseki.okcore.client.gui.component.input.GuiArrowedListField;
import ruiseki.okcore.client.gui.component.input.GuiNumberField;
import ruiseki.okcore.client.gui.component.input.IInputListener;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.ValueNotifierHelpers;
import ruiseki.okcore.init.ModBase;

/**
 * @author rubensworks
 */
public class GuiPartInterfaceCraftingSettings extends GuiPartSettings implements IInputListener {

    private GuiArrowedListField<IngredientComponent<?, ?>> ingredientComponentSideSelector = null;
    private GuiTextFieldDropdown<ForgeDirection> dropdownFieldSide = null;
    private List<SideDropdownEntry> dropdownEntries;
    private IngredientComponent<?, ?> selectedIngredientComponent = null;
    private GuiNumberField numberFieldChannelInterfaceCrafting = null;

    public GuiPartInterfaceCraftingSettings(EntityPlayer player, PartTarget target, IPartContainer partContainer,
        IPartType partType) {
        super(
            new ContainerPartInterfaceCraftingSettings(player, target, partContainer, partType),
            player,
            target,
            partContainer,
            partType);
    }

    protected ResourceLocation constructResourceLocation() {
        return new ResourceLocation(Reference.MOD_ID, getGuiTexture());
    }

    @Override
    public String getGuiTexture() {
        return IntegratedCrafting._instance.getReferenceValue(ModBase.REFKEY_TEXTURE_PATH_GUI)
            + "part_interface_settings.png";
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
                ((ContainerPartInterfaceCraftingSettings) getContainer())
                    .getTargetSideOverrideValueId(selectedIngredientComponent),
                side);

            int channelInterface = numberFieldChannelInterfaceCrafting.getInt();
            ValueNotifierHelpers.setValue(
                getContainer(),
                ((ContainerPartInterfaceCraftingSettings) getContainer()).getLastChannelInterfaceCraftingValueId(),
                channelInterface);
        } catch (NumberFormatException e) {}
    }

    @Override
    public void initGui() {
        super.initGui();

        ingredientComponentSideSelector = new GuiArrowedListField<IngredientComponent<?, ?>>(
            0,
            Minecraft.getMinecraft().fontRenderer,
            guiLeft + 106,
            guiTop + 9,
            68,
            15,
            true,
            true,
            Lists.newArrayList(IngredientComponent.REGISTRY.values())) {

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
            0,
            Minecraft.getMinecraft().fontRenderer,
            guiLeft + 106,
            guiTop + 34,
            68,
            14,
            true,
            Sets.newHashSet(dropdownEntries));
        setSideInDropdownField(
            selectedIngredientComponent,
            ((ContainerPartInterfaceCraftingSettings) container)
                .getTargetSideOverrideValue(selectedIngredientComponent));
        dropdownFieldSide.setMaxStringLength(15);
        dropdownFieldSide.setVisible(true);
        dropdownFieldSide.setTextColor(16777215);
        dropdownFieldSide.setCanLoseFocus(true);

        numberFieldChannelInterfaceCrafting = new GuiNumberField(
            0,
            Minecraft.getMinecraft().fontRenderer,
            guiLeft + 106,
            guiTop + 134,
            70,
            14,
            true,
            true);
        numberFieldChannelInterfaceCrafting.setPositiveOnly(false);
        numberFieldChannelInterfaceCrafting.setMaxStringLength(15);
        numberFieldChannelInterfaceCrafting.setVisible(true);
        numberFieldChannelInterfaceCrafting.setTextColor(16777215);
        numberFieldChannelInterfaceCrafting.setCanLoseFocus(true);

        this.refreshValues();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (!this.checkHotbarKeys(keyCode)) {
            if (!this.numberFieldChannelInterfaceCrafting.textboxKeyTyped(typedChar, keyCode)
                && !this.dropdownFieldSide.textboxKeyTyped(typedChar, keyCode)) {
                super.keyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.ingredientComponentSideSelector.mouseClicked(mouseX, mouseY, mouseButton);
        this.dropdownFieldSide.mouseClicked(mouseX, mouseY, mouseButton);
        this.numberFieldChannelInterfaceCrafting.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
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
        ingredientComponentSideSelector.drawTextBox(Minecraft.getMinecraft(), mouseX, mouseY);
        dropdownFieldSide.drawTextBox(Minecraft.getMinecraft(), mouseX, mouseY);

        fontRendererObj.drawString(
            LangHelpers.localize("gui.integratedcrafting.partsettings.channel.interface"),
            guiLeft + 8,
            guiTop + 137,
            0);
        numberFieldChannelInterfaceCrafting.drawTextBox(Minecraft.getMinecraft(), mouseX, mouseY);
    }

    @Override
    protected int getBaseYSize() {
        return 236;
    }

    protected void setSideInDropdownField(IngredientComponent<?, ?> ingredientComponent, ForgeDirection side) {
        if (selectedIngredientComponent == ingredientComponent) {
            dropdownFieldSide.selectPossibility(dropdownEntries.get(side.ordinal()));
        }
    }

    @Override
    public void onUpdate(int valueId, NBTTagCompound value) {
        super.onUpdate(valueId, value);
        for (IngredientComponent<?, ?> ingredientComponent : IngredientComponent.REGISTRY.values()) {
            if (valueId == ((ContainerPartInterfaceCraftingSettings) getContainer())
                .getTargetSideOverrideValueId(ingredientComponent)) {
                int side = ((ContainerPartInterfaceCraftingSettings) getContainer())
                    .getTargetSideOverrideValue(ingredientComponent)
                    .ordinal();
                setSideInDropdownField(
                    ingredientComponent,
                    side == -1 ? getDefaultSide() : ForgeDirection.VALID_DIRECTIONS[side]);
            }
        }
        if (valueId
            == ((ContainerPartInterfaceCraftingSettings) getContainer()).getLastChannelInterfaceCraftingValueId()) {
            numberFieldChannelInterfaceCrafting.setText(
                Integer.toString(
                    ((ContainerPartInterfaceCraftingSettings) getContainer()).getLastChannelInterfaceValue()));
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
