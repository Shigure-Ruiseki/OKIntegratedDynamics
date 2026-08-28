package ruiseki.integratedterminals.api.terminalstorage.event;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

import org.apache.commons.lang3.tuple.Pair;

import cpw.mods.fml.common.eventhandler.Event;

/**
 * An event that is emitted on the Forge event bus to determine the width and height of the terminal storage screen.
 * 
 * @author rubensworks
 */
public class TerminalStorageScreenSizeEvent extends Event {

    private int width;
    private int height;

    public TerminalStorageScreenSizeEvent(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public static Pair<Integer, Integer> getWidthHeight() {
        TerminalStorageScreenSizeEvent event = new TerminalStorageScreenSizeEvent(
            Minecraft.getMinecraft().displayWidth,
            Minecraft.getMinecraft().displayHeight);
        MinecraftForge.EVENT_BUS.post(event);
        return Pair.of(event.getWidth(), event.getHeight());
    }
}
