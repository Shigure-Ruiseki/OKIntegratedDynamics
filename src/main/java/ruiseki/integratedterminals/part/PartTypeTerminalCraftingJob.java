package ruiseki.integratedterminals.part;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.core.part.PartStateEmpty;
import ruiseki.integratedterminals.GeneralConfig;
import ruiseki.integratedterminals.client.gui.container.GuiTerminalCraftingJobs;
import ruiseki.integratedterminals.core.part.PartTypeTerminal;
import ruiseki.integratedterminals.core.terminalstorage.crafting.TerminalStorageTabIngredientCraftingHandlers;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalCraftingJobs;
import ruiseki.okcore.helper.LangHelpers;

/**
 * A part that exposes a gui using which players can view and manage the active crafting jobs in the network.
 *
 * @author rubensworks
 */
public class PartTypeTerminalCraftingJob
    extends PartTypeTerminal<PartTypeTerminalCraftingJob, PartStateEmpty<PartTypeTerminalCraftingJob>> {

    public PartTypeTerminalCraftingJob(String name) {
        super(name);
    }

    @Override
    public int getConsumptionRate(PartStateEmpty<PartTypeTerminalCraftingJob> state) {
        return GeneralConfig.terminalCraftingBaseConsumption;
    }

    @Override
    protected PartStateEmpty<PartTypeTerminalCraftingJob> constructDefaultState() {
        return new PartStateEmpty<PartTypeTerminalCraftingJob>() {

            @Override
            public int getUpdateInterval() {
                return 1; // For enabling energy consumption
            }
        };
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Class<? extends GuiScreen> getGui() {
        return GuiTerminalCraftingJobs.class;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerTerminalCraftingJobs.class;
    }

    @Override
    public void loadTooltip(ItemStack itemStack, List<String> lines) {
        super.loadTooltip(itemStack, lines);
        if (TerminalStorageTabIngredientCraftingHandlers.REGISTRY.getHandlers()
            .isEmpty()) {
            lines.add(
                EnumChatFormatting.GOLD + LangHelpers
                    .localize("parttype.parttypes.integratedterminals.terminal_crafting_job.tooltip.nohandlers"));
        }
    }
}
