package ruiseki.integratedterminals.core.terminalstorage.button;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Predicate;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integratedterminals.Reference;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalButton;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabClient;
import ruiseki.integratedterminals.client.gui.image.Images;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentClient;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentCommon;
import ruiseki.integratedterminals.inventory.container.TerminalStorageState;
import ruiseki.okcore.client.gui.component.button.GuiButtonImage;
import ruiseki.okcore.client.gui.image.IImage;
import ruiseki.okcore.helper.LangHelpers;

/**
 * A button for clearing the crafting grid.
 * 
 * @author rubensworks
 */
public class TerminalButtonFilterCrafting<T> implements
    ITerminalButton<TerminalStorageTabIngredientComponentClient<T, ?>, TerminalStorageTabIngredientComponentCommon<T, ?>, GuiButtonImage> {

    private final TerminalStorageState state;
    private final String buttonName;

    private FilterType active;

    public TerminalButtonFilterCrafting(TerminalStorageState state, ITerminalStorageTabClient<?> clientTab) {
        this.state = state;
        this.buttonName = "filter_crafting";

        if (state.hasButton(
            clientTab.getName()
                .toString(),
            this.buttonName)) {
            NBTTagCompound data = (NBTTagCompound) state.getButton(
                clientTab.getName()
                    .toString(),
                this.buttonName);
            this.active = FilterType.values()[data.getInteger("active")];
        } else {
            this.active = FilterType.ALL;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiButtonImage createButton(int x, int y) {
        return new GuiButtonImage(
            0,
            x,
            y,
            active == FilterType.ALL ? Images.BUTTON_BACKGROUND_INACTIVE : Images.BUTTON_BACKGROUND_ACTIVE,
            active.getImage());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onClick(TerminalStorageTabIngredientComponentClient<T, ?> clientTab,
        TerminalStorageTabIngredientComponentCommon<T, ?> commomTab, GuiButtonImage guiButton, int channel,
        int mouseButton) {
        this.active = mouseButton == 0 ? FilterType.values()[(this.active.ordinal() + 1) % FilterType.values().length]
            : FilterType.ALL;

        NBTTagCompound data = new NBTTagCompound();
        data.setInteger("active", active.ordinal());
        state.setButton(
            clientTab.getName()
                .toString(),
            this.buttonName,
            data);

        clientTab.resetFilteredIngredientsViews(channel);
    }

    @Override
    public String getTranslationKey() {
        return "gui." + Reference.MOD_ID + ".terminal_storage.crafting.filter";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getTooltip(EntityPlayer player, boolean tooltipFlag, List<String> lines) {
        lines.add(LangHelpers.localize("gui." + Reference.MOD_ID + ".terminal_storage.crafting.filter.info"));
        lines.add(LangHelpers.localize(active.getLabel()));
    }

    public Predicate<TerminalStorageTabIngredientComponentClient.InstanceWithMetadata<T>> getEffectiveFilter() {
        return (Predicate) active.getFilter();
    }

    public static enum FilterType {

        ALL(Images.BUTTON_MIDDLE_FILTER_CRAFTING_ALL,
            "gui.integratedterminals.terminal_storage.crafting.filter.type.all", i -> true),
        STORAGE(Images.BUTTON_MIDDLE_FILTER_CRAFTING_STORAGE,
            "gui.integratedterminals.terminal_storage.crafting.filter.type.storage",
            i -> i.getCraftingOption() == null),
        CRAFTABLE(Images.BUTTON_MIDDLE_FILTER_CRAFTING_CRAFTABLE,
            "gui.integratedterminals.terminal_storage.crafting.filter.type.craftable",
            i -> i.getCraftingOption() != null);

        @Nullable
        private final IImage image;
        private final String label;
        private final Predicate<TerminalStorageTabIngredientComponentClient.InstanceWithMetadata<?>> filter;

        FilterType(@Nullable IImage image, String label,
            Predicate<TerminalStorageTabIngredientComponentClient.InstanceWithMetadata<?>> filter) {
            this.image = image;
            this.label = label;
            this.filter = filter;
        }

        @Nullable
        public IImage getImage() {
            return image;
        }

        public String getLabel() {
            return label;
        }

        public Predicate<TerminalStorageTabIngredientComponentClient.InstanceWithMetadata<?>> getFilter() {
            return filter;
        }
    }
}
