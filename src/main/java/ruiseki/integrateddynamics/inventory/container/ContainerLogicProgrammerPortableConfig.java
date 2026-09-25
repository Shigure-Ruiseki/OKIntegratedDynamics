package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.client.gui.GuiScreen;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammerPortable;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.client.gui.GuiScreens;
import ruiseki.okcore.client.gui.IContainerAccess;
import ruiseki.okcore.client.gui.ScreenFactorySafe;
import ruiseki.okcore.config.extendedconfig.GuiConfig;

/**
 * Config for {@link ContainerLogicProgrammer}.
 *
 * @author rubensworks
 */
public class ContainerLogicProgrammerPortableConfig extends GuiConfig<ContainerLogicProgrammerPortable> {

    /**
     * The unique instance.
     */
    public static ContainerLogicProgrammerPortableConfig _instance;

    public ContainerLogicProgrammerPortableConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "logic_programmer_portable",
            null,
            eConfig -> new ContainerType<>(
                (i, inventoryPlayer,
                    extendedBuffer) -> new ContainerLogicProgrammerPortable(inventoryPlayer, extendedBuffer)));
    }

    @Override
    public <U extends GuiScreen & IContainerAccess<ContainerLogicProgrammerPortable>> GuiScreens.ScreenConstructor<ContainerLogicProgrammerPortable, U> getScreenFactory() {
        return new ScreenFactorySafe<>(GuiLogicProgrammerPortable::new);
    }
}
