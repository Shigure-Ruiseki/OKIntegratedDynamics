package ruiseki.integratedterminals.core.terminalstorage.button;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.Reference;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalButton;
import ruiseki.integratedterminals.client.gui.image.Images;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentClient;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentItemStackCraftingCommon;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientItemStackCraftingGridBalance;
import ruiseki.okcore.client.gui.component.button.GuiButtonImage;
import ruiseki.okcore.helper.LangHelpers;

/**
 * A button for balancing all slots in the crafting grid.
 *
 * @author rubensworks
 */
public class TerminalButtonItemStackCraftingGridBalance<T> implements
    ITerminalButton<TerminalStorageTabIngredientComponentClient<T, ?>, TerminalStorageTabIngredientComponentItemStackCraftingCommon, GuiButtonImage> {

    @Override
    public int getX(int guiLeft, int offset) {
        return guiLeft + 85;
    }

    @Override
    public int getY(int guiTop, int offset) {
        return guiTop + 67;
    }

    @Override
    public boolean isInLeftColumn() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiButtonImage createButton(int x, int y) {
        return new GuiButtonImage(0, x, y, Images.BUTTON_SMALL_BACKGROUND_INACTIVE, Images.BUTTON_SMALL_OVERLAY_SQUARE);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onClick(TerminalStorageTabIngredientComponentClient<T, ?> clientTab,
        TerminalStorageTabIngredientComponentItemStackCraftingCommon commomTab, GuiButtonImage guiButton, int channel,
        int mouseButton) {
        IntegratedTerminals._instance.getPacketHandler()
            .sendToServer(
                new TerminalStorageIngredientItemStackCraftingGridBalance(
                    commomTab.getName()
                        .toString()));
    }

    @Override
    public String getTranslationKey() {
        return "gui.integratedterminals.terminal_storage.craftinggrid.balance";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getTooltip(EntityPlayer player, boolean tooltipFlag, List<String> lines) {
        lines.add(LangHelpers.localize("gui." + Reference.MOD_ID + ".terminal_storage.craftinggrid.balance.info"));
    }
}
