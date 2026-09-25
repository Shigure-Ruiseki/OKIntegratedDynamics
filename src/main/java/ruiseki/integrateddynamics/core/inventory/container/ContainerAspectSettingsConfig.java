package ruiseki.integrateddynamics.core.inventory.container;

import net.minecraft.client.gui.GuiScreen;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.core.client.gui.container.GuiAspectSettings;
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
public class ContainerAspectSettingsConfig extends GuiConfig<ContainerAspectSettings> {

    /**
     * The unique instance.
     */
    public static ContainerAspectSettingsConfig _instance;

    public ContainerAspectSettingsConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "aspect_settings",
            null,
            eConfig -> new ContainerTypeData<>(
                (windowId, playerInv, extraData) -> new ContainerAspectSettings(playerInv, extraData)));
    }

    @Override
    public <U extends GuiScreen & IContainerAccess<ContainerAspectSettings>> GuiScreens.ScreenConstructor<ContainerAspectSettings, U> getScreenFactory() {
        return new ScreenFactorySafe<ContainerAspectSettings, U>(
            (container, inventoryPlayer) -> new GuiAspectSettings(container));
    }
}
