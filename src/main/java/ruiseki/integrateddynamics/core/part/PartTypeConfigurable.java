package ruiseki.integrateddynamics.core.part;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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

    protected IGuiContainerProvider constructSettingsGuiProvider(int guiId) {
        return new GuiProviderBase(guiId, getModGui()) {

            @Override
            public Class<? extends Container> getContainer() {
                return ContainerPartSettings.class;
            }

            @Override
            @SideOnly(Side.CLIENT)
            public Class<? extends GuiScreen> getGui() {
                return GuiPartSettings.class;
            }
        };
    }

    protected IGuiContainerProvider constructPartOffsetsGuiProvider(int guiId) {
        return new GuiProviderBase(guiId, getModGui()) {

            @Override
            public Class<? extends Container> getContainer() {
                return ContainerPartOffset.class;
            }

            @Override
            @SideOnly(Side.CLIENT)
            public Class<? extends GuiScreen> getGui() {
                return GuiPartOffset.class;
            }
        };
    }

    public boolean hasSettings() {
        return true;
    }

    @Data
    public static abstract class GuiProviderBase implements IGuiContainerProvider {

        private final int guiID;
        private final ModBase modGui;

        public GuiProviderBase(int guiID, ModBase modGui) {
            this.guiID = guiID;
            this.modGui = modGui;
        }
    }

}
