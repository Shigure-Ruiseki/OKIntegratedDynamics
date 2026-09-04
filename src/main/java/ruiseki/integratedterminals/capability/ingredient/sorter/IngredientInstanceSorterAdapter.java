package ruiseki.integratedterminals.capability.ingredient.sorter;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integratedterminals.Reference;
import ruiseki.integratedterminals.api.ingredient.IIngredientInstanceSorter;
import ruiseki.okcore.client.gui.image.IImage;
import ruiseki.okcore.helper.LangHelpers;

/**
 * An adapter implementation of {@link IIngredientInstanceSorter}.
 *
 * @author rubensworks
 */
public abstract class IngredientInstanceSorterAdapter<T> implements IIngredientInstanceSorter<T> {

    private final IImage icon;
    private final String unlocalizedName;

    public IngredientInstanceSorterAdapter(IImage icon, String ingredientType, String kind) {
        this.icon = icon;
        this.unlocalizedName = "gui." + Reference.MOD_ID + ".terminal_storage.sort." + ingredientType + "." + kind;
    }

    @Override
    public IImage getIcon() {
        return icon;
    }

    @Override
    public String getTranslationKey() {
        return this.unlocalizedName;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getTooltip(EntityPlayer player, boolean tooltipFlag, List<String> lines) {
        lines.add(EnumChatFormatting.GRAY + LangHelpers.localize(this.unlocalizedName + ".info"));
    }
}
