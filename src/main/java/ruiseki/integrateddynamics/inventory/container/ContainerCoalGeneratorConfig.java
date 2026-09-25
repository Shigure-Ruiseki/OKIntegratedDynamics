package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.client.gui.GuiScreen;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.client.gui.GuiCoalGenerator;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.client.gui.GuiScreens;
import ruiseki.okcore.client.gui.IContainerAccess;
import ruiseki.okcore.client.gui.ScreenFactorySafe;
import ruiseki.okcore.config.extendedconfig.GuiConfig;

public class ContainerCoalGeneratorConfig extends GuiConfig<ContainerCoalGenerator> {

    /**
     * The unique instance.
     */
    public static ContainerCoalGeneratorConfig _instance;

    public ContainerCoalGeneratorConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "coal_generator",
            null,
            eConfig -> new ContainerType<>(
                (i, inventoryPlayer, extendedBuffer) -> new ContainerCoalGenerator(inventoryPlayer)));
    }

    @Override
    public <U extends GuiScreen & IContainerAccess<ContainerCoalGenerator>> GuiScreens.ScreenConstructor<ContainerCoalGenerator, U> getScreenFactory() {
        return new ScreenFactorySafe<ContainerCoalGenerator, U>(
            (container, inventoryPlayer) -> new GuiCoalGenerator(container));
    }
}
