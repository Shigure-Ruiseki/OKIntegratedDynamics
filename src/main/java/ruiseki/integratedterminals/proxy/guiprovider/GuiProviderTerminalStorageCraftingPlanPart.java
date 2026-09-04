package ruiseki.integratedterminals.proxy.guiprovider;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integratedterminals.client.gui.container.GuiTerminalStorageCraftingPlanPart;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageCraftingPlanPart;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.inventory.IGuiContainerProvider;

/**
 * @author rubensworks
 */
public class GuiProviderTerminalStorageCraftingPlanPart implements IGuiContainerProvider {

    private final int guiID;
    private final ModBase modGui;

    public GuiProviderTerminalStorageCraftingPlanPart(int guiID, ModBase modGui) {
        this.guiID = guiID;
        this.modGui = modGui;
    }

    @Override
    public int getGuiID() {
        return guiID;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerTerminalStorageCraftingPlanPart.class;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends GuiScreen> getGui() {
        return GuiTerminalStorageCraftingPlanPart.class;
    }

    @Override
    public ModBase getModGui() {
        return modGui;
    }

}
