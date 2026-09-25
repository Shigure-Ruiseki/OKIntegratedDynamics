package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.client.gui.GuiScreen;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.part.read.IPartStateReader;
import ruiseki.integrateddynamics.api.part.read.IPartTypeReader;
import ruiseki.integrateddynamics.client.gui.GuiPartReader;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.client.gui.GuiScreens;
import ruiseki.okcore.client.gui.IContainerAccess;
import ruiseki.okcore.client.gui.ScreenFactorySafe;
import ruiseki.okcore.config.extendedconfig.GuiConfig;

public class ContainerPartReaderConfig extends GuiConfig<ContainerPartReader> {

    /**
     * The unique instance.
     */
    public static ContainerPartReaderConfig _instance;

    public ContainerPartReaderConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "part_reader",
            null,
            eConfig -> new ContainerType<>(
                (i, inventoryPlayer, extendedBuffer) -> new ContainerPartReader<>(inventoryPlayer, extendedBuffer)));
    }

    @Override
    public <U extends GuiScreen & IContainerAccess<ContainerPartReader>> GuiScreens.ScreenConstructor<ContainerPartReader, U> getScreenFactory() {
        // Due to our use of generics, we have to delegate to a separate function.
        return new ScreenFactorySafe<>((GuiScreens.ScreenConstructor) createScreenFactory());
    }

    @SideOnly(Side.CLIENT)
    protected static <P extends IPartTypeReader<P, S>, S extends IPartStateReader<P>> GuiScreens.ScreenConstructor<ContainerPartReader<P, S>, GuiPartReader<P, S>> createScreenFactory() {
        return (container, inventoryPlayer) -> new GuiPartReader<>(container);
    }
}
