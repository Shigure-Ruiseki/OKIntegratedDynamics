package ruiseki.integratedterminals.inventory.container;

import net.minecraft.client.gui.GuiScreen;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.client.gui.container.GuiTerminalCraftingJobs;
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
public class ContainerTerminalCraftingJobsConfig extends GuiConfig<ContainerTerminalCraftingJobs> {

    /**
     * The unique instance.
     */
    public static ContainerTerminalCraftingJobsConfig _instance;

    public ContainerTerminalCraftingJobsConfig() {
        super(
            IntegratedTerminals._instance,
            true,
            "part_terminal_crafting_jobs",
            null,
            eConfig -> new ContainerType<>(ContainerTerminalCraftingJobs::new));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public <U extends GuiScreen & IContainerAccess<ContainerTerminalCraftingJobs>> GuiScreens.ScreenConstructor<ContainerTerminalCraftingJobs, U> getScreenFactory() {
        return new ScreenFactorySafe<>(GuiTerminalCraftingJobs::new);
    }
}
