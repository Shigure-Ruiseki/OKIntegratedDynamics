package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.client.gui.GuiScreen;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.client.gui.GuiLabeller;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.client.gui.GuiScreens;
import ruiseki.okcore.client.gui.IContainerAccess;
import ruiseki.okcore.client.gui.ScreenFactorySafe;
import ruiseki.okcore.config.extendedconfig.GuiConfig;

public class ContainerLabellerConfig extends GuiConfig<ContainerLabeller> {

    /**
     * The unique instance.
     */
    public static ContainerLabellerConfig _instance;

    public ContainerLabellerConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "labeller",
            null,
            eConfig -> new ContainerType<>(ContainerLabeller::new));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public <U extends GuiScreen & IContainerAccess<ContainerLabeller>> GuiScreens.ScreenConstructor<ContainerLabeller, U> getScreenFactory() {
        return new ScreenFactorySafe<ContainerLabeller, U>((container, inventoryPlayer) -> new GuiLabeller(container));
    }
}
