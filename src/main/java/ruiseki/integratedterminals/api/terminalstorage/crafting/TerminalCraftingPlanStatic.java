package ruiseki.integratedterminals.api.terminalstorage.crafting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ruiseki.commoncapabilities.api.ingredient.IIngredientMatcher;
import ruiseki.commoncapabilities.api.ingredient.IPrototypedIngredient;
import ruiseki.commoncapabilities.api.ingredient.PrototypedIngredient;

/**
 * @author rubensworks
 */
public class TerminalCraftingPlanStatic<I> implements ITerminalCraftingPlan<I> {

    private final I id;
    private final List<ITerminalCraftingPlan<I>> dependencies;
    private final List<IPrototypedIngredient<?, ?>> outputs;
    private TerminalCraftingJobStatus status;
    private final long craftingQuantity;
    private final List<IPrototypedIngredient<?, ?>> storageIngredients;
    private final List<List<IPrototypedIngredient<?, ?>>> lastMissingIngredients;
    private TerminalCraftingPlanStatic.Label label;
    @Nullable
    private String unlocalizedLabelOverride;
    private final long tickDuration;
    private final int channel;
    @Nullable
    private final String initiatorName;

    public TerminalCraftingPlanStatic(I id, List<ITerminalCraftingPlan<I>> dependencies,
        List<IPrototypedIngredient<?, ?>> outputs, TerminalCraftingJobStatus status, long craftingQuantity,
        List<IPrototypedIngredient<?, ?>> storageIngredients,
        List<List<IPrototypedIngredient<?, ?>>> lastMissingIngredients, TerminalCraftingPlanStatic.Label label,
        long tickDuration, int channel, @Nullable String initiatorName) {
        this.id = id;
        this.dependencies = dependencies;
        this.outputs = outputs;
        this.status = status;
        this.craftingQuantity = craftingQuantity;
        this.storageIngredients = storageIngredients;
        this.lastMissingIngredients = lastMissingIngredients;
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
    public List<ITerminalCraftingPlan<I>> getDependencies() {
        return dependencies;
    }

    @Override
    public List<IPrototypedIngredient<?, ?>> getOutputs() {
        return outputs;
    }

    @Override
    public TerminalCraftingJobStatus getStatus() {
        return status;
    }

    @Override
    public long getCraftingQuantity() {
        return craftingQuantity;
    }

    @Override
    public List<IPrototypedIngredient<?, ?>> getStorageIngredients() {
        return storageIngredients;
    }

    @Override
    public List<List<IPrototypedIngredient<?, ?>>> getLastMissingIngredients() {
        return lastMissingIngredients;
    }

    public Label getLabel() {
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
        if (this.unlocalizedLabelOverride == null) {
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

    @Override
    public ITerminalCraftingPlanFlat<I> flatten() {
        // Group dependencies by prototype
        IndexedEntries indexedEntries = new IndexedEntries();
        Set<I> handledPlans = new HashSet<>();
        groupDependenciesByPrototype(indexedEntries, handledPlans, this);

        // Make plan
        TerminalCraftingPlanFlatStatic<I> planFlat = new TerminalCraftingPlanFlatStatic<>(
            getId(),
            indexedEntries.getEntries()
                .stream()
                .sorted((e1, e2) -> {
                    if (e1.getQuantityMissing() != e2.getQuantityMissing()) {
                        return Math.toIntExact(e2.getQuantityMissing() - e1.getQuantityMissing());
                    }
                    if (e1.getQuantityCrafting() != e2.getQuantityCrafting()) {
                        return Math.toIntExact(e2.getQuantityCrafting() - e1.getQuantityCrafting());
                    }
                    if (e1.getQuantityToCraft() != e2.getQuantityToCraft()) {
                        return Math.toIntExact(e2.getQuantityToCraft() - e1.getQuantityToCraft());
                    }
                    if (e1.getQuantityInStorage() != e2.getQuantityInStorage()) {
                        return Math.toIntExact(e2.getQuantityInStorage() - e1.getQuantityInStorage());
                    }
                    return 0;
                })
                .toList(),
            getOutputs(),
            getStatus(),
            getLabel(),
            getTickDuration(),
            getChannel(),
            getInitiatorName());
        if (getUnlocalizedLabelOverride() != null) {
            planFlat.setUnlocalizedLabelOverride(getUnlocalizedLabelOverride());
        }
        return planFlat;
    }

    public static class IndexedEntries {

        /**
         * Entries indexed by a canonical list of prototype ingredients.
         * <p>
         * Each key is a list of {@link IPrototypedIngredient} where:
         * <ul>
         * <li>Every element has quantity 1.</li>
         * <li>Every element uses the exact-match-no-quantity condition.</li>
         * </ul>
         * The original (possibly non-normalized) alternatives list is stored inside the
         * {@link TerminalCraftingPlanFlatStatic.Entry} for rendering purposes.
         */
        private final Map<List<IPrototypedIngredient<?, ?>>, TerminalCraftingPlanFlatStatic.Entry> indexedEntries;

        public IndexedEntries() {
            this.indexedEntries = Maps.newHashMap();
        }

        /**
         * Get (or create) the entry corresponding to the given list of alternative ingredients.
         *
         * @param prototypedIngredients A non-empty list of alternatives.
         * @return The corresponding flat plan entry.
         */
        public TerminalCraftingPlanFlatStatic.Entry get(List<IPrototypedIngredient<?, ?>> prototypedIngredients) {
            List<IPrototypedIngredient<?, ?>> key = getPrototypes(prototypedIngredients);
            return indexedEntries.computeIfAbsent(key, k -> new TerminalCraftingPlanFlatStatic.Entry(k));
        }

        /**
         * Build a canonical list of prototype ingredients for the given alternatives.
         * Quantities are normalized to 1 and the exact-match-no-quantity condition is used.
         */
        protected List<IPrototypedIngredient<?, ?>> getPrototypes(
            List<IPrototypedIngredient<?, ?>> prototypedIngredients) {
            List<IPrototypedIngredient<?, ?>> result = new ArrayList<>(prototypedIngredients.size());
            for (IPrototypedIngredient<?, ?> ingredient : prototypedIngredients) {
                IIngredientMatcher matcher = ingredient.getComponent()
                    .getMatcher();
                result.add(
                    new PrototypedIngredient(
                        ingredient.getComponent(),
                        matcher.withQuantity(ingredient.getPrototype(), 1L),
                        matcher.getExactMatchNoQuantityCondition()));
            }
            return result;
        }

        /**
         * Get the quantity associated with the given alternatives list.
         * <p>
         * This is derived from the first element, which is consistent with prior behaviour
         * when only a single prototype was available.
         */
        public static long getQuantity(List<IPrototypedIngredient<?, ?>> prototypedIngredients) {
            if (prototypedIngredients.isEmpty()) {
                return 0;
            }
            IPrototypedIngredient<?, ?> first = prototypedIngredients.get(0);
            IIngredientMatcher matcher = first.getComponent()
                .getMatcher();
            return matcher.getQuantity(first.getPrototype());
        }

        public Collection<TerminalCraftingPlanFlatStatic.Entry> getEntries() {
            return indexedEntries.values();
        }
    }

    protected static <I> void groupDependenciesByPrototype(IndexedEntries indexedEntries, Set<I> handledPlans,
        ITerminalCraftingPlan<I> plan) {
        // Since jobs can have multiple dependents due to job splitting, we only consider each job once during
        // flattening.
        if ((!(plan.getId() instanceof Integer id) || id > 0) && handledPlans.contains(plan.getId())) {
            return;
        }
        handledPlans.add(plan.getId());

        // Determine outputs that are invalid or will be crafted
        for (IPrototypedIngredient<?, ?> output : plan.getOutputs()) {
            List<IPrototypedIngredient<?, ?>> outputs = List.of(output);
            TerminalCraftingPlanFlatStatic.Entry entry = indexedEntries.get(outputs);
            long quantity = IndexedEntries.getQuantity(outputs);

            if (plan.getStatus() == TerminalCraftingJobStatus.ERROR
                || plan.getStatus() == TerminalCraftingJobStatus.INVALID
                || plan.getStatus() == TerminalCraftingJobStatus.INVALID_INPUTS) {
                if (plan.getDependencies()
                    .isEmpty()) {
                    entry.setQuantityMissing(entry.getQuantityMissing() + quantity);
                } else {
                    entry.setQuantityToCraft(entry.getQuantityToCraft() + quantity);
                }
            }
            if (plan.getStatus() == TerminalCraftingJobStatus.QUEUEING
                || plan.getStatus() == TerminalCraftingJobStatus.PENDING_DEPENDENCIES
                || plan.getStatus() == TerminalCraftingJobStatus.PENDING_INPUTS
                || plan.getStatus() == TerminalCraftingJobStatus.UNSTARTED) {
                entry.setQuantityToCraft(entry.getQuantityToCraft() + quantity);
            }
            if (plan.getStatus() == TerminalCraftingJobStatus.CRAFTING
                || plan.getStatus() == TerminalCraftingJobStatus.FINISHED) {
                entry.setQuantityCrafting(entry.getQuantityCrafting() + quantity);
            }
        }

        // Determine storage ingredients
        for (IPrototypedIngredient<?, ?> output : plan.getStorageIngredients()) {
            List<IPrototypedIngredient<?, ?>> outputs = List.of(output);
            TerminalCraftingPlanFlatStatic.Entry entry = indexedEntries.get(outputs);
            long quantity = IndexedEntries.getQuantity(outputs);
            entry.setQuantityInStorage(entry.getQuantityInStorage() + quantity);
        }

        // Determine missing ingredients
        for (List<IPrototypedIngredient<?, ?>> outputVariants : plan.getLastMissingIngredients()) {
            TerminalCraftingPlanFlatStatic.Entry entry = indexedEntries.get(outputVariants);
            long quantity = IndexedEntries.getQuantity(outputVariants);
            entry.setQuantityMissing(entry.getQuantityMissing() + quantity * plan.getCraftingQuantity());
        }

        // Recurse into dependencies
        for (ITerminalCraftingPlan<I> dependency : plan.getDependencies()) {
            groupDependenciesByPrototype(indexedEntries, handledPlans, dependency);
        }
    }

    public static <I> NBTTagCompound serialize(TerminalCraftingPlanStatic<I> plan,
        ITerminalStorageTabIngredientCraftingHandler<?, I> handler) {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setTag("id", handler.serializeCraftingJobId(plan.getId()));

        NBTTagList dependencies = new NBTTagList();
        for (ITerminalCraftingPlan<I> dependency : plan.getDependencies()) {
            dependencies
                .appendTag(TerminalCraftingPlanStatic.serialize((TerminalCraftingPlanStatic) dependency, handler));
        }
        tag.setTag("dependencies", dependencies);

        NBTTagList outputs = new NBTTagList();
        for (IPrototypedIngredient<?, ?> output : plan.getOutputs()) {
            outputs.appendTag(IPrototypedIngredient.serialize((PrototypedIngredient) output));
        }
        tag.setTag("outputs", outputs);

        tag.setInteger(
            "status",
            plan.getStatus()
                .ordinal());

        tag.setLong("craftingQuantity", plan.getCraftingQuantity());

        NBTTagList storageIngredients = new NBTTagList();
        for (IPrototypedIngredient<?, ?> storageIngredient : plan.getStorageIngredients()) {
            storageIngredients.appendTag(IPrototypedIngredient.serialize((PrototypedIngredient) storageIngredient));
        }
        tag.setTag("storageIngredients", storageIngredients);

        NBTTagList lastMissingIngredients = new NBTTagList();
        for (List<IPrototypedIngredient<?, ?>> lastMissingIngredient : plan.getLastMissingIngredients()) {
            NBTTagList lastMissingIngredientTag = new NBTTagList();
            for (IPrototypedIngredient<?, ?> prototypedIngredient : lastMissingIngredient) {
                lastMissingIngredientTag
                    .appendTag(IPrototypedIngredient.serialize((PrototypedIngredient) prototypedIngredient));
            }
            lastMissingIngredients.appendTag(lastMissingIngredientTag);
        }
        tag.setTag("lastMissingIngredients", lastMissingIngredients);

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

    public static <I> TerminalCraftingPlanStatic<I> deserialize(NBTTagCompound tag,
        ITerminalStorageTabIngredientCraftingHandler<?, I> handler) {
        if (!tag.hasKey("id")) {
            throw new IllegalArgumentException("Could not find an id entry in the given tag");
        }
        if (!tag.hasKey("dependencies", Constants.NBT.TAG_LIST)) {
            throw new IllegalArgumentException("Could not find a dependencies entry in the given tag");
        }
        if (!tag.hasKey("outputs", Constants.NBT.TAG_LIST)) {
            throw new IllegalArgumentException("Could not find a outputs entry in the given tag");
        }
        if (!tag.hasKey("status", Constants.NBT.TAG_INT)) {
            throw new IllegalArgumentException("Could not find a status entry in the given tag");
        }
        if (!tag.hasKey("craftingQuantity", Constants.NBT.TAG_LONG)) {
            throw new IllegalArgumentException("Could not find a craftingQuantity entry in the given tag");
        }
        if (!tag.hasKey("storageIngredients", Constants.NBT.TAG_LIST)) {
            throw new IllegalArgumentException("Could not find a storageIngredients entry in the given tag");
        }
        if (!tag.hasKey("lastMissingIngredients", Constants.NBT.TAG_LIST)) {
            throw new IllegalArgumentException("Could not find a lastMissingIngredients entry in the given tag");
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

        I id = handler.deserializeCraftingJobId(tag.getTag("id"));

        NBTTagList dependenciesTag = tag.getTagList("dependencies", Constants.NBT.TAG_COMPOUND);
        List<ITerminalCraftingPlan<I>> dependencies = Lists.newArrayListWithExpectedSize(dependenciesTag.tagCount());
        for (Object nbtBase : dependenciesTag.tagList) {
            dependencies.add(TerminalCraftingPlanStatic.deserialize((NBTTagCompound) nbtBase, handler));
        }

        NBTTagList outputsTag = tag.getTagList("outputs", Constants.NBT.TAG_COMPOUND);
        List<IPrototypedIngredient<?, ?>> outputs = Lists.newArrayListWithExpectedSize(outputsTag.tagCount());
        for (Object nbtBase : outputsTag.tagList) {
            outputs.add(IPrototypedIngredient.deserialize((NBTTagCompound) nbtBase));
        }

        TerminalCraftingJobStatus status = TerminalCraftingJobStatus.values()[tag.getInteger("status")];

        long craftingQuantity = tag.getLong("craftingQuantity");

        NBTTagList storageIngredientsTag = tag.getTagList("storageIngredients", Constants.NBT.TAG_COMPOUND);
        List<IPrototypedIngredient<?, ?>> storageIngredients = Lists
            .newArrayListWithExpectedSize(storageIngredientsTag.tagCount());
        for (Object nbtBase : storageIngredientsTag.tagList) {
            storageIngredients.add(IPrototypedIngredient.deserialize((NBTTagCompound) nbtBase));
        }

        NBTTagList lastMissingIngredientsTag = tag.getTagList("lastMissingIngredients", Constants.NBT.TAG_LIST);
        List<List<IPrototypedIngredient<?, ?>>> lastMissingIngredients = Lists
            .newArrayListWithExpectedSize(lastMissingIngredientsTag.tagCount());
        for (Object nbtBase : lastMissingIngredientsTag.tagList) {
            NBTTagList list = ((NBTTagList) nbtBase);
            List<IPrototypedIngredient<?, ?>> lastMissingIngredient = Lists
                .newArrayListWithExpectedSize(list.tagCount());
            for (Object base : list.tagList) {
                lastMissingIngredient.add(IPrototypedIngredient.deserialize((NBTTagCompound) base));
            }
            lastMissingIngredients.add(lastMissingIngredient);
        }

        Label label = Label.values()[tag.getInteger("label")];

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

        TerminalCraftingPlanStatic<I> plan = new TerminalCraftingPlanStatic<>(
            id,
            dependencies,
            outputs,
            status,
            craftingQuantity,
            storageIngredients,
            lastMissingIngredients,
            label,
            tickDuration,
            channel,
            initiatorName);
        if (unlocalizedLabelOverride != null) {
            plan.unlocalizedLabelOverride = unlocalizedLabelOverride;
        }
        return plan;
    }

    public static enum Label {

        RUNNING("gui.integratedterminals.terminal_storage.craftingplan.label.running"),
        VALID("gui.integratedterminals.terminal_storage.craftingplan.label.valid"),
        INCOMPLETE("gui.integratedterminals.terminal_storage.craftingplan.label.failed.incomplete"),
        RECURSION("gui.integratedterminals.terminal_storage.craftingplan.label.failed.recursion"),
        ERROR("ERROR");

        private final String unlocalizedMessage;

        Label(String unlocalizedMessage) {
            this.unlocalizedMessage = unlocalizedMessage;
        }

        public String getUnlocalizedMessage() {
            return this.unlocalizedMessage;
        }
    }
}
