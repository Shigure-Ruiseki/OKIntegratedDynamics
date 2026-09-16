package ruiseki.integratednbt.evaluate.nbt.path;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import ruiseki.integratednbt.IntegratedNbt;
import ruiseki.okcore.helper.LangHelpers;

public class SegmentedNbtPath {

    private static final int MAX_EXTRACTION_DEPTH = 128;
    private static final String KEY_PATH = "path";
    private static final String KEY_TYPE = "type";
    private static final String TYPE_KEY = "key";
    private static final String KEY_KEY = "key";
    private static final String TYPE_INDEX = "index";
    private static final String KEY_INDEX = "index";

    private final List<Segment> segments;

    public SegmentedNbtPath(List<Segment> segments) {
        this.segments = new ArrayList<>(segments);
    }

    public SegmentedNbtPath() {
        this.segments = new ArrayList<>();
    }

    public static Optional<SegmentedNbtPath> fromNBT(NBTBase nbt) {
        if (nbt == null) {
            return Optional.empty();
        }

        try {
            if (nbt instanceof NBTTagCompound) {
                nbt = ((NBTTagCompound) nbt).getTag(KEY_PATH);
            }

            if (!(nbt instanceof NBTTagList list)) {
                return Optional.empty();
            }

            /* 10 = NBTTagCompound */
            if (list.func_150303_d() != 10 && list.tagCount() != 0) {
                return Optional.empty();
            }

            if (list.tagCount() > MAX_EXTRACTION_DEPTH) {
                return Optional.empty();
            }

            List<Segment> segments = new ArrayList<>(list.tagCount());

            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound compound = list.getCompoundTagAt(i);
                String type = compound.getString(KEY_TYPE);

                if (type.isEmpty()) {
                    return Optional.empty();
                }

                if (TYPE_KEY.equals(type)) {
                    if (!compound.hasKey(KEY_KEY)) {
                        return Optional.empty();
                    }
                    String key = compound.getString(KEY_KEY);
                    if (key.isEmpty()) {
                        return Optional.empty();
                    }
                    segments.add(new KeySegment(key));

                } else if (TYPE_INDEX.equals(type)) {
                    if (!compound.hasKey(KEY_INDEX)) {
                        return Optional.empty();
                    }
                    int index = compound.getInteger(KEY_INDEX);
                    if (index < 0) {
                        return Optional.empty();
                    }
                    segments.add(new IndexSegment(index));

                } else {
                    return Optional.empty();
                }
            }

            return Optional.of(new SegmentedNbtPath(segments));

        } catch (Exception ex) {
            ex.printStackTrace();
            IntegratedNbt.clog("Failed to decode NBT for ExtractionPath.");
            return Optional.empty();
        }
    }

    public void pushKey(String key) {
        this.segments.add(new KeySegment(key));
    }

    public void pushIndex(int index) {
        this.segments.add(new IndexSegment(index));
    }

    public void pop() {
        if (!this.segments.isEmpty()) {
            this.segments.remove(this.segments.size() - 1);
        }
    }

    public SegmentedNbtPath copy() {
        return new SegmentedNbtPath(new ArrayList<>(this.segments));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        SegmentedNbtPath that = (SegmentedNbtPath) o;
        return this.segments.equals(that.segments);
    }

    @Override
    public int hashCode() {
        return this.segments.hashCode();
    }

    public NBTTagCompound toNBTCompound() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag(KEY_PATH, this.toNBT());
        return compound;
    }

    public NBTTagList toNBT() {
        NBTTagList list = new NBTTagList();
        for (Segment segment : this.segments) {
            if (segment instanceof KeySegment keySegment) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString(KEY_TYPE, TYPE_KEY);
                tag.setString(KEY_KEY, keySegment.getKey());
                list.appendTag(tag);
            } else if (segment instanceof IndexSegment indexSegment) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString(KEY_TYPE, TYPE_INDEX);
                tag.setInteger(KEY_INDEX, indexSegment.getIndex());
                list.appendTag(tag);
            }
        }
        return list;
    }

    public NBTBase extract(NBTBase source) {
        for (Segment segment : this.segments) {
            if (source == null) {
                return null;
            }
            source = segment.access(source);
        }
        return source;
    }

    public String getDisplayText() {
        if (this.segments.isEmpty()) {
            return LangHelpers.localize("integratednbt:nbt_extractor.root");
        } else {
            String arrow = LangHelpers.localize("integratednbt:nbt_extractor.arrow");
            StringBuilder sb = new StringBuilder(LangHelpers.localize("integratednbt:nbt_extractor.root"));
            for (Segment segment : this.segments) {
                sb.append(arrow)
                    .append(segment.getDisplayText());
            }
            return sb.toString();
        }
    }

    public String getCompactDisplayText() {
        if (this.segments.isEmpty()) {
            return "id";
        } else {
            StringBuilder sb = new StringBuilder();
            for (Segment segment : this.segments) {
                sb.append(segment.getCompactDisplayText());
            }
            return sb.toString();
        }
    }

    public int getDepth() {
        return this.segments.size();
    }

    public String getCyclopsNBTPath() {
        StringBuilder stringBuilder = new StringBuilder().append('$');
        for (Segment segment : this.segments) {
            segment.buildCyclopsNBTPath(stringBuilder);
        }
        return stringBuilder.toString();
    }
}
