package ruiseki.integratedterminals.proxy.guiprovider;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integratedterminals.client.gui.container.GuiTerminalStorageCraftingOptionAmountPart;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageCraftingOptionAmountPart;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.inventory.IGuiContainerProvider;

/**
 * @author rubensworks
 */
public class GuiProviderTerminalStorageCraftingOptionAmountPart implements IGuiContainerProvider {

    private final int guiID;
    private final ModBase modGui;

    public GuiProviderTerminalStorageCraftingOptionAmountPart(int guiID, ModBase modGui) {
        this.guiID = guiID;
        this.modGui = modGui;
    }

    @Override
    public int getGuiID() {
        return guiID;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerTerminalStorageCraftingOptionAmountPart.class;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends GuiScreen> getGui() {
        return GuiTerminalStorageCraftingOptionAmountPart.class;
    }

    @Override
    public ModBase getModGui() {
        return modGui;
    }

}
