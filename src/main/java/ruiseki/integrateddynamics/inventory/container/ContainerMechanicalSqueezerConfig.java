package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.client.gui.GuiScreen;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.client.gui.GuiMechanicalSqueezer;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.client.gui.GuiScreens;
import ruiseki.okcore.client.gui.IContainerAccess;
import ruiseki.okcore.client.gui.ScreenFactorySafe;
import ruiseki.okcore.config.extendedconfig.GuiConfig;

public class ContainerMechanicalSqueezerConfig extends GuiConfig<ContainerMechanicalSqueezer> {

    /**
     * The unique instance.
     */
    public static ContainerMechanicalSqueezerConfig _instance;

    public ContainerMechanicalSqueezerConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "mechanical_squeezer",
            null,
            eConfig -> new ContainerType<>(
                (i, inventoryPlayer, extendedBuffer) -> new ContainerMechanicalSqueezer(inventoryPlayer)));
    }

    @Override
    public <U extends GuiScreen & IContainerAccess<ContainerMechanicalSqueezer>> GuiScreens.ScreenConstructor<ContainerMechanicalSqueezer, U> getScreenFactory() {
        return new ScreenFactorySafe<ContainerMechanicalSqueezer, U>(
            (container, inventoryPlayer) -> new GuiMechanicalSqueezer(container));
    }
}
