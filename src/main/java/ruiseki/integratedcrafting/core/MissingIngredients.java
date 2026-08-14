package ruiseki.integratedcrafting.core;

import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ruiseki.commoncapabilities.api.ingredient.IPrototypedIngredient;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;

/**
 * A list with missing ingredients (non-slot-based).
 *
 * @param <T> The instance type.
 * @param <M> The matching condition parameter, may be Void.
 * @author rubensworks
 */
public class MissingIngredients<T, M> {

    private final List<MissingIngredients.Element<T, M>> elements;

    public MissingIngredients(List<MissingIngredients.Element<T, M>> elements) {
        this.elements = elements;
    }

    public List<Element<T, M>> getElements() {
        return elements;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MissingIngredients && this.getElements()
            .equals(((MissingIngredients) obj).getElements());
    }

    @Override
    public String toString() {
        return getElements().toString();
    }

    /**
     * Serialize ingredients to NBTTagList.
     *
     * @param ingredients Ingredients.
     * @return An NBTTagList representation of the given ingredients.
     */
    public static NBTTagList serialize(Map<IngredientComponent<?, ?>, MissingIngredients<?, ?>> ingredients) {
        NBTTagList rootList = new NBTTagList();

        for (Map.Entry<IngredientComponent<?, ?>, MissingIngredients<?, ?>> entry : ingredients.entrySet()) {
            NBTTagCompound componentEntry = new NBTTagCompound();

            componentEntry.setString(
                "component",
                entry.getKey()
                    .getName()
                    .toString());

            NBTTagList missingIngredientsList = new NBTTagList();
            for (Element<?, ?> element : entry.getValue()
                .getElements()) {
                NBTTagList elementsList = new NBTTagList();
                for (PrototypedWithRequested<?, ?> alternative : element.getAlternatives()) {
                    NBTTagCompound alternativeTag = new NBTTagCompound();
                    alternativeTag.setTag(
                        "requestedPrototype",
                        IPrototypedIngredient.serialize(alternative.getRequestedPrototype()));
                    alternativeTag.setLong("quantityMissing", alternative.getQuantityMissing());
                    elementsList.appendTag(alternativeTag);
                }
                missingIngredientsList.appendTag(elementsList);
            }

            componentEntry.setTag("missingIngredients", missingIngredientsList);
            rootList.appendTag(componentEntry);
        }

        return rootList;
    }

    /**
     * Deserialize ingredients from NBTTagList
     *
     * @param tagList An NBTTagList.
     * @return A new mixed ingredients instance map.
     * @throws IllegalArgumentException If the given tag is invalid or does not contain data on the given ingredients.
     */
    public static Map<IngredientComponent<?, ?>, MissingIngredients<?, ?>> deserialize(NBTTagList tagList)
        throws IllegalArgumentException {
        Map<IngredientComponent<?, ?>, MissingIngredients<?, ?>> map = Maps.newIdentityHashMap();

        for (Object rawEntry : tagList.tagList) {
            if (!(rawEntry instanceof NBTTagCompound componentEntry)) {
                continue;
            }
            String componentName = componentEntry.getString("component");

            IngredientComponent<?, ?> component = IngredientComponent.REGISTRY.get(new ResourceLocation(componentName));
            if (component == null) {
                throw new IllegalArgumentException("Could not find the ingredient component type " + componentName);
            }

            MissingIngredients<?, ?> missingIngredients = deserializeComponent(componentEntry);
            map.put(component, missingIngredients);
        }

        return map;
    }

    /**
     * Helper deserialize method to bind generics correctly without type mismatch errors.
     */
    @SuppressWarnings("unchecked")
    private static <T, M> MissingIngredients<T, M> deserializeComponent(NBTTagCompound componentEntry) {
        List<MissingIngredients.Element<T, M>> elements = Lists.newArrayList();
        NBTTagList missingIngredientsList = componentEntry.getTagList("missingIngredients", Constants.NBT.TAG_LIST);

        for (Object rawElement : missingIngredientsList.tagList) {
            if (!(rawElement instanceof NBTTagList elementsList)) {
                continue;
            }

            List<MissingIngredients.PrototypedWithRequested<T, M>> alternatives = Lists.newArrayList();

            for (Object rawAlt : elementsList.tagList) {
                if (!(rawAlt instanceof NBTTagCompound alternativeTag)) {
                    continue;
                }

                IPrototypedIngredient<T, M> requestedPrototype = (IPrototypedIngredient<T, M>) IPrototypedIngredient
                    .deserialize(alternativeTag.getCompoundTag("requestedPrototype"));
                long quantityMissing = alternativeTag.getLong("quantityMissing");

                alternatives.add(new PrototypedWithRequested<>(requestedPrototype, quantityMissing));
            }

            elements.add(new Element<>(alternatives));
        }

        return new MissingIngredients<>(elements);
    }

    /**
     * A list of alternatives for the given element.
     *
     * @param <T> The instance type.
     * @param <M> The matching condition parameter, may be Void.
     */
    public static class Element<T, M> {

        private final List<MissingIngredients.PrototypedWithRequested<T, M>> alternatives;

        public Element(List<MissingIngredients.PrototypedWithRequested<T, M>> alternatives) {
            this.alternatives = alternatives;
        }

        public List<PrototypedWithRequested<T, M>> getAlternatives() {
            return alternatives;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Element && this.getAlternatives()
                .equals(((Element) obj).getAlternatives());
        }

        @Override
        public String toString() {
            return getAlternatives().toString();
        }
    }

    /**
     * A prototype with a missing quantity,
     * together with the total requested quantity.
     *
     * @param <T> The instance type.
     * @param <M> The matching condition parameter, may be Void.
     */
    public static class PrototypedWithRequested<T, M> {

        private final IPrototypedIngredient<T, M> requestedPrototype;
        private final long quantityMissing;

        public PrototypedWithRequested(IPrototypedIngredient<T, M> requestedPrototype, long quantityMissing) {
            this.requestedPrototype = requestedPrototype;
            this.quantityMissing = quantityMissing;
        }

        public IPrototypedIngredient<T, M> getRequestedPrototype() {
            return requestedPrototype;
        }

        public long getQuantityMissing() {
            return quantityMissing;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof PrototypedWithRequested && this.getRequestedPrototype()
                .equals(((PrototypedWithRequested) obj).getRequestedPrototype())
                && this.getQuantityMissing() == ((PrototypedWithRequested) obj).getQuantityMissing();
        }

        @Override
        public String toString() {
            return String.format("[Prototype: %s; missing: %s]", getRequestedPrototype(), getQuantityMissing());
        }
    }

}
