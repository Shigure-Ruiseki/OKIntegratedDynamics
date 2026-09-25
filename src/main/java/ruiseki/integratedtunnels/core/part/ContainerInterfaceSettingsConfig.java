package ruiseki.integratedtunnels.core.part;

import net.minecraft.client.gui.GuiScreen;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.core.inventory.container.ContainerAspectSettings;
import ruiseki.okcore.client.gui.GuiScreens;
import ruiseki.okcore.client.gui.IContainerAccess;
import ruiseki.okcore.client.gui.ScreenFactorySafe;
import ruiseki.okcore.config.extendedconfig.GuiConfig;
import ruiseki.okcore.inventory.container.ContainerTypeData;

/**
 * Config for {@link ContainerAspectSettings}.
 *
 * @author rubensworks
 */
public class ContainerInterfaceSettingsConfig extends GuiConfig<ContainerInterfaceSettings> {

    /**
     * The unique instance.
     */
    public static ContainerInterfaceSettingsConfig _instance;

    public ContainerInterfaceSettingsConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "part_interface_settings",
            null,
            eConfig -> new ContainerTypeData<>(
                (windowId, playerInv, extraData) -> new ContainerInterfaceSettings(playerInv, extraData)));
    }

    @Override
    public <U extends GuiScreen & IContainerAccess<ContainerInterfaceSettings>> GuiScreens.ScreenConstructor<ContainerInterfaceSettings, U> getScreenFactory() {
        return new ScreenFactorySafe<ContainerInterfaceSettings, U>(
            (container, inventoryPlayer) -> new GuiInterfaceSettings(container));
    }
}
