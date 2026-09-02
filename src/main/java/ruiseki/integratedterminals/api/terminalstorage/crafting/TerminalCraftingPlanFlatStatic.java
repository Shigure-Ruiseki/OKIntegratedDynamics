package ruiseki.integratedterminals.api.terminalstorage.crafting;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import ruiseki.commoncapabilities.api.ingredient.IPrototypedIngredient;
import ruiseki.commoncapabilities.api.ingredient.PrototypedIngredient;

/**
 * @author rubensworks
 */
public class TerminalCraftingPlanFlatStatic<I> implements ITerminalCraftingPlanFlat<I> {

    private final I id;
    private final List<IPrototypedIngredient<?, ?>> outputs;
    private final List<TerminalCraftingPlanFlatStatic.Entry> entries;
    private TerminalCraftingJobStatus status;
    private TerminalCraftingPlanStatic.Label label;
    @Nullable
    private String unlocalizedLabelOverride;
    private final long tickDuration;
    private final int channel;
    @Nullable
    private final String initiatorName;

    public TerminalCraftingPlanFlatStatic(I id, List<TerminalCraftingPlanFlatStatic.Entry> entries,
        List<IPrototypedIngredient<?, ?>> outputs, TerminalCraftingJobStatus status,
        TerminalCraftingPlanStatic.Label label, long tickDuration, int channel, @Nullable String initiatorName) {
        this.id = id;
        this.entries = entries;
        this.outputs = outputs;
        this.status = status;
        this.label = label;
        this.unlocalizedLabelOverride = null;
        this.tickDuration = tickDuration;
        this.channel = channel;
        this.initiatorName = initiatorName;
    }

    @Override
    public I getId() {
        return id;
    }

    @Override
    public List<TerminalCraftingPlanFlatStatic.Entry> getEntries() {
        return this.entries;
    }

    @Override
    public List<IPrototypedIngredient<?, ?>> getOutputs() {
        return outputs;
    }

    @Override
    public TerminalCraftingJobStatus getStatus() {
        return status;
    }

    public TerminalCraftingPlanStatic.Label getLabel() {
        return label;
    }

    @Nullable
    public String getUnlocalizedLabelOverride() {
        return this.unlocalizedLabelOverride;
    }

    public void setUnlocalizedLabelOverride(@Nullable String unlocalizedLabelOverride) {
        this.unlocalizedLabelOverride = unlocalizedLabelOverride;
    }

    @Override
    public String getUnlocalizedLabel() {
        if (this.getUnlocalizedLabelOverride() == null) {
            return this.label.getUnlocalizedMessage();
        }
        return this.unlocalizedLabelOverride;
    }

    @Override
    public long getTickDuration() {
        return tickDuration;
    }

    @Override
    public int getChannel() {
        return channel;
    }

    @Override
    @Nullable
    public String getInitiatorName() {
        return initiatorName;
    }

    @Override
    public void setError(String unlocalizedError) {
        this.status = TerminalCraftingJobStatus.ERROR;
        this.unlocalizedLabelOverride = unlocalizedError;
    }

    public static <I> NBTTagCompound serialize(TerminalCraftingPlanFlatStatic<I> plan,
        ITerminalStorageTabIngredientCraftingHandler<?, I> handler) {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setTag("id", handler.serializeCraftingJobId(plan.getId()));

        NBTTagList entries = new NBTTagList();
        for (TerminalCraftingPlanFlatStatic.Entry entry : plan.getEntries()) {
            entries.appendTag(TerminalCraftingPlanFlatStatic.Entry.serialize(entry));
        }
        tag.setTag("entries", entries);

        NBTTagList outputs = new NBTTagList();
        for (IPrototypedIngredient<?, ?> output : plan.getOutputs()) {
            outputs.appendTag(IPrototypedIngredient.serialize((PrototypedIngredient) output));
        }
        tag.setTag("outputs", outputs);

        tag.setInteger(
            "status",
            plan.getStatus()
                .ordinal());

        tag.setInteger("label", plan.label.ordinal());
        if (plan.unlocalizedLabelOverride != null) {
            tag.setString("unlocalizedLabelOverride", plan.unlocalizedLabelOverride);
        }

        tag.setLong("tickDuration", plan.getTickDuration());

        tag.setInteger("channel", plan.getChannel());

        if (plan.getInitiatorName() != null) {
            tag.setString("initiatorName", plan.getInitiatorName());
        }

        return tag;
    }

    public static <I> TerminalCraftingPlanFlatStatic<I> deserialize(NBTTagCompound tag,
        ITerminalStorageTabIngredientCraftingHandler<?, I> handler) {
        if (!tag.hasKey("id")) {
            throw new IllegalArgumentException("Could not find an id entry in the given tag");
        }
        if (!tag.hasKey("entries", Constants.NBT.TAG_LIST)) {
            throw new IllegalArgumentException("Could not find a entries entry in the given tag");
        }
        if (!tag.hasKey("outputs", Constants.NBT.TAG_LIST)) {
            throw new IllegalArgumentException("Could not find a outputs entry in the given tag");
        }
        if (!tag.hasKey("status", Constants.NBT.TAG_INT)) {
            throw new IllegalArgumentException("Could not find a status entry in the given tag");
        }
        if (!tag.hasKey("label", Constants.NBT.TAG_INT)) {
            throw new IllegalArgumentException("Could not find a label entry in the given tag");
        }
        if (!tag.hasKey("tickDuration", Constants.NBT.TAG_LONG)) {
            throw new IllegalArgumentException("Could not find a tickDuration entry in the given tag");
        }
        if (!tag.hasKey("channel", Constants.NBT.TAG_INT)) {
            throw new IllegalArgumentException("Could not find a channel entry in the given tag");
        }

        I id = handler.deserializeCraftingJobId(tag.getCompoundTag("id"));

        NBTTagList entriesTag = tag.getTagList("entries", Constants.NBT.TAG_COMPOUND);
        List<TerminalCraftingPlanFlatStatic.Entry> entries = Lists.newArrayListWithExpectedSize(entriesTag.tagCount());
        for (int i = 0; i < entriesTag.tagCount(); i++) {
            entries.add(TerminalCraftingPlanFlatStatic.Entry.deserialize(entriesTag.getCompoundTagAt(i)));
        }

        NBTTagList outputsTag = tag.getTagList("outputs", Constants.NBT.TAG_COMPOUND);
        List<IPrototypedIngredient<?, ?>> outputs = Lists.newArrayListWithExpectedSize(outputsTag.tagCount());
        for (int i = 0; i < outputsTag.tagCount(); i++) {
            outputs.add(IPrototypedIngredient.deserialize(outputsTag.getCompoundTagAt(i)));
        }

        TerminalCraftingJobStatus status = TerminalCraftingJobStatus.values()[tag.getInteger("status")];

        TerminalCraftingPlanStatic.Label label = TerminalCraftingPlanStatic.Label.values()[tag.getInteger("label")];

        String unlocalizedLabelOverride = null;
        if (tag.hasKey("unlocalizedLabelOverride")) {
            unlocalizedLabelOverride = tag.getString("unlocalizedLabelOverride");
        }

        long tickDuration = tag.getLong("tickDuration");

        int channel = tag.getInteger("channel");

        String initiatorName = null;
        if (tag.hasKey("initiatorName", Constants.NBT.TAG_STRING)) {
            initiatorName = tag.getString("initiatorName");
        }

        TerminalCraftingPlanFlatStatic<I> plan = new TerminalCraftingPlanFlatStatic<>(
            id,
            entries,
            outputs,
            status,
            label,
            tickDuration,
            channel,
            initiatorName);
        if (unlocalizedLabelOverride != null) {
            plan.unlocalizedLabelOverride = unlocalizedLabelOverride;
        }
        return plan;
    }

    public static class Entry implements ITerminalCraftingPlanFlat.IEntry {

        private final IPrototypedIngredient<?, ?> instance;
        private long quantityToCraft;
        private long quantityCrafting;
        private long quantityInStorage;
        private long quantityMissing;

        public Entry(IPrototypedIngredient<?, ?> instance, long quantityToCraft, long quantityCrafting,
            long quantityInStorage, long quantityMissing) {
            this.instance = instance;
            this.quantityToCraft = quantityToCraft;
            this.quantityCrafting = quantityCrafting;
            this.quantityInStorage = quantityInStorage;
            this.quantityMissing = quantityMissing;
        }

        public Entry(IPrototypedIngredient<?, ?> instance) {
            this(instance, 0, 0, 0, 0);
        }

        @Override
        public IPrototypedIngredient<?, ?> getInstance() {
            return instance;
        }

        @Override
        public long getQuantityToCraft() {
            return quantityToCraft;
        }

        public void setQuantityToCraft(long quantityToCraft) {
            this.quantityToCraft = quantityToCraft;
        }

        @Override
        public long getQuantityCrafting() {
            return quantityCrafting;
        }

        public void setQuantityCrafting(long quantityCrafting) {
            this.quantityCrafting = quantityCrafting;
        }

        @Override
        public long getQuantityInStorage() {
            return quantityInStorage;
        }

        public void setQuantityInStorage(long quantityInStorage) {
            this.quantityInStorage = quantityInStorage;
        }

        @Override
        public long getQuantityMissing() {
            return quantityMissing;
        }

        public void setQuantityMissing(long quantityMissing) {
            this.quantityMissing = quantityMissing;
        }

        public static NBTTagCompound serialize(TerminalCraftingPlanFlatStatic.Entry entry) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setTag("instance", IPrototypedIngredient.serialize(entry.getInstance()));
            tag.setLong("quantityToCraft", entry.getQuantityToCraft());
            tag.setLong("quantityCrafting", entry.getQuantityCrafting());
            tag.setLong("quantityInStorage", entry.getQuantityInStorage());
            tag.setLong("quantityMissing", entry.getQuantityMissing());
            return tag;
        }

        public static TerminalCraftingPlanFlatStatic.Entry deserialize(NBTTagCompound tag) {
            if (!tag.hasKey("instance", Constants.NBT.TAG_COMPOUND)) {
                throw new IllegalArgumentException("Could not find a instance entry in the given tag");
            }
            if (!tag.hasKey("quantityToCraft", Constants.NBT.TAG_LONG)) {
                throw new IllegalArgumentException("Could not find a quantityToCraft entry in the given tag");
            }
            if (!tag.hasKey("quantityCrafting", Constants.NBT.TAG_LONG)) {
                throw new IllegalArgumentException("Could not find a quantityCrafting entry in the given tag");
            }
            if (!tag.hasKey("quantityInStorage", Constants.NBT.TAG_LONG)) {
                throw new IllegalArgumentException("Could not find a quantityInStorage entry in the given tag");
            }
            if (!tag.hasKey("quantityMissing", Constants.NBT.TAG_LONG)) {
                throw new IllegalArgumentException("Could not find a quantityMissing entry in the given tag");
            }

            IPrototypedIngredient<?, ?> instance = IPrototypedIngredient.deserialize(tag.getCompoundTag("instance"));
            long quantityToCraft = tag.getLong("quantityToCraft");
            long quantityCrafting = tag.getLong("quantityCrafting");
            long quantityInStorage = tag.getLong("quantityInStorage");
            long quantityMissing = tag.getLong("quantityMissing");

            return new TerminalCraftingPlanFlatStatic.Entry(
                instance,
                quantityToCraft,
                quantityCrafting,
                quantityInStorage,
                quantityMissing);
        }

    }
}
