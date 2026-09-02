package ruiseki.integratedterminals.client.gui.container;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import ruiseki.commoncapabilities.api.ingredient.IPrototypedIngredient;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalCraftingPlanFlat;
import ruiseki.integratedterminals.capability.ingredient.IngredientComponentTerminalStorageHandlerConfig;
import ruiseki.integratedterminals.core.client.gui.GuiTerminalStorage;
import ruiseki.okcore.client.gui.RenderItemExtendedSlotCount;
import ruiseki.okcore.client.gui.component.GuiScrollBar;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.helper.RenderHelpers;

/**
 * A gui component for visualizing {@link ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData} as a flat
 * list.
 *
 * The using gui must call the following methods from its respective method:
 * * {@link #handleMouseInput()}
 * * {@link #drawScreen(int, int, float)} (int, int, float)}
 * * {@link #drawGuiContainerBackgroundLayer(float, int, int)}
 * * {@link #drawGuiContainerForegroundLayer(int, int)}
 * * {@link #mouseClicked(int, int, int)}
 *
 * @author rubensworks
 */
public class GuiCraftingPlanFlat extends Gui {

    private static final int COLUMNS = 2;
    private static final int COLUMN_PADDING = 2;
    private static final int ELEMENT_WIDTH = 110;
    private static final int ELEMENT_HEIGHT = 16;
    private static final int ELEMENT_HEIGHT_TOTAL = 18;

    protected static final int TICK_DELAY = 30;

    private final GuiContainer parentGui;
    private final int guiLeft;
    private final int guiTop;
    private final int x;
    private final int y;
    private final List<GuiCraftingPlanFlat.Element> elements;
    private final List<GuiCraftingPlanFlat.Element> visibleElements;
    private final boolean valid;
    private final GuiScrollBar scrollBar;
    private final String label;
    private final long tickDuration;
    private final int channel;
    @Nullable
    private final String initiatorName;

    private int firstRow;

    public GuiCraftingPlanFlat(GuiContainer parentGui, ITerminalCraftingPlanFlat<?> craftingPlan, int guiLeft,
        int guiTop, int x, int y, int visibleRows) {
        this.parentGui = parentGui;
        this.guiLeft = guiLeft;
        this.guiTop = guiTop;
        this.x = x;
        this.y = y;
        this.elements = getElements(craftingPlan);
        this.visibleElements = Lists.newArrayList(this.elements);
        this.valid = craftingPlan.getStatus()
            .isValid();
        this.scrollBar = new GuiScrollBar(guiLeft + x + 227, guiTop + y + 0, 178, this::setFirstRow, visibleRows);
        refreshList();
        this.label = LangHelpers.localize(craftingPlan.getUnlocalizedLabel());
        this.tickDuration = craftingPlan.getTickDuration();
        this.channel = craftingPlan.getChannel();
        this.initiatorName = craftingPlan.getInitiatorName();
    }

    public void inheritVisualizationState(GuiCraftingPlanFlat guiCraftingPlan) {
        // Inherit scroll state
        float lastScroll = guiCraftingPlan.scrollBar.getCurrentScroll();
        this.scrollBar.scrollTo(lastScroll);

        // Recalculate visible items
        refreshList();
    }

    protected void refreshList() {
        this.scrollBar.setTotalRows((int) Math.ceil(visibleElements.size() / COLUMNS));
    }

    public void setFirstRow(int firstRow) {
        this.firstRow = Math.max(0, firstRow);
    }

    protected int getTick() {
        return (int) Minecraft.getMinecraft().theWorld.getWorldTime() / TICK_DELAY;
    }

    protected List<Element> getVisibleElements() {
        return this.visibleElements
            .subList(firstRow, Math.min(this.visibleElements.size(), firstRow + scrollBar.getVisibleRows()));
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        scrollBar.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void drawGuiContainerLayer(int guiLeft, int guiTop, GuiTerminalStorage.DrawLayer layer, float partialTick,
        int mouseX, int mouseY) {
        int offsetY = 0;
        int column = 0;
        for (GuiCraftingPlanFlat.Element element : getVisibleElements()) {
            drawElement(
                element,
                guiLeft + x + (column * (ELEMENT_WIDTH + COLUMN_PADDING)),
                guiTop + y + offsetY,
                ELEMENT_WIDTH + (column == 0 ? 1 : 0),
                ELEMENT_HEIGHT,
                layer,
                partialTick,
                mouseX,
                mouseY);

            column++;
            if (column >= COLUMNS) {
                column = 0;
                offsetY += ELEMENT_HEIGHT_TOTAL;
            }
        }
    }

    private void drawElement(Element element, int x, int y, int width, int height, GuiTerminalStorage.DrawLayer layer,
        float partialTick, int mouseX, int mouseY) {
        if (layer == GuiTerminalStorage.DrawLayer.BACKGROUND) {
            // Draw background
            drawRect(x, y, x + width, y + height + 1, element.getColor());
        }

        int xOriginal = x;

        // Draw instance (rotate over alternatives if present)
        List<IPrototypedIngredient<?, ?>> instances = element.getInstances();
        if (!instances.isEmpty()) {
            int tick = getTick();
            IPrototypedIngredient<?, ?> output = instances.get(tick % instances.size());
            IngredientComponent<?, ?> ingredientComponent = output.getComponent();
            long quantity = ((IngredientComponent) ingredientComponent).getMatcher()
                .getQuantity(output.getPrototype());
            int finalX = x;
            int finalY = y;
            ingredientComponent.getCapability(IngredientComponentTerminalStorageHandlerConfig.CAPABILITY)
                .ifPresent(
                    h -> h.drawInstance(
                        output.getPrototype(),
                        quantity,
                        "",
                        this.parentGui,
                        layer,
                        partialTick,
                        finalX,
                        finalY,
                        mouseX,
                        mouseY,
                        null));
        }

        x = xOriginal + width - 50;
        if (layer == GuiTerminalStorage.DrawLayer.BACKGROUND) {
            // Draw counters
            int moved = 0;
            if (element.getStorageQuantity() > 0) {
                renderItem(new ItemStack(Blocks.chest), x, y, 0.45F);
                RenderHelpers.drawScaledString(
                    Minecraft.getMinecraft().fontRenderer,
                    LangHelpers
                        .localize("gui.integratedterminals.terminal_storage.stored", element.getStorageQuantity()),
                    x + 9,
                    y + 1,
                    0.5F,
                    16777215,
                    true);
                y += 8;
                moved++;
            }
            if (element.getToCraftQuantity() > 0) {
                renderItem(new ItemStack(Items.painting), x, y, 0.45F);
                RenderHelpers.drawScaledString(
                    Minecraft.getMinecraft().fontRenderer,
                    LangHelpers
                        .localize("gui.integratedterminals.terminal_storage.crafting", element.getToCraftQuantity()),
                    x + 9,
                    y + 1,
                    0.5F,
                    16777215,
                    true);
                y += 8;
                moved++;
            }
            if (element.getCraftingQuantity() > 0) {
                if (moved == 2) {
                    y -= 16;
                    x -= 44;
                }
                renderItem(new ItemStack(Blocks.crafting_table), x, y, 0.45F);
                RenderHelpers.drawScaledString(
                    Minecraft.getMinecraft().fontRenderer,
                    LangHelpers
                        .localize("gui.integratedterminals.terminal_storage.crafting", element.getCraftingQuantity()),
                    x + 9,
                    y + 1,
                    0.5F,
                    16777215,
                    true);
                y += 8;
            }
            if (element.getMissingQuantity() > 0) {
                if (moved == 2) {
                    y -= 16;
                    x -= 44;
                }
                renderItem(new ItemStack(Blocks.iron_bars), x, y, 0.45F);
                RenderHelpers.drawScaledString(
                    Minecraft.getMinecraft().fontRenderer,
                    LangHelpers
                        .localize("gui.integratedterminals.terminal_storage.missing", element.getMissingQuantity()),
                    x + 9,
                    y + 1,
                    0.5F,
                    16777215,
                    true);
            }
            GlStateManager.color(1, 1, 1, 1);
        }
    }

    protected static void renderItem(ItemStack itemStack, int x, int y, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, scale);

        RenderItemExtendedSlotCount renderItem = RenderItemExtendedSlotCount.getInstance();
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableDepth();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        renderItem.renderItemAndEffectIntoGUI(
            Minecraft.getMinecraft().fontRenderer,
            Minecraft.getMinecraft()
                .getTextureManager(),
            itemStack,
            0,
            0);
        renderItem.renderItemOverlayIntoGUI(
            Minecraft.getMinecraft().fontRenderer,
            Minecraft.getMinecraft()
                .getTextureManager(),
            itemStack,
            0,
            0,
            "");
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
    }

    public static String getDurationString(long tickDuration) {
        long durationMs = tickDuration * 1000 / MinecraftHelpers.SECOND_IN_TICKS;
        return LangHelpers.localize(
            "gui.integratedterminals.terminal_crafting_job.craftingplan.duration",
            DurationFormatUtils.formatDuration(durationMs, "H:mm:ss", true));
    }

    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

        // Draw plan label
        drawCenteredString(
            Minecraft.getMinecraft().fontRenderer,
            this.label,
            guiLeft + x + ELEMENT_WIDTH / 2 + 8,
            guiTop + y - 13,
            16777215);

        // Draw duration
        if (tickDuration >= 0) {
            String durationString = getDurationString(tickDuration);
            RenderHelpers.drawScaledString(
                fontRenderer,
                durationString,
                guiLeft + x + 200,
                guiTop + y - 14,
                0.5f,
                16777215,
                true);
        }

        // Draw channel
        if (channel != -1) {
            String channelString = LangHelpers
                .localize("gui.integratedterminals.terminal_crafting_job.craftingplan.crafting_channel", channel);
            RenderHelpers
                .drawScaledString(fontRenderer, channelString, guiLeft + x + 200, guiTop + y - 8, 0.5f, 16777215, true);
        }

        // Draw initiator
        if (initiatorName != null) {
            String initiatorString = LangHelpers
                .localize("gui.integratedterminals.terminal_crafting_job.craftingplan.owner", initiatorName);
            RenderHelpers.drawScaledString(
                fontRenderer,
                initiatorString,
                guiLeft + x - 4,
                guiTop + y - 14,
                0.5f,
                16777215,
                true);
        }

        drawGuiContainerLayer(guiLeft, guiTop, GuiTerminalStorage.DrawLayer.BACKGROUND, partialTicks, mouseX, mouseY);
        scrollBar.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }

    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawGuiContainerLayer(0, 0, GuiTerminalStorage.DrawLayer.FOREGROUND, 0, mouseX, mouseY);
    }

    public void handleMouseInput() {
        scrollBar.handleMouseInput();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

    }

    public static List<Element> getElements(ITerminalCraftingPlanFlat<?> craftingPlan) {
        List<Element> elements = Lists.newArrayList();
        for (ITerminalCraftingPlanFlat.IEntry entry : craftingPlan.getEntries()) {
            addElements(entry, elements);
        }
        return elements;
    }

    protected static void addElements(ITerminalCraftingPlanFlat.IEntry craftingPlan, List<Element> elements) {
        elements.add(
            new Element(
                craftingPlan.getInstances(),
                craftingPlan.getQuantityInStorage(),
                craftingPlan.getQuantityToCraft(),
                craftingPlan.getQuantityCrafting(),
                craftingPlan.getQuantityMissing()));
    }

    public boolean isValid() {
        return valid;
    }

    public static class Element {

        private final List<IPrototypedIngredient<?, ?>> instances;
        private final long storageQuantity;
        private final long toCraftQuantity;
        private final long craftingQuantity;
        private final long missingQuantity;

        public Element(List<IPrototypedIngredient<?, ?>> instances, long storageQuantity, long toCraftQuantity,
            long craftingQuantity, long missingQuantity) {
            this.instances = instances;
            this.storageQuantity = storageQuantity;
            this.toCraftQuantity = toCraftQuantity;
            this.craftingQuantity = craftingQuantity;
            this.missingQuantity = missingQuantity;
        }

        public List<IPrototypedIngredient<?, ?>> getInstances() {
            return instances;
        }

        public long getStorageQuantity() {
            return storageQuantity;
        }

        public long getToCraftQuantity() {
            return toCraftQuantity;
        }

        public long getCraftingQuantity() {
            return craftingQuantity;
        }

        public long getMissingQuantity() {
            return missingQuantity;
        }

        public int getColor() {
            if (getMissingQuantity() > 0) {
                return Helpers.RGBAToInt(250, 10, 13, 150);
            }
            if (getCraftingQuantity() > 0) {
                return Helpers.RGBAToInt(43, 174, 231, 150);
            }
            if (getToCraftQuantity() > 0) {
                return Helpers.RGBAToInt(243, 245, 150, 150);
            }
            return Helpers.RGBAToInt(43, 231, 47, 150);
        }
    }
}
