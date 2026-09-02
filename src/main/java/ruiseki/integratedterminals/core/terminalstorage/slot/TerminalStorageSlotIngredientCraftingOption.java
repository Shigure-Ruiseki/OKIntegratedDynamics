package ruiseki.integratedterminals.core.terminalstorage.slot;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.EnumChatFormatting;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.commoncapabilities.api.ingredient.IIngredientMatcher;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integratedterminals.api.ingredient.IIngredientComponentTerminalStorageHandler;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabClient;
import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalCraftingOption;
import ruiseki.integratedterminals.core.client.gui.GuiTerminalStorage;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentClient;
import ruiseki.integratedterminals.core.terminalstorage.crafting.HandlerWrappedTerminalCraftingOption;
import ruiseki.integratedterminals.core.terminalstorage.crafting.PendingCraftingJobOutput;
import ruiseki.okcore.client.gui.RenderItemExtendedSlotCount;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.LangHelpers;

/**
 * An ingredient slot for a crafting option.
 *
 * @param <T> The instance type.
 * @param <M> The matching condition parameter.
 * @author rubensworks
 */
public class TerminalStorageSlotIngredientCraftingOption<T, M> extends TerminalStorageSlotIngredient<T, M> {

    private final HandlerWrappedTerminalCraftingOption<T> craftingOption;

    public TerminalStorageSlotIngredientCraftingOption(
        IIngredientComponentTerminalStorageHandler<T, M> ingredientComponentViewHandler, T instance,
        HandlerWrappedTerminalCraftingOption<T> craftingOption) {
        super(ingredientComponentViewHandler, instance);
        this.craftingOption = craftingOption;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawGuiContainerLayer(GuiContainer gui, GuiTerminalStorage.DrawLayer layer, float partialTick, int x,
        int y, int mouseX, int mouseY, ITerminalStorageTabClient tab, int channel, @Nullable String label) {
        IIngredientComponentTerminalStorageHandler<T, M> viewHandler = getIngredientComponentViewHandler();
        long maxQuantity = ((TerminalStorageTabIngredientComponentClient) tab).getMaxQuantity(channel);
        PendingCraftingJobOutput<T> pendingCraftingJobOutput = getPendingCraftingJobOutput(tab, channel, label);
        if (layer == GuiTerminalStorage.DrawLayer.BACKGROUND) {
            viewHandler
                .drawInstance(getInstance(), maxQuantity, null, gui, layer, partialTick, x, y, mouseX, mouseY, null);
            drawCraftLabel(x, y);
        } else {
            viewHandler.drawInstance(
                getInstance(),
                maxQuantity,
                label,
                gui,
                layer,
                partialTick,
                x,
                y,
                mouseX,
                mouseY,
                getTooltipLines(pendingCraftingJobOutput));
        }
        drawCraftingJobOverlay(gui, layer, x, y, pendingCraftingJobOutput);
    }

    protected List<String> getTooltipLines(@Nullable PendingCraftingJobOutput<T> pendingCraftingJobOutput) {
        List<String> tooltipLines = Lists.newArrayList();
        if (pendingCraftingJobOutput != null) {
            addCraftingJobTooltipLines(tooltipLines, pendingCraftingJobOutput);
        }
        tooltipLines.add(
            EnumChatFormatting.YELLOW
                + LangHelpers.localize("gui.integratedterminals.terminal_storage.tooltip.requirements"));
        ITerminalCraftingOption<T> option = getCraftingOption().getCraftingOption();
        for (IngredientComponent<?, ?> inputComponent : option.getInputComponents()) {
            IIngredientMatcher matcher = inputComponent.getMatcher();
            for (Object inputInstance : option.getInputs(inputComponent)) {
                if (!matcher.isEmpty(inputInstance)) {
                    tooltipLines.add(
                        String.format(
                            "%s- %s (%s)",
                            EnumChatFormatting.GRAY,
                            matcher.localize(inputInstance),
                            matcher.getQuantity(inputInstance)));
                }
            }
        }
        return tooltipLines;
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    protected PendingCraftingJobOutput<T> getPendingCraftingJobOutput(ITerminalStorageTabClient tab, int channel,
        @Nullable String label) {
        // The same instance can also be shown as a stored ingredient.
        // In that case, only that slot indicates the running crafting jobs, to avoid indicating them twice.
        return ((TerminalStorageTabIngredientComponentClient<T, M>) tab).isShownAsStoredInstance(channel, getInstance())
            ? null
            : super.getPendingCraftingJobOutput(tab, channel, label);
    }

    public HandlerWrappedTerminalCraftingOption<T> getCraftingOption() {
        return craftingOption;
    }

    private void drawCraftLabel(int x, int y) {
        RenderItemExtendedSlotCount.drawSlotText(
            Minecraft.getMinecraft().fontRenderer,
            EnumChatFormatting.GOLD + LangHelpers.localize("gui.integratedterminals.terminal_storage.craft"),
            x,
            y - 11);
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableLighting();
    }

}
