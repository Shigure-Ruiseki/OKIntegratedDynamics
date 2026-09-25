package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.client.gui.GuiScreen;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.client.gui.GuiPartDisplay;
import ruiseki.integrateddynamics.core.part.panel.PartTypePanelVariableDriven;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.client.gui.GuiScreens;
import ruiseki.okcore.client.gui.IContainerAccess;
import ruiseki.okcore.client.gui.ScreenFactorySafe;
import ruiseki.okcore.config.extendedconfig.GuiConfig;

public class ContainerPartDisplayConfig extends GuiConfig<ContainerPartPanelVariableDriven> {

    /**
     * The unique instance.
     */
    public static ContainerPartDisplayConfig _instance;

    public ContainerPartDisplayConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "part_display",
            null,
            eConfig -> new ContainerType<>(
                (i, inventoryPlayer,
                    extendedBuffer) -> new ContainerPartPanelVariableDriven(inventoryPlayer, extendedBuffer)));
    }

    @Override
    public <U extends GuiScreen & IContainerAccess<ContainerPartPanelVariableDriven>> GuiScreens.ScreenConstructor<ContainerPartPanelVariableDriven, U> getScreenFactory() {
        // Due to our use of generics, we have to delegate to a separate function.
        return new ScreenFactorySafe<>((GuiScreens.ScreenConstructor) createScreenFactory());
    }

    @SideOnly(Side.CLIENT)
    protected static <P extends PartTypePanelVariableDriven<P, S>, S extends PartTypePanelVariableDriven.State<P, S>> GuiScreens.ScreenConstructor<ContainerPartPanelVariableDriven<P, S>, GuiPartDisplay<P, S>> createScreenFactory() {
        return (container, inventoryPlayer) -> new GuiPartDisplay<>(container);
    }
}
