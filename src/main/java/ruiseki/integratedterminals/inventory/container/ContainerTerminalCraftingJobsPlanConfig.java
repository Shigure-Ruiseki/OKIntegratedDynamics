package ruiseki.integratedterminals.inventory.container;

import java.io.IOException;

import net.minecraft.client.gui.GuiScreen;

import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.client.gui.container.GuiTerminalCraftingJobsPlan;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.client.gui.GuiScreens;
import ruiseki.okcore.client.gui.IContainerAccess;
import ruiseki.okcore.client.gui.ScreenFactorySafe;
import ruiseki.okcore.config.extendedconfig.GuiConfig;

/**
 * Config for {@link ContainerTerminalCraftingJobs}.
 *
 * @author rubensworks
 */
public class ContainerTerminalCraftingJobsPlanConfig extends GuiConfig<ContainerTerminalCraftingJobsPlan> {

    /**
     * The unique instance.
     */
    public static ContainerTerminalCraftingJobsPlanConfig _instance;

    public ContainerTerminalCraftingJobsPlanConfig() {
        super(
            IntegratedTerminals._instance,
            true,
            "part_terminal_crafting_jobs_plan",
            null,
            eConfig -> new ContainerType<>((i, inventoryPlayer, extendedBuffer) -> {
                try {
                    return new ContainerTerminalCraftingJobsPlan(inventoryPlayer, extendedBuffer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));
    }

    @Override
    public <U extends GuiScreen & IContainerAccess<ContainerTerminalCraftingJobsPlan>> GuiScreens.ScreenConstructor<ContainerTerminalCraftingJobsPlan, U> getScreenFactory() {
        return new ScreenFactorySafe<>(GuiTerminalCraftingJobsPlan::new);
    }
}
