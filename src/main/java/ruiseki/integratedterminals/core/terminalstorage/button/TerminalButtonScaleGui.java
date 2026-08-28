package ruiseki.integratedterminals.core.terminalstorage.button;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import org.jetbrains.annotations.Nullable;

import ruiseki.integratedterminals.GeneralConfig;
import ruiseki.integratedterminals.Reference;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalButton;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalRowColumnProvider;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabClient;
import ruiseki.integratedterminals.client.gui.image.Images;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentClient;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentCommon;
import ruiseki.integratedterminals.inventory.container.TerminalStorageState;
import ruiseki.okcore.client.gui.component.button.GuiButtonImage;
import ruiseki.okcore.client.gui.image.IImage;
import ruiseki.okcore.helper.GuiHelpers;
import ruiseki.okcore.helper.LangHelpers;

/**
 * A button for configuring the scale of the gui.
 *
 * @author rubensworks
 */
public class TerminalButtonScaleGui<T> implements
    ITerminalButton<TerminalStorageTabIngredientComponentClient<T, ?>, TerminalStorageTabIngredientComponentCommon<T, ?>, GuiButtonImage> {

    private final TerminalStorageState state;
    private final String buttonName;
    private final ITerminalStorageTabClient<?> clientTab;

    private GuiScale scale;

    public TerminalButtonScaleGui(TerminalStorageState state, ITerminalStorageTabClient<?> clientTab) {
        this.state = state;
        this.buttonName = "gui_scale";
        this.clientTab = clientTab;

        reloadFromState();
    }

    @Override
    public void reloadFromState() {
        if (state.hasButton("minecraft:itemstack", this.buttonName)) {
            NBTTagCompound data = (NBTTagCompound) state.getButton("minecraft:itemstack", this.buttonName);
            this.scale = GuiScale.values()[data.getInteger("scale")];
        } else {
            this.scale = GuiScale.SCALE_XY;
        }
    }

    @Override
    public GuiButtonImage createButton(int x, int y) {
        return new GuiButtonImage(
            0,
            x,
            y,
            scale == GuiScale.SCALE_XY ? Images.BUTTON_BACKGROUND_INACTIVE : Images.BUTTON_BACKGROUND_ACTIVE,
            scale.getImage());
    }

    @Override
    public void onClick(TerminalStorageTabIngredientComponentClient<T, ?> clientTab,
        @Nullable TerminalStorageTabIngredientComponentCommon<T, ?> commonTab, GuiButtonImage guiButton, int channel,
        int mouseButton) {
        this.scale = mouseButton == 0 ? GuiScale.values()[(this.scale.ordinal() + 1) % GuiScale.values().length]
            : GuiScale.SCALE_XY;

        NBTTagCompound data = new NBTTagCompound();
        data.setInteger("scale", scale.ordinal());
        state.setButton(
            clientTab.getTabSettingsName()
                .toString(),
            this.buttonName,
            data);

        clientTab.resetScale();
    }

    @Override
    public String getTranslationKey() {
        return "gui.integratedterminals.terminal_storage.scale";
    }

    @Override
    public void getTooltip(EntityPlayer player, boolean tooltipFlag, List<String> lines) {
        lines.add(
            EnumChatFormatting.GRAY + LangHelpers.localize("gui." + Reference.MOD_ID + ".terminal_storage.scale.info"));
        lines.add(LangHelpers.localize(scale.getLabel()));
    }

    public ITerminalRowColumnProvider getRowColumnProvider() {
        return this.scale.getRowColumnProvider();
    }

    public static enum GuiScale {

        SCALE_XY(Images.BUTTON_MIDDLE_SCALE_XY, "gui.integratedterminals.terminal_storage.scale.scalexy",
            () -> new ITerminalRowColumnProvider.RowsAndColumns(
                (int) Math.min(
                    Math.max(1, Math.ceil((Minecraft.getMinecraft().displayHeight - 146) / GuiHelpers.SLOT_SIZE)),
                    GeneralConfig.guiStorageScaleMaxRows),
                (int) Math.min(
                    Math.max(1, Math.ceil((Minecraft.getMinecraft().displayWidth - 56) / GuiHelpers.SLOT_SIZE)),
                    GeneralConfig.guiStorageScaleMaxColumns))),
        SCALE_Y(Images.BUTTON_MIDDLE_SCALE_Y, "gui.integratedterminals.terminal_storage.scale.scaley",
            () -> new ITerminalRowColumnProvider.RowsAndColumns(
                (int) Math.min(
                    Math.max(1, Math.ceil((Minecraft.getMinecraft().displayHeight - 146) / GuiHelpers.SLOT_SIZE)),
                    GeneralConfig.guiStorageScaleMaxRows),
                GeneralConfig.guiStorageScaleHeightColumns)),
        SCALE_X(Images.BUTTON_MIDDLE_SCALE_X, "gui.integratedterminals.terminal_storage.scale.scalex",
            () -> new ITerminalRowColumnProvider.RowsAndColumns(
                GeneralConfig.guiStorageScaleWidthRows,
                (int) Math.min(
                    Math.max(1, Math.ceil((Minecraft.getMinecraft().displayWidth - 56) / GuiHelpers.SLOT_SIZE)),
                    GeneralConfig.guiStorageScaleMaxColumns))),
        SMALL(Images.BUTTON_MIDDLE_SCALE_SMALL, "gui.integratedterminals.terminal_storage.scale.small",
            () -> new ITerminalRowColumnProvider.RowsAndColumns(
                GeneralConfig.guiStorageScaleSmallRows,
                GeneralConfig.guiStorageScaleSmallColumns)),
        MEDIUM(Images.BUTTON_MIDDLE_SCALE_MEDIUM, "gui.integratedterminals.terminal_storage.scale.medium",
            () -> new ITerminalRowColumnProvider.RowsAndColumns(
                GeneralConfig.guiStorageScaleMediumRows,
                GeneralConfig.guiStorageScaleMediumColumns)),
        LARGE(Images.BUTTON_MIDDLE_SCALE_LARGE, "gui.integratedterminals.terminal_storage.scale.large",
            () -> new ITerminalRowColumnProvider.RowsAndColumns(
                GeneralConfig.guiStorageScaleLargeRows,
                GeneralConfig.guiStorageScaleLargeColumns));

        @Nullable
        private final IImage image;
        private final String label;
        private final ITerminalRowColumnProvider rowColumnProvider;

        GuiScale(@Nullable IImage image, String label, ITerminalRowColumnProvider rowColumnProvider) {
            this.image = image;
            this.label = label;
            this.rowColumnProvider = rowColumnProvider;
        }

        @Nullable
        public IImage getImage() {
            return image;
        }

        public String getLabel() {
            return label;
        }

        public ITerminalRowColumnProvider getRowColumnProvider() {
            return rowColumnProvider;
        }
    }
}
