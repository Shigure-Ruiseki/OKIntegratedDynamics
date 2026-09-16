package ruiseki.integratednbt.client.gui.component;

import static ruiseki.integratednbt.client.gui.container.GuiNbtExtractor.GUI_TEXTURE;
import static ruiseki.integratednbt.client.gui.container.GuiNbtExtractor.SCREEN_EDGE;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integratednbt.evaluate.nbt.NbtValueConverter;
import ruiseki.integratednbt.evaluate.nbt.path.SegmentedNbtPath;
import ruiseki.okcore.client.gui.container.GuiContainerExtended;
import ruiseki.okcore.datastructure.Wrapper;
import ruiseki.okcore.helper.GuiHelpers;
import ruiseki.okcore.helper.LangHelpers;

public abstract class NbtTreeViewer {

    private static final long SMOOTH_SCROLLING_TRANSITION_TIME_MS = 75;
    private static final double SCROLL_SPEED = 30;
    private static final int LINE_SPACE = 1;
    private static final int EXPAND_BUTTON_RIGHT_MARGIN = 3;
    private static final int INDENTATION = 10;
    private static final int HIGHLIGHT_COLOR = 0xFF505050; // Dark gray
    private static final int SELECTED_COLOR = 0xFF506850; // Lighter Dark gray
    private static final int LABEL_COLOR = 0xFFFF55; // Yellow
    private static final int EXPAND_COLOR = 0xFFBB11; // Orange
    private static final int NUMBER_COLOR = 0x66FFFF; // Cyan
    private static final int STRING_COLOR = 0x55FF55; // Green
    private static final int COMPLEX_COLOR = 0xAAAAAA; // Gray
    private static final int EMPTY_COLOR = 0x777777; // Gray
    private static final int SCREEN_BACKGROUND_COLOR = 0xFF303030;
    private static final int SCROLL_BAR_COLOR = 0xFFCCCCCC;
    private static final int SCROLL_BAR_PADDING = 2;
    private static final int SCROLL_BAR_WIDTH = 3;
    private static final int EXPAND_BUTTON_SIZE = 7;
    private static final TexturePart PLUS_BUTTON = GUI_TEXTURE
        .createPart(48, 0, EXPAND_BUTTON_SIZE, EXPAND_BUTTON_SIZE);
    private static final TexturePart PLUS_BUTTON_HOVER = GUI_TEXTURE
        .createPart(48, 9, EXPAND_BUTTON_SIZE, EXPAND_BUTTON_SIZE);
    private static final TexturePart MINUS_BUTTON = GUI_TEXTURE
        .createPart(57, 0, EXPAND_BUTTON_SIZE, EXPAND_BUTTON_SIZE);
    private static final TexturePart MINUS_BUTTON_HOVER = GUI_TEXTURE
        .createPart(57, 9, EXPAND_BUTTON_SIZE, EXPAND_BUTTON_SIZE);

    private final Set<SegmentedNbtPath> expandedPaths;
    private final Wrapper<Integer> scrollTop;
    private final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    private GuiContainerExtended gui;
    private int left;
    private int top;
    private int width;
    private int height;
    private double renderScroll;
    private long renderScrollTransitionStartTime;
    private double renderScrollTransitionStartLocation;
    private int maxScroll = 0;
    private int currentY;
    private int currentX;
    private SegmentedNbtPath currentPath;
    private SegmentedNbtPath hoveringPath;
    private NBTBase hoveringNBTNode;
    private SegmentedNbtPath selecting;
    /**
     * X coordinate of mouse in the screen
     */
    private int mouseX;
    /**
     * Y coordinate of mouse in the screen
     */
    private int mouseY;
    private SegmentedNbtPath hoveringExpandableButton;
    private boolean isScrollbarDragging = false;
    /**
     * Y offset from the top of the scrollbar thumb at the time the drag started (absolute screen coords)
     */
    private double scrollbarDragOffsetY = 0;
    /**
     * Total content height, cached from the last render call; used by drag calculations
     */
    private int cachedTotalHeight = 0;

    public NbtTreeViewer(GuiContainerExtended gui, Set<SegmentedNbtPath> expandedPaths, Wrapper<Integer> scrollTop) {
        this.gui = gui;
        this.expandedPaths = expandedPaths;
        this.scrollTop = scrollTop;
        this.renderScroll = scrollTop.get();
    }

    public void updateBounds(int left, int top, int width, int height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }

    public void mouseScrolled(double dWheel) {
        int newValue = (int) (this.scrollTop.get() - dWheel * SCROLL_SPEED);
        if (newValue < 0) {
            newValue = 0;
        } else if (newValue > this.maxScroll) {
            newValue = this.maxScroll;
        }
        if (this.scrollTop.get() != newValue) {
            this.scrollTop.set(newValue);
            this.startScrollTransition();
        }
    }

    private void startScrollTransition() {
        this.renderScrollTransitionStartLocation = this.renderScroll;
        this.renderScrollTransitionStartTime = System.currentTimeMillis();
    }

    private boolean renderKVPair(GuiScreen gui, String label, String value, int valueColor) {
        if (label.isEmpty()) {
            return false;
        }
        boolean isSelected = this.getSelectedPath()
            .equals(this.currentPath);
        boolean isHovering = false;
        int bottomBoundary = this.currentY + this.fontRenderer.FONT_HEIGHT;

        boolean isThreeSideInBounds = ((this.mouseX >= 0) && (this.mouseX < this.width)
            && (this.mouseY >= this.renderScroll)
            && (this.mouseY < this.renderScroll + this.height)
            && (this.mouseY >= this.currentY)
            && (this.mouseY < bottomBoundary + LINE_SPACE)
            && (this.mouseX >= this.currentX));

        if (isThreeSideInBounds || isSelected) {
            int rightBoundary = this.currentX
                + this.fontRenderer.getStringWidth(label + (value.isEmpty() ? "" : (": " + value)));
            if (isSelected || this.mouseX < rightBoundary) {
                if (isThreeSideInBounds && this.mouseX < rightBoundary) {
                    isHovering = true;
                }
                Gui.drawRect(
                    this.currentX - 1,
                    this.currentY - 1,
                    rightBoundary + 1,
                    bottomBoundary + 1,
                    isSelected ? SELECTED_COLOR : HIGHLIGHT_COLOR);
            }
        }

        if (value.isEmpty()) {
            this.fontRenderer.drawString(label, this.currentX, this.currentY, LABEL_COLOR);
        } else {
            int valueX = this.fontRenderer.drawString(label + ": ", this.currentX, this.currentY, LABEL_COLOR);
            this.fontRenderer.drawString(value, valueX, this.currentY, valueColor);
        }
        return isHovering;
    }

    private void renderExpandableButton(GuiScreen gui, boolean expanded) {
        boolean hovering = (this.mouseX >= this.currentX && this.mouseX < (this.currentX + EXPAND_BUTTON_SIZE)
            && this.mouseY >= this.currentY
            && this.mouseY < (this.currentY + PLUS_BUTTON.getHeight()));
        TexturePart part = expanded ? (hovering ? MINUS_BUTTON_HOVER : MINUS_BUTTON)
            : (hovering ? PLUS_BUTTON_HOVER : PLUS_BUTTON);
        if (hovering) {
            this.hoveringExpandableButton = this.currentPath.copy();
        }
        part.renderTo(gui, this.currentX, this.currentY, EXPAND_COLOR);
        this.currentX += EXPAND_BUTTON_SIZE + EXPAND_BUTTON_RIGHT_MARGIN;
    }

    public void render(GuiScreen gui, NBTBase nbt, int absMouseX, int absMouseY) {
        this.hoveringPath = null;
        this.hoveringNBTNode = null;
        this.hoveringExpandableButton = null;
        this.currentPath = new SegmentedNbtPath();
        this.currentY = SCREEN_EDGE;
        this.updateScroll();
        this.mouseX = absMouseX - this.left;
        this.mouseY = (int) (absMouseY - this.top + this.renderScroll);

        GL11.glPushMatrix();
        try {
            GL11.glTranslatef((float) this.left, (float) (this.top - this.renderScroll), 0.0F);

            if (nbt == null) {
                this.fontRenderer.drawString(
                    LangHelpers.localize("integratednbt:nbt_extractor.empty"),
                    SCREEN_EDGE,
                    this.currentY,
                    EMPTY_COLOR);
            } else {
                this.renderNode(gui, LangHelpers.localize("integratednbt:nbt_extractor.root"), nbt);
            }

            int totalHeight = this.currentY + SCREEN_EDGE;
            this.cachedTotalHeight = totalHeight;

            GL11.glTranslatef(0.0F, (float) this.renderScroll, 0.0F);

            this.maxScroll = Math.max(totalHeight - this.height, 0);
            if (this.scrollTop.get() > this.maxScroll) {
                this.scrollTop.set(this.maxScroll);
                this.startScrollTransition();
            }

            if (this.maxScroll != 0) {
                Gui.drawRect(
                    this.width - SCROLL_BAR_PADDING * 2 - SCROLL_BAR_WIDTH - 1,
                    -1,
                    this.width + 1,
                    this.height,
                    SCREEN_BACKGROUND_COLOR);
                Gui.drawRect(
                    this.width - SCROLL_BAR_PADDING - SCROLL_BAR_WIDTH,
                    SCROLL_BAR_PADDING
                        + (int) (this.renderScroll / totalHeight * (this.height - SCROLL_BAR_PADDING * 2)),
                    this.width - SCROLL_BAR_PADDING,
                    SCROLL_BAR_PADDING
                        + (int) (this.renderScroll / totalHeight * (this.height - SCROLL_BAR_PADDING * 2))
                        + (int) (Math
                            .ceil((double) this.height / totalHeight * (this.height - SCROLL_BAR_PADDING * 2))),
                    SCROLL_BAR_COLOR);
            }
        } finally {
            GL11.glPopMatrix();
        }
    }

    public void renderTooltip(GuiContainer gui, int absMouseX, int absMouseY) {
        if (this.hoveringPath != null) {
            IValueType<? extends IValue> hoveringValueType = NbtValueConverter.mapNBTToValueType(this.hoveringNBTNode);
            List<String> list = new ArrayList<String>(5);
            list.add(this.hoveringPath.getDisplayText());

            String nbtTypeName = (this.hoveringNBTNode.getId() >= 0
                && this.hoveringNBTNode.getId() < NBTBase.NBTTypes.length)
                    ? NBTBase.NBTTypes[this.hoveringNBTNode.getId()]
                    : "UNKNOWN";

            list.add(LangHelpers.localize("integratednbt:nbt_extractor.tooltip.nbt_type", nbtTypeName));
            list.add(
                LangHelpers.localize(
                    "integratednbt:nbt_extractor.tooltip.converted_type",
                    hoveringValueType.getDisplayColorFormat()
                        + LangHelpers.localize(hoveringValueType.getUnlocalizedName())));
            list.add(
                LangHelpers.localize(
                    "integratednbt:nbt_extractor.tooltip.default_value",
                    NbtValueConverter.getDefaultValueDisplayText(this.hoveringNBTNode.getId())));

            if (Objects.equals(this.getSelectedPath(), this.hoveringPath)) {
                list.add(LangHelpers.localize("integratednbt:nbt_extractor.tooltip.selected"));
            } else if (Objects.equals(this.selecting, this.hoveringPath)) {
                list.add(LangHelpers.localize("integratednbt:nbt_extractor.tooltip.selecting"));
            } else {
                list.add(LangHelpers.localize("integratednbt:nbt_extractor.tooltip.left_click"));
            }

            if (isNodeExpandable(this.hoveringNBTNode)) {
                if (this.expandedPaths.contains(this.hoveringPath)) {
                    list.add(LangHelpers.localize("integratednbt:nbt_extractor.tooltip.right_click_collapse"));
                } else {
                    list.add(LangHelpers.localize("integratednbt:nbt_extractor.tooltip.right_click_expand"));
                }
            }

            GuiHelpers.drawTooltip(gui, list, absMouseX, absMouseY);
        }
    }

    private void updateScroll() {
        long transitionTime = System.currentTimeMillis() - this.renderScrollTransitionStartTime;
        if (transitionTime > SMOOTH_SCROLLING_TRANSITION_TIME_MS) {
            // Transition ended
            this.renderScroll = this.scrollTop.get();
            return;
        }
        double ratio = (double) transitionTime / SMOOTH_SCROLLING_TRANSITION_TIME_MS;
        this.renderScroll = this.renderScrollTransitionStartLocation
            + (this.scrollTop.get() - this.renderScrollTransitionStartLocation) * ratio;
    }

    public void mouseClicked(double absMouseX, double absMouseY, int mouseButton) {
        if (mouseButton == 0) { // Left click
            // Check if clicking on the scrollbar track
            if (this.maxScroll > 0 && this.isMouseOnScrollbarTrack(absMouseX, absMouseY)) {
                this.isScrollbarDragging = true;
                int thumbTopAbsolute = this.getScrollbarThumbTopAbsolute();
                int thumbHeight = this.getScrollbarThumbHeight();
                this.scrollbarDragOffsetY = absMouseY - thumbTopAbsolute;
                // If clicked outside the thumb, centre the drag on the thumb
                if (this.scrollbarDragOffsetY < 0 || this.scrollbarDragOffsetY > thumbHeight) {
                    this.scrollbarDragOffsetY = thumbHeight / 2.0;
                }
                return;
            }
            if (this.hoveringExpandableButton != null) {
                this.toggleExpanded(this.hoveringExpandableButton);
            }
            if (this.hoveringPath != null) {
                this.selecting = this.hoveringPath;
                this.onUpdateSelectedPath(this.hoveringPath, this.hoveringNBTNode);
            }
        } else if (mouseButton == 1) { // Right click
            if (this.hoveringPath != null && isNodeExpandable(this.hoveringNBTNode)) {
                this.toggleExpanded(this.hoveringPath);
            }
        }
    }

    public boolean mouseDragged(double absMouseX, double absMouseY, int mouseButton) {
        if (mouseButton == 0 && this.isScrollbarDragging) {
            int thumbHeight = this.getScrollbarThumbHeight();
            int trackAvailableHeight = this.height - SCROLL_BAR_PADDING * 2 - thumbHeight;
            int newThumbRelative = (int) (absMouseY - this.top - this.scrollbarDragOffsetY - SCROLL_BAR_PADDING);
            newThumbRelative = MathHelper.clamp_int(newThumbRelative, 0, trackAvailableHeight);
            int newScroll = trackAvailableHeight > 0
                ? (int) ((double) newThumbRelative / trackAvailableHeight * this.maxScroll)
                : 0;
            newScroll = MathHelper.clamp_int(newScroll, 0, this.maxScroll);
            if (this.scrollTop.get() != newScroll) {
                this.scrollTop.set(newScroll);
                this.startScrollTransition();
            }
            return true;
        }
        return false;
    }

    public void mouseReleased(double absMouseX, double absMouseY, int mouseButton) {
        if (mouseButton == 0) {
            this.isScrollbarDragging = false;
        }
    }

    private boolean isMouseOnScrollbarTrack(double absMouseX, double absMouseY) {
        int scrollbarLeft = this.left + this.width - SCROLL_BAR_PADDING * 2 - SCROLL_BAR_WIDTH;
        int scrollbarRight = this.left + this.width;
        return absMouseX >= scrollbarLeft && absMouseX < scrollbarRight
            && absMouseY >= this.top
            && absMouseY < this.top + this.height;
    }

    private int getScrollbarThumbHeight() {
        if (this.cachedTotalHeight == 0) {
            return this.height;
        }
        return (int) Math.ceil((double) this.height / this.cachedTotalHeight * (this.height - SCROLL_BAR_PADDING * 2));
    }

    private int getScrollbarThumbTopAbsolute() {
        if (this.cachedTotalHeight == 0) {
            return this.top + SCROLL_BAR_PADDING;
        }
        int thumbTopRelative = SCROLL_BAR_PADDING
            + (int) ((double) this.scrollTop.get() / this.cachedTotalHeight * (this.height - SCROLL_BAR_PADDING * 2));
        return this.top + thumbTopRelative;
    }

    private void toggleExpanded(SegmentedNbtPath path) {
        if (this.expandedPaths.contains(path)) {
            this.expandedPaths.remove(path);
        } else {
            this.expandedPaths.add(path.copy());
        }
    }

    public abstract void onUpdateSelectedPath(SegmentedNbtPath newPath, NBTBase nbt);

    private static boolean isNodeExpandable(NBTBase nbt) {
        int nbtId = nbt.getId();
        return nbtId == 9 || nbtId == 10;
    }

    private void renderEmpty(GuiScreen gui) {
        this.currentX = (this.currentPath.getDepth() + 1) * INDENTATION + SCREEN_EDGE
            + EXPAND_BUTTON_RIGHT_MARGIN
            + EXPAND_BUTTON_SIZE;
        this.fontRenderer.drawString(
            LangHelpers.localize("integratednbt:nbt_extractor.empty"),
            this.currentX,
            this.currentY,
            EMPTY_COLOR);
        this.currentY += this.fontRenderer.FONT_HEIGHT + LINE_SPACE;
    }

    private void renderNode(GuiScreen gui, String label, NBTBase node) {
        this.currentX = this.currentPath.getDepth() * INDENTATION + SCREEN_EDGE;
        boolean isExpandedIfExpandable = false;
        if (isNodeExpandable(node)) {
            isExpandedIfExpandable = this.expandedPaths.contains(this.currentPath);
        } else if (this.currentPath.getDepth() != 0) {
            this.currentX += EXPAND_BUTTON_RIGHT_MARGIN + EXPAND_BUTTON_SIZE;
        }

        boolean isHoveringText;

        // Render Value
        switch (node.getId()) {
            case 1: // Byte
                isHoveringText = this
                    .renderKVPair(gui, label, String.valueOf(((NBTTagByte) node).func_150287_d()), NUMBER_COLOR);
                break;
            case 2: // Short
                isHoveringText = this
                    .renderKVPair(gui, label, String.valueOf(((NBTTagShort) node).func_150287_d()), NUMBER_COLOR);
                break;
            case 3: // Int
                isHoveringText = this
                    .renderKVPair(gui, label, String.valueOf(((NBTTagInt) node).func_150287_d()), NUMBER_COLOR);
                break;
            case 4: // Long
                isHoveringText = this
                    .renderKVPair(gui, label, String.valueOf(((NBTTagLong) node).func_150291_c()), NUMBER_COLOR);
                break;
            case 5: // Float
                isHoveringText = this
                    .renderKVPair(gui, label, String.valueOf(((NBTTagFloat) node).func_150288_h()), NUMBER_COLOR);
                break;
            case 6: // Double
                isHoveringText = this
                    .renderKVPair(gui, label, String.valueOf(((NBTTagDouble) node).func_150286_g()), NUMBER_COLOR);
                break;
            case 7: // Byte Array
            case 11: // Int Array
            case 12: // Long Array
                isHoveringText = this.renderKVPair(gui, label, "[]", NUMBER_COLOR);
                break;
            case 8: // String
                isHoveringText = this
                    .renderKVPair(gui, label, '"' + ((NBTTagString) node).func_150285_a_() + '"', STRING_COLOR);
                break;
            case 9: // List
            case 10: { // Compound
                this.renderExpandableButton(gui, isExpandedIfExpandable);
                isHoveringText = this
                    .renderKVPair(gui, label, (isExpandedIfExpandable ? "" : node.toString()), COMPLEX_COLOR);
                break;
            }
            default:
                throw new RuntimeException("Unexpected NBT id:" + node.getId());
        }

        if (isHoveringText) {
            this.hoveringPath = this.currentPath.copy();
            this.hoveringNBTNode = node;
        }
        this.currentY += this.fontRenderer.FONT_HEIGHT + LINE_SPACE;

        if (isExpandedIfExpandable) {
            // Recursive calls
            switch (node.getId()) {
                case 9: { // List
                    NBTTagList list = (NBTTagList) node;
                    int count = list.tagCount();
                    if (count == 0) {
                        this.renderEmpty(gui);
                        break;
                    }
                    for (int i = 0; i < count; i++) {
                        this.currentPath.pushIndex(i);
                        // Lấy NBTBase từ NBTTagList trong 1.7.10
                        this.renderNode(gui, "#" + i, list.getCompoundTagAt(i));
                        this.currentPath.pop();
                    }
                    break;
                }
                case 10: { // Compound
                    NBTTagCompound compound = (NBTTagCompound) node;
                    @SuppressWarnings("unchecked")
                    Set<String> keys = compound.func_150296_c(); // Lấy tập hợp key dạng Set<String>
                    if (keys.isEmpty()) {
                        this.renderEmpty(gui);
                        break;
                    }
                    for (String key : keys) {
                        this.currentPath.pushKey(key);
                        this.renderNode(gui, key, compound.getTag(key));
                        this.currentPath.pop();
                    }
                    break;
                }
            }
        }
    }

    public abstract SegmentedNbtPath getSelectedPath();
}
