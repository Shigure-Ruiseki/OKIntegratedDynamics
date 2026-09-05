package ruiseki.integrateddynamics.core.part;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;

import lombok.Data;
import lombok.Getter;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartRenderPosition;
import ruiseki.integrateddynamics.core.client.gui.ExtendedGuiHandler;
import ruiseki.integrateddynamics.core.client.gui.container.GuiPartOffset;
import ruiseki.integrateddynamics.core.client.gui.container.GuiPartSettings;
import ruiseki.integrateddynamics.core.inventory.container.ContainerPartOffset;
import ruiseki.integrateddynamics.core.inventory.container.ContainerPartSettings;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.inventory.IGuiContainerProvider;

/**
 * An abstract {@link IPartType} that can have settings.
 *
 * @author rubensworks
 */
public abstract class PartTypeConfigurable<P extends IPartType<P, S>, S extends IPartState<P>>
    extends PartTypeBase<P, S> {

    @Getter
    private final IGuiContainerProvider settingsGuiProvider;
    @Getter
    private final IGuiContainerProvider offsetsGuiProvider;

    public PartTypeConfigurable(String name, PartRenderPosition partRenderPosition) {
        super(name, partRenderPosition);
        if (hasSettings()) {
            int guiIDSettings = Helpers.getNewId(getModGui(), Helpers.IDType.GUI);
            getModGui().getGuiHandler()
                .registerGUI(
                    (settingsGuiProvider = constructSettingsGuiProvider(guiIDSettings)),
                    ExtendedGuiHandler.PART);
        } else {
            settingsGuiProvider = null;
        }

        if (supportsOffsets()) {
            int guiIDSettings = Helpers.getNewId(getModGui(), Helpers.IDType.GUI);
            getModGui().getGuiHandler()
                .registerGUI(
                    (offsetsGuiProvider = constructPartOffsetsGuiProvider(guiIDSettings)),
                    ExtendedGuiHandler.PART);
        } else {
            offsetsGuiProvider = null;
        }
    }

    protected Class<? extends Container> getSettingsContainer() {
        return ContainerPartSettings.class;
    }

    protected Class<? extends GuiScreen> getSettingsGui() {
        return GuiPartSettings.class;
    }

    protected Class<? extends Container> getOffsetsContainer() {
        return ContainerPartOffset.class;
    }

    protected Class<? extends GuiScreen> getOffsetsGui() {
        return GuiPartOffset.class;
    }

    protected IGuiContainerProvider constructSettingsGuiProvider(int guiId) {
        return new GuiProvider(guiId, getModGui(), getSettingsContainer(), getSettingsGui());
    }

    protected IGuiContainerProvider constructPartOffsetsGuiProvider(int guiId) {
        return new GuiProvider(guiId, getModGui(), getOffsetsContainer(), getOffsetsGui());
    }

    public boolean hasSettings() {
        return true;
    }

    @Data
    public static class GuiProvider implements IGuiContainerProvider {

        private final int guiID;
        private final ModBase modGui;
        private final Class<? extends Container> container;
        private final Class<? extends GuiScreen> gui;

        public GuiProvider(int guiID, ModBase modGui) {
            this(guiID, modGui, ContainerPartSettings.class, GuiPartSettings.class);
        }

        public GuiProvider(int guiID, ModBase modGui, Class<? extends Container> container,
            Class<? extends GuiScreen> gui) {
            this.guiID = guiID;
            this.modGui = modGui;
            this.container = container;
            this.gui = gui;
        }

        @Override
        public Class<? extends Container> getContainer() {
            return container;
        }

        @Override
        public Class<? extends GuiScreen> getGui() {
            return gui;
        }
    }
}
