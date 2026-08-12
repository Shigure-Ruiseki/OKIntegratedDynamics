package ruiseki.integrateddynamics.core.ingredient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import ruiseki.commoncapabilities.api.capability.itemhandler.ItemMatch;
import ruiseki.commoncapabilities.api.capability.recipehandler.IPrototypedIngredientAlternatives;
import ruiseki.commoncapabilities.api.capability.recipehandler.PrototypedIngredientAlternativesItemStackOredictionary;
import ruiseki.commoncapabilities.api.capability.recipehandler.PrototypedIngredientAlternativesList;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.PrototypedIngredient;
import ruiseki.integrateddynamics.Reference;
import ruiseki.okcore.helper.ItemStackHelpers;

/**
 * Different methods for matching ItemStacks.
 *
 * @author rubensworks
 */
public enum ItemMatchType {

    ITEMMETA(new FlaggedPrototypeHandler(ItemMatch.ITEM | ItemMatch.DAMAGE)),
    ITEM(new FlaggedPrototypeHandler(ItemMatch.ITEM)),
    ITEMMETANBT(new FlaggedPrototypeHandler(ItemMatch.ITEM | ItemMatch.DAMAGE | ItemMatch.NBT)),
    ITEMNBT(new FlaggedPrototypeHandler(ItemMatch.ITEM | ItemMatch.NBT)),
    OREDICT(itemStack -> {
        return new PrototypedIngredientAlternativesItemStackOredictionary(
            getOreDictKeys(itemStack),
            ItemMatch.ITEM | ItemMatch.DAMAGE | ItemMatch.NBT,
            itemStack.stackSize);
    });

    private static final LoadingCache<ItemStack, List<String>> CACHE_OREDICT = CacheBuilder.newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .build(new CacheLoader<ItemStack, List<String>>() {

            @Override
            public List<String> load(ItemStack key) {
                if (key == null) {
                    return Collections.emptyList();
                }
                return Arrays.stream(OreDictionary.getOreIDs(key))
                    .mapToObj(OreDictionary::getOreName)
                    .collect(Collectors.toList());
            }
        });

    protected static List<String> getOreDictKeys(ItemStack itemStack) {
        try {
            return CACHE_OREDICT.get(itemStack);
        } catch (ExecutionException e) {
            return Collections.emptyList();
        }
    }

    private final IPrototypeHandler prototypeHandler;

    ItemMatchType(IPrototypeHandler prototypeHandler) {
        this.prototypeHandler = prototypeHandler;
    }

    public ItemMatchType next() {
        ItemMatchType[] values = ItemMatchType.values();
        return this.ordinal() == values.length - 1 ? values[0] : values[this.ordinal() + 1];
    }

    public ResourceLocation getSlotSpriteName() {
        return new ResourceLocation(
            Reference.MOD_ID,
            "textures/slot/" + this.name()
                .toLowerCase(Locale.ENGLISH) + ".png");
    }

    public IPrototypeHandler getPrototypeHandler() {
        return this.prototypeHandler;
    }

    public static interface IPrototypeHandler {

        /**
         * Create prototypes.
         *
         * @param itemStack An ItemStack to derive prototypes from.
         * @return The list of prototypes.
         */
        public IPrototypedIngredientAlternatives<ItemStack, Integer> getPrototypesFor(ItemStack itemStack);
    }

    public static class FlaggedPrototypeHandler implements ItemMatchType.IPrototypeHandler {

        private final int flags;

        public FlaggedPrototypeHandler(int flags) {
            this.flags = flags;
        }

        @Override
        public IPrototypedIngredientAlternatives<ItemStack, Integer> getPrototypesFor(ItemStack itemStack) {
            return new PrototypedIngredientAlternativesList<>(
                ItemStackHelpers.getVariants(itemStack)
                    .stream()
                    .filter(Objects::nonNull)
                    .map(stack -> new PrototypedIngredient<>(IngredientComponent.ITEMSTACK, stack, flags))
                    .collect(Collectors.toList()));
        }
    }
}
