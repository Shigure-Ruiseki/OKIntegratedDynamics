package ruiseki.integratedterminals.core.client.gui;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetwork;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.Reference;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalButton;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageSlot;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabClient;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabCommon;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentItemStackCraftingCommon;
import ruiseki.integratedterminals.core.terminalstorage.button.TerminalButtonItemStackCraftingGridClear;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientItemStackCraftingGridBalance;
import ruiseki.integratedterminals.proxy.ClientProxy;
import ruiseki.okcore.client.gui.RenderItemExtendedSlotCount;
import ruiseki.okcore.client.gui.component.GuiScrollBar;
import ruiseki.okcore.client.gui.component.button.GuiButtonImage;
import ruiseki.okcore.client.gui.component.input.GuiArrowedListField;
import ruiseki.okcore.client.gui.component.input.GuiTextFieldExtended;
import ruiseki.okcore.client.gui.container.GuiContainerExtended;
import ruiseki.okcore.client.gui.image.IImage;
import ruiseki.okcore.client.gui.image.Images;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.GuiHelpers;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.helper.RenderHelpers;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.inventory.container.InventoryContainer;

/**
 * @author rubensworks
 */
public class GuiTerminalStorage<L, C extends ContainerTerminalStorageBase<L>> extends GuiContainerExtended {

    private static int TAB_OFFSET_X = 24;
    private static int TAB_WIDTH = 24;
    private static int TAB_UNSELECTED_HEIGHT = 21;
    private static int TAB_SELECTED_HEIGHT = 24;
    private static int TAB_ICON_OFFSET = 4;
    private static int TAB_UNSELECTED_TEXTURE_X = 118;
    private static int TAB_SELECTED_TEXTURE_X = 142;
    private static int TAB_UNSELECTED_TEXTURE_Y = 0;
    private static int TAB_SELECTED_TEXTURE_Y = 0;
    private static int SCROLL_Y = 40;

    private static int SEARCH_X = 103;
    private static int SEARCH_Y = 27;
    private static int SEARCH_HEIGHT = 20;

    private static int CHANNEL_X = 58;
    private static int CHANNEL_Y = 25;
    private static int CHANNEL_WIDTH = 42;
    private static int CHANNEL_HEIGHT = 15;

    private static int BUTTONS_OFFSET_X = 0;
    private static int BUTTONS_OFFSET_Y = 22;
    private static int BUTTONS_OFFSET = 4;

    private GuiArrowedListField<String> fieldChannel;
    private GuiScrollBar scrollBar;
    private GuiTextFieldExtended fieldSearch;
    private GuiButtonImage buttonSetDefaults;
    private int firstRow;
    private boolean initialized;
    protected final Set<Slot> terminalDragSplittingSlots = Sets.<Slot>newHashSet();
    protected boolean terminalDragSplitting;
    private int terminalDragMode;
    private int terminalDragSplittingButton;
    private int terminalDragSplittingRemnant;
    private boolean clicked;
    protected boolean swallowNextCharacter = false;

    public GuiTerminalStorage(C container) {
        super(container);
        container.screen = this;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        super.initGui();
        this.initialized = false;

        fieldChannel = new GuiArrowedListField<>(
            0,
            Minecraft.getMinecraft().fontRenderer,
            guiLeft + CHANNEL_X,
            guiTop + CHANNEL_Y,
            CHANNEL_WIDTH,
            CHANNEL_HEIGHT,
            true,
            true,
            getContainer().getChannelStrings());
        fieldChannel.setMaxStringLength(15);
        fieldChannel.setVisible(true);
        fieldChannel.setTextColor(16777215);
        fieldChannel.setCanLoseFocus(true);
        fieldChannel.setEnabled(true);
        int activeChannel = getContainer().getSelectedChannel();
        if (activeChannel != IPositionedAddonsNetwork.WILDCARD_CHANNEL) {
            fieldChannel.setText(Integer.toString(activeChannel));
        }
        firstRow = 0;

        scrollBar = new GuiScrollBar(
            guiLeft + getGridXSize() + 33,
            guiTop + SCROLL_Y + 1,
            getScrollHeight() - 2,
            firstRow -> this.firstRow = firstRow,
            0) {

            @Override
            public int getTotalRows() {
                ContainerTerminalStorageBase container = getContainer();
                Optional<ITerminalStorageTabClient<?>> tabOptional = getSelectedClientTab();
                if (!tabOptional.isPresent()) {
                    return 0;
                }
                int totalSlots = tabOptional.get()
                    .getSlotCount(container.getSelectedChannel());
                return (int) Math.ceil((double) totalSlots / getSlotRowLength());
            }

            @Override
            public int getVisibleRows() {
                return getSlotVisibleRows();
            }
        };

        fieldSearch = new GuiTextFieldExtended(
            1,
            Minecraft.getMinecraft().fontRenderer,
            guiLeft + SEARCH_X,
            guiTop + SEARCH_Y,
            getSearchWidth() - 10,
            SEARCH_HEIGHT);
        fieldSearch.setMaxStringLength(50);
        fieldSearch.setVisible(true);
        fieldSearch.setTextColor(16777215);
        fieldSearch.setCanLoseFocus(true);
        fieldSearch.setEnabled(true);
        fieldSearch.setEnableBackgroundDrawing(false);

        buttonSetDefaults = new GuiButtonImage(
            ContainerTerminalStorageBase.BUTTON_SET_DEFAULTS,
            this.guiLeft + ITerminalStorageTabClient.DEFAULT_SLOT_OFFSET_X
                + (getGridXSize() / 2)
                + getPlayerInventoryOffsetX()
                + (9 * GuiHelpers.SLOT_SIZE / 2)
                + 27,
            this.guiTop + getGridYSize() + getPlayerInventoryOffsetY() + 120,
            15,
            15,
            new IImage[] { Images.ANVIL },
            -2,
            -3,
            false);
        this.buttonList.add(buttonSetDefaults);

        repositionInventorySlots();
    }

    public void repositionInventorySlots() {
        int gridXSize = getGridXSize();
        int gridYSize = getGridYSize();
        int playerInventoryOffsetX = getPlayerInventoryOffsetX();
        int playerInventoryOffsetY = getPlayerInventoryOffsetY();
        ITerminalStorageTabCommon.SlotPositionFactors factors = new ITerminalStorageTabCommon.SlotPositionFactors(
            offsetX,
            offsetY,
            gridXSize,
            gridYSize,
            playerInventoryOffsetX,
            playerInventoryOffsetY);

        // Reposition regular inventory slots
        for (int y = 0; y < 1; y++) {
            for (int x = 0; x < 9; x++) {
                Slot slot = this.container.getSlot(x + y * 9 + 0);
                InventoryContainer.setSlotPosX(
                    slot,
                    offsetX + ITerminalStorageTabClient.DEFAULT_SLOT_OFFSET_X
                        - 1
                        + (gridXSize / 2)
                        - (9 * GuiHelpers.SLOT_SIZE / 2)
                        + playerInventoryOffsetX
                        + 19
                        + x * GuiHelpers.SLOT_SIZE);
                InventoryContainer.setSlotPosY(
                    slot,
                    offsetY + 58 + 63 + gridYSize + playerInventoryOffsetY + y * GuiHelpers.SLOT_SIZE);
            }
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                Slot slot = this.container.getSlot(x + y * 9 + 9);
                InventoryContainer.setSlotPosX(
                    slot,
                    offsetX + ITerminalStorageTabClient.DEFAULT_SLOT_OFFSET_X
                        - 1
                        + (gridXSize / 2)
                        - (9 * GuiHelpers.SLOT_SIZE / 2)
                        + playerInventoryOffsetX
                        + 19
                        + x * GuiHelpers.SLOT_SIZE);
                InventoryContainer
                    .setSlotPosY(slot, offsetY + 63 + gridYSize + playerInventoryOffsetY + y * GuiHelpers.SLOT_SIZE);
            }
        }
        for (int y = 0; y < 4; y++) {
            Slot slot = this.container.getSlot(36 + y);
            InventoryContainer.setSlotPosX(
                slot,
                offsetX + ITerminalStorageTabClient.DEFAULT_SLOT_OFFSET_X
                    - 1
                    + (gridXSize / 2)
                    - (9 * GuiHelpers.SLOT_SIZE / 2)
                    + playerInventoryOffsetX
                    - 19
                    + (y % 2) * GuiHelpers.SLOT_SIZE);
            InventoryContainer.setSlotPosY(
                slot,
                offsetY + 63
                    + gridYSize
                    + playerInventoryOffsetY
                    + 9
                    + ((int) Math.floor(y / 2)) * GuiHelpers.SLOT_SIZE);
        }
        {
            Slot slot = this.container.getSlot(40);
            InventoryContainer.setSlotPosX(
                slot,
                offsetX + ITerminalStorageTabClient.DEFAULT_SLOT_OFFSET_X
                    - 1
                    + (gridXSize / 2)
                    - (9 * GuiHelpers.SLOT_SIZE / 2)
                    + playerInventoryOffsetX
                    - 10);
            InventoryContainer.setSlotPosY(slot, offsetY + 63 + gridYSize + playerInventoryOffsetY + 9 + 49);
        }

        // Reposition tab slots
        Optional<ITerminalStorageTabClient<?>> tabOptional = getSelectedClientTab();
        tabOptional.ifPresent(tab -> {
            String tabName = getContainer().getSelectedTab();
            Optional<ITerminalStorageTabCommon> tabCommonOptional = getCommonTab(tabName);
            tabCommonOptional.ifPresent(tabCommon -> {
                for (Pair<Slot, ITerminalStorageTabCommon.ISlotPositionCallback> slot : getContainer()
                    .getTabSlots(tabName)) {
                    Pair<Integer, Integer> slotPos = slot.getRight()
                        .getSlotPosition(factors);
                    InventoryContainer.setSlotPosX(slot.getLeft(), slotPos.getLeft());
                    InventoryContainer.setSlotPosY(slot.getLeft(), slotPos.getRight());
                }
            });
        });
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (!initialized && getSelectedClientTab().isPresent()) {
            initialized = true;
            String filter = getSelectedClientTab().get()
                .getInstanceFilter(getContainer().getSelectedChannel());
            if (filter != null && !"".equals(filter)) {
                fieldSearch.setText(filter);
                getSelectedClientTab().get()
                    .setInstanceFilter(getContainer().getSelectedChannel(), filter); // Forces event to be sent
            }
        }
    }

    @Override
    protected ResourceLocation constructResourceLocation() {
        return new ResourceLocation(Reference.MOD_ID, this.getGuiTexture());
    }

    @Override
    public String getGuiTexture() {
        return IntegratedTerminals._instance.getReferenceValue(ModBase.REFKEY_TEXTURE_PATH_GUI)
            + "part_terminal_storage.png";
    }

    public int getGridXSize() {
        return getSlotRowLength() * GuiHelpers.SLOT_SIZE;
    }

    public int getGridYSize() {
        return getSlotVisibleRows() * GuiHelpers.SLOT_SIZE;
    }

    public int getScrollHeight() {
        return getGridYSize();
    }

    public int getSearchWidth() {
        return getBaseXSize() - 7 * GuiHelpers.SLOT_SIZE - 2;
    }

    @Override
    public int getBaseXSize() {
        return 56 + getGridXSize();
    }

    @Override
    public int getBaseYSize() {
        return 135 + getGridYSize() + getPlayerInventoryOffsetY() + 10;
    }

    protected int getPlayerInventoryOffsetX() {
        return getSelectedClientTab().map(ITerminalStorageTabClient::getPlayerInventoryOffsetX)
            .orElse(0);
    }

    protected int getPlayerInventoryOffsetY() {
        return getSelectedClientTab().map(ITerminalStorageTabClient::getPlayerInventoryOffsetY)
            .orElse(0);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
        // super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);
        RenderHelpers.bindTexture(texture);
        this.renderBgTab(f, mouseX, mouseY);
        this.renderBgPlayerInventory(f, mouseX, mouseY);

        fieldChannel.drawTextBox(Minecraft.getMinecraft(), mouseX, mouseY);
        fieldSearch.drawTextBox(Minecraft.getMinecraft(), mouseX, mouseY);
        drawTabsBackground();
        drawTabContents(
            getContainer().getSelectedTab(),
            getContainer().getSelectedChannel(),
            DrawLayer.BACKGROUND,
            f,
            getGuiLeftTotal() + getSlotsOffsetX(),
            getGuiTopTotal() + getSlotsOffsetY(),
            mouseX,
            mouseY);
        scrollBar.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);

        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableLighting();
        Optional<ITerminalStorageTabClient<?>> tabOptional = getSelectedClientTab();
        tabOptional.ifPresent(tab -> {
            int offset = 0;
            int gridXSize = getGridXSize();
            int gridYSize = getGridYSize();
            int playerInventoryOffsetX = getPlayerInventoryOffsetX();
            int playerInventoryOffsetY = getPlayerInventoryOffsetY();
            ITerminalStorageTabCommon.SlotPositionFactors factors = new ITerminalStorageTabCommon.SlotPositionFactors(
                offsetX,
                offsetY,
                gridXSize,
                gridYSize,
                playerInventoryOffsetX,
                playerInventoryOffsetY);
            for (ITerminalButton button : tab.getButtons()) {
                GuiButton guiButton = button.createButton(
                    button.getX(
                        guiLeft,
                        BUTTONS_OFFSET_X,
                        gridXSize,
                        gridYSize,
                        playerInventoryOffsetX,
                        playerInventoryOffsetY),
                    button.getY(
                        guiTop,
                        BUTTONS_OFFSET_Y + offset,
                        gridXSize,
                        gridYSize,
                        playerInventoryOffsetX,
                        playerInventoryOffsetY));
                guiButton.drawButton(mc, mouseX, mouseY);
                if (button.isInLeftColumn()) {
                    offset += BUTTONS_OFFSET + guiButton.height;
                }
            }

            String tabName = getContainer().getSelectedTab();
            Optional<ITerminalStorageTabCommon> tabCommonOptional = getCommonTab(tabName);
            tabCommonOptional.ifPresent(tabCommon -> {
                for (Pair<Slot, ITerminalStorageTabCommon.ISlotPositionCallback> slot : getContainer()
                    .getTabSlots(tabName)) {
                    Pair<Integer, Integer> slotPos = slot.getRight()
                        .getSlotPosition(factors);
                    tab.onCommonSlotRender(
                        this,
                        DrawLayer.BACKGROUND,
                        0,
                        guiLeft + slotPos.getLeft(),
                        guiTop + slotPos.getRight(),
                        mouseX,
                        mouseY,
                        slot.getLeft()
                            .getSlotIndex(),
                        tabCommon);
                }
            });
        });
    }

    protected void renderBgTab(float f, int mouseX, int mouseY) {
        int tabWidth = getGridXSize() + 29;
        int tabHeight = getGridYSize() + 40;
        int offset = 21;
        int cornerSize = 7;
        int columns = getSlotRowLength();
        int rows = getSlotVisibleRows();

        // Corners
        drawTexturedModalRect(guiLeft + offset, guiTop + offset, cornerSize, 0, cornerSize, cornerSize); // top-left
        drawTexturedModalRect(guiLeft + offset + tabWidth - cornerSize, guiTop + offset, 0, 0, cornerSize, cornerSize); // top-right
        drawTexturedModalRect(
            guiLeft + offset + tabWidth - cornerSize,
            guiTop + offset + tabHeight - cornerSize,
            cornerSize * 2,
            0,
            cornerSize,
            cornerSize); // bottom-right
        drawTexturedModalRect(
            guiLeft + offset,
            guiTop + offset + tabHeight - cornerSize,
            cornerSize * 3,
            0,
            cornerSize,
            cornerSize); // bottom-left

        // Sides
        blitRescalable(
            guiLeft + offset + cornerSize,
            guiTop + offset,
            cornerSize + 4,
            0,
            1,
            cornerSize,
            tabWidth - cornerSize * 2,
            cornerSize); // top
        blitRescalable(
            guiLeft + offset + tabWidth - cornerSize,
            guiTop + offset + cornerSize,
            0,
            4,
            cornerSize,
            1,
            cornerSize,
            tabHeight - cornerSize * 2); // right
        blitRescalable(
            guiLeft + offset + cornerSize,
            guiTop + offset + tabHeight - cornerSize,
            25,
            0,
            1,
            cornerSize,
            tabWidth - cornerSize * 2,
            cornerSize); // bottom
        blitRescalable(
            guiLeft + offset,
            guiTop + offset + cornerSize,
            cornerSize,
            4,
            cornerSize,
            1,
            cornerSize,
            tabHeight - cornerSize * 2); // left

        // Background
        blitRescalable(
            guiLeft + offset + cornerSize,
            guiTop + offset + cornerSize,
            0,
            3,
            1,
            1,
            tabWidth - cornerSize * 2,
            tabHeight - cornerSize * 2);

        // Slots
        for (int j = 0; j < rows; j++) {
            int renderRows = Math.min(3, rows - j); // Try rendering multiple rows for optimizing efficiency (if
                                                    // possible)
            for (int i = 0; i < columns; i++) {
                int renderColumns = Math.min(9, columns - i); // Try rendering multiple columns for optimizing
                                                              // efficiency (if possible)
                drawTexturedModalRect(
                    guiLeft + offset + 10 + i * GuiHelpers.SLOT_SIZE,
                    guiTop + offset + 18 + j * GuiHelpers.SLOT_SIZE,
                    80,
                    34,
                    GuiHelpers.SLOT_SIZE * renderColumns,
                    GuiHelpers.SLOT_SIZE * renderRows);
                i += renderColumns - 1;
            }
            j += renderRows - 1;
        }

        // Scrollbar background
        drawTexturedModalRect(guiLeft + getGridXSize() + 32, guiTop + SCROLL_Y - 1, 20, 12, 14, 1); // top
        blitRescalable(guiLeft + getGridXSize() + 32, guiTop + SCROLL_Y, 20, 13, 14, 1, 14, getScrollHeight() - 2); // middle
        drawTexturedModalRect(guiLeft + getGridXSize() + 32, guiTop + SCROLL_Y + getScrollHeight() - 2, 20, 101, 14, 1); // bottom

        // Textbox background
        drawTexturedModalRect(guiLeft + SEARCH_X - 1, guiTop + SEARCH_Y - 2, 28, 0, 1, SEARCH_HEIGHT - 8); // left
        blitRescalable(
            guiLeft + SEARCH_X,
            guiTop + SEARCH_Y - 2,
            29,
            0,
            1,
            SEARCH_HEIGHT - 8,
            getSearchWidth(),
            SEARCH_HEIGHT - 8); // middle
        drawTexturedModalRect(
            guiLeft + SEARCH_X + getSearchWidth() - 1,
            guiTop + SEARCH_Y - 2,
            117,
            0,
            1,
            SEARCH_HEIGHT - 8); // right

        // Render tab-specific things
        if (getSelectedClientTab().isPresent()) {
            getSelectedClientTab().get()
                .onTabBackgroundRender(this, f, mouseX, mouseY);
        }
    }

    /**
     * Phương thức thay thế blitRescalable tương thích với Gui 1.7.10 (texture 256x256 mặc định)
     */
    public static void blitRescalable(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height) {
        drawScaledCustomSizeModalRect(x, y, u, v, uWidth, vHeight, width, height, 256.0F, 256.0F);
    }

    public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width,
        int height, float tileWidth, float tileHeight) {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(
            (double) x,
            (double) (y + height),
            0.0D,
            (double) (u * f),
            (double) ((v + (float) vHeight) * f1));
        tessellator.addVertexWithUV(
            (double) (x + width),
            (double) (y + height),
            0.0D,
            (double) ((u + (float) uWidth) * f),
            (double) ((v + (float) vHeight) * f1));
        tessellator.addVertexWithUV(
            (double) (x + width),
            (double) y,
            0.0D,
            (double) ((u + (float) uWidth) * f),
            (double) (v * f1));
        tessellator.addVertexWithUV((double) x, (double) y, 0.0D, (double) (u * f), (double) (v * f1));
        tessellator.draw();
    }

    protected void renderBgPlayerInventory(float f, int mouseX, int mouseY) {
        // Render player inventory
        drawTexturedModalRect(
            guiLeft + (getGridXSize() / 2) - (9 * GuiHelpers.SLOT_SIZE / 2) + getPlayerInventoryOffsetX() + 3,
            guiTop + 52 + getGridYSize() + getPlayerInventoryOffsetY(),
            34,
            24,
            216,
            93);

        // Auxiliary slots
        drawTexturedModalRect(
            guiLeft + (getGridXSize() / 2) + (9 * GuiHelpers.SLOT_SIZE / 2) + getPlayerInventoryOffsetX() + 57,
            guiTop + 61 + getGridYSize() + getPlayerInventoryOffsetY(),
            0,
            12,
            20,
            57);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawTabsForeground(mouseX, mouseY);
        drawTabContents(
            getContainer().getSelectedTab(),
            getContainer().getSelectedChannel(),
            DrawLayer.FOREGROUND,
            0,
            getSlotsOffsetX(),
            getSlotsOffsetY(),
            mouseX,
            mouseY);
        this.zLevel = 150.0F;
        RenderItemExtendedSlotCount.getInstance().zLevel = 150.0F;
        drawActiveStorageSlotItem(mouseX, mouseY);
        RenderItemExtendedSlotCount.getInstance().zLevel = 0F;
        this.zLevel = 0.0F;

        // Draw button tooltips
        Optional<ITerminalStorageTabClient<?>> tabOptional = getSelectedClientTab();
        tabOptional.ifPresent(tab -> {
            int offset = 0;
            int gridXSize = getGridXSize();
            int gridYSize = getGridYSize();
            int playerInventoryOffsetX = getPlayerInventoryOffsetX();
            int playerInventoryOffsetY = getPlayerInventoryOffsetY();
            ITerminalStorageTabCommon.SlotPositionFactors factors = new ITerminalStorageTabCommon.SlotPositionFactors(
                offsetX,
                offsetY,
                gridXSize,
                gridYSize,
                playerInventoryOffsetX,
                playerInventoryOffsetY);
            for (ITerminalButton button : tab.getButtons()) {
                GuiButton guiButton = button.createButton(
                    button.getX(
                        guiLeft,
                        BUTTONS_OFFSET_X,
                        gridXSize,
                        gridYSize,
                        playerInventoryOffsetX,
                        playerInventoryOffsetY),
                    button.getY(
                        guiTop,
                        BUTTONS_OFFSET_Y + offset,
                        gridXSize,
                        gridYSize,
                        playerInventoryOffsetX,
                        playerInventoryOffsetY));
                if (func_146978_c(
                    button.getX(
                        0,
                        BUTTONS_OFFSET_X,
                        gridXSize,
                        gridYSize,
                        playerInventoryOffsetX,
                        playerInventoryOffsetY),
                    button.getY(
                        0,
                        BUTTONS_OFFSET_Y + offset,
                        gridXSize,
                        gridYSize,
                        playerInventoryOffsetX,
                        playerInventoryOffsetY),
                    guiButton.width,
                    guiButton.height,
                    mouseX,
                    mouseY)) {
                    List<String> lines = Lists.newArrayList();
                    lines.add(LangHelpers.localize(button.getTranslationKey()));
                    button.getTooltip(mc.thePlayer, false, lines);
                    drawTooltip(lines, mouseX - guiLeft, mouseY - guiTop);
                }
                if (button.isInLeftColumn()) {
                    offset += BUTTONS_OFFSET + guiButton.height;
                }
            }

            String tabName = getContainer().getSelectedTab();
            Optional<ITerminalStorageTabCommon> tabCommonOptional = getCommonTab(tabName);
            tabCommonOptional.ifPresent(tabCommon -> {
                for (Pair<Slot, ITerminalStorageTabCommon.ISlotPositionCallback> slot : getContainer()
                    .getTabSlots(tabName)) {
                    Pair<Integer, Integer> slotPos = slot.getRight()
                        .getSlotPosition(factors);
                    tab.onCommonSlotRender(
                        this,
                        DrawLayer.FOREGROUND,
                        0,
                        guiLeft + slotPos.getLeft(),
                        guiTop + slotPos.getRight(),
                        mouseX,
                        mouseY,
                        slot.getLeft()
                            .getSlotIndex(),
                        tabCommon);
                }
            });
        });

        // Draw save defaults button
        if (buttonSetDefaults != null && buttonSetDefaults.visible) {
            int buttonX = buttonSetDefaults.xPosition - guiLeft;
            int buttonY = buttonSetDefaults.yPosition - guiTop;

            if (func_146978_c(buttonX, buttonY, buttonSetDefaults.width, buttonSetDefaults.height, mouseX, mouseY)) {
                List<String> lines = Lists.newArrayList();
                lines.add(LangHelpers.localize("gui.integratedterminals.terminal_storage.setdefaults"));
                lines.add(
                    EnumChatFormatting.GRAY
                        + LangHelpers.localize("gui.integratedterminals.terminal_storage.setdefaults.info"));

                drawTooltip(lines, mouseX - guiLeft, mouseY - guiTop);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        scrollBar.drawScreen(mouseX, mouseY, partialTicks);

        ResourceLocation oldTexture = this.texture;
        getSelectedClientTab().ifPresent(tab -> {
            ResourceLocation texture = tab.getBackgroundTexture();
            if (texture != null) {
                this.texture = texture;
            }
        });

        super.drawScreen(mouseX, mouseY, partialTicks);

        // Draw slots
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        this.zLevel = 0F;
        for (int i1 = 0; i1 < this.inventorySlots.inventorySlots.size(); ++i1) {
            Slot slot = this.inventorySlots.inventorySlots.get(i1);

            if (slot.func_111238_b()) {
                this.drawSlotOverlay(slot);
            }
        }
        this.zLevel = 0F;

        this.texture = oldTexture;
    }

    private void drawSlotOverlay(Slot slot) {
        getSelectedClientTab().ifPresent(tab -> {
            if (this.terminalDragSplitting && this.terminalDragSplittingSlots.contains(slot)) {
                if (tab.isSlotValidForDraggingInto(getContainer().getSelectedChannel(), slot)) {
                    if (this.terminalDragSplittingSlots.size() == 1) {
                        return;
                    }

                    int dragQuantity = tab.computeDraggingQuantity(
                        this.terminalDragSplittingSlots,
                        this.terminalDragMode,
                        slot.getStack(),
                        tab.getActiveSlotQuantity());
                    if (dragQuantity > 0) {
                        String dragString = "+" + GuiHelpers.quantityToScaledString(dragQuantity);
                        RenderHelpers.drawScaledString(
                            fontRendererObj,
                            dragString,
                            guiLeft + slot.xDisplayPosition,
                            guiTop + slot.yDisplayPosition,
                            0.5F,
                            16777045,
                            true);
                    }
                } else {
                    this.terminalDragSplittingSlots.remove(slot);
                    this.updateTerminalDragSplitting(tab);
                }
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public C getContainer() {
        return (C) super.getContainer();
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        scrollBar.handleMouseInput();
    }

    protected Optional<ITerminalStorageTabClient<?>> getTabByIndex(int tabIndex) {
        Collection<ITerminalStorageTabClient<?>> tabsClientList = getContainer().getTabsClient()
            .values();
        if (tabIndex >= 0 && tabIndex < tabsClientList.size()) {
            return Optional.of(Iterables.get(tabsClientList, tabIndex));
        }
        return Optional.empty();
    }

    protected void setTabByIndex(int tabIndex) {
        // Save tab index
        getTabByIndex(tabIndex).ifPresent(tab -> {
            getContainer().setSelectedTab(
                tab.getName()
                    .toString());

            // Reset active slot
            tab.resetActiveSlot();

            // Update the filter
            fieldSearch.setText(tab.getInstanceFilter(getContainer().getSelectedChannel()));
        });

        // Reset scrollbar
        scrollBar.scrollTo(0);

        // Re-init screen, as scale might be different in the new tab
        initGui();
    }

    protected void playButtonClickSound() {
        this.mc.getSoundHandler()
            .playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        Optional<ITerminalStorageTabClient<?>> tabOptional = getSelectedClientTab();
        this.clicked = true;

        // Select a tab
        if (mouseButton == 0 && mouseY < guiTop + TAB_UNSELECTED_HEIGHT
            && mouseX > guiLeft + TAB_OFFSET_X
            && mouseX <= guiLeft + TAB_OFFSET_X + (TAB_WIDTH * getContainer().getTabsClientCount() - 1)) {
            // Save tab index
            setTabByIndex((mouseX - TAB_OFFSET_X - guiLeft) / TAB_WIDTH);
            playButtonClickSound();
            return;
        }

        // Update channel when changing channel field
        this.fieldChannel.mouseClicked(mouseX, mouseY, mouseButton);
        if (func_146978_c(
            this.fieldChannel.xPosition - guiLeft,
            this.fieldChannel.yPosition - guiTop,
            this.fieldChannel.width,
            this.fieldChannel.height,
            mouseX,
            mouseY)) {
            int channel;
            try {
                channel = Integer.parseInt(this.fieldChannel.getText());
            } catch (NumberFormatException e) {
                channel = -1;
            }
            final int finalChannel = channel;
            getContainer().setSelectedChannel(channel);
            scrollBar.scrollTo(0); // Reset scrollbar

            // Update the filter
            tabOptional.ifPresent(tab -> fieldSearch.setText(tab.getInstanceFilter(finalChannel)));

            playButtonClickSound();
            return;
        }

        if (tabOptional.isPresent()) {
            ITerminalStorageTabClient<?> tab = tabOptional.get();
            Slot playerSlot = getSlotUnderMouse();

            // Check if pick block key was clicked
            boolean isPickBlock = this.mc.gameSettings.keyBindPickBlock.getKeyCode() == (mouseButton - 100);

            // Start dragging over container slots when a storage slot is selected
            if (tab.getActiveSlotId() >= 0 && (mouseButton == 0 || mouseButton == 1 || isPickBlock)) {
                if (playerSlot != null && !this.terminalDragSplitting) {
                    this.terminalDragSplitting = true;
                    this.terminalDragSplittingButton = mouseButton;
                    this.terminalDragSplittingSlots.clear();

                    if (mouseButton == 0) {
                        this.terminalDragMode = 0;
                    } else if (mouseButton == 1) {
                        this.terminalDragMode = 1;
                    } else if (isPickBlock) {
                        this.terminalDragMode = 2;
                    }
                    return;
                }
            }
            if (MinecraftHelpers.isShifted() && playerSlot != null && tab.isQuickMovePrevented(playerSlot)) {
                return;
            }
        } else if (getSlotUnderMouse() != null) {
            // Don't allow shift clicking items into container when no tab has been selected
            return;
        }

        // Click in search field
        fieldSearch.mouseClicked(mouseX, mouseY, mouseButton);

        // Handle buttons clicks
        tabOptional.ifPresent(tab -> {
            int offset = 0;
            ITerminalStorageTabCommon tabCommon = getContainer().getTabCommon(
                tab.getName()
                    .toString());
            int gridXSize = getGridXSize();
            int gridYSize = getGridYSize();
            int playerInventoryOffsetX = getPlayerInventoryOffsetX();
            int playerInventoryOffsetY = getPlayerInventoryOffsetY();
            for (ITerminalButton button : tab.getButtons()) {
                GuiButton guiButton = button.createButton(
                    button.getX(
                        guiLeft,
                        BUTTONS_OFFSET_X,
                        gridXSize,
                        gridYSize,
                        playerInventoryOffsetX,
                        playerInventoryOffsetY),
                    button.getY(
                        guiTop,
                        BUTTONS_OFFSET_Y + offset,
                        gridXSize,
                        gridYSize,
                        playerInventoryOffsetX,
                        playerInventoryOffsetY));
                if (func_146978_c(
                    button.getX(
                        0,
                        BUTTONS_OFFSET_X,
                        gridXSize,
                        gridYSize,
                        playerInventoryOffsetX,
                        playerInventoryOffsetY),
                    button.getY(
                        0,
                        BUTTONS_OFFSET_Y + offset,
                        gridXSize,
                        gridYSize,
                        playerInventoryOffsetX,
                        playerInventoryOffsetY),
                    guiButton.width,
                    guiButton.height,
                    mouseX,
                    mouseY)) {
                    button.onClick(tab, tabCommon, guiButton, getContainer().getSelectedChannel(), mouseButton);
                    playButtonClickSound();
                    this.clicked = false; // To avoid grid slots being selected on mouse release
                    return;
                }
                if (button.isInLeftColumn()) {
                    offset += BUTTONS_OFFSET + guiButton.height;
                }
            }
        });

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Nullable
    public Slot getSlotUnderMouse() {
        Slot slot = GuiHelpers.getSlotUnderMouse(this);
        // Safety for hacky disabled slots
        if (slot != null && slot.xDisplayPosition < 0) {
            return null;
        }
        return slot;
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        getSelectedClientTab().ifPresent(tab -> {
            if (this.terminalDragSplitting & tab.getActiveSlotId() >= 0) {
                Slot slot = this.getSlotUnderMouse();
                if (slot != null
                    && (tab.getActiveSlotQuantity() > this.terminalDragSplittingSlots.size()
                        || this.terminalDragMode == 2)
                    && tab.isSlotValidForDraggingInto(getContainer().getSelectedChannel(), slot)) {
                    this.terminalDragSplittingSlots.add(slot);
                    this.updateTerminalDragSplitting(tab);
                    return;
                }
            }
        });

        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    private void updateTerminalDragSplitting(ITerminalStorageTabClient<?> tab) {
        if (this.terminalDragSplitting) {
            int quantityTotal = tab.getActiveSlotQuantity();
            this.terminalDragSplittingRemnant = tab.getActiveSlotQuantity();

            for (Slot slot : this.terminalDragSplittingSlots) {
                if (tab.isSlotValidForDraggingInto(getContainer().getSelectedChannel(), slot)) {
                    int dragQuantity = tab.computeDraggingQuantity(
                        this.terminalDragSplittingSlots,
                        this.terminalDragMode,
                        slot.getStack(),
                        quantityTotal);
                    this.terminalDragSplittingRemnant -= tab
                        .dragIntoSlot(container, getContainer().getSelectedChannel(), slot, dragQuantity, true);
                }
            }
        }
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int mouseButton) {
        // Validate dragging process
        if (this.terminalDragSplitting
            && (this.terminalDragSplittingSlots.size() <= 1 || this.terminalDragSplittingButton != mouseButton)) {
            this.terminalDragSplitting = false;
            this.terminalDragSplittingSlots.clear();
            if (this.terminalDragSplittingButton != mouseButton) {
                return;
            }
        }

        // Handle dragging ends
        boolean dragged = false;
        if (this.terminalDragSplitting) {
            dragged = true;
            // If we were dragging, distribute the dragging instance over the dragged slots.
            getSelectedClientTab().ifPresent(tab -> {
                if (tab.getActiveSlotQuantity() > 0) {
                    int quantityTotal = tab.getActiveSlotQuantity();
                    int quantity = quantityTotal;
                    for (Slot slot : this.terminalDragSplittingSlots) {
                        if (tab.isSlotValidForDraggingInto(getContainer().getSelectedChannel(), slot)) {
                            int dragQuantity = tab.computeDraggingQuantity(
                                this.terminalDragSplittingSlots,
                                this.terminalDragMode,
                                slot.getStack(),
                                quantityTotal);
                            quantity -= tab.dragIntoSlot(
                                container,
                                getContainer().getSelectedChannel(),
                                slot,
                                dragQuantity,
                                false);
                        }
                    }
                    tab.setActiveSlotQuantity(quantity);
                }
            });
        }

        // Reset dragging state
        this.terminalDragSplitting = false;
        this.terminalDragSplittingSlots.clear();
        this.terminalDragSplittingButton = -1;
        this.terminalDragMode = -1;
        this.terminalDragSplittingRemnant = 0;

        // Handle plain clicks
        if (!dragged && this.clicked) {
            this.clicked = false;
            Optional<ITerminalStorageTabClient<?>> tabOptional = getSelectedClientTab();
            if (tabOptional.isPresent()) {
                int slot = getStorageSlotIndexAtPosition(mouseX, mouseY);
                Slot playerSlot = getSlotUnderMouse();

                // Handle clicks on storage slots
                boolean hasClickedOutside = this.hasClickedOutside(mouseX, mouseY, this.guiLeft, this.guiTop);
                boolean hasClickedInStorage = this.hasClickedInStorage(mouseX, mouseY);
                if (tabOptional.get()
                    .handleClick(
                        getContainer(),
                        getContainer().getSelectedChannel(),
                        slot,
                        mouseButton,
                        hasClickedOutside,
                        hasClickedInStorage,
                        playerSlot != null ? playerSlot.slotNumber : -1,
                        false)) {
                    return;
                }
            }
        }

        super.mouseMovedOrUp(mouseX, mouseY, mouseButton);
    }

    protected boolean handleKeyCodeFirst(int keyCode) {
        if (ruiseki.integrateddynamics.proxy.ClientProxy.FOCUS_LP_SEARCH.isActiveAndMatches(keyCode)) {
            fieldSearch.setFocused(true);
            swallowNextCharacter = true;
            return true;
        } else if (ClientProxy.TERMINAL_TAB_NEXT.isActiveAndMatches(keyCode)) {
            if (getContainer().getTabsClientCount() > 0) {
                // Go to next tab
                setTabByIndex((getSelectedClientTabIndex() + 1) % getContainer().getTabsClientCount());
                playButtonClickSound();
                return true;
            }
        } else if (ClientProxy.TERMINAL_TAB_PREVIOUS.isActiveAndMatches(keyCode)) {
            if (getContainer().getTabsClientCount() > 0) {
                // Go to previous tab
                setTabByIndex(
                    (getContainer().getTabsClientCount() + getSelectedClientTabIndex() - 1)
                        % getContainer().getTabsClientCount());
                playButtonClickSound();
                return true;
            }
        }
        return false;
    }

    protected boolean handleKeyCodeLast(int keyCode) {
        if (ClientProxy.TERMINAL_CRAFTINGGRID_CLEARPLAYER.isActiveAndMatches(keyCode)) {
            clearCraftingGrid(false);
            playButtonClickSound();
            return true;
        } else if (ClientProxy.TERMINAL_CRAFTINGGRID_CLEARSTORAGE.isActiveAndMatches(keyCode)) {
            clearCraftingGrid(true);
            playButtonClickSound();
            return true;
        } else if (ClientProxy.TERMINAL_CRAFTINGGRID_BALANCE.isActiveAndMatches(keyCode)) {
            balanceCraftingGrid();
            playButtonClickSound();
            return true;
        }
        return false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (handleKeyCodeFirst(keyCode)) {
            return;
        }
        if (fieldSearch.isFocused()) {
            if (fieldSearch.textboxKeyTyped(typedChar, keyCode)) {
                getSelectedClientTab().ifPresent(
                    tab -> tab.setInstanceFilter(getContainer().getSelectedChannel(), fieldSearch.getText()));
                return;
            }
        }
        if (handleKeyCodeLast(keyCode)) return;
        super.keyTyped(typedChar, keyCode);
    }

    protected void clearCraftingGrid(boolean toStorage) {
        ITerminalStorageTabCommon commonTab = getContainer().getTabCommon(getContainer().getSelectedTab());
        if (commonTab instanceof TerminalStorageTabIngredientComponentItemStackCraftingCommon) {
            TerminalButtonItemStackCraftingGridClear.clearGrid(
                (TerminalStorageTabIngredientComponentItemStackCraftingCommon) commonTab,
                getContainer().getSelectedChannel(),
                toStorage);
        }
    }

    protected void balanceCraftingGrid() {
        ITerminalStorageTabCommon commonTab = getContainer().getTabCommon(getContainer().getSelectedTab());
        if (commonTab instanceof TerminalStorageTabIngredientComponentItemStackCraftingCommon) {
            IntegratedTerminals._instance.getPacketHandler()
                .sendToServer(
                    new TerminalStorageIngredientItemStackCraftingGridBalance(
                        commonTab.getName()
                            .toString()));
        }
    }

    private boolean hasClickedInStorage(int mouseX, int mouseY) {
        return mouseX >= getGuiLeftTotal() + getSlotsOffsetX()
            && mouseX < getGuiLeftTotal() + getSlotsOffsetX() + getSlotRowLength() * GuiHelpers.SLOT_SIZE - 1
            && mouseY >= getGuiTopTotal() + getSlotsOffsetY()
            && mouseY < getGuiTopTotal() + getSlotsOffsetY() + getSlotVisibleRows() * GuiHelpers.SLOT_SIZE;
    }

    public int getStorageSlotIndexAtPosition(int mouseX, int mouseY) {
        if (hasClickedInStorage(mouseX, mouseY)) {
            if ((mouseX - getGuiLeftTotal() - getSlotsOffsetX()) % GuiHelpers.SLOT_SIZE < GuiHelpers.SLOT_SIZE_INNER
                && (mouseY - getGuiTopTotal() - getSlotsOffsetY()) % GuiHelpers.SLOT_SIZE
                    < GuiHelpers.SLOT_SIZE_INNER) {
                int rowLength = getSlotRowLength();
                int offset = getSelectedFirstRow() * rowLength;
                return offset + ((((int) mouseX) - getGuiLeftTotal() - getSlotsOffsetX()) / GuiHelpers.SLOT_SIZE)
                    + ((((int) mouseY) - getGuiTopTotal() - getSlotsOffsetY()) / GuiHelpers.SLOT_SIZE)
                        * getSlotRowLength();
            }
        }

        return -1;
    }

    protected void drawTabsBackground() {
        int offsetX = TAB_OFFSET_X;

        // Draw channels label
        drawString(
            fontRendererObj,
            LangHelpers.localize("gui.integratedterminals.terminal_storage.channel"),
            guiLeft + 30,
            guiTop + 26,
            16777215);

        // Draw all tabs next to each other horizontally
        for (ITerminalStorageTabClient tab : getContainer().getTabsClient()
            .values()) {
            boolean selected = tab.getName()
                .toString()
                .equals(getContainer().getSelectedTab());
            int x = guiLeft + offsetX;
            int y = guiTop;
            int width = TAB_WIDTH;
            int height = selected ? TAB_SELECTED_HEIGHT : TAB_UNSELECTED_HEIGHT;
            int textureX = selected ? TAB_SELECTED_TEXTURE_X : TAB_UNSELECTED_TEXTURE_X;
            int textureY = selected ? TAB_SELECTED_TEXTURE_Y : TAB_UNSELECTED_TEXTURE_Y;

            // Draw background
            this.mc.renderEngine.bindTexture(this.texture);
            this.drawTexturedModalRect(x, y, textureX, textureY, width, height);

            // Draw icon
            ItemStack icon = tab.getIcon();
            RenderItem renderItem = RenderItem.getInstance();
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableRescaleNormal();
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            renderItem.renderItemAndEffectIntoGUI(
                Minecraft.getMinecraft().fontRenderer,
                Minecraft.getMinecraft()
                    .getTextureManager(),
                icon,
                x + TAB_ICON_OFFSET,
                y + TAB_ICON_OFFSET);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();
            GL11.glDisable(GL11.GL_DEPTH_TEST);

            offsetX += width;
        }
    }

    protected int getSlotsOffsetX() {
        return getSelectedClientTab().map(ITerminalStorageTabClient::getSlotOffsetX)
            .orElse(ITerminalStorageTabClient.DEFAULT_SLOT_OFFSET_X);
    }

    protected int getSlotsOffsetY() {
        return getSelectedClientTab().map(ITerminalStorageTabClient::getSlotOffsetY)
            .orElse(ITerminalStorageTabClient.DEFAULT_SLOT_OFFSET_Y);
    }

    protected int getSlotVisibleRows() {
        return getSelectedClientTab().map(ITerminalStorageTabClient::getSlotVisibleRows)
            .orElse(ITerminalStorageTabClient.DEFAULT_SLOT_VISIBLE_ROWS);
    }

    protected int getSlotRowLength() {
        return getSelectedClientTab().map(ITerminalStorageTabClient::getSlotRowLength)
            .orElse(ITerminalStorageTabClient.DEFAULT_SLOT_ROW_LENGTH);
    }

    protected int getSelectedFirstRow() {
        return firstRow;
    }

    protected void drawTabContents(String tabId, int channel, DrawLayer layer, float partialTick, int x, int y,
        int mouseX, int mouseY) {
        Optional<ITerminalStorageTabClient<?>> optionalTab = getClientTab(tabId);
        if (optionalTab.isPresent()) {
            ITerminalStorageTabClient<?> tab = optionalTab.get();
            if (layer == DrawLayer.BACKGROUND) {
                drawCenteredString(
                    fontRendererObj,
                    tab.getStatus(channel),
                    guiLeft + ITerminalStorageTabClient.DEFAULT_SLOT_OFFSET_X
                        + (GuiHelpers.SLOT_SIZE * tab.getRowColumnProvider()
                            .getRowsAndColumns()
                            .columns()) / 2,
                    y + 2 + getSlotVisibleRows() * GuiHelpers.SLOT_SIZE,
                    16777215);
                GlStateManager.color(1, 1, 1, 1);
            }

            // Draw slots
            int rowLength = getSlotRowLength();
            int limit = getSlotVisibleRows() * rowLength;
            int offset = getSelectedFirstRow() * rowLength;
            List<ITerminalStorageSlot> slots = (List<ITerminalStorageSlot>) tab.getSlots(channel, offset, limit);
            int slotX = x;
            int slotY = y;
            int slotI = 0;
            for (ITerminalStorageSlot slot : slots) {
                if (layer == DrawLayer.BACKGROUND) {
                    RenderHelpers.bindTexture(this.texture);
                    if (RenderHelpers.isPointInRegion(
                        slotX,
                        slotY,
                        GuiHelpers.SLOT_SIZE_INNER,
                        GuiHelpers.SLOT_SIZE_INNER,
                        mouseX,
                        mouseY)) {
                        drawRect(
                            slotX,
                            slotY,
                            slotX + GuiHelpers.SLOT_SIZE_INNER,
                            slotY + GuiHelpers.SLOT_SIZE_INNER,
                            -2130706433);
                    }
                }

                this.zLevel = 200F;
                RenderHelper.enableGUIStandardItemLighting();

                slot.drawGuiContainerLayer(this, layer, partialTick, slotX, slotY, mouseX, mouseY, tab, channel, null);

                GlStateManager.disableLighting();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableTexture2D();
                this.zLevel = 0F;

                if (++slotI >= rowLength) {
                    slotX = x;
                    slotY += GuiHelpers.SLOT_SIZE;
                    slotI = 0;
                } else {
                    slotX += GuiHelpers.SLOT_SIZE;
                }
            }
        } else {
            GlStateManager.color(0.3F, 0.3F, 0.3F, 0.3F);
            drawRect(
                x - 1,
                y - 1,
                x - 1 + GuiHelpers.SLOT_SIZE * getSlotRowLength(),
                y - 1 + GuiHelpers.SLOT_SIZE * getSlotVisibleRows(),
                Helpers.RGBAToInt(50, 50, 50, 100));
            GlStateManager.color(1, 1, 1, 1);
        }
    }

    private void drawActiveStorageSlotItem(int mouseX, int mouseY) {
        Optional<ITerminalStorageTabClient<?>> optionalTab = getSelectedClientTab();
        optionalTab.ifPresent(tab -> {
            int slotId = tab.getActiveSlotId();
            if (slotId >= 0) {
                int quantity = tab.getActiveSlotQuantity();
                List<?> slots = tab.getSlots(getContainer().getSelectedChannel(), slotId, 1);
                if (!slots.isEmpty()) {
                    ITerminalStorageSlot slot = (ITerminalStorageSlot) slots.get(0);
                    RenderHelpers.bindTexture(this.texture);
                    GlStateManager.color(1, 1, 1, 1);

                    if (this.terminalDragSplitting && this.terminalDragSplittingSlots.size() > 1) {
                        quantity = this.terminalDragSplittingRemnant;
                    }

                    String quantityString = GuiHelpers.quantityToScaledString(quantity);
                    if (quantity == 0) {
                        quantityString = EnumChatFormatting.YELLOW + quantityString;
                    }

                    this.zLevel = 300F;
                    RenderHelper.enableGUIStandardItemLighting();

                    slot.drawGuiContainerLayer(
                        this,
                        DrawLayer.BACKGROUND,
                        0,
                        mouseX - this.guiLeft - GuiHelpers.SLOT_SIZE_INNER / 4,
                        mouseY - this.guiTop - GuiHelpers.SLOT_SIZE_INNER / 4,
                        mouseX,
                        mouseY,
                        tab,
                        getContainer().getSelectedChannel(),
                        quantityString);

                    RenderHelper.disableStandardItemLighting();
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.enableTexture2D();
                    this.zLevel = 0F;
                }
            }
        });
    }

    protected Optional<ITerminalStorageTabClient<?>> getClientTab(String tab) {
        return Optional.ofNullable(
            getContainer().getTabsClient()
                .get(tab));
    }

    protected Optional<ITerminalStorageTabCommon> getCommonTab(String tab) {
        return Optional.ofNullable(
            getContainer().getTabsCommon()
                .get(tab));
    }

    public Optional<ITerminalStorageTabClient<?>> getSelectedClientTab() {
        return getClientTab(getContainer().getSelectedTab());
    }

    protected int getSelectedClientTabIndex() {
        Optional<ITerminalStorageTabClient<?>> selectedTab = getSelectedClientTab();
        if (selectedTab.isPresent()) {
            int tabIndex = 0;
            for (ITerminalStorageTabClient<?> tabClient : getContainer().getTabsClient()
                .values()) {
                if (tabClient == selectedTab.get()) {
                    return tabIndex;
                }
                tabIndex++;
            }
        }
        return -1;
    }

    protected void drawTabsForeground(int mouseX, int mouseY) {
        if (mouseY < guiTop + TAB_UNSELECTED_HEIGHT && mouseX > guiLeft + TAB_OFFSET_X
            && mouseX <= guiLeft + TAB_OFFSET_X + (TAB_WIDTH * getContainer().getTabsClientCount() - 1)) {
            int tabIndex = (mouseX - TAB_OFFSET_X - guiLeft) / TAB_WIDTH;
            getTabByIndex(tabIndex)
                .ifPresent(tab -> this.drawTooltip(tab.getTooltip(), mouseX - guiLeft, mouseY - guiTop));
        }
    }

    public GuiTextFieldExtended getFieldSearch() {
        return fieldSearch;
    }

    /**
     * The layer to draw on.
     */
    public static enum DrawLayer {
        BACKGROUND,
        FOREGROUND
    }
}
