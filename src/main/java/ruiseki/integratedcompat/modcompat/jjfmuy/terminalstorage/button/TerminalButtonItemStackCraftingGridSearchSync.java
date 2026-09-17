package ruiseki.integratedcompat.modcompat.jjfmuy.terminalstorage.button;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalButton;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabClient;
import ruiseki.integratedterminals.client.gui.image.Images;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentClient;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentCommon;
import ruiseki.integratedterminals.inventory.container.TerminalStorageState;
import ruiseki.okcore.client.gui.component.button.GuiButtonImage;
import ruiseki.okcore.client.gui.image.Image;
import ruiseki.okcore.helper.LangHelpers;

public class TerminalButtonItemStackCraftingGridSearchSync implements
    ITerminalButton<TerminalStorageTabIngredientComponentClient<?, ?>, TerminalStorageTabIngredientComponentCommon<?, ?>, GuiButtonImage> {

    private final String mod;
    private final TerminalStorageState state;
    private final String buttonName;
    private final ITerminalStorageTabClient<?> clientTab;
    private final Image image;

    private boolean active;

    public TerminalButtonItemStackCraftingGridSearchSync(String mod, TerminalStorageState state,
        ITerminalStorageTabClient<?> clientTab, Image image) {
        this.mod = mod;
        this.state = state;
        this.buttonName = "itemstack_grid_" + mod + "searchsync";
        this.clientTab = clientTab;
        this.image = image;

        reloadFromState();
    }

    @Override
    public void reloadFromState() {
        if (state.hasButton(
            clientTab.getTabSettingsName()
                .toString(),
            this.buttonName)) {
            NBTTagCompound data = (NBTTagCompound) state.getButton(
                clientTab.getTabSettingsName()
                    .toString(),
                this.buttonName);
            this.active = data.getBoolean("active");
        } else {
            this.active = false;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiButtonImage createButton(int x, int y) {
        return new GuiButtonImage(
            x,
            y,
            LangHelpers.localize("gui.integratedcompat.terminal_storage.craftinggrid." + mod + "sync"),
            (b) -> {},
            active ? Images.BUTTON_BACKGROUND_ACTIVE : Images.BUTTON_BACKGROUND_INACTIVE,
            this.image);
    }

    @Override
    public void onClick(TerminalStorageTabIngredientComponentClient<?, ?> clientTab,
        @Nullable TerminalStorageTabIngredientComponentCommon<?, ?> commonTab, GuiButtonImage guiButton, int channel,
        int mouseButton) {
        this.active = !this.active;

        NBTTagCompound data = new NBTTagCompound();
        data.setBoolean("active", active);
        state.setButton(
            clientTab.getName()
                .toString(),
            this.buttonName,
            data);
    }

    @Override
    public String getTranslationKey() {
        return "gui.integratedcompat.terminal_storage.craftinggrid." + mod + "sync";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getTooltip(EntityPlayer player, boolean tooltipFlag, List<String> lines) {
        lines.add(
            EnumChatFormatting.GRAY
                + LangHelpers.localize("gui.integratedcompat.terminal_storage.craftinggrid." + mod + "sync.info"));
        lines.add(
            EnumChatFormatting.ITALIC
                + LangHelpers.localize(active ? "general.okcore.info.enabled" : "general.okcore.info.disabled"));
    }

    public boolean isActive() {
        return active;
    }

    public static boolean isSearchSynced(ITerminalStorageTabClient<?> clientTab) {
        for (ITerminalButton<?, ?, ?> button : clientTab.getButtons()) {
            if (button instanceof TerminalButtonItemStackCraftingGridSearchSync) {
                return ((TerminalButtonItemStackCraftingGridSearchSync) button).isActive();
            }
        }
        return false;
    }
}
