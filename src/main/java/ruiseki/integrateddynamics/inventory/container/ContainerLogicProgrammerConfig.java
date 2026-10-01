package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.client.gui.GuiScreen;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammer;
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
public class ContainerLogicProgrammerConfig extends GuiConfig<ContainerLogicProgrammer> {

    /**
     * The unique instance.
     */
    public static ContainerLogicProgrammerConfig _instance;

    public ContainerLogicProgrammerConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "logic_programmer",
            null,
            eConfig -> new ContainerType<>(
                (id, inventoryPlayer, extendedBuffer) -> new ContainerLogicProgrammer(id, inventoryPlayer)));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public <U extends GuiScreen & IContainerAccess<ContainerLogicProgrammer>> GuiScreens.ScreenConstructor<ContainerLogicProgrammer, U> getScreenFactory() {
        return new ScreenFactorySafe<>(GuiLogicProgrammer::new);
    }
}
