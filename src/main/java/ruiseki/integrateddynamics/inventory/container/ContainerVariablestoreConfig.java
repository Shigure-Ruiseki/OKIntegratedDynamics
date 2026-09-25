package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.client.gui.GuiScreen;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.client.gui.GuiVariablestore;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.client.gui.GuiScreens;
import ruiseki.okcore.client.gui.IContainerAccess;
import ruiseki.okcore.client.gui.ScreenFactorySafe;
import ruiseki.okcore.config.extendedconfig.GuiConfig;

public class ContainerVariablestoreConfig extends GuiConfig<ContainerVariablestore> {

    /**
     * The unique instance.
     */
    public static ContainerVariablestoreConfig _instance;

    public ContainerVariablestoreConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "variablestore",
            null,
            eConfig -> new ContainerType<>(
                (i, inventoryPlayer, extendedBuffer) -> new ContainerVariablestore(inventoryPlayer)));
    }

    @Override
    public <U extends GuiScreen & IContainerAccess<ContainerVariablestore>> GuiScreens.ScreenConstructor<ContainerVariablestore, U> getScreenFactory() {
        return new ScreenFactorySafe<ContainerVariablestore, U>(
            (container, inventoryPlayer) -> new GuiVariablestore(container));
    }
}
