package ruiseki.integrateddynamics.core.ingredient;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.capability.itemhandler.ItemMatch;
import ruiseki.commoncapabilities.api.capability.recipehandler.IPrototypedIngredientAlternatives;
import ruiseki.commoncapabilities.api.capability.recipehandler.PrototypedIngredientAlternativesItemStackOredictionary;
import ruiseki.commoncapabilities.api.capability.recipehandler.PrototypedIngredientAlternativesItemStackTag;
import ruiseki.commoncapabilities.api.capability.recipehandler.PrototypedIngredientAlternativesList;
import ruiseki.commoncapabilities.api.ingredient.IPrototypedIngredient;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.PrototypedIngredient;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.network.PacketCodec;

/**
 * @author rubensworks
 */
public class ItemMatchProperties {

    static {
        PacketCodec.addCodedAction(ItemMatchProperties.class, new PacketCodec.ICodecAction() {

            @Override
            public void encode(Object o, ExtendedBuffer packetBuffer) throws IOException {
                ItemMatchProperties props = ((ItemMatchProperties) o);
                PacketCodec.getAction(ItemStack.class)
                    .encode(props.itemStack, packetBuffer);
                packetBuffer.writeBoolean(props.nbt);
                packetBuffer.writeString(props.itemTag != null ? props.itemTag : "");
                packetBuffer.writeInt(props.tagQuantity);
                packetBuffer.writeBoolean(props.reusable);
            }

            @Override
            public Object decode(ExtendedBuffer packetBuffer) {
                ItemStack itemStack = (ItemStack) PacketCodec.getAction(ItemStack.class)
                    .decode(packetBuffer);
                boolean nbt = packetBuffer.readBoolean();
                String itemTag = packetBuffer.readString();
                int tagQuantity = packetBuffer.readInt();
                boolean reusable = packetBuffer.readBoolean();
                return new ItemMatchProperties(
                    itemStack,
                    nbt,
                    itemTag.isEmpty() ? null : itemTag,
                    tagQuantity,
                    reusable);
            }
        });

        PacketCodec.addCodedAction(PartPos.class, new PacketCodec.ICodecAction() {

            @Override
            public void encode(Object o, ExtendedBuffer extendedBuffer) throws IOException {
                PacketCodec.getAction(DimPos.class)
                    .encode(((PartPos) o).getPos(), extendedBuffer);
                PacketCodec.getAction(ForgeDirection.class)
                    .encode(((PartPos) o).getSide(), extendedBuffer);
            }

            @Override
            public Object decode(ExtendedBuffer extendedBuffer) {
                DimPos pos = (DimPos) PacketCodec.getAction(DimPos.class)
                    .decode(extendedBuffer);
                ForgeDirection side = (ForgeDirection) PacketCodec.getAction(ForgeDirection.class)
                    .decode(extendedBuffer);
                return PartPos.of(pos, side);
            }
        });
    }

    private final ItemStack itemStack;
    private boolean nbt;
    @Nullable
    private String itemTag;
    private int tagQuantity;
    private boolean reusable;

    public ItemMatchProperties(ItemStack itemStack) {
        this(itemStack, false, null, 1, false);
    }

    public ItemMatchProperties(ItemStack itemStack, boolean nbt, @Nullable String itemTag, int tagQuantity) {
        this(itemStack, nbt, itemTag, tagQuantity, false);
    }

    public ItemMatchProperties(ItemStack itemStack, boolean nbt, @Nullable String itemTag, int tagQuantity,
        boolean reusable) {
        this.itemStack = itemStack;
        this.nbt = nbt;
        this.itemTag = itemTag;
        this.tagQuantity = tagQuantity;
        this.reusable = reusable;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public boolean isNbt() {
        return nbt;
    }

    public void setNbt(boolean nbt) {
        this.nbt = nbt;
    }

    @Nullable
    public String getItemTag() {
        return itemTag;
    }

    public void setItemTag(@Nullable String itemTag) {
        this.itemTag = itemTag;
    }

    public int getTagQuantity() {
        return tagQuantity;
    }

    public void setTagQuantity(int tagQuantity) {
        this.tagQuantity = tagQuantity;
    }

    public boolean isReusable() {
        return reusable;
    }

    public void setReusable(boolean reusable) {
        this.reusable = reusable;
    }

    public boolean isValid() {
        return getItemTag() != null || getItemStack() != null;
    }

    public IPrototypedIngredientAlternatives<ItemStack, Integer> createPrototypedIngredient() {
        if (getItemTag() == null) {
            int flags = isNbt() ? ItemMatch.ITEM | ItemMatch.NBT : ItemMatch.ITEM;
            return new PrototypedIngredientAlternativesList<>(
                Collections.singletonList(new PrototypedIngredient<>(IngredientComponent.ITEMSTACK, itemStack, flags)));
        } else {
            String tag = getItemTag();
            int matchFlags = ItemMatch.ITEM | (isNbt() ? ItemMatch.NBT : 0);
            if (tag.contains(":")) {
                return new PrototypedIngredientAlternativesItemStackTag(
                    Collections.singletonList(tag),
                    matchFlags,
                    getTagQuantity());
            } else {
                return new PrototypedIngredientAlternativesItemStackOredictionary(
                    Collections.singletonList(tag),
                    matchFlags,
                    getTagQuantity());
            }
        }
    }

    public static ItemMatchProperties fromPrototypedIngredient(
        IPrototypedIngredientAlternatives<ItemStack, Integer> prototypedIngredient, boolean reusable) {
        ItemMatchProperties props = new ItemMatchProperties(ItemHelpers.EMPTY);
        if (prototypedIngredient instanceof PrototypedIngredientAlternativesItemStackTag prototypedTag) {
            prototypedTag.getKeys()
                .stream()
                .findFirst()
                .ifPresent(props::setItemTag);
            props.setTagQuantity((int) prototypedTag.getQuantity());
        } else
            if (prototypedIngredient instanceof PrototypedIngredientAlternativesItemStackOredictionary oredictionary) {
                oredictionary.getKeys()
                    .stream()
                    .findFirst()
                    .ifPresent(props::setItemTag);
                props.setTagQuantity((int) oredictionary.getQuantity());
            } else
                if (prototypedIngredient instanceof PrototypedIngredientAlternativesList<ItemStack, Integer>prototypedList) {
                    Collection<IPrototypedIngredient<ItemStack, Integer>> alternatives = prototypedList
                        .getAlternatives();
                    IPrototypedIngredient<ItemStack, Integer> prototype = alternatives.stream()
                        .findFirst()
                        .orElse(null);
                    if (prototype != null) {
                        props = new ItemMatchProperties(prototype.getPrototype());
                        props.setNbt(
                            IngredientComponent.ITEMSTACK.getMatcher()
                                .hasCondition(prototype.getCondition(), ItemMatch.NBT));
                    }
                }
        props.setReusable(reusable);
        return props;
    }
}
