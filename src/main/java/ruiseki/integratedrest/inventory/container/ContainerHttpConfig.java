package ruiseki.integratedrest.inventory.container;

import net.minecraft.client.gui.GuiScreen;

import ruiseki.integratedrest.IntegratedRest;
import ruiseki.integratedrest.client.gui.GuiHttp;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.client.gui.GuiScreens;
import ruiseki.okcore.client.gui.IContainerAccess;
import ruiseki.okcore.client.gui.ScreenFactorySafe;
import ruiseki.okcore.config.extendedconfig.GuiConfig;

/**
 * Config for {@link ContainerHttp}.
 * 
 * @author rubensworks
 */
public class ContainerHttpConfig extends GuiConfig<ContainerHttp> {

    /**
     * The unique instance.
     */
    public static ContainerHttpConfig _instance;

    public ContainerHttpConfig() {
        super(
            IntegratedRest._instance,
            true,
            "http",
            null,
            eConfig -> new ContainerType<>((i, inventoryPlayer, extendedBuffer) -> new ContainerHttp(inventoryPlayer)));
    }

    @Override
    public <U extends GuiScreen & IContainerAccess<ContainerHttp>> GuiScreens.ScreenConstructor<ContainerHttp, U> getScreenFactory() {
        return new ScreenFactorySafe<ContainerHttp, U>((container, inventoryPlayer) -> new GuiHttp(container));
    }
}
