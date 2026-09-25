package ruiseki.integratedcrafting.inventory.container;

import net.minecraft.client.gui.GuiScreen;

import ruiseki.integratedcrafting.client.gui.GuiPartInterfaceCrafting;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.client.gui.GuiScreens;
import ruiseki.okcore.client.gui.IContainerAccess;
import ruiseki.okcore.client.gui.ScreenFactorySafe;
import ruiseki.okcore.config.extendedconfig.GuiConfig;

public class ContainerPartInterfaceCraftingConfig extends GuiConfig<ContainerPartInterfaceCrafting> {

    /**
     * The unique instance.
     */
    public static ContainerPartInterfaceCraftingConfig _instance;

    public ContainerPartInterfaceCraftingConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "part_interface_crafting",
            null,
            eConfig -> new ContainerType<>(
                (i, inventoryPlayer,
                    extendedBuffer) -> new ContainerPartInterfaceCrafting(inventoryPlayer, extendedBuffer)));
    }

    @Override
    public <U extends GuiScreen & IContainerAccess<ContainerPartInterfaceCrafting>> GuiScreens.ScreenConstructor<ContainerPartInterfaceCrafting, U> getScreenFactory() {
        return new ScreenFactorySafe<ContainerPartInterfaceCrafting, U>(
            (container, inventoryPlayer) -> new GuiPartInterfaceCrafting(container));
    }
}
