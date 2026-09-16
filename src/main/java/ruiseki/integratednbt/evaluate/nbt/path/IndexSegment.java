package ruiseki.integratednbt.evaluate.nbt.path;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;

import ruiseki.okcore.helper.LangHelpers;

/**
 * @author rubensworks
 */
public class IndexSegment implements Segment {

    private final int index;

    public IndexSegment(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        IndexSegment that = (IndexSegment) o;

        return this.index == that.index;
    }

    @Override
    public int hashCode() {
        return this.index;
    }

    @Override
    public String getDisplayText() {
        return LangHelpers.localize("integratednbt:nbt_extractor.index", String.valueOf(index));
    }

    @Override
    public String getCompactDisplayText() {
        return "[" + this.index + "]";
    }

    @Override
    public NBTBase access(NBTBase parent) {
        if (parent instanceof NBTTagList parentList) {
            if (this.index < 0 || this.index >= parentList.tagCount()) {
                return null;
            }

            NBTBase base = parentList.getCompoundTagAt(this.index);

            if (base == null || base.getId() == 0 /* NBTTagEnd */) {
                return null;
            }
            return base;
        } else {
            return null;
        }
    }

    @Override
    public void buildCyclopsNBTPath(StringBuilder stringBuilder) {
        stringBuilder.append("[")
            .append(this.index)
            .append("]");
    }
}
