package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.client.gui.GuiScreen;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.part.write.IPartStateWriter;
import ruiseki.integrateddynamics.api.part.write.IPartTypeWriter;
import ruiseki.integrateddynamics.client.gui.GuiPartWriter;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.client.gui.GuiScreens;
import ruiseki.okcore.client.gui.IContainerAccess;
import ruiseki.okcore.client.gui.ScreenFactorySafe;
import ruiseki.okcore.config.extendedconfig.GuiConfig;

public class ContainerPartWriterConfig extends GuiConfig<ContainerPartWriter> {

    /**
     * The unique instance.
     */
    public static ContainerPartWriterConfig _instance;

    public ContainerPartWriterConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "part_writer",
            null,
            eConfig -> new ContainerType<>(
                (i, inventoryPlayer, extendedBuffer) -> new ContainerPartWriter<>(inventoryPlayer, extendedBuffer)));
    }

    @Override
    public <U extends GuiScreen & IContainerAccess<ContainerPartWriter>> GuiScreens.ScreenConstructor<ContainerPartWriter, U> getScreenFactory() {
        // Due to our use of generics, we have to delegate to a separate function.
        return new ScreenFactorySafe<ContainerPartWriter, U>((GuiScreens.ScreenConstructor) createScreenFactory());
    }

    @SideOnly(Side.CLIENT)
    protected static <P extends IPartTypeWriter<P, S>, S extends IPartStateWriter<P>> GuiScreens.ScreenConstructor<ContainerPartWriter<P, S>, GuiPartWriter<P, S>> createScreenFactory() {
        return (container, inventoryPlayer) -> new GuiPartWriter(container);
    }
}
