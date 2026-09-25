package ruiseki.integratedcrafting.inventory.container;

import net.minecraft.client.gui.GuiScreen;

import ruiseki.integratedcrafting.client.gui.GuiPartInterfaceCraftingSettings;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.client.gui.GuiScreens;
import ruiseki.okcore.client.gui.IContainerAccess;
import ruiseki.okcore.client.gui.ScreenFactorySafe;
import ruiseki.okcore.config.extendedconfig.GuiConfig;

public class ContainerPartInterfaceCraftingSettingsConfig extends GuiConfig<ContainerPartInterfaceCraftingSettings> {

    /**
     * The unique instance.
     */
    public static ContainerPartInterfaceCraftingSettingsConfig _instance;

    public ContainerPartInterfaceCraftingSettingsConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "part_interface_crafting_settings",
            null,
            eConfig -> new ContainerType<>(
                (i, inventoryPlayer,
                    extendedBuffer) -> new ContainerPartInterfaceCraftingSettings(inventoryPlayer, extendedBuffer)));
    }

    @Override
    public <U extends GuiScreen & IContainerAccess<ContainerPartInterfaceCraftingSettings>> GuiScreens.ScreenConstructor<ContainerPartInterfaceCraftingSettings, U> getScreenFactory() {
        return new ScreenFactorySafe<ContainerPartInterfaceCraftingSettings, U>(
            (container, inventoryPlayer) -> new GuiPartInterfaceCraftingSettings(container));
    }
}
