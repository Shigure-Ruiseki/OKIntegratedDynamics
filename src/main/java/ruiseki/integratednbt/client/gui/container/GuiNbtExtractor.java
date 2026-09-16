package ruiseki.integratednbt.client.gui.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import ruiseki.integratednbt.IntegratedNbt;
import ruiseki.integratednbt.client.gui.component.HoverTextImageButton;
import ruiseki.integratednbt.client.gui.component.NbtTreeViewer;
import ruiseki.integratednbt.client.gui.component.Texture;
import ruiseki.integratednbt.client.gui.component.TexturePart;
import ruiseki.integratednbt.evaluate.NbtExtractorOutputMode;
import ruiseki.integratednbt.evaluate.nbt.path.SegmentedNbtPath;
import ruiseki.integratednbt.inventory.container.ContainerNbtExtractor;
import ruiseki.integratednbt.network.packet.NbtExtractorSetExtractionPathPacket;
import ruiseki.integratednbt.network.packet.NbtExtractorSetOutputModePacket;
import ruiseki.integratednbt.network.packet.NbtExtractorUpdateAutoRefreshPacket;
import ruiseki.integratednbt.network.packet.UpdateClientNbtExtractorPacket;
import ruiseki.integratednbt.tileentity.TileNbtExtractor;
import ruiseki.okcore.client.gui.component.button.GuiButtonExtended;
import ruiseki.okcore.client.gui.container.GuiContainerExtended;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.helper.LangHelpers;

public class GuiNbtExtractor extends GuiContainerExtended<ContainerNbtExtractor> {

    public static final int SCREEN_EDGE = 4;
    public static final Texture GUI_TEXTURE = new Texture("integratednbt", "textures/gui/nbt_extractor.png");

    // Different parts of the texture; See texture file for definitions
    private static final TexturePart PART0 = GUI_TEXTURE.createPart(0, 0, 8, 24);
    private static final TexturePart PART1 = GUI_TEXTURE.createPart(12, 0, 4, 24);
    private static final TexturePart PART2 = GUI_TEXTURE.createPart(20, 0, 8, 24);
    private static final TexturePart PART3 = GUI_TEXTURE.createPart(0, 28, 8, 4);
    private static final TexturePart PART4 = GUI_TEXTURE.createPart(12, 28, 4, 4);
    private static final TexturePart PART5 = GUI_TEXTURE.createPart(20, 28, 8, 4);
    private static final TexturePart PART6 = GUI_TEXTURE.createPart(0, 36, 8, 8);
    private static final TexturePart PART7 = GUI_TEXTURE.createPart(12, 36, 4, 8);
    private static final TexturePart PART8 = GUI_TEXTURE.createPart(20, 36, 178, 110);
    private static final TexturePart PART9 = GUI_TEXTURE.createPart(202, 36, 8, 8);
    private static final int BUTTON_SIZE = 12;
    private static final TexturePart BUTTON_UNKNOWN = GUI_TEXTURE.createPart(78, 0, BUTTON_SIZE, BUTTON_SIZE);
    private static final TexturePart BUTTON_UNKNOWN_HOVER = GUI_TEXTURE.createPart(78, 12, BUTTON_SIZE, BUTTON_SIZE);
    private static final TexturePart BUTTON_REFRESH_ON = GUI_TEXTURE.createPart(126, 0, BUTTON_SIZE, BUTTON_SIZE);
    private static final TexturePart BUTTON_REFRESH_ON_HOVER = GUI_TEXTURE
        .createPart(126, 12, BUTTON_SIZE, BUTTON_SIZE);
    private static final TexturePart BUTTON_REFRESH_OFF = GUI_TEXTURE.createPart(138, 0, BUTTON_SIZE, BUTTON_SIZE);
    private static final TexturePart BUTTON_REFRESH_OFF_HOVER = GUI_TEXTURE
        .createPart(138, 12, BUTTON_SIZE, BUTTON_SIZE);
    private static final int BASE_PADDING = 200;
    private static final int INVENTORY_WIDTH = 178;
    private static final int INVENTORY_HEIGHT = 110;
    private static final int TOP_BORDER_SIZE = 24;
    private static final int SIDE_BORDER_SIZE = 8;
    private static final double CENTERED_TEXT_MAX_RATIO = 0.8;
    private static final int BUTTON_SPACING = 2;

    // These are static because GUI sometimes opens after receiving the update packets.
    private static GuiNbtExtractor lastInstance = null;
    // Null signify that the first update packet has not arrived yet.
    private static UpdateClientNbtExtractorPacket.ErrorCode errorCode = null;
    private static NBTBase nbt;
    private static SegmentedNbtPath extractionPath = null;
    private static NbtExtractorOutputMode outputMode = null;
    private static LangHelpers.UnlocalizedString errorMessage = null;
    private static Boolean autoRefresh = null;

    private NbtTreeViewer treeViewer;
    private ContainerNbtExtractor nbtExtractorContainer;
    private FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    /**
     * Padding outside the GUI; Responsive; Updated by updateCalculations
     */
    private int padding;
    /**
     * The width of NBT screen; Responsive; Updated by updateCalculations
     */
    private int screenWidth;
    /**
     * The height of NBT screen; Responsive; Updated by updateCalculations
     */
    private int screenHeight;
    /**
     * The scale factor of Minecraft; Updated by updateCalculations
     */
    private double scaleFactor;
    private HoverTextImageButton outputModeButton;
    private HoverTextImageButton autoRefreshButton;

    public GuiNbtExtractor(ContainerNbtExtractor screenContainer) {
        super(screenContainer);
        GuiNbtExtractor.lastInstance = this;
        this.nbtExtractorContainer = screenContainer;
        TileNbtExtractor tileEntity = this.nbtExtractorContainer.getTile();
        this.treeViewer = new NbtTreeViewer(this, tileEntity.getExpandedPaths(), tileEntity.getScrollTop()) {

            @Override
            public void onUpdateSelectedPath(SegmentedNbtPath newPath, NBTBase nbt) {
                IntegratedNbt._instance.getPacketHandler()
                    .sendToServer(
                        new NbtExtractorSetExtractionPathPacket(
                            GuiNbtExtractor.this.nbtExtractorContainer.getTile()
                                .getPos(),
                            newPath,
                            nbt != null ? nbt.getId() : 0));
            }

            @Override
            public SegmentedNbtPath getSelectedPath() {
                return extractionPath;
            }
        };
    }

    @Override
    protected ResourceLocation constructGuiTexture() {
        return null;
    }

    public static void updateError(UpdateClientNbtExtractorPacket.ErrorCode errorCode) {
        GuiNbtExtractor.errorCode = errorCode;
    }

    public static void updateNBT(NBTBase nbt) {
        GuiNbtExtractor.nbt = nbt;
    }

    public static void updateExtractionPath(SegmentedNbtPath extractionPath) {
        GuiNbtExtractor.extractionPath = extractionPath;
    }

    public static void updateOutputMode(NbtExtractorOutputMode outputMode) {
        GuiNbtExtractor.outputMode = outputMode;
        if (lastInstance != null) {
            lastInstance.updateOutputModeButton();
        }
    }

    private void updateOutputModeButton() {
        if (this.outputModeButton == null) {
            return;
        }
        ArrayList<String> messages = new ArrayList<>();
        if (outputMode == null) {
            this.outputModeButton.setTexture(BUTTON_UNKNOWN, BUTTON_UNKNOWN_HOVER);
            messages.add(
                LangHelpers.localize(
                    "integratednbt:nbt_extractor.output_mode",
                    LangHelpers.localize("integratednbt:nbt_extractor.loading")));
        } else {
            this.outputModeButton.setTexture(outputMode.getButtonTextureNormal(), outputMode.getButtonTextureHover());
            messages.add(LangHelpers.localize("integratednbt:nbt_extractor.output_mode", outputMode.getName()));
        }
        messages.add(
            EnumChatFormatting.GRAY
                + LangHelpers.localize("integratednbt:nbt_extractor.output_mode.description.begin"));
        messages.add(" ");
        Arrays.stream(NbtExtractorOutputMode.values())
            .forEach(
                describingOutputMode -> messages
                    .add(describingOutputMode.getDescription(describingOutputMode.equals(outputMode))));
        messages.add(" ");
        messages.add(
            EnumChatFormatting.GRAY + LangHelpers.localize(
                "integratednbt:nbt_extractor.output_mode.description.end",
                NbtExtractorOutputMode.REFERENCE.getName()));
        this.outputModeButton.setHoverText(messages);
    }

    public static void updateErrorMessage(LangHelpers.UnlocalizedString errorMessage) {
        GuiNbtExtractor.errorMessage = errorMessage;
    }

    public static void updateAutoRefresh(Boolean autoRefresh) {
        GuiNbtExtractor.autoRefresh = autoRefresh;
        if (lastInstance != null) {
            lastInstance.updateAutoRefreshButton();
        }
    }

    private void updateAutoRefreshButton() {
        if (this.autoRefreshButton == null) {
            return;
        }
        ArrayList<String> messages = new ArrayList<>();
        if (autoRefresh == null) {
            this.autoRefreshButton.setTexture(BUTTON_UNKNOWN, BUTTON_UNKNOWN_HOVER);
            messages.add(
                LangHelpers.localize(
                    "integratednbt:nbt_extractor.auto_refresh",
                    LangHelpers.localize("integratednbt:nbt_extractor.loading")));
        } else if (autoRefresh) {
            this.autoRefreshButton.setTexture(BUTTON_REFRESH_ON, BUTTON_REFRESH_ON_HOVER);
            messages.add(
                LangHelpers.localize(
                    "integratednbt:nbt_extractor.auto_refresh",
                    LangHelpers.localize("integratednbt:nbt_extractor.auto_refresh.on")));
        } else {
            this.autoRefreshButton.setTexture(BUTTON_REFRESH_OFF, BUTTON_REFRESH_OFF_HOVER);
            messages.add(
                LangHelpers.localize(
                    "integratednbt:nbt_extractor.auto_refresh",
                    LangHelpers.localize("integratednbt:nbt_extractor.auto_refresh.off")));
        }
        messages.addAll(
            Arrays.asList(
                LangHelpers.localize("integratednbt:nbt_extractor.auto_refresh.description")
                    .split("\\\\n")));
        this.autoRefreshButton.setHoverTextRaw(messages);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.treeViewer.mouseClicked(mouseX, mouseY, mouseButton);
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        if (this.treeViewer.mouseDragged(mouseX, mouseY, mouseButton)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        this.treeViewer.mouseReleased(mouseX, mouseY, mouseButton);
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public void initGui() {
        this.updateCalculations();
        this.xSize = this.width - 2 * this.padding;
        this.ySize = this.height - 2 * this.padding;
        super.initGui();
        this.nbtExtractorContainer.setSlotOffset((this.xSize - INVENTORY_WIDTH) / 2, this.ySize - INVENTORY_HEIGHT);
        this.treeViewer.updateBounds(
            this.padding + SIDE_BORDER_SIZE,
            this.padding + TOP_BORDER_SIZE,
            this.screenWidth,
            this.screenHeight);
        this.outputModeButton = new HoverTextImageButton(
            this,
            this.width - this.padding - 7 - BUTTON_SIZE,
            this.padding + 7,
            this::onOutputModeButtonClick);
        this.updateOutputModeButton();
        this.addWidget(this.outputModeButton);
        this.autoRefreshButton = new HoverTextImageButton(
            this,
            this.width - this.padding - 7 - BUTTON_SIZE * 2 - BUTTON_SPACING,
            this.padding + 7,
            this::onAutoRefreshButtonClick);
        this.updateAutoRefreshButton();
        this.addWidget(this.autoRefreshButton);
    }

    /**
     * Update calculations based on resolution
     */
    private void updateCalculations() {
        Minecraft mc = Minecraft.getMinecraft();

        ScaledResolution scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        this.scaleFactor = scaledResolution.getScaleFactor();

        this.padding = (int) Math
            .min(Math.max(BASE_PADDING / Math.pow(this.scaleFactor, 3), 4), Math.min(this.width, this.height) / 10.0);
        this.screenWidth = this.width - 2 * this.padding - 2 * SIDE_BORDER_SIZE;
        this.screenHeight = this.height - 2 * this.padding - TOP_BORDER_SIZE - INVENTORY_HEIGHT;
    }

    public void onOutputModeButtonClick(GuiButtonExtended ignored) {
        if (outputMode == null) {
            return;
        }
        IntegratedNbt._instance.getPacketHandler()
            .sendToServer(
                new NbtExtractorSetOutputModePacket(
                    this.nbtExtractorContainer.getTile()
                        .getPos(),
                    NbtExtractorOutputMode.values()[(outputMode.ordinal() + 1)
                        % NbtExtractorOutputMode.values().length]));
    }

    public void onAutoRefreshButtonClick(GuiButtonExtended ignored) {
        if (autoRefresh == null) {
            return;
        }
        IntegratedNbt._instance.getPacketHandler()
            .sendToServer(
                new NbtExtractorUpdateAutoRefreshPacket(
                    this.nbtExtractorContainer.getTile()
                        .getPos(),
                    !autoRefresh));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dWheel) {
        super.mouseScrolled(mouseX, mouseY, dWheel);
        if (errorCode == UpdateClientNbtExtractorPacket.ErrorCode.NO_ERROR && nbt != null) {
            this.treeViewer.mouseScrolled(dWheel);
        }
        return true;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.outputModeButton.drawHover(this, mouseX, mouseY);
        this.autoRefreshButton.drawHover(this, mouseX, mouseY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawBackground(0);
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.outputModeButton.drawScreen(mouseX, mouseY, partialTicks);
        this.autoRefreshButton.drawScreen(mouseX, mouseY, partialTicks);
        this.renderToolTip(ItemHelpers.EMPTY, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.renderGuiParts();
        this.drawString(
            this.fontRenderer,
            LangHelpers.localize("tile.blocks.integratednbt.nbt_extractor.name"),
            this.padding + 8,
            this.padding + 9,
            4210752);

        // Scissor test allows restricting rendering to a rectangular portion of the screen.
        // In this case, we only want to render in the screen area of the NBT Extractor.
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
            (int) (this.scaleFactor * (this.padding + SIDE_BORDER_SIZE)),
            (int) (this.scaleFactor * (this.padding + INVENTORY_HEIGHT)),
            (int) (this.scaleFactor * this.screenWidth),
            (int) (this.scaleFactor * this.screenHeight));
        Slot srcNBTSlot = this.nbtExtractorContainer.getSrcNBTSlot();
        if (!srcNBTSlot.getHasStack()) {
            errorCode = null;
            this.renderWelcome();
        } else if (errorCode == null) {
            this.renderLoading();
        } else if (!errorCode.equals(UpdateClientNbtExtractorPacket.ErrorCode.NO_ERROR)) {
            this.renderError();
        } else {
            this.treeViewer.render(this, nbt, mouseX, mouseY);
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    protected void renderToolTip(ItemStack itemIn, int x, int y) {
        super.renderToolTip(itemIn, x, y);
        this.treeViewer.renderTooltip(this, x, y);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void renderGuiParts() {
        int padding = this.padding;
        int screenWidth = this.screenWidth;
        int screenHeight = this.screenHeight;
        PART0.renderTo(this, padding, padding);
        PART1.renderToScaled(this, padding + SIDE_BORDER_SIZE, padding, screenWidth, -1);
        PART2.renderTo(this, this.width - padding - SIDE_BORDER_SIZE, padding);
        PART3.renderToScaled(this, padding, padding + TOP_BORDER_SIZE, -1, screenHeight);
        PART4.renderToScaled(this, padding + SIDE_BORDER_SIZE, padding + TOP_BORDER_SIZE, screenWidth, screenHeight);
        PART5
            .renderToScaled(this, this.width - padding - SIDE_BORDER_SIZE, padding + TOP_BORDER_SIZE, -1, screenHeight);
        int topOfPart6789 = this.height - padding - INVENTORY_HEIGHT;
        PART6.renderTo(this, padding, topOfPart6789);
        int part7Width2x = this.width - 2 * padding - 2 * SIDE_BORDER_SIZE - INVENTORY_WIDTH;
        int part7WidthFloor = (int) Math.floor(part7Width2x / 2.0);
        int part7WidthCeil = (int) Math.ceil(part7Width2x / 2.0);
        PART7.renderToScaled(this, padding + SIDE_BORDER_SIZE, topOfPart6789, part7WidthFloor, -1);
        PART8.renderTo(this, padding + SIDE_BORDER_SIZE + part7WidthFloor, topOfPart6789);
        PART7.renderToScaled(
            this,
            padding + SIDE_BORDER_SIZE + part7WidthFloor + INVENTORY_WIDTH,
            topOfPart6789,
            part7WidthCeil,
            -1);
        PART9.renderTo(this, this.width - padding - SIDE_BORDER_SIZE, topOfPart6789);
    }

    private void renderWelcome() {
        this.renderCenteredTextGroup(
            LangHelpers.localize("integratednbt:nbt_extractor.welcome"),
            0x00FFFF,
            LangHelpers.localize("integratednbt:nbt_extractor.welcome.description"));
    }

    private void renderLoading() {
        this.renderCenteredTextGroup(
            LangHelpers.localize("integratednbt:nbt_extractor.loading"),
            0xFFFF00,
            LangHelpers.localize("integratednbt:nbt_extractor.loading.description"));
    }

    private void renderError() {
        String message = "";
        if (errorMessage != null) {
            message = errorMessage.localize();
        } else {
            switch (errorCode) {
                case EVAL_ERROR:
                    message = LangHelpers.localize("integratednbt:nbt_extractor.error.eval");
                    break;
                case TYPE_ERROR:
                    message = LangHelpers.localize("integratednbt:nbt_extractor.error.type");
                    break;
                case UNEXPECTED_ERROR:
                    message = LangHelpers.localize("integratednbt:nbt_extractor.error.unexpected");
                    break;
            }
        }
        this.renderCenteredTextGroup(LangHelpers.localize("integratednbt:nbt_extractor.error"), 0xFF5555, message);
    }

    private void renderCenteredTextGroup(String title, int titleColor, String description) {
        int centerX = this.padding + SIDE_BORDER_SIZE + this.screenWidth / 2;
        int maxTextWidth = (int) (this.screenWidth * CENTERED_TEXT_MAX_RATIO);

        List<String> descLines = this.fontRenderer.listFormattedStringToWidth(description, maxTextWidth);
        int totalHeight = this.fontRenderer.FONT_HEIGHT + 4 + (descLines.size() * (this.fontRenderer.FONT_HEIGHT + 2));
        int startY = this.padding + TOP_BORDER_SIZE + (this.screenHeight - totalHeight) / 2;

        this.drawCenteredString(this.fontRenderer, title, centerX, startY, titleColor);

        int currentY = startY + this.fontRenderer.FONT_HEIGHT + 4;
        for (String line : descLines) {
            this.drawCenteredString(this.fontRenderer, line, centerX, currentY, 0xAAAAAA);
            currentY += this.fontRenderer.FONT_HEIGHT + 2;
        }
    }
}
