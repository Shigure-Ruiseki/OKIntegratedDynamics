package ruiseki.integratedterminals.core.terminalstorage.button;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.Reference;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalButton;
import ruiseki.integratedterminals.client.gui.image.Images;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentClient;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentItemStackCraftingCommon;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientItemStackCraftingGridClear;
import ruiseki.okcore.client.gui.component.button.GuiButtonImage;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;

/**
 * A button for clearing the crafting grid.
 *
 * @author rubensworks
 */
public class TerminalButtonItemStackCraftingGridClear<T> implements
    ITerminalButton<TerminalStorageTabIngredientComponentClient<T, ?>, TerminalStorageTabIngredientComponentItemStackCraftingCommon, GuiButtonImage> {

    @Override
    public void reloadFromState() {

    }

    @Override
    public int getX(int guiLeft, int offset, int gridXSize, int gridYSize, int playerInventoryOffsetX,
        int playerInventoryOffsetY) {
        return guiLeft + (gridXSize / 2) + 32 - (playerInventoryOffsetX > 0 ? 107 : 0);
    }

    @Override
    public int getY(int guiTop, int offset, int gridXSize, int gridYSize, int playerInventoryOffsetX,
        int playerInventoryOffsetY) {
        return guiTop + gridYSize + 59;
    }

    @Override
    public boolean isInLeftColumn() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiButtonImage createButton(int x, int y) {
        return new GuiButtonImage(0, x, y, Images.BUTTON_SMALL_BACKGROUND_INACTIVE, Images.BUTTON_SMALL_OVERLAY_CROSS);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onClick(TerminalStorageTabIngredientComponentClient<T, ?> clientTab,
        TerminalStorageTabIngredientComponentItemStackCraftingCommon commomTab, GuiButtonImage guiButton, int channel,
        int mouseButton) {
        boolean toStorage = !MinecraftHelpers.isShifted();
        TerminalButtonItemStackCraftingGridClear.clearGrid(commomTab, channel, toStorage);
    }

    @Override
    public String getTranslationKey() {
        return "gui.integratedterminals.terminal_storage.craftinggrid.clear";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getTooltip(EntityPlayer player, boolean tooltipFlag, List<String> lines) {
        lines.add(
            EnumChatFormatting.GRAY
                + LangHelpers.localize("gui." + Reference.MOD_ID + ".terminal_storage.craftinggrid.clear.info"));
    }

    public static void clearGrid(TerminalStorageTabIngredientComponentItemStackCraftingCommon commomTab, int channel,
        boolean toStorage) {
        IntegratedTerminals._instance.getPacketHandler()
            .sendToServer(
                new TerminalStorageIngredientItemStackCraftingGridClear(
                    commomTab.getName()
                        .toString(),
                    channel,
                    toStorage));
        commomTab.getInventoryCraftResult()
            .setInventorySlotContents(0, null);
    }
}
