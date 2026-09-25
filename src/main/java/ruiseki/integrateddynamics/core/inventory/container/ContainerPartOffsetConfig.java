package ruiseki.integrateddynamics.core.inventory.container;

import net.minecraft.client.gui.GuiScreen;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.core.client.gui.container.GuiPartOffset;
import ruiseki.okcore.client.gui.GuiScreens;
import ruiseki.okcore.client.gui.IContainerAccess;
import ruiseki.okcore.client.gui.ScreenFactorySafe;
import ruiseki.okcore.config.extendedconfig.GuiConfig;
import ruiseki.okcore.inventory.container.ContainerTypeData;

/**
 * Config for {@link ContainerPartSettings}.
 *
 * @author rubensworks
 */
public class ContainerPartOffsetConfig extends GuiConfig<ContainerPartOffset> {

    /**
     * The unique instance.
     */
    public static ContainerPartOffsetConfig _instance;

    public ContainerPartOffsetConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "part_offset",
            null,
            eConfig -> new ContainerTypeData<>(
                (windowId, playerInv, extraData) -> new ContainerPartOffset(playerInv, extraData)));
    }

    @Override
    public <U extends GuiScreen & IContainerAccess<ContainerPartOffset>> GuiScreens.ScreenConstructor<ContainerPartOffset, U> getScreenFactory() {
        return new ScreenFactorySafe<ContainerPartOffset, U>(
            (container, inventoryPlayer) -> new GuiPartOffset<>(container));
    }
}
