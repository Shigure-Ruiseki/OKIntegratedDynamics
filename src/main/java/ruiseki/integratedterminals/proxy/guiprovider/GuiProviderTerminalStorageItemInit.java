package ruiseki.integratedterminals.proxy.guiprovider;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integratedterminals.client.gui.container.GuiTerminalStorageItem;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageItem;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.inventory.IGuiContainerProvider;

/**
 * @author rubensworks
 */
public class GuiProviderTerminalStorageItemInit implements IGuiContainerProvider {

    private final int guiID;
    private final ModBase modGui;

    public GuiProviderTerminalStorageItemInit(int guiID, ModBase modGui) {
        this.guiID = guiID;
        this.modGui = modGui;
    }

    @Override
    public int getGuiID() {
        return guiID;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerTerminalStorageItem.class;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends GuiScreen> getGui() {
        return GuiTerminalStorageItem.class;
    }

    @Override
    public ModBase getModGui() {
        return modGui;
    }

}
