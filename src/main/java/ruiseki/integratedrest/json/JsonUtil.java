package ruiseki.integratedrest.json;

import java.util.Locale;
import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.IValueInterface;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.network.IIdentifiableNetworkElement;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.network.IPartNetworkElement;
import ruiseki.integrateddynamics.api.network.IPositionedNetworkElement;
import ruiseki.integrateddynamics.api.network.ISidedNetworkElement;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.api.part.aspect.IAspectRead;
import ruiseki.integrateddynamics.api.part.aspect.IAspectWrite;
import ruiseki.integrateddynamics.api.part.read.IPartTypeReader;
import ruiseki.integrateddynamics.api.part.write.IPartStateWriter;
import ruiseki.integrateddynamics.api.part.write.IPartTypeWriter;
import ruiseki.integrateddynamics.capability.valueinterface.ValueInterfaceConfig;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integratedrest.GeneralConfig;
import ruiseki.integratedrest.api.json.IReverseValueTypeJsonHandler;
import ruiseki.integratedrest.api.json.IValueTypeJsonHandler;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Utility methods for converting objects to JSON.
 *
 * @author rubensworks
 */
public class JsonUtil {

    public static String absolutizePath(String path) {
        if (path.charAt(0) == '/') {
            path = path.substring(1);
        }
        return GeneralConfig.apiBaseUrl + path;
    }

    public static void addNetworkInfo(JsonObject jsonObject, INetwork network) {
        jsonObject.addProperty("@id", JsonUtil.absolutizePath("network/" + Integer.toString(network.hashCode())));
        IPartNetwork partNetwork = NetworkHelpers.getPartNetworkChecked(network);
        JsonArray types = new JsonArray();
        types.add(new JsonPrimitive("Network"));
        if (partNetwork != null) {
            types.add(new JsonPrimitive("PartNetwork"));
        }
        jsonObject.add("@type", types);
        jsonObject.addProperty("cableCount", network.getCablesCount());
        jsonObject.addProperty(
            "elementCount",
            network.getElements()
                .size());
        jsonObject.addProperty("crashed", network.isCrashed());
        jsonObject.addProperty("initialized", network.isInitialized());
    }

    public static void addNetworkElementInfo(JsonObject jsonObject, INetworkElement networkElement, INetwork network) {
        IPartNetwork partNetwork = NetworkHelpers.getPartNetworkChecked(network);
        JsonArray types = new JsonArray();
        types.add(new JsonPrimitive("NetworkElement"));

        if (networkElement instanceof IIdentifiableNetworkElement) {
            IIdentifiableNetworkElement identifiable = (IIdentifiableNetworkElement) networkElement;
            jsonObject.addProperty(
                "@id",
                JsonUtil.absolutizePath(
                    "networkElement/" + resourceLocationToPath(identifiable.getGroup()) + "/" + identifiable.getId()));
        }

        if (networkElement instanceof IPositionedNetworkElement) {
            ForgeDirection side = null;
            if (networkElement instanceof ISidedNetworkElement) {
                side = ((ISidedNetworkElement) networkElement).getSide();
            }
            jsonObject.add("position", posToJson(((IPositionedNetworkElement) networkElement).getPosition(), side));
        }

        DimPos pos = getNetworkElementPosition(networkElement);
        if (pos != null) {
            Block block = pos.getBlockPos()
                .getBlock(pos.getWorld());
            jsonObject.addProperty(
                "block",
                JsonUtil
                    .absolutizePath("registry/block/" + JsonUtil.resourceLocationToPath(Helpers.getLocation(block))));
        }

        jsonObject.add("@type", types);
        jsonObject.addProperty("channel", networkElement.getChannel());
        jsonObject.addProperty("priority", networkElement.getPriority());
        jsonObject.addProperty("updateInterval", networkElement.getUpdateInterval());
        jsonObject.addProperty("network", JsonUtil.absolutizePath("network/" + network.hashCode()));

        getNetworkElementCapability(networkElement, ValueInterfaceConfig.CAPABILITY)
            .ifPresent(valueInterface -> JsonUtil.addValueInterfaceInfo(jsonObject, valueInterface));

        if (partNetwork != null && networkElement instanceof IPartNetworkElement) {
            JsonUtil.addPartNetworkElementInfo(jsonObject, (IPartNetworkElement<?, ?>) networkElement, partNetwork);
        }
    }

    @Nullable
    public static DimPos getNetworkElementPosition(INetworkElement networkElement) {
        if (networkElement instanceof IPositionedNetworkElement) {
            return ((IPositionedNetworkElement) networkElement).getPosition();
        }
        return null;
    }

    @Nullable
    public static PartPos getNetworkElementPositionSided(INetworkElement networkElement) {
        if (networkElement instanceof ISidedNetworkElement) {
            DimPos pos = getNetworkElementPosition(networkElement);
            ForgeDirection side = ((ISidedNetworkElement) networkElement).getSide();
            return PartPos.of(pos, side);
        }
        return null;
    }

    public static <T> LazyOptional<T> getNetworkElementCapability(INetworkElement networkElement,
        Capability<T> capability) {
        PartPos partPos = getNetworkElementPositionSided(networkElement);
        if (partPos != null) {
            return CapabilityHelpers.getCapability(partPos.getPos(), capability, partPos.getSide());
        }
        DimPos pos = getNetworkElementPosition(networkElement);
        if (pos != null) {
            return CapabilityHelpers.getCapability(pos, capability);
        }
        return LazyOptional.empty();
    }

    public static void addPartNetworkElementInfo(JsonObject jsonObject, IPartNetworkElement<?, ?> networkElement,
        IPartNetwork partNetwork) {
        jsonObject.add(
            "target",
            partPosToJson(
                networkElement.getTarget()
                    .getTarget()));
        jsonObject.addProperty("loaded", networkElement.isLoaded());
        ((JsonArray) jsonObject.get("@type")).add(
            new JsonPrimitive(
                JsonUtil.absolutizePath(
                    "registry/part/" + networkElement.getPart()
                        .getUniqueName())));

        IPartState partState = networkElement.getPartState();
        if (partState instanceof IPartStateWriter && ((IPartStateWriter) partState).getActiveAspect() != null) {
            JsonObject object = new JsonObject();
            addAspectTypeInfo(object, ((IPartStateWriter) partState).getActiveAspect());
            jsonObject.add("activeAspect", object);
        }
    }

    public static void addValueInterfaceInfo(JsonObject jsonObject, IValueInterface valueInterface) {
        try {
            Optional<IValue> value = valueInterface.getValue();
            if (value.isPresent()) {
                addValueInfo(jsonObject, value.get());
            }
        } catch (EvaluationException e) {
            jsonObject.addProperty("error", e.getMessage());
        }
    }

    public static JsonObject partPosToJson(PartPos partPos) {
        return posToJson(partPos.getPos(), partPos.getSide());
    }

    public static JsonObject posToJson(DimPos pos, @Nullable ForgeDirection side) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("world", pos.getDimensionId());
        jsonObject.addProperty(
            "x",
            pos.getBlockPos()
                .getX());
        jsonObject.addProperty(
            "y",
            pos.getBlockPos()
                .getY());
        jsonObject.addProperty(
            "z",
            pos.getBlockPos()
                .getZ());
        if (side != null) {
            jsonObject.addProperty(
                "side",
                "ir:" + side.name()
                    .toLowerCase(Locale.ENGLISH));
        }
        return jsonObject;
    }

    public static void addVariableInfo(JsonObject jsonObject, IVariable variable) {
        try {
            addValueInfo(jsonObject, variable.getValue());
        } catch (EvaluationException e) {
            jsonObject.addProperty("error", e.getMessage());
        }
    }

    public static void addValueInfo(JsonObject jsonObject, IValue value) {
        jsonObject.addProperty(
            "valueType",
            JsonUtil.absolutizePath(
                "registry/value/" + value.getType()
                    .getTypeName()));
        jsonObject.add("value", valueToJson(value));
    }

    public static JsonElement valueToJson(IValue value) {
        IValueTypeJsonHandler<IValue> valueTypeJsonHandler = ValueTypeJsonHandlers.REGISTRY.getHandler(value.getType());
        if (valueTypeJsonHandler != null) {
            return valueTypeJsonHandler.handle(value);
        } else {
            return new JsonObject();
        }
    }

    public static Optional<IValue> jsonToValue(JsonElement jsonElement) {
        for (IReverseValueTypeJsonHandler<?> handler : ValueTypeJsonHandlers.REGISTRY.getReverseHandlers()) {
            IValue value = handler.handle(jsonElement);
            if (value != null) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    public static String resourceLocationToPath(ResourceLocation resourceLocation) {
        if (resourceLocation == null) return "";
        return resourceLocation.getResourceDomain() + "/" + resourceLocation.getResourcePath();
    }

    public static void addPartTypeInfo(JsonObject jsonObject, IPartType partType) {
        jsonObject.addProperty("@id", JsonUtil.absolutizePath("registry/part/" + partType.getUniqueName()));
        jsonObject.addProperty(
            "resourceLocation",
            partType.getUniqueName()
                .toString());
        JsonArray types = new JsonArray();
        if (partType instanceof IPartTypeReader) {
            types.add(new JsonPrimitive("ReadPart"));
        }
        if (partType instanceof IPartTypeWriter) {
            types.add(new JsonPrimitive("WritePart"));
        }
        jsonObject.add("@type", types);
        jsonObject.addProperty(
            "item",
            JsonUtil.absolutizePath(
                "registry/item/" + JsonUtil.resourceLocationToPath(Helpers.getLocation(partType.getItem()))));
        if (partType instanceof IPartTypeReader) {
            JsonArray array = new JsonArray();
            for (IAspect aspect : ((IPartTypeReader<?, ?>) partType).getReadAspects()) {
                JsonObject object = new JsonObject();
                addAspectTypeInfo(object, aspect);
                array.add(object);
            }
            jsonObject.add("readAspects", array);
        }
        if (partType instanceof IPartTypeWriter) {
            JsonArray array = new JsonArray();
            for (IAspect aspect : ((IPartTypeWriter<?, ?>) partType).getWriteAspects()) {
                JsonObject object = new JsonObject();
                addAspectTypeInfo(object, aspect);
                array.add(object);
            }
            jsonObject.add("writeAspects", array);
        }
    }

    public static void addAspectTypeInfo(JsonObject jsonObject, IAspect aspect) {
        jsonObject.addProperty("@id", JsonUtil.absolutizePath("registry/aspect/" + aspect.getUnlocalizedName()));
        jsonObject.addProperty(
            "resourceLocation",
            aspect.getUniqueName()
                .toString());
        JsonArray types = new JsonArray();
        if (aspect instanceof IAspectRead) {
            types.add(new JsonPrimitive("ReadAspect"));
        }
        if (aspect instanceof IAspectWrite) {
            types.add(new JsonPrimitive("WriteAspect"));
        }
        jsonObject.add("@type", types);
        jsonObject.addProperty("label", LangHelpers.localize(aspect.getUnlocalizedName()));
        jsonObject.addProperty("comment", LangHelpers.localize(aspect.getUnlocalizedName() + ".info"));
        jsonObject.addProperty("unlocalizedName", aspect.getUnlocalizedName());
        jsonObject.addProperty(
            "valueType",
            JsonUtil.absolutizePath(
                "registry/value/" + aspect.getValueType()
                    .getUnlocalizedName()
                    .replace('.', '/')));
    }

    public static void addValueTypeInfo(JsonObject jsonObject, IValueType valueType) {
        jsonObject.addProperty("@id", JsonUtil.absolutizePath("registry/value/" + valueType.getTypeName()));
        jsonObject.addProperty(
            "resourceLocation",
            valueType.getUniqueName()
                .toString());
        jsonObject.addProperty("label", valueType.getTypeName());
        jsonObject.addProperty("unlocalizedName", valueType.getUnlocalizedName());
        jsonObject.add("value", JsonUtil.valueToJson(valueType.getDefault()));
        jsonObject.addProperty("color", valueType.getDisplayColor());
    }

    public static void addItemInfo(JsonObject jsonObject, Item item) {
        jsonObject.addProperty(
            "@id",
            JsonUtil.absolutizePath("registry/item/" + JsonUtil.resourceLocationToPath(Helpers.getLocation(item))));
        jsonObject.addProperty(
            "mod",
            JsonUtil.absolutizePath(
                "registry/mod/" + Helpers.getLocation(item)
                    .getResourceDomain()));
        jsonObject.addProperty("unlocalizedName", item.getUnlocalizedName());
        jsonObject.addProperty(
            "resourceLocation",
            Helpers.getLocation(item)
                .toString());
        Block block = Block.getBlockFromItem(item);
        if (block != null) {
            jsonObject.addProperty(
                "block",
                JsonUtil
                    .absolutizePath("registry/block/" + JsonUtil.resourceLocationToPath(Helpers.getLocation(block))));
        }
    }

    public static void addBlockInfo(JsonObject jsonObject, Block block) {
        jsonObject.addProperty(
            "@id",
            JsonUtil.absolutizePath("registry/block/" + JsonUtil.resourceLocationToPath(Helpers.getLocation(block))));
        jsonObject.addProperty(
            "mod",
            JsonUtil.absolutizePath(
                "registry/mod/" + Helpers.getLocation(block)
                    .getResourceDomain()));
        jsonObject.addProperty("unlocalizedName", block.getUnlocalizedName());
        jsonObject.addProperty(
            "resourceLocation",
            Helpers.getLocation(block)
                .toString());
        Item item = Item.getItemFromBlock(block);
        if (item != null) {
            jsonObject.addProperty(
                "item",
                JsonUtil.absolutizePath("registry/item/" + JsonUtil.resourceLocationToPath(Helpers.getLocation(item))));
        }
    }

    public static void addFluidInfo(JsonObject jsonObject, Fluid fluid) {
        jsonObject.addProperty("@id", JsonUtil.absolutizePath("registry/fluid/" + fluid.getName()));
        jsonObject.addProperty("unlocalizedName", fluid.getUnlocalizedName());
        if (fluid.getBlock() != null) {
            jsonObject.addProperty(
                "block",
                JsonUtil.absolutizePath(
                    "registry/block/" + JsonUtil.resourceLocationToPath(Helpers.getLocation(fluid.getBlock()))));
        }
    }

    public static void addModInfo(JsonObject jsonObject, ModContainer modContainer) {
        jsonObject.addProperty("@id", JsonUtil.absolutizePath("registry/mod/" + modContainer.getModId()));
        jsonObject.addProperty("label", modContainer.getName());
        jsonObject.addProperty("version", modContainer.getVersion());

        JsonArray dependencies = new JsonArray();
        for (ArtifactVersion artifactVersion : modContainer.getRequirements()) {
            JsonObject jsonVersion = new JsonObject();
            addDependencyInfo(jsonVersion, artifactVersion);
            dependencies.add(jsonVersion);
        }
        jsonObject.add("dependencies", dependencies);
    }

    public static void addDependencyInfo(JsonObject jsonObject, ArtifactVersion artifactVersion) {
        jsonObject.addProperty("mod", JsonUtil.absolutizePath("registry/mod/" + artifactVersion.getLabel()));
        jsonObject.addProperty("versionRange", artifactVersion.getRangeString());
    }

}
