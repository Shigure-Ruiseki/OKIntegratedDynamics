package ruiseki.integratedterminals.api.terminalstorage;

import net.minecraft.client.gui.inventory.GuiContainer;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integratedterminals.client.gui.container.GuiTerminalStorage;

/**
 * A single slot in a storage terminal
 * 
 * @author rubensworks
 */
public interface ITerminalStorageSlot {

    @SideOnly(Side.CLIENT)
    public void drawGuiContainerLayer(GuiContainer gui, GuiTerminalStorage.DrawLayer layer, float partialTick, int x,
        int y, int mouseX, int mouseY, ITerminalStorageTabClient tab, int channel, @Nullable String label);

}
