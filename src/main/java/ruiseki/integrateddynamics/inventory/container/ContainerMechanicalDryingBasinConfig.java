package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.client.gui.GuiScreen;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.client.gui.GuiMechanicalDryingBasin;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.client.gui.GuiScreens;
import ruiseki.okcore.client.gui.IContainerAccess;
import ruiseki.okcore.client.gui.ScreenFactorySafe;
import ruiseki.okcore.config.extendedconfig.GuiConfig;

public class ContainerMechanicalDryingBasinConfig extends GuiConfig<ContainerMechanicalDryingBasin> {

    /**
     * The unique instance.
     */
    public static ContainerMechanicalDryingBasinConfig _instance;

    public ContainerMechanicalDryingBasinConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "mechanical_drying_basin",
            null,
            eConfig -> new ContainerType<>(
                (i, inventoryPlayer, extendedBuffer) -> new ContainerMechanicalDryingBasin(inventoryPlayer)));
    }

    @Override
    public <U extends GuiScreen & IContainerAccess<ContainerMechanicalDryingBasin>> GuiScreens.ScreenConstructor<ContainerMechanicalDryingBasin, U> getScreenFactory() {
        return new ScreenFactorySafe<ContainerMechanicalDryingBasin, U>(
            (container, inventoryPlayer) -> new GuiMechanicalDryingBasin(container));
    }
}
