package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.client.gui.GuiScreen;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.client.gui.GuiMaterializer;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.client.gui.GuiScreens;
import ruiseki.okcore.client.gui.IContainerAccess;
import ruiseki.okcore.client.gui.ScreenFactorySafe;
import ruiseki.okcore.config.extendedconfig.GuiConfig;

public class ContainerMaterializerConfig extends GuiConfig<ContainerMaterializer> {

    /**
     * The unique instance.
     */
    public static ContainerMaterializerConfig _instance;

    public ContainerMaterializerConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "materializer",
            null,
            eConfig -> new ContainerType<>(
                (i, inventoryPlayer, extendedBuffer) -> new ContainerMaterializer(inventoryPlayer)));
    }

    @Override
    public <U extends GuiScreen & IContainerAccess<ContainerMaterializer>> GuiScreens.ScreenConstructor<ContainerMaterializer, U> getScreenFactory() {
        return new ScreenFactorySafe<ContainerMaterializer, U>(
            (container, inventoryPlayer) -> new GuiMaterializer(container));
    }
}
