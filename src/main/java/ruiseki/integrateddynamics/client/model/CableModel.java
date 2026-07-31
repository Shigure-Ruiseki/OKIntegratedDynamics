package ruiseki.integrateddynamics.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.Level;

import com.gtnewhorizon.gtnhlib.client.model.BakedModelQuadContext;
import com.gtnewhorizon.gtnhlib.client.model.JSONVariant;
import com.gtnewhorizon.gtnhlib.client.model.baked.BakedModel;
import com.gtnewhorizon.gtnhlib.client.model.loading.ModelDeserializer.Position;
import com.gtnewhorizon.gtnhlib.client.model.loading.ModelRegistry;
import com.gtnewhorizon.gtnhlib.client.model.loading.ResourceLoc;
import com.gtnewhorizon.gtnhlib.client.model.unbaked.JSONModel;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.ModelQuadView;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.properties.ModelQuadFacing;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.IPartType.RenderPosition;
import ruiseki.integrateddynamics.core.tileentity.TileMultipartTicking;
import ruiseki.okcore.datastructure.ThreadsafeCache;
import ruiseki.okcore.mixins.early.gtnhlib.JSONModelAccessor;

public class CableModel implements BakedModel {

    private static final int RADIUS = 4;
    private static final int TEXTURE_SIZE = 16;

    private static final int LENGTH_CONNECTION = (TEXTURE_SIZE - RADIUS) / 2;
    public static final float MIN = (float) LENGTH_CONNECTION / (float) TEXTURE_SIZE; // 0.375F
    public static final float MAX = 1.0F - MIN; // 0.625F

    private record PartCacheKey(ResourceLoc.ModelLoc modelLoc, ForgeDirection side) {}

    private static final ThreadsafeCache<PartCacheKey, BakedModel> BAKED_MODEL_CACHE = new ThreadsafeCache<>(
        256,
        key -> bakePartModel(((PartCacheKey) key).modelLoc(), ((PartCacheKey) key).side()),
        false);

    private static final Map<ForgeDirection, List<ModelQuadView>> CACHED_CORE_FACES = new EnumMap<>(
        ForgeDirection.class);
    private static final Map<ForgeDirection, List<ModelQuadView>> CACHED_STANDARD_CONNECTIONS = new EnumMap<>(
        ForgeDirection.class);

    private static List<ModelQuadView> CACHED_ITEM_INVENTORY_QUADS = null;
    private static IIcon cachedIcon = null;

    private static void ensureStaticCache(IIcon icon) {
        if (cachedIcon == icon && !CACHED_CORE_FACES.isEmpty()) {
            return;
        }
        cachedIcon = icon;

        CACHED_CORE_FACES.clear();
        CACHED_STANDARD_CONNECTIONS.clear();

        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            CACHED_CORE_FACES.put(side, buildCoreCapFace(side, icon));
            CACHED_STANDARD_CONNECTIONS.put(side, QuadBuilderHelper.buildCustomConnectionSegment(side, 1.0f, icon));
        }

        List<ModelQuadView> itemQuads = new ArrayList<>(48);
        for (List<ModelQuadView> faceQuads : CACHED_CORE_FACES.values()) {
            itemQuads.addAll(faceQuads);
        }
        itemQuads.addAll(CACHED_STANDARD_CONNECTIONS.get(ForgeDirection.WEST));
        itemQuads.addAll(CACHED_STANDARD_CONNECTIONS.get(ForgeDirection.EAST));
        CACHED_ITEM_INVENTORY_QUADS = Collections.unmodifiableList(itemQuads);
    }

    @Override
    public List<ModelQuadView> getQuads(BakedModelQuadContext context) {
        IIcon cableIcon = getParticle(context);
        if (cableIcon == null) {
            return Collections.emptyList();
        }

        ensureStaticCache(cableIcon);

        if (!(context instanceof BakedModelQuadContext.World worldContext)) {
            return CACHED_ITEM_INVENTORY_QUADS;
        }

        TileEntity te = worldContext.getWorld()
            .getTileEntity(worldContext.getX(), worldContext.getY(), worldContext.getZ());
        if (!(te instanceof TileMultipartTicking cable)) {
            return Collections.emptyList();
        }

        List<ModelQuadView> combinedQuads = new ArrayList<>(48);
        boolean realCable = cable.isRealCable();

        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            boolean hasPart = cable.hasPart(side);
            boolean isConnected = realCable && cable.isConnected(side);

            if (hasPart) {
                IPartType<?, ?> part = cable.getPart(side);
                if (part != null) {
                    if (realCable) {
                        RenderPosition renderPos = part.getRenderPosition();
                        float depthFactor = renderPos != null ? renderPos.getDepthFactor() : 0.0f;
                        float targetDepth = 1.0f - depthFactor;

                        if (targetDepth > MAX) {
                            combinedQuads
                                .addAll(QuadBuilderHelper.buildCustomConnectionSegment(side, targetDepth, cableIcon));
                        }
                    }

                    String modelPath = part.getBlockModelPath(cable, side);
                    if (modelPath != null && !modelPath.isEmpty()) {
                        ResourceLoc.ModelLoc partModelLoc = parseModelLocStatic(modelPath);
                        addPartQuads(combinedQuads, partModelLoc, side, context);
                    }
                }
            } else if (isConnected) {
                List<ModelQuadView> connQuads = CACHED_STANDARD_CONNECTIONS.get(side);
                if (connQuads != null) {
                    combinedQuads.addAll(connQuads);
                }
            }

            if (realCable && !isConnected && !hasPart) {
                List<ModelQuadView> capQuads = CACHED_CORE_FACES.get(side);
                if (capQuads != null) {
                    combinedQuads.addAll(capQuads);
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
                if (partQuads != null && !partQuads.isEmpty()) {
                    targetList.addAll(partQuads);
                }
            }
        } catch (Exception e) {
            IntegratedDynamics.clog(Level.ERROR, "Error render part from cache: " + modelLoc, e);
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

        JSONVariant variant = createVariantForPart(modelLoc, side);

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

    @Override
    public Position.ModelDisplay getDisplay(Position pos, BakedModelQuadContext context) {
        return Position.ModelDisplay.DEFAULT;
    }

    @Override
    public IIcon getParticle(BakedModelQuadContext context) {
        return Minecraft.getMinecraft()
            .getTextureMapBlocks()
            .getAtlasSprite(Reference.MOD_ID + ":cable");
    }

    public static BakedModel getBakedPartModel(String modelPath, ForgeDirection side) {
        if (modelPath == null || modelPath.isEmpty()) return null;
        ResourceLoc.ModelLoc modelLoc = parseModelLocStatic(modelPath);
        return BAKED_MODEL_CACHE.get(new PartCacheKey(modelLoc, side));
    }

    private static List<ModelQuadView> buildCoreCapFace(ForgeDirection side, IIcon icon) {
        float minX = MIN, minY = MIN, minZ = MIN;
        float maxX = MAX, maxY = MAX, maxZ = MAX;

        switch (side) {
            case DOWN -> maxY = MIN;
            case UP -> minY = MAX;
            case NORTH -> maxZ = MIN;
            case SOUTH -> minZ = MAX;
            case WEST -> maxX = MIN;
            case EAST -> minX = MAX;
        }

        Map<ModelQuadFacing, ArrayList<ModelQuadView>> store = QuadBuilderHelper
            .buildCuboidStore(minX, minY, minZ, maxX, maxY, maxZ, icon, null);

        ModelQuadFacing facing = ModelQuadFacing.fromForgeDir(side);
        List<ModelQuadView> quads = store.get(facing);
        return quads != null ? quads : Collections.emptyList();
    }
}
