package ruiseki.integrateddynamics.core.inventory.container;

import net.minecraft.client.gui.GuiScreen;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.core.client.gui.container.GuiPartSettings;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.client.gui.GuiScreens;
import ruiseki.okcore.client.gui.IContainerAccess;
import ruiseki.okcore.client.gui.ScreenFactorySafe;
import ruiseki.okcore.config.extendedconfig.GuiConfig;

/**
 * Config for {@link ContainerPartSettings}.
 *
 * @author rubensworks
 */
public class ContainerPartSettingsConfig extends GuiConfig<ContainerPartSettings> {

    /**
     * The unique instance.
     */
    public static ContainerPartSettingsConfig _instance;

    public ContainerPartSettingsConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "part_settings",
            null,
            eConfig -> new ContainerType<>(ContainerPartSettings::new));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public <U extends GuiScreen & IContainerAccess<ContainerPartSettings>> GuiScreens.ScreenConstructor<ContainerPartSettings, U> getScreenFactory() {
        return new ScreenFactorySafe<ContainerPartSettings, U>(
            (container, inventoryPlayer) -> new GuiPartSettings<>(container));
    }
}
