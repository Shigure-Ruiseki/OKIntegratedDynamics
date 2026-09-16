package ruiseki.integratednbt.evaluate.nbt.path;

import java.util.regex.Pattern;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

import org.apache.commons.lang3.StringUtils;

/**
 * @author rubensworks
 */
public class KeySegment implements Segment {

    private final String key;
    private static final Pattern NON_SPECIAL = Pattern.compile("^[a-zA-Z_0-9]+$");

    public KeySegment(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        KeySegment that = (KeySegment) o;

        return this.key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return this.key.hashCode();
    }

    @Override
    public String getDisplayText() {
        return this.key;
    }

    @Override
    public String getCompactDisplayText() {
        return "." + this.key;
    }

    @Override
    public NBTBase access(NBTBase parent) {
        if (parent instanceof NBTTagCompound) {
            return ((NBTTagCompound) parent).getCompoundTag(this.key);
        } else {
            return null;
        }
    }

    @Override
    public void buildCyclopsNBTPath(StringBuilder stringBuilder) {
        // .length is reserved in Cyclops NBT Path
        if (NON_SPECIAL.matcher(this.key)
            .matches() && !this.key.equals("length")) {
            stringBuilder.append('.')
                .append(this.key);
        } else {
            // Cyclops NBT Path currently does not support escaping
            stringBuilder.append("[\"")
                .append(StringUtils.replace(StringUtils.replace(this.key, "\\", "\\\\"), "\"", "\\\""))
                .append("\"]");
        }
    }
}
