package ruiseki.integrateddynamics.core.client.gui;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;
import ruiseki.okcore.client.gui.component.input.GuiTextFieldExtended;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.helper.RenderHelpers;

/**
 * A text field that can show a dropdown for autocomplete.
 *
 * @param <T> The dropdown entry type.
 * @author rubensworks
 */
public class GuiTextFieldDropdown<T> extends GuiTextFieldExtended {

    private final Set<IDropdownEntry<T>> possibilities;
    private List<IDropdownEntry<T>> visiblePossibilities = Collections.emptyList();
    private int visiblePossibilitiesIndex = -1;
    @Getter
    private IDropdownEntry<T> selectedDropdownPossibility = null;
    @Getter
    @Setter
    private int dropdownSize = 5;
    @Getter
    @Setter
    private IDropdownEntryListener<T> dropdownEntryListener;

    private int enabledColor = 14737632;
    private int disabledColor = 7368816;

    public GuiTextFieldDropdown(int componentId, FontRenderer fontrenderer, int x, int y, int width, int height,
        boolean background, Set<IDropdownEntry<T>> possibilities) {
        super(componentId, fontrenderer, x, y, width, height, background);
        this.possibilities = Objects.requireNonNull(possibilities);
    }

    public GuiTextFieldDropdown(int componentId, FontRenderer fontrenderer, int x, int y, int width, int height,
        boolean background) {
        this(componentId, fontrenderer, x, y, width, height, background, Collections.emptySet());
    }

    @Override
    public boolean textboxKeyTyped(char typedChar, int keyCode) {
        selectedDropdownPossibility = null;
        if (!possibilities.isEmpty()) {
            switch (keyCode) {
                case Keyboard.KEY_UP:
                    if (visiblePossibilitiesIndex >= 0) {
                        visiblePossibilitiesIndex--;
                    } else {
                        visiblePossibilitiesIndex = visiblePossibilities.size() - 1;
                    }
                    return true;
                case Keyboard.KEY_TAB:
                case Keyboard.KEY_DOWN:
                    if (visiblePossibilitiesIndex < visiblePossibilities.size() - 1) {
                        visiblePossibilitiesIndex++;
                    } else {
                        visiblePossibilitiesIndex = 0;
                    }
                    return true;
                case Keyboard.KEY_NUMPADENTER:
                case Keyboard.KEY_RIGHT:
                    if (visiblePossibilitiesIndex >= 0 && visiblePossibilitiesIndex < visiblePossibilities.size()) {
                        selectVisiblePossibility(visiblePossibilitiesIndex);
                        return true;
                    }
            }
        }
        if (super.textboxKeyTyped(typedChar, keyCode)) {
            // Remove all colors and formatting when changing text
            if (getText().contains("§")) {
                setText(getText().replaceAll("§.", ""));
            }
            if (!possibilities.isEmpty()) {
                visiblePossibilities = Lists.newArrayList();
                for (IDropdownEntry<T> possibility : possibilities) {
                    if (possibility.getMatchString()
                        .toLowerCase()
                        .contains(getText().toLowerCase())) {
                        visiblePossibilities.add(possibility);
                    }
                }
                visiblePossibilitiesIndex = -1;
                if (visiblePossibilities.size() == 1 && visiblePossibilities.get(0)
                    .getMatchString()
                    .equals(getText())) {
                    selectedDropdownPossibility = visiblePossibilities.get(0);
                }
                if (dropdownEntryListener != null) {
                    dropdownEntryListener.onSetDropdownPossiblity(selectedDropdownPossibility);
                }
            }
            return true;
        }
        return false;
    }

    protected void selectVisiblePossibility(int index) {
        visiblePossibilitiesIndex = index;
        selectPossibility(visiblePossibilities.get(visiblePossibilitiesIndex));
    }

    public void selectPossibility(IDropdownEntry<T> entry) {
        selectedDropdownPossibility = entry;
        setText(selectedDropdownPossibility.getDisplayString());
        visiblePossibilities = Lists.newArrayList();
        visiblePossibilitiesIndex = -1;
        if (dropdownEntryListener != null) {
            dropdownEntryListener.onSetDropdownPossiblity(selectedDropdownPossibility);
        }
    }

    @Override
    public void drawTextBox(Minecraft minecraft, int mouseX, int mouseY) {
        super.drawTextBox(minecraft, mouseX, mouseY);
        if (this.getVisible() && isFocused()) {
            FontRenderer fontRenderer = minecraft.fontRenderer;
            int yOffset = fontRenderer.FONT_HEIGHT + 3;

            int x = this.xPosition;
            int y = this.yPosition + yOffset;
            int width = this.getWidth() + 9;
            int startIndex = Math
                .max(0, Math.min(visiblePossibilitiesIndex, visiblePossibilities.size() - getDropdownSize()));
            int endIndex = Math.min(startIndex + getDropdownSize(), visiblePossibilities.size());
            int cy = y;

            // Draw ... if we are not at the first element
            if (startIndex > 0) {
                // Draw background
                drawRect(x, cy - 1, x + width, cy + 11, -6250336);
                drawRect(x - 1, cy, x + width - 1, cy + 10, -16777216);

                fontRenderer.drawStringWithShadow("...", x + 1, cy + 2, disabledColor);

                cy += 10;
            }

            for (int i = startIndex; i < endIndex; i++) {
                // Initialize entry
                IDropdownEntry<?> dropdownEntry = visiblePossibilities.get(i);
                String possibility = dropdownEntry.getDisplayString();
                String displayPossibility = fontRenderer.trimStringToWidth(possibility, width);
                boolean active = visiblePossibilitiesIndex == i;
                int entryHeight = yOffset;

                // Optionally initialize tooltip
                boolean addTooltip = (active && MinecraftHelpers.isShifted())
                    || RenderHelpers.isPointInRegion(x, cy, getWidth(), yOffset, mouseX, mouseY);
                List<String> tooltipLines = null;
                if (addTooltip) {
                    tooltipLines = dropdownEntry.getTooltip();
                    entryHeight += tooltipLines.size() * yOffset;
                }

                // Draw background
                drawRect(x, cy - 1, x + width, cy + entryHeight + 1, -6250336);
                drawRect(x - 1, cy, x + width - 1, cy + entryHeight, -16777216);

                // Draw text
                fontRenderer
                    .drawStringWithShadow(displayPossibility, x + 1, cy + 2, active ? enabledColor : disabledColor);
                if (addTooltip) {
                    int tooltipLineOffsetY = 2;
                    for (String tooltipLine : tooltipLines) {
                        tooltipLineOffsetY += yOffset;
                        fontRenderer.drawStringWithShadow(tooltipLine, x + 1, cy + tooltipLineOffsetY, enabledColor);
                    }
                }

                cy += entryHeight;
            }

            // Draw ... if we haven't reached the end of the list
            if (endIndex < visiblePossibilities.size()) {
                // Draw background
                drawRect(x, cy - 1, x + width, cy + 11, -6250336);
                drawRect(x - 1, cy, x + width - 1, cy + 10, -16777216);

                fontRenderer.drawStringWithShadow("...", x + 1, cy + 2, disabledColor);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.getVisible() && isFocused()) {
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
            int yOffset = fontRenderer.FONT_HEIGHT + 3;

            int x = this.xPosition;
            int y = this.yPosition + yOffset;
            int startIndex = Math
                .max(0, Math.min(visiblePossibilitiesIndex, visiblePossibilities.size() - getDropdownSize()));
            int endIndex = Math.min(startIndex + getDropdownSize(), visiblePossibilities.size());
            int cy = y;
            for (int i = startIndex; i < endIndex; i++) {
                // Initialize entry
                IDropdownEntry<?> dropdownEntry = visiblePossibilities.get(i);
                boolean active = visiblePossibilitiesIndex == i;
                int entryHeight = yOffset;

                // Optionally initialize tooltip
                boolean addTooltip = (active && MinecraftHelpers.isShifted())
                    || RenderHelpers.isPointInRegion(x, cy, getWidth(), yOffset, mouseX, mouseY);
                if (RenderHelpers.isPointInRegion(x, cy, getWidth(), yOffset, mouseX, mouseY)) {
                    selectVisiblePossibility(i);
                    return;
                }
                List<String> tooltipLines = null;
                if (addTooltip) {
                    tooltipLines = dropdownEntry.getTooltip();
                    entryHeight += tooltipLines.size() * yOffset;
                }

                cy += entryHeight;
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
