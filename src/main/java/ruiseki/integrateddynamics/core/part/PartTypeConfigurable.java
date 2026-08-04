package ruiseki.integrateddynamics.core.part;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;

import lombok.Data;
import lombok.Getter;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.core.client.gui.ExtendedGuiHandler;
import ruiseki.integrateddynamics.core.client.gui.container.GuiPartSettings;
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

    public PartTypeConfigurable(String name, RenderPosition renderPosition) {
        super(name, renderPosition);
        if (hasSettings()) {
            int guiIDSettings = Helpers.getNewId(getMod(), Helpers.IDType.GUI);
            getMod().getGuiHandler()
                .registerGUI(
                    (settingsGuiProvider = constructSettingsGuiProvider(guiIDSettings)),
                    ExtendedGuiHandler.PART);
        } else {
            settingsGuiProvider = null;
        }
    }

    protected IGuiContainerProvider constructSettingsGuiProvider(int guiId) {
        return new GuiProviderSettings(guiId, getMod());
    }

    public boolean hasSettings() {
        return true;
    }

    @Data
    public static class GuiProviderSettings implements IGuiContainerProvider {

        private final int guiID;
        private final ModBase mod;

        @Override
        public Class<? extends Container> getContainer() {
            return ContainerPartSettings.class;
        }

        @Override
        public Class<? extends GuiScreen> getGui() {
            return GuiPartSettings.class;
        }
    }

}
