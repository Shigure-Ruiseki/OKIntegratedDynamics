package ruiseki.integratedterminals.core.helpers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

@SideOnly(Side.CLIENT)
public class TerminalClientUtils {

    public static FontRenderer getFontRenderer() {
        return Minecraft.getMinecraft().fontRenderer;
    }

    public static EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    public static List<String> getTooltip(ItemStack stack) {
        EntityPlayer player = getClientPlayer();
        if (player == null) return null;
        return stack.getTooltip(player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
    }

    public static TextureManager getTextureManager() {
        return Minecraft.getMinecraft().getTextureManager();
    }
}
