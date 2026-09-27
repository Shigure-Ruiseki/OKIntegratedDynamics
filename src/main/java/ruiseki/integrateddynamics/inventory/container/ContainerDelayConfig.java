package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.client.gui.GuiScreen;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.client.gui.GuiDelay;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.client.gui.GuiScreens;
import ruiseki.okcore.client.gui.IContainerAccess;
import ruiseki.okcore.client.gui.ScreenFactorySafe;
import ruiseki.okcore.config.extendedconfig.GuiConfig;

public class ContainerDelayConfig extends GuiConfig<ContainerDelay> {

    /**
     * The unique instance.
     */
    public static ContainerDelayConfig _instance;

    public ContainerDelayConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "delay",
            null,
            eConfig -> new ContainerType<>(
                (i, inventoryPlayer, extendedBuffer) -> new ContainerDelay(inventoryPlayer)));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public <U extends GuiScreen & IContainerAccess<ContainerDelay>> GuiScreens.ScreenConstructor<ContainerDelay, U> getScreenFactory() {
        return new ScreenFactorySafe<ContainerDelay, U>((container, inventoryPlayer) -> new GuiDelay(container));
    }
}
