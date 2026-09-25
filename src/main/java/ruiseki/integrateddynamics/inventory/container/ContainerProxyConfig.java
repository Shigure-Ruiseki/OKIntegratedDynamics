package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.client.gui.GuiScreen;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.client.gui.GuiProxy;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.client.gui.GuiScreens;
import ruiseki.okcore.client.gui.IContainerAccess;
import ruiseki.okcore.client.gui.ScreenFactorySafe;
import ruiseki.okcore.config.extendedconfig.GuiConfig;

public class ContainerProxyConfig extends GuiConfig<ContainerProxy> {

    /**
     * The unique instance.
     */
    public static ContainerProxyConfig _instance;

    public ContainerProxyConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "proxy",
            null,
            eConfig -> new ContainerType<>(
                (i, inventoryPlayer, extendedBuffer) -> new ContainerProxy(inventoryPlayer)));
    }

    @Override
    public <U extends GuiScreen & IContainerAccess<ContainerProxy>> GuiScreens.ScreenConstructor<ContainerProxy, U> getScreenFactory() {
        return new ScreenFactorySafe<ContainerProxy, U>((container, inventoryPlayer) -> new GuiProxy(container));
    }
}
