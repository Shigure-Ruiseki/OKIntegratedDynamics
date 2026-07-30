package ruiseki.integrateddynamics.client.model;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.Level;

import com.google.gson.Gson;
import com.gtnewhorizon.gtnhlib.client.model.BakedModelQuadContext;
import com.gtnewhorizon.gtnhlib.client.model.JSONVariant;
import com.gtnewhorizon.gtnhlib.client.model.baked.BakedModel;
import com.gtnewhorizon.gtnhlib.client.model.loading.ModelDeserializer.Position;
import com.gtnewhorizon.gtnhlib.client.model.loading.ModelRegistry;
import com.gtnewhorizon.gtnhlib.client.model.loading.ResourceLoc;
import com.gtnewhorizon.gtnhlib.client.model.unbaked.JSONModel;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.ModelQuadView;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.client.model.json.BlockStateJson;
import ruiseki.integrateddynamics.core.tileentity.TileMultipartTicking;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.ThreadsafeCache;
import ruiseki.okcore.mixins.early.gtnhlib.JSONModelAccessor;

public class CableModel implements BakedModel {

    private static final Gson GSON = new Gson();

    private static final int RADIUS = 4;
    private static final int TEXTURE_SIZE = 16;

    private static final int LENGTH_CONNECTION = (TEXTURE_SIZE - RADIUS) / 2;
    public static final float MIN = (float) LENGTH_CONNECTION / (float) TEXTURE_SIZE;
    public static final float MAX = 1.0F - MIN;

    private record PartCacheKey(ResourceLoc.ModelLoc modelLoc, ForgeDirection side) {}

    private static final ThreadsafeCache<PartCacheKey, BakedModel> BAKED_MODEL_CACHE = new ThreadsafeCache<>(
        256,
        key -> bakePartModel(((PartCacheKey) key).modelLoc(), ((PartCacheKey) key).side()),
        false);

    private static final ThreadsafeCache<ResourceLoc.ModelLoc, BlockStateJson> BLOCKSTATE_CACHE = new ThreadsafeCache<>(
        128,
        key -> readBlockStateJson((ResourceLoc.ModelLoc) key),
        false);

    private static BlockStateJson readBlockStateJson(ResourceLoc.ModelLoc modelLoc) {
        String domain = modelLoc.owner();
        String path = modelLoc.path();

        if (path.startsWith("block/")) {
            return null;
        }

        ResourceLocation resLoc = new ResourceLocation(domain, "blockstates/" + path + ".json");
        try (Reader reader = new InputStreamReader(
            Minecraft.getMinecraft()
                .getResourceManager()
                .getResource(resLoc)
                .getInputStream(),
            StandardCharsets.UTF_8)) {
            return GSON.fromJson(reader, BlockStateJson.class);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static final ResourceLoc.ModelLoc MODEL_CORE = new ResourceLoc.ModelLoc(
        Reference.MOD_ID,
        "block/cable_core");
    private static final ResourceLoc.ModelLoc MODEL_CONNECTION = new ResourceLoc.ModelLoc(
        Reference.MOD_ID,
        "block/cable_connection");

    @Override
    public List<ModelQuadView> getQuads(BakedModelQuadContext context) {
        List<ModelQuadView> combinedQuads = new ArrayList<>();

        // Core cable
        addPartQuads(combinedQuads, MODEL_CORE, ForgeDirection.UNKNOWN, context);

        if (context instanceof BakedModelQuadContext.World worldContext) {
            BlockPos pos = new BlockPos(worldContext.getX(), worldContext.getY(), worldContext.getZ());
            TileEntity te = worldContext.getWorld()
                .getTileEntity(pos.getX(), pos.getY(), pos.getZ());

            if (te instanceof TileMultipartTicking cable) {
                for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
                    if (cable.isConnected(side)) {
                        addPartQuads(combinedQuads, MODEL_CONNECTION, side, context);
                    }
                    if (cable.hasPart(side)) {
                        IPartType<?, ?> part = cable.getPart(side);
                        String modelPath = part.getBlockModelPath(cable, side);

                        if (modelPath != null && !modelPath.isEmpty()) {
                            ResourceLoc.ModelLoc partModelLoc = parseModelLocStatic(modelPath);
                            addPartQuads(combinedQuads, partModelLoc, side, context);
                        }
                    }
                }
            }
        }

        return combinedQuads;
    }

    private void addPartQuads(List<ModelQuadView> targetList, ResourceLoc.ModelLoc modelLoc, ForgeDirection side,
        BakedModelQuadContext ctx) {
        try {
            BakedModel bakedPart = BAKED_MODEL_CACHE.get(new PartCacheKey(modelLoc, side));

            if (bakedPart != null) {
                List<ModelQuadView> partQuads = bakedPart.getQuads(ctx);
                if (partQuads != null) {
                    targetList.addAll(partQuads);
                }
            }
        } catch (Exception e) {
            IntegratedDynamics.clog(Level.ERROR, "Lỗi render part qua cache: " + modelLoc, e);
        }
    }

    private static BakedModel bakePartModel(ResourceLoc.ModelLoc modelLoc, ForgeDirection side) {
        JSONModel baseModel = ModelRegistry.getJSONModel(modelLoc);

        if (baseModel == null) return null;

        Object2ObjectMap<String, String> mergedTextures = new Object2ObjectOpenHashMap<>();
        if (baseModel.getTextures() != null) {
            mergedTextures.putAll(baseModel.getTextures());
        }

        JSONModelAccessor accessor = (JSONModelAccessor) baseModel;

        JSONModel finalModelToBake = new JSONModel(
            accessor.getParentId(),
            accessor.isUseAO(),
            accessor.getDisplay(),
            mergedTextures,
            accessor.getElements());

        JSONVariant variant;
        if (MODEL_CONNECTION.equals(modelLoc)) {
            variant = createVariantForConnection(modelLoc, side);
        } else {
            variant = createVariantForPart(modelLoc, side);
        }

        return finalModelToBake.bake(variant);
    }

    private static ResourceLoc.ModelLoc parseModelLocStatic(String modelPath) {
        if (modelPath == null || modelPath.isEmpty()) {
            return new ResourceLoc.ModelLoc(Reference.MOD_ID, "");
        }

        String domain = Reference.MOD_ID;
        String path = modelPath;

        if (path.contains(":")) {
            String[] split = path.split(":", 2);
            domain = split[0];
            path = split[1];
        }

        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        return new ResourceLoc.ModelLoc(domain, path);
    }

    private static JSONVariant createVariantForPart(ResourceLoc.ModelLoc modelLoc, ForgeDirection side) {
        int rotX = 0;
        int rotY = 0;

        switch (side) {
            case DOWN -> rotX = 90;
            case UP -> rotX = 270;
            case NORTH -> rotY = 180;
            case SOUTH -> rotY = 0;
            case WEST -> rotY = 270;
            case EAST -> rotY = 90;
            default -> {}
        }

        return new JSONVariant(modelLoc, rotX, rotY, false);
    }

    private static JSONVariant createVariantForConnection(ResourceLoc.ModelLoc modelLoc, ForgeDirection side) {
        int rotX = 0;
        int rotY = 0;

        switch (side) {
            case DOWN -> rotX = 270;
            case UP -> rotX = 90;
            case NORTH -> rotY = 0;
            case SOUTH -> rotY = 180;
            case WEST -> rotY = 90;
            case EAST -> rotY = 270;
            default -> {}
        }

        return new JSONVariant(modelLoc, rotX, rotY, false);
    }

    @Override
    public Position.ModelDisplay getDisplay(Position pos, BakedModelQuadContext context) {
        BakedModel bakedCore = BAKED_MODEL_CACHE.get(new PartCacheKey(MODEL_CORE, ForgeDirection.UNKNOWN));
        if (bakedCore != null) {
            return bakedCore.getDisplay(pos, context);
        }
        return Position.ModelDisplay.DEFAULT;
    }

    @Override
    public IIcon getParticle(BakedModelQuadContext context) {
        BakedModel bakedCore = BAKED_MODEL_CACHE.get(new PartCacheKey(MODEL_CORE, ForgeDirection.UNKNOWN));
        if (bakedCore != null) {
            return bakedCore.getParticle(context);
        }
        return null;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
