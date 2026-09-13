package ruiseki.integratedrest.json;

import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import cpw.mods.fml.common.registry.GameData;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeListProxy;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeBlock;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeEntity;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeFluidStack;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeItemStack;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeDouble;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeInteger;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeList;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeLong;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeNbt;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeString;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integratedrest.IntegratedRest;
import ruiseki.integratedrest.Uris;
import ruiseki.integratedrest.api.json.IValueTypeJsonHandlerRegistry;
import ruiseki.integratedrest.json.handler.CheckedValueTypeJsonHandlerBase;
import ruiseki.integratedrest.json.handler.TypedObjectValueTypeJsonHandlerBase;
import ruiseki.okcore.helper.BlockStateHelpers;
import ruiseki.okcore.helper.FluidHelpers;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.ItemHelpers;

/**
 * Registration code for value type JSON handlers.
 * 
 * @author rubensworks
 */
public class ValueTypeJsonHandlers {

    public static IValueTypeJsonHandlerRegistry REGISTRY = IntegratedRest._instance.getRegistryManager()
        .getRegistry(IValueTypeJsonHandlerRegistry.class);

    public static void load() {
        REGISTRY.registerHandler(ValueTypes.BOOLEAN, value -> new JsonPrimitive(value.getRawValue()));
        REGISTRY.registerReverseHandler(new CheckedValueTypeJsonHandlerBase<ValueTypeBoolean.ValueBoolean>() {

            @Override
            public ValueTypeBoolean.ValueBoolean handleUnchecked(JsonElement jsonElement)
                throws IllegalStateException, ClassCastException {
                return jsonElement instanceof JsonPrimitive && ((JsonPrimitive) jsonElement).isBoolean()
                    ? ValueTypeBoolean.ValueBoolean.of(jsonElement.getAsBoolean())
                    : null;
            }
        });

        REGISTRY.registerHandler(ValueTypes.INTEGER, value -> new JsonPrimitive(value.getRawValue()));
        REGISTRY.registerReverseHandler(new CheckedValueTypeJsonHandlerBase<ValueTypeInteger.ValueInteger>() {

            @Override
            public ValueTypeInteger.ValueInteger handleUnchecked(JsonElement jsonElement)
                throws IllegalStateException, ClassCastException {
                return jsonElement instanceof JsonPrimitive && ((JsonPrimitive) jsonElement).isNumber()
                    ? ValueTypeInteger.ValueInteger.of(jsonElement.getAsInt())
                    : null;
            }
        });

        REGISTRY.registerHandler(ValueTypes.DOUBLE, value -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("@type", Uris.XSD_DECIMAL);
            jsonObject.addProperty("@value", value.getRawValue());
            return jsonObject;
        });
        REGISTRY.registerReverseHandler(
            new TypedObjectValueTypeJsonHandlerBase<ValueTypeDouble.ValueDouble>(Uris.XSD_DECIMAL) {

                @Override
                protected ValueTypeDouble.ValueDouble handleTypedValueString(String valueString) {
                    return ValueTypeDouble.ValueDouble.of(Double.valueOf(valueString));
                }
            });

        REGISTRY.registerHandler(ValueTypes.LONG, value -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("@type", Uris.XSD_LONG);
            jsonObject.addProperty("@value", value.getRawValue());
            return jsonObject;
        });
        REGISTRY
            .registerReverseHandler(new TypedObjectValueTypeJsonHandlerBase<ValueTypeLong.ValueLong>(Uris.XSD_LONG) {

                @Override
                protected ValueTypeLong.ValueLong handleTypedValueString(String valueString) {
                    return ValueTypeLong.ValueLong.of(Long.valueOf(valueString));
                }
            });

        REGISTRY.registerHandler(ValueTypes.STRING, value -> new JsonPrimitive(value.getRawValue()));
        REGISTRY.registerReverseHandler(new CheckedValueTypeJsonHandlerBase<ValueTypeString.ValueString>() {

            @Override
            public ValueTypeString.ValueString handleUnchecked(JsonElement jsonElement)
                throws IllegalStateException, ClassCastException {
                return ValueTypeString.ValueString.of(jsonElement.getAsString());
            }
        });

        REGISTRY.registerHandler(ValueTypes.LIST, value -> {
            if (value.getRawValue()
                .isInfinite()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("@type", "ValueInfiniteList");
                return jsonObject;
            } else {
                JsonArray jsonArray = new JsonArray();
                for (IValue v : (IValueTypeListProxy<?, IValue>) value.getRawValue()) {
                    jsonArray.add(JsonUtil.valueToJson(v));
                }
                return jsonArray;
            }

        });
        REGISTRY.registerReverseHandler(new CheckedValueTypeJsonHandlerBase<ValueTypeList.ValueList>() {

            @Override
            public ValueTypeList.ValueList handleUnchecked(JsonElement jsonElement)
                throws IllegalStateException, ClassCastException {
                if (jsonElement instanceof JsonArray) {
                    List<IValue> elements = Lists.newArrayList();
                    IValueType type = ValueTypes.CATEGORY_ANY;
                    JsonArray jsonArray = (JsonArray) jsonElement;
                    for (JsonElement element : jsonArray) {
                        JsonUtil.jsonToValue(element)
                            .ifPresent(elements::add);
                    }
                    if (elements.size() > 0) {
                        IValueType maybeType = elements.get(0)
                            .getType();
                        if (elements.stream()
                            .map(IValue::getType)
                            .allMatch(maybeType::equals)) {
                            // If all of the elements are the same type, then use that type as the list's type.
                            // Otherwise, use Any as the list's type.
                            type = maybeType;
                        }
                    }
                    return ValueTypeList.ValueList.ofList(type, elements);
                }
                return null;
            }
        });

        REGISTRY.registerHandler(ValueTypes.OPERATOR, value -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("@type", "ValueOperator");
            jsonObject.addProperty(
                "@value",
                value.getRawValue()
                    .getUniqueName()
                    .toString());
            return jsonObject;
        });
        // No reverse handler

        REGISTRY.registerHandler(ValueTypes.NBT, value -> {
            JsonObject jsonObject = new JsonObject();
            NBTTagCompound tag = value.getRawValue();
            jsonObject.add("nbt", new JsonParser().parse(tag.toString()));
            return jsonObject;
        });
        REGISTRY.registerReverseHandler(new CheckedValueTypeJsonHandlerBase<ValueTypeNbt.ValueNbt>() {

            @Override
            public ValueTypeNbt.ValueNbt handleUnchecked(JsonElement jsonElement)
                throws IllegalStateException, ClassCastException {
                if (jsonElement instanceof JsonObject && ((JsonObject) jsonElement).has("@type")
                    && ((JsonObject) jsonElement).get("@type")
                        .getAsString()
                        .equals("ValueNbt")) {
                    JsonObject jsonObject = (JsonObject) jsonElement;
                    if (jsonObject.has("nbt")) {
                        try {
                            NBTTagCompound tag = (NBTTagCompound) JsonToNBT.func_150315_a(
                                jsonObject.get("nbt")
                                    .toString());
                            return ValueTypeNbt.ValueNbt.of(tag);
                        } catch (NBTException e) {
                            throw new IllegalStateException("Lỗi parse NBT", e);
                        }
                    } else {
                        return ValueTypeNbt.ValueNbt.of(new NBTTagCompound());
                    }
                }
                return null;
            }
        });

        REGISTRY.registerHandler(ValueTypes.OBJECT_BLOCK, value -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("@type", "ValueBlock");

            if (value.getRawValue()
                .isPresent()) {
                BlockState blockState = value.getRawValue()
                    .get();
                ResourceLocation resourceLocation = Helpers.getLocation(blockState.getBlock());
                if (resourceLocation != null) {
                    jsonObject.addProperty(
                        "block",
                        JsonUtil.absolutizePath("registry/block/" + JsonUtil.resourceLocationToPath(resourceLocation)));
                    jsonObject.addProperty("resourceLocation", resourceLocation.toString());
                }

                jsonObject.add("state", BlockStateHelpers.toJson(blockState));
            }

            return jsonObject;
        });
        REGISTRY.registerReverseHandler(new CheckedValueTypeJsonHandlerBase<ValueObjectTypeBlock.ValueBlock>() {

            @Override
            public ValueObjectTypeBlock.ValueBlock handleUnchecked(JsonElement jsonElement)
                throws IllegalStateException, ClassCastException {
                if (jsonElement instanceof JsonObject && ((JsonObject) jsonElement).has("@type")
                    && ((JsonObject) jsonElement).get("@type")
                        .getAsString()
                        .equals("ValueBlock")) {
                    JsonObject jsonObject = (JsonObject) jsonElement;
                    if (!jsonObject.has("resourceLocation")) {
                        return ValueObjectTypeBlock.ValueBlock.of(null);
                    } else {
                        ResourceLocation resourceLocation = new ResourceLocation(
                            jsonObject.get("resourceLocation")
                                .getAsString());
                        Block block = GameData.getBlockRegistry()
                            .getObject(resourceLocation.toString());
                        if (block != null) {
                            return ValueObjectTypeBlock.ValueBlock.of(BlockStateHelpers.fromJson(jsonObject, "state"));
                        }
                    }
                }
                return null;
            }
        });

        REGISTRY.registerHandler(ValueTypes.OBJECT_ITEMSTACK, value -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("@type", "ValueItem");
            if (!ItemHelpers.isEmpty(value.getRawValue())) {
                ItemStack itemStack = value.getRawValue();
                jsonObject.addProperty(
                    "item",
                    JsonUtil.absolutizePath(
                        "registry/item/" + JsonUtil.resourceLocationToPath(Helpers.getLocation(itemStack.getItem()))));
                jsonObject.addProperty(
                    "resourceLocation",
                    Helpers.getLocation(itemStack.getItem())
                        .toString());
                jsonObject.addProperty("meta", itemStack.getItemDamage());
                jsonObject.addProperty("count", itemStack.stackSize);
                if (itemStack.hasTagCompound()) {
                    jsonObject.add(
                        "nbt",
                        new JsonParser().parse(
                            itemStack.getTagCompound()
                                .toString()));
                }
            }
            return jsonObject;
        });
        REGISTRY.registerReverseHandler(new CheckedValueTypeJsonHandlerBase<ValueObjectTypeItemStack.ValueItemStack>() {

            @Override
            public ValueObjectTypeItemStack.ValueItemStack handleUnchecked(JsonElement jsonElement)
                throws IllegalStateException, ClassCastException {
                if (jsonElement instanceof JsonObject && ((JsonObject) jsonElement).has("@type")
                    && ((JsonObject) jsonElement).get("@type")
                        .getAsString()
                        .equals("ValueItem")) {
                    JsonObject jsonObject = (JsonObject) jsonElement;
                    if (!jsonObject.has("resourceLocation")) {
                        return ValueObjectTypeItemStack.ValueItemStack.of(ItemHelpers.EMPTY);
                    } else {
                        ResourceLocation resourceLocation = new ResourceLocation(
                            jsonObject.get("resourceLocation")
                                .getAsString());
                        Item item = GameData.getItemRegistry()
                            .getObject(resourceLocation.toString());
                        if (item != null) {
                            int count = 1;
                            if (jsonObject.has("count")) {
                                count = jsonObject.get("count")
                                    .getAsInt();
                            }

                            int meta = 0;
                            if (jsonObject.has("meta")) {
                                meta = jsonObject.get("meta")
                                    .getAsInt();
                            }

                            ItemStack itemStack = new ItemStack(item, count, meta);
                            if (jsonObject.has("nbt")) {
                                NBTTagCompound tag;
                                try {
                                    tag = (NBTTagCompound) JsonToNBT.func_150315_a(
                                        jsonObject.get("nbt")
                                            .toString());
                                } catch (NBTException e) {
                                    throw new RuntimeException(e);
                                }
                                itemStack.setTagCompound(tag);
                            }
                            return ValueObjectTypeItemStack.ValueItemStack.of(itemStack);
                        }
                    }
                }
                return null;
            }
        });

        REGISTRY.registerHandler(ValueTypes.OBJECT_ENTITY, value -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("@type", "ValueEntity");
            if (value.getRawValue()
                .isPresent()
                && value.getRawValue()
                    .get() != null) {
                jsonObject.addProperty(
                    "uuid",
                    value.getRawValue()
                        .get()
                        .getUniqueID()
                        .toString());
            }
            return jsonObject;
        });
        REGISTRY.registerReverseHandler(new CheckedValueTypeJsonHandlerBase<ValueObjectTypeEntity.ValueEntity>() {

            @Override
            public ValueObjectTypeEntity.ValueEntity handleUnchecked(JsonElement jsonElement)
                throws IllegalStateException, ClassCastException {
                if (jsonElement instanceof JsonObject && ((JsonObject) jsonElement).has("@type")
                    && ((JsonObject) jsonElement).get("@type")
                        .getAsString()
                        .equals("ValueEntity")) {

                    JsonObject jsonObject = (JsonObject) jsonElement;
                    if (jsonObject.has("uuid")) {
                        try {
                            UUID uuid = UUID.fromString(
                                jsonObject.get("uuid")
                                    .getAsString());
                            Entity entity = ValueObjectTypeEntity.getEntityByUUID(uuid);
                            return ValueObjectTypeEntity.ValueEntity.of(entity);
                        } catch (IllegalArgumentException e) {
                            throw new IllegalStateException("Invalid UUID format", e);
                        }
                    } else {
                        return ValueObjectTypeEntity.ValueEntity.of(null);
                    }
                }
                return null;
            }
        });

        REGISTRY.registerHandler(ValueTypes.OBJECT_FLUIDSTACK, value -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("@type", "ValueFluid");
            if (value.getRawValue() != null) {
                FluidStack fluidStack = value.getRawValue();
                String fluidName = FluidRegistry.getFluidName(fluidStack.getFluid());

                jsonObject.addProperty("fluid", JsonUtil.absolutizePath("registry/fluid/" + fluidName));
                jsonObject.addProperty("fluidName", fluidName);
                jsonObject.addProperty("count", fluidStack.amount);

                if (fluidStack.tag != null) {
                    jsonObject.add("nbt", new JsonParser().parse(fluidStack.tag.toString()));
                }
            }
            return jsonObject;
        });
        REGISTRY
            .registerReverseHandler(new CheckedValueTypeJsonHandlerBase<ValueObjectTypeFluidStack.ValueFluidStack>() {

                @Override
                public ValueObjectTypeFluidStack.ValueFluidStack handleUnchecked(JsonElement jsonElement)
                    throws IllegalStateException, ClassCastException {
                    if (jsonElement instanceof JsonObject && ((JsonObject) jsonElement).has("@type")
                        && ((JsonObject) jsonElement).get("@type")
                            .getAsString()
                            .equals("ValueFluid")) {

                        JsonObject jsonObject = (JsonObject) jsonElement;
                        if (!jsonObject.has("fluidName")) {
                            return ValueObjectTypeFluidStack.ValueFluidStack.of(null);
                        } else {
                            Fluid fluid = FluidRegistry.getFluid(
                                jsonObject.get("fluidName")
                                    .getAsString());
                            if (fluid != null) {
                                int count = FluidHelpers.BUCKET_VOLUME;
                                if (jsonObject.has("count")) {
                                    count = jsonObject.get("count")
                                        .getAsInt();
                                }

                                FluidStack fluidStack = new FluidStack(fluid, count);
                                if (jsonObject.has("nbt")) {
                                    try {
                                        NBTTagCompound tag = (NBTTagCompound) JsonToNBT.func_150315_a(
                                            jsonObject.get("nbt")
                                                .toString());
                                        fluidStack.tag = tag;
                                    } catch (NBTException e) {
                                        throw new IllegalStateException("Lỗi parse NBT FluidStack", e);
                                    }
                                }
                                return ValueObjectTypeFluidStack.ValueFluidStack.of(fluidStack);
                            }
                        }
                    }
                    return null;
                }
            });
    }

}
