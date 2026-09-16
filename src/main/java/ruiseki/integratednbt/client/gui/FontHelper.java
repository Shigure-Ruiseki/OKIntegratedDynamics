package ruiseki.integratednbt.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public abstract class FontHelper {

    public static List<String> wrap(List<String> lines, int lineWidth) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        List<String> result = new ArrayList<>();

        for (String line : lines) {
            if (line == null) continue;
            List<String> splitLines = font.listFormattedStringToWidth(line, lineWidth);
            result.addAll(splitLines);
        }

        return result;
    }
}
