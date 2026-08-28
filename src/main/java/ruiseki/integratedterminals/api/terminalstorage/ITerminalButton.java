package ruiseki.integratedterminals.api.terminalstorage;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * @author rubensworks
 */
public interface ITerminalButton<C extends ITerminalStorageTabClient, O extends ITerminalStorageTabCommon, B extends GuiButton> {

    /**
     * Reload the button's visual representation based on the container's state.
     */
    public void reloadFromState();

    /**
     * Calculate the final X position for this button.
     *
     * @param guiLeft   Gui left X
     * @param offset    X offset.
     * @param gridXSize The X size of the grid.
     * @param gridYSize The Y size of the grid.
     * @return The final X position.
     */
    public default int getX(int guiLeft, int offset, int gridXSize, int gridYSize) {
        return guiLeft + offset;
    }

    /**
     * Calculate the final Y position for this button.
     *
     * @param guiTop    Gui top Y
     * @param offset    Y offset.
     * @param gridXSize The X size of the grid.
     * @param gridYSize The Y size of the grid.
     * @return The final Y position.
     */
    public default int getY(int guiTop, int offset, int gridXSize, int gridYSize) {
        return guiTop + offset;
    }

    /**
     * @return If the button should be placed in the left grid column.
     */
    public default boolean isInLeftColumn() {
        return true;
    }

    /**
     * Create a gui button for displaying this button.
     *
     * @param x The button X position.
     * @param y The button Y position.
     * @return The gui button.
     */
    @SideOnly(Side.CLIENT)
    public B createButton(int x, int y);

    /**
     * Callback for when the gui button has been clicked.
     *
     * @param clientTab   The client tab in which the button was clicked.
     * @param commonTab   The common tab in which the button was clicked.
     * @param guiButton   The gui button.
     * @param channel     The active channel.
     * @param mouseButton The mouse button that was used to click with.
     */
    @SideOnly(Side.CLIENT)
    public void onClick(C clientTab, @Nullable O commonTab, B guiButton, int channel, int mouseButton);

    /**
     * @return The unlocalized name
     */
    public String getTranslationKey();

    /**
     * Get the tooltip of this sorter.
     *
     * @param player      The player that is requesting the tooltip.
     * @param tooltipFlag The tooltip flag.
     * @param lines       The tooltip lines.
     */
    @SideOnly(Side.CLIENT)
    public void getTooltip(EntityPlayer player, boolean tooltipFlag, List<String> lines);

}
