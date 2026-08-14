package ruiseki.integratedterminals.api.ingredient;

import java.util.Comparator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.client.gui.image.IImage;

/**
 * A sorting comparator for ingredient instances.
 *
 * These don't have to be 0-equals-safe,
 * meaning that non-equal instances don't necessarily have to return a non-0 value.
 * This is because these sorters are typically chained.
 *
 * @author rubensworks
 */
public interface IIngredientInstanceSorter<T> extends Comparator<T> {

    /**
     * @return The icon that can be used to represent this sorter.
     */
    public IImage getIcon();

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
