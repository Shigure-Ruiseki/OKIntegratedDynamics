package ruiseki.integrateddynamics.api.client.gui.subgui;

import java.util.List;

import net.minecraft.client.gui.Gui;
import net.minecraft.inventory.Container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import ruiseki.okcore.helper.LangHelpers;

/**
 * An element inside the logic programmer.
 *
 * @param <G> The type of gui.
 * @param <C> The type of container.
 * @param <S> The sub gui box type.
 * @author rubensworks
 */
public interface IGuiInputElement<S extends ISubGuiBox, G extends Gui, C extends Container> {

    /**
     * @return Localized name used for rendering.
     */
    public String getLocalizedNameFull();

    /**
     * @param lines The list to add tooltip lines to.
     */
    public void loadTooltip(List<String> lines);

    /**
     * @return The render pattern.
     */
    public IConfigRenderPattern getRenderPattern();

    /**
     * Called when this element is activated.
     */
    public void activate();

    /**
     * Called when this element is deactivated.
     */
    public void deactivate();

    /**
     * Validates the current state of the element.
     *
     * @return An error or null.
     */
    public LangHelpers.UnlocalizedString validate();

    /**
     * @return The color used to identify this element.
     */
    public int getColor();

    /**
     * @return The symbol used to identify this element.
     */
    public String getSymbol();

    /**
     * @param baseX     Base x
     * @param baseY     Base y
     * @param maxWidth  Max width
     * @param maxHeight Max height
     * @param gui       The parent gui
     * @param container The parent container
     * @return A subgui that is shown when activated.
     */
    @SideOnly(Side.CLIENT)
    public S createSubGui(int baseX, int baseY, int maxWidth, int maxHeight, G gui, C container);

}
