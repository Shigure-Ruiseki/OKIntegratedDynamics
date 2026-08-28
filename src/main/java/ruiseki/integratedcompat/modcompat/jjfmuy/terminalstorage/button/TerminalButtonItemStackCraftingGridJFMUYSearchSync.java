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
import ruiseki.okcore.helper.LangHelpers;

public class TerminalButtonItemStackCraftingGridJFMUYSearchSync implements
    ITerminalButton<TerminalStorageTabIngredientComponentClient<?, ?>, TerminalStorageTabIngredientComponentCommon<?, ?>, GuiButtonImage> {

    private final TerminalStorageState state;
    private final String buttonName;

    private boolean active;

    public TerminalButtonItemStackCraftingGridJFMUYSearchSync(TerminalStorageState state,
        ITerminalStorageTabClient<?> clientTab) {
        this.state = state;
        this.buttonName = "itemstack_grid_jeisearchsync";

        if (state.hasButton(
            clientTab.getName()
                .toString(),
            this.buttonName)) {
            NBTTagCompound data = (NBTTagCompound) state.getButton(
                clientTab.getName()
                    .toString(),
                this.buttonName);
            this.active = data.getBoolean("active");
        } else {
            this.active = false;
        }
    }

    @Override
    public void reloadFromState() {

    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiButtonImage createButton(int x, int y) {
        return new GuiButtonImage(
            0,
            x,
            y,
            active ? Images.BUTTON_BACKGROUND_ACTIVE : Images.BUTTON_BACKGROUND_INACTIVE,
            Images.BUTTON_MIDDLE_JEI_SYNC);
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
        return "gui.integratedcompat.terminal_storage.craftinggrid.jeisync";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getTooltip(EntityPlayer player, boolean tooltipFlag, List<String> lines) {
        lines.add(LangHelpers.localize("gui.integratedcompat.terminal_storage.craftinggrid.jeisync.info"));
        lines.add(
            EnumChatFormatting.ITALIC
                + LangHelpers.localize(active ? "general.okcore.info.enabled" : "general.okcore.info.disabled"));
    }

    public boolean isActive() {
        return active;
    }
}
