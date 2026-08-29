package ruiseki.integrateddynamics.core.logicprogrammer;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.google.common.collect.Sets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammerBase;
import ruiseki.integrateddynamics.core.client.gui.GuiTextFieldDropdown;
import ruiseki.integrateddynamics.core.client.gui.IDropdownEntry;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.ingredient.ItemMatchProperties;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import ruiseki.okcore.client.gui.component.button.GuiButtonCheckbox;
import ruiseki.okcore.client.gui.component.button.GuiButtonImage;
import ruiseki.okcore.client.gui.image.Images;
import ruiseki.okcore.helper.GuiHelpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.TagHelpers;
import ruiseki.okcore.tag.Registries;
import ruiseki.okcore.tag.TagKey;
import ruiseki.okcore.tag.TagManager;

/**
 * Selection panel for the list element value type.
 */
@SideOnly(Side.CLIENT)
public class ValueTypeRecipeLPElementPropertiesSubGui
    extends RenderPattern<ValueTypeRecipeLPElement, GuiLogicProgrammerBase, ContainerLogicProgrammerBase> {

    private final int slotId;
    private GuiButtonCheckbox inputNbt;
    private GuiButtonCheckbox inputTags;
    private GuiTextFieldDropdown<ResourceLocation> inputTagsDropdown;
    private GuiButtonImage inputSave;

    public ValueTypeRecipeLPElementPropertiesSubGui(ValueTypeRecipeLPElement element, int baseX, int baseY,
        int maxWidth, int maxHeight, GuiLogicProgrammerBase gui, ContainerLogicProgrammerBase container, int slotId) {
        super(element, baseX, baseY, maxWidth, maxHeight, gui, container);
        this.slotId = slotId;
    }

    @Override
    public void initGui(int guiLeft, int guiTop) {
        super.initGui(guiLeft, guiTop);

        this.inputNbt = new GuiButtonCheckbox(
            0,
            guiLeft + getX() - 2,
            guiTop + getY() + 2,
            20,
            10,
            LangHelpers.localize(L10NValues.GUI_RECIPE_STRICTNBT),
            false) {

            @Override
            public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                // Only allow one checkbox to be true at the same time
                if (inputNbt.isChecked()) {
                    inputTags.setChecked(false);
                }
                saveGuiToState();
                loadStateToGui();
                return super.mousePressed(mc, mouseX, mouseY);
            }
        };
        this.buttonList.add(this.inputNbt);
        this.inputTags = new GuiButtonCheckbox(
            1,
            guiLeft + getX() - 2,
            guiTop + getY() + 12,
            20,
            10,
            LangHelpers.localize(L10NValues.GUI_RECIPE_TAGVARIANTS),
            false) {

            @Override
            public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                // Only allow one checkbox to be true at the same time
                if (inputTags.isChecked()) {
                    inputNbt.setChecked(false);
                }
                saveGuiToState();
                loadStateToGui();
                if (inputTags.isChecked()) {
                    inputTagsDropdown.setFocused(true);
                }
                return super.mousePressed(mc, mouseX, mouseY);
            }
        };
        this.buttonList.add(this.inputTags);
        this.inputTagsDropdown = new GuiTextFieldDropdown<>(
            2,
            Minecraft.getMinecraft().fontRenderer,
            guiLeft + getX() + 2,
            guiTop + getY() + 23,
            134,
            14,
            true,
            Sets.newHashSet());
        this.inputTagsDropdown.setDropdownEntryListener((entry) -> saveGuiToState());
        this.inputTagsDropdown.setMaxStringLength(64);
        this.inputTagsDropdown.setDropdownSize(4);
        this.inputTagsDropdown.setEnableBackgroundDrawing(false);
        this.inputTagsDropdown.setTextColor(16777215);
        this.inputTagsDropdown.setCanLoseFocus(true);
        this.inputSave = new GuiButtonImage(3, guiLeft + getX() + 116, guiTop + getY() + 72, Images.OK) {

            @Override
            public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                // If tag checkbox is checked, only allow exiting if a valid tag has been set
                if (!inputTags.isChecked() || inputTagsDropdown.getSelectedDropdownPossibility() != null) {
                    element.lastGui.setRecipeSubGui();
                } else {
                    inputTagsDropdown.setFocused(true);
                }
                return super.mousePressed(mc, mouseX, mouseY);
            }
        };
        this.buttonList.add(this.inputSave);

        // Load button states
        loadStateToGui();
        // Show dropdown if a tag was already set
        if (this.inputTags.isChecked()) {
            this.inputTagsDropdown.setFocused(true);
        }
    }

    public ItemStack getSlotContents() {
        return container.inventorySlots.get(slotId + ValueTypeRecipeLPElement.SLOT_OFFSET)
            .getStack();
    }

    public ItemMatchProperties getSlotProperties() {
        return getElement().getInputStacks()
            .get(slotId);
    }

    private Set<IDropdownEntry<ResourceLocation>> getDropdownEntries() {
        LinkedHashSet<IDropdownEntry<ResourceLocation>> set = Sets.newLinkedHashSet();
        ItemStack stack = getSlotContents();

        if (stack == null || stack.getItem() == null) {
            for (TagKey<?> tagKey : TagManager.getManager()
                .getTags()
                .keySet()) {
                if (tagKey.registry()
                    .equals(Registries.ITEM)) {
                    set.add(new DropdownEntry(tagKey.location()));
                }
            }
        } else {
            Set<TagKey<Item>> tags = TagHelpers.getTags(stack);
            for (TagKey<Item> tagKey : tags) {
                set.add(new DropdownEntry(tagKey.location()));
            }
        }
        return set;
    }

    public void loadStateToGui() {
        ItemMatchProperties props = getSlotProperties();
        this.inputNbt.setChecked(props.isNbt());
        this.inputTags.setChecked(props.getItemTag() != null);
        this.inputTagsDropdown.setVisible(this.inputTags.isChecked());

        if (this.inputTags.isChecked()) {
            Set<IDropdownEntry<ResourceLocation>> dropdownEntries = getDropdownEntries();
            this.inputTagsDropdown.setPossibilities(dropdownEntries);
            if (props.getItemTag() != null) {
                this.inputTagsDropdown.selectPossibility(
                    dropdownEntries.stream()
                        .filter(
                            e -> e.getMatchString()
                                .equals(props.getItemTag()))
                        .findFirst()
                        .orElse(null));
            } else {
                if (!dropdownEntries.isEmpty()) {
                    this.inputTagsDropdown.selectPossibility(
                        dropdownEntries.iterator()
                            .next());
                } else {
                    this.inputTagsDropdown.selectPossibility(null);
                }
            }
        } else {
            this.inputTagsDropdown.setText("");
            this.inputTagsDropdown.setPossibilities(Collections.emptySet());
        }
    }

    public void saveGuiToState() {
        boolean nbt = this.inputNbt.isChecked();
        String tag = this.inputTags.isChecked() ? this.inputTagsDropdown.getText() : null;
        getSlotProperties().setNbt(nbt);
        getSlotProperties().setItemTag(tag);
        element.sendSlotPropertiesToServer(slotId, getSlotProperties());
    }

    @Override
    protected boolean drawRenderPattern() {
        return false;
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

        drawSlot(getX() + guiLeft + 116, getY() + guiTop + 2);

        fontRenderer.drawString(
            LangHelpers.localize(L10NValues.GUI_RECIPE_STRICTNBT),
            guiLeft + getX() + 24,
            guiTop + getY() + 3,
            0);
        fontRenderer.drawString(
            LangHelpers.localize(L10NValues.GUI_RECIPE_TAGVARIANTS),
            guiLeft + getX() + 24,
            guiTop + getY() + 13,
            0);
        this.inputTagsDropdown.drawTextBox(Minecraft.getMinecraft(), mouseX, mouseY);
    }

    @Override
    public void drawGuiContainerForegroundLayer(int guiLeft, int guiTop, TextureManager textureManager,
        FontRenderer fontRenderer, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(guiLeft, guiTop, textureManager, fontRenderer, mouseX, mouseY);

        if (this.inputTagsDropdown.isFocused()) {
            int i = this.inputTagsDropdown.getHoveredVisiblePossibility(mouseX, mouseY);
            if (i >= 0) {
                IDropdownEntry<ResourceLocation> hoveredPossibility = this.inputTagsDropdown.getVisiblePossibility(i);
                drawTagsTooltip(hoveredPossibility, guiLeft, guiTop, mouseX + 10, mouseY - 20, 6, GuiHelpers.SLOT_SIZE);
            }
        }
    }

    protected void drawTagsTooltip(IDropdownEntry<ResourceLocation> hoveredPossibility, int guiLeft, int guiTop,
        int mouseX, int mouseY, int columns, int offset) {
        int x = mouseX - guiLeft;
        int y = mouseY - guiTop;

        TagKey<Item> tagKey = TagKey.create(Registries.ITEM, hoveredPossibility.getValue());
        List<ItemStack> stacks = TagHelpers.toItemStacks(tagKey);

        // Draw background
        GuiHelpers.drawTooltipBackground(
            x,
            y,
            Math.min(stacks.size(), columns) * offset,
            ((stacks.size() % columns == 0 ? 0 : 1) + (stacks.size() / columns)) * offset);

        // Draw item grid
        int passed = 0;
        RenderItem itemRenderer = RenderItem.getInstance();
        itemRenderer.zLevel = 300F;
        for (ItemStack stack : stacks) {
            itemRenderer.renderItemAndEffectIntoGUI(
                Minecraft.getMinecraft().fontRenderer,
                Minecraft.getMinecraft()
                    .getTextureManager(),
                stack,
                x,
                y);
            x += offset;
            if (passed++ % columns == columns - 1) {
                y += offset;
                x = mouseX - guiLeft;
            }
        }
        itemRenderer.zLevel = 0F;
    }

    @Override
    public boolean keyTyped(boolean checkHotbarKeys, char typedChar, int keyCode) throws IOException {
        if (inputTagsDropdown.isFocused()) {
            inputTagsDropdown.textboxKeyTyped(typedChar, keyCode);
            return true;
        }
        return super.keyTyped(checkHotbarKeys, typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        inputTagsDropdown.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public static class DropdownEntry implements IDropdownEntry<ResourceLocation> {

        private final ResourceLocation tag;

        public DropdownEntry(ResourceLocation tag) {
            this.tag = tag;
        }

        @Override
        public String getMatchString() {
            return this.tag.toString();
        }

        @Override
        public String getDisplayString() {
            return this.tag.toString();
        }

        @Override
        public List<String> getTooltip() {
            return Collections.emptyList();
        }

        @Override
        public ResourceLocation getValue() {
            return this.tag;
        }
    }
}
