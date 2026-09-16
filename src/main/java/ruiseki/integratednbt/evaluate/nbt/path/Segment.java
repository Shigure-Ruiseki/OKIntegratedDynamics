package ruiseki.integratednbt.evaluate.nbt.path;

import net.minecraft.nbt.NBTBase;

/**
 * @author rubensworks
 */
public interface Segment {

    String getDisplayText();

    String getCompactDisplayText();

    NBTBase access(NBTBase parent);

    void buildCyclopsNBTPath(StringBuilder stringBuilder);
}
