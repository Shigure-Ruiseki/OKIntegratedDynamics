package ruiseki.integratedterminals.core.terminalstorage.button;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;

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

/**
 * A button for clearing the crafting grid.
 *
 * @author rubensworks
 */
public class TerminalButtonItemStackCraftingGridClear<T> implements
    ITerminalButton<TerminalStorageTabIngredientComponentClient<T, ?>, TerminalStorageTabIngredientComponentItemStackCraftingCommon, GuiButtonImage> {

    @Override
    public int getX(int guiLeft, int offset) {
        return guiLeft + 85;
    }

    @Override
    public int getY(int guiTop, int offset) {
        return guiTop + 57;
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
        boolean toStorage = (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
        TerminalButtonItemStackCraftingGridClear.clearGrid(commomTab, channel, toStorage);
    }

    @Override
    public String getTranslationKey() {
        return "gui.integratedterminals.terminal_storage.craftinggrid.clear";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getTooltip(EntityPlayer player, boolean tooltipFlag, List<String> lines) {
        lines.add(LangHelpers.localize("gui." + Reference.MOD_ID + ".terminal_storage.craftinggrid.clear.info"));
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
