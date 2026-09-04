package ruiseki.integratedterminals.core.terminalstorage.slot;

import java.util.List;
import java.util.Locale;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.EnumChatFormatting;

import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integratedterminals.api.ingredient.IIngredientComponentTerminalStorageHandler;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageSlot;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabClient;
import ruiseki.integratedterminals.client.gui.image.Images;
import ruiseki.integratedterminals.core.client.gui.GuiTerminalStorage;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentClient;
import ruiseki.integratedterminals.core.terminalstorage.crafting.PendingCraftingJobOutput;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;

/**
 * An ingredient slot.
 *
 * @param <T> The instance type.
 * @param <M> The matching condition parameter.
 * @author rubensworks
 */
public class TerminalStorageSlotIngredient<T, M> implements ITerminalStorageSlot {

    /**
     * The duration in milliseconds of a single frame of the crafting spinner.
     */
    private static final long SPINNER_FRAME_DURATION = 100;

    private final IIngredientComponentTerminalStorageHandler<T, M> ingredientComponentViewHandler;
    private final T instance;

    public TerminalStorageSlotIngredient(
        IIngredientComponentTerminalStorageHandler<T, M> ingredientComponentViewHandler, T instance) {
        this.ingredientComponentViewHandler = ingredientComponentViewHandler;
        this.instance = instance;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawGuiContainerLayer(GuiContainer gui, GuiTerminalStorage.DrawLayer layer, float partialTick, int x,
        int y, int mouseX, int mouseY, ITerminalStorageTabClient tab, int channel, @Nullable String label) {
        long maxQuantity = ((TerminalStorageTabIngredientComponentClient) tab).getMaxQuantity(channel);
        PendingCraftingJobOutput<T> pendingCraftingJobOutput = getPendingCraftingJobOutput(tab, channel, label);
        ingredientComponentViewHandler.drawInstance(
            instance,
            maxQuantity,
            label,
            gui,
            layer,
            partialTick,
            x,
            y,
            mouseX,
            mouseY,
            createCraftingJobTooltipLines(pendingCraftingJobOutput));
        drawCraftingJobOverlay(gui, layer, x, y, pendingCraftingJobOutput);
    }

    public IIngredientComponentTerminalStorageHandler<T, M> getIngredientComponentViewHandler() {
        return ingredientComponentViewHandler;
    }

    public T getInstance() {
        return instance;
    }

    /**
     * Get the pending output of the running crafting jobs that will produce this slot's instance.
     *
     * @param tab     The tab this slot is being rendered in.
     * @param channel The channel this slot is being rendered in.
     * @param label   An optional label that is rendered instead of the quantity.
     *                Slots with such a label are not part of the storage overview,
     *                such as the instance that is being moved around by the player,
     *                so they don't get a crafting indication.
     * @return The pending crafting job output, or null if this slot's instance is not being crafted.
     */
    @Nullable
    @SideOnly(Side.CLIENT)
    protected PendingCraftingJobOutput<T> getPendingCraftingJobOutput(ITerminalStorageTabClient tab, int channel,
        @Nullable String label) {
        return label == null
            ? ((TerminalStorageTabIngredientComponentClient<T, M>) tab)
                .getPendingCraftingJobOutput(channel, getInstance())
            : null;
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    protected List<String> createCraftingJobTooltipLines(
        @Nullable PendingCraftingJobOutput<T> pendingCraftingJobOutput) {
        if (pendingCraftingJobOutput == null) {
            return null;
        }
        List<String> tooltipLines = Lists.newArrayList();
        addCraftingJobTooltipLines(tooltipLines, pendingCraftingJobOutput);
        return tooltipLines;
    }

    @SideOnly(Side.CLIENT)
    protected void addCraftingJobTooltipLines(List<String> tooltipLines,
        PendingCraftingJobOutput<T> pendingCraftingJobOutput) {
        tooltipLines.add(
            EnumChatFormatting.AQUA + LangHelpers.localize(
                "gui.integratedterminals.terminal_storage.tooltip.crafting",
                getIngredientComponentViewHandler().formatQuantity(pendingCraftingJobOutput.getInstance())));
        String unlocalizedStatus = "gui.integratedterminals.craftingplan.status." + pendingCraftingJobOutput.getStatus()
            .name()
            .toLowerCase(Locale.ENGLISH);
        tooltipLines.add(
            EnumChatFormatting.GRAY + LangHelpers
                .localize("gui.integratedterminals.craftingplan.status", LangHelpers.localize(unlocalizedStatus)));
        tooltipLines.add(EnumChatFormatting.DARK_GRAY + LangHelpers.localize(unlocalizedStatus + ".desc"));
    }

    /**
     * Draw a spinner over this slot when its instance is being crafted.
     * The spinner is colored based on the status of the crafting jobs.
     */
    @SideOnly(Side.CLIENT)
    protected void drawCraftingJobOverlay(GuiContainer guiGraphics, GuiTerminalStorage.DrawLayer layer, int x, int y,
        @Nullable PendingCraftingJobOutput<T> pendingCraftingJobOutput) {
        if (layer != GuiTerminalStorage.DrawLayer.BACKGROUND || pendingCraftingJobOutput == null) {
            return;
        }

        Triple<Float, Float, Float> color = Helpers.intToRGB(
            pendingCraftingJobOutput.getStatus()
                .getColor());
        int frame = (int) ((System.currentTimeMillis() / SPINNER_FRAME_DURATION) % Images.SPINNER.length);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 300.0F);
        GlStateManager.enableBlend();

        Images.SPINNER[frame]
            .drawWithColor(guiGraphics, x, y, color.getLeft(), color.getMiddle(), color.getRight(), 1F);

        GlStateManager.popMatrix();
    }
}
