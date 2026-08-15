package ruiseki.integrateddynamics.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.Level;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;
import com.gtnewhorizon.gtnhlib.client.model.BakedModelQuadContext;
import com.gtnewhorizon.gtnhlib.client.model.baked.BakedModel;
import com.gtnewhorizon.gtnhlib.client.model.loading.ModelDeserializer.Position;
import com.gtnewhorizon.gtnhlib.client.model.loading.ModelRegistry;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.ModelQuad;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.ModelQuadView;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.properties.ModelQuadFacing;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartRenderPosition;
import ruiseki.integrateddynamics.core.helper.CableHelpers;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.QuadBuilderHelpers;

public class CableModel implements BakedModel {

    private static final int RADIUS = 4;
    private static final int TEXTURE_SIZE = 16;

    private static final int LENGTH_CONNECTION = (TEXTURE_SIZE - RADIUS) / 2;
    public static final float MIN = (float) LENGTH_CONNECTION / (float) TEXTURE_SIZE; // 0.375F
    public static final float MAX = 1.0F - MIN; // 0.625F

    private static final PartRenderPosition CABLE_RENDERPOSITION = new PartRenderPosition(
        -1,
        (((float) TEXTURE_SIZE - (float) RADIUS) / 2 / (float) TEXTURE_SIZE),
        (float) RADIUS / (float) TEXTURE_SIZE,
        (float) RADIUS / (float) TEXTURE_SIZE);

    private static final Map<ForgeDirection, List<ModelQuadView>> CACHED_CORE_FACES = new EnumMap<>(
        ForgeDirection.class);
    private static final Map<ForgeDirection, List<ModelQuadView>> CACHED_STANDARD_CONNECTIONS = new EnumMap<>(
        ForgeDirection.class);
    private static List<ModelQuadView> CACHED_CENTER_CORE_QUADS = Collections.emptyList();

    private static List<ModelQuadView> CACHED_ITEM_INVENTORY_QUADS = null;
    private static IIcon cachedIcon = null;

    private static void ensureStaticCache(IIcon icon) {
        if (cachedIcon == icon && !CACHED_CORE_FACES.isEmpty()) {
            return;
        }
        cachedIcon = icon;

        CACHED_CORE_FACES.clear();
        CACHED_STANDARD_CONNECTIONS.clear();

        Map<ModelQuadFacing, ArrayList<ModelQuadView>> centerStore = QuadBuilderHelpers
            .buildCuboidStore(MIN, MIN, MIN, MAX, MAX, MAX, icon, null);

        List<ModelQuadView> centerQuads = new ArrayList<>(24);
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            ModelQuadFacing facing = ModelQuadFacing.fromForgeDir(side);
            List<ModelQuadView> cap = centerStore.get(facing);
            if (cap != null) {
                CACHED_CORE_FACES.put(side, cap);
                centerQuads.addAll(cap);
            }
            CACHED_STANDARD_CONNECTIONS.put(side, buildCustomConnectionSegment(side, 1.0f, icon));
        }
        CACHED_CENTER_CORE_QUADS = Collections.unmodifiableList(centerQuads);

        List<ModelQuadView> itemQuads = new ArrayList<>(48);
        itemQuads.addAll(CACHED_CENTER_CORE_QUADS);
        itemQuads.addAll(CACHED_STANDARD_CONNECTIONS.get(ForgeDirection.WEST));
        itemQuads.addAll(CACHED_STANDARD_CONNECTIONS.get(ForgeDirection.EAST));
        CACHED_ITEM_INVENTORY_QUADS = Collections.unmodifiableList(itemQuads);
    }

    @Override
    public List<ModelQuadView> getQuads(BakedModelQuadContext context) {
        try {
            IIcon cableIcon = getParticle(context);
            if (cableIcon == null) {
                return Collections.emptyList();
            }

            ensureStaticCache(cableIcon);

            if (!(context instanceof BakedModelQuadContext.World worldContext)) {
                return CACHED_ITEM_INVENTORY_QUADS;
            }

            IBlockAccess world = worldContext.getWorld();
            BlockPos pos = new BlockPos(worldContext.getX(), worldContext.getY(), worldContext.getZ());

            List<ModelQuadView> combinedQuads = new ArrayList<>(48);
            boolean realCable = CableHelpers.isNoFakeCable(world, pos, null);
            IPartContainer partContainer = PartHelpers.getPartContainer(world, pos, null);

            if (realCable) {
                combinedQuads.addAll(CACHED_CENTER_CORE_QUADS);
            }

            for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
                boolean hasPart = partContainer != null && partContainer.hasPart(side);
                boolean isConnected = realCable && CableHelpers.isCableConnected(world, pos, side);

                if (hasPart) {
                    IPartType<?, ?> part = partContainer.getPart(side);
                    if (part != null) {
                        if (realCable) {
                            PartRenderPosition renderPos = part.getPartRenderPosition();
                            float depthFactor = renderPos != null ? renderPos.getDepthFactor() : 0.0f;
                            float targetDepth = 1.0f - depthFactor;

                            if (targetDepth > MAX) {
                                combinedQuads.addAll(buildCustomConnectionSegment(side, targetDepth, cableIcon));
                            }
                        }

                        BlockState partState = part.getBlockState(partContainer, side);
                        if (partState != null) {
                            BakedModel partModel = ModelRegistry.getBakedModel(partState);
                            if (partModel != null) {
                                combinedQuads.addAll(partModel.getQuads(context));
                            }
                        }
                    }
                } else if (isConnected) {
                    List<ModelQuadView> connQuads = CACHED_STANDARD_CONNECTIONS.get(side);
                    if (connQuads != null) {
                        combinedQuads.addAll(connQuads);
                    }
                }
            }

            BlockState facade = CableHelpers.getFacade(world, pos);
            if (facade != null) {
                IIcon facadeIcon = facade.getBlock()
                    .getIcon(0, facade.getBlockMeta(0));
                if (facadeIcon == null) {
                    facadeIcon = facade.getBlock()
                        .getBlockTextureFromSide(0);
                }

                if (facadeIcon != null) {
                    for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
                        boolean isConnected = CableHelpers.isCableConnected(world, pos, side);
                        boolean hasPart = partContainer != null && partContainer.hasPart(side);

                        PartRenderPosition partRenderPosition = PartRenderPosition.NONE;
                        if (hasPart) {
                            IPartType<?, ?> part = partContainer.getPart(side);
                            if (part != null) {
                                partRenderPosition = part.getPartRenderPosition();
                            }
                        } else if (isConnected) {
                            partRenderPosition = CABLE_RENDERPOSITION;
                        }

                        combinedQuads.addAll(getFacadeQuads(facadeIcon, side, partRenderPosition));
                    }
                }
            }

            return combinedQuads;
        } catch (Throwable t) {
            IntegratedDynamics.clog(Level.ERROR, "Fatal error building quads for CableModel at context", t);
            return CACHED_CENTER_CORE_QUADS;
        }
    }

    public static List<ModelQuadView> getFacadeQuads(IIcon texture, ForgeDirection side,
        PartRenderPosition partRenderPosition) {
        List<ModelQuadView> ret = new ArrayList<>(8);
        if (partRenderPosition == null || partRenderPosition == PartRenderPosition.NONE) {
            addBakedQuad(ret, 0F, 1F, 0F, 1F, 1F, texture, side);
        } else {
            float w = partRenderPosition.getWidthFactorSide();
            float h = partRenderPosition.getHeightFactorSide();

            float x0 = 0F;
            float x1 = (1F - w) / 2F;
            float x2 = x1 + w;
            float x3 = 1F;

            float z0 = 0F;
            float z1 = (1F - h) / 2F;
            float z2 = z1 + h;
            float z3 = 1F;

            addBakedQuad(ret, x0, x1, z0, z1, 1F, texture, side); // 1
            addBakedQuad(ret, x1, x2, z0, z1, 1F, texture, side); // 2
            addBakedQuad(ret, x2, x3, z0, z1, 1F, texture, side); // 3

            addBakedQuad(ret, x0, x1, z1, z2, 1F, texture, side); // 4
            // P (Skipped Part)
            addBakedQuad(ret, x2, x3, z1, z2, 1F, texture, side); // 5

            addBakedQuad(ret, x0, x1, z2, z3, 1F, texture, side); // 6
            addBakedQuad(ret, x1, x2, z2, z3, 1F, texture, side); // 7
            addBakedQuad(ret, x2, x3, z2, z3, 1F, texture, side); // 8
        }
        return ret;
    }

    private static void addBakedQuad(List<ModelQuadView> quads, float x0, float x1, float z0, float z1, float depth,
        IIcon icon, ForgeDirection side) {
        float minX = x0, minY = 0F, minZ = z0;
        float maxX = x1, maxY = depth, maxZ = z1;

        switch (side) {
            case DOWN -> {
                minY = 1.0F - depth;
                maxY = 1.0F;
            }
            case UP -> {
                minY = 0.0F;
                maxY = depth;
            }
            case NORTH -> {
                minZ = 1.0F - depth;
                maxZ = 1.0F;
            }
            case SOUTH -> {
                minZ = 0.0F;
                maxZ = depth;
            }
            case WEST -> {
                minX = 1.0F - depth;
                maxX = 1.0F;
            }
            case EAST -> {
                minX = 0.0F;
                maxX = depth;
            }
            default -> {}
        }

        float u0 = x0 * 16.0f;
        float v0 = z0 * 16.0f;
        float u1 = x1 * 16.0f;
        float v1 = z1 * 16.0f;

        ModelQuad quad = QuadBuilderHelpers
            .buildFaceQuad(side, minX, minY, minZ, maxX, maxY, maxZ, icon, u0, v0, u1, v1);
        quads.add(quad);
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

    public static ArrayList<ModelQuadView> buildCustomConnectionSegment(ForgeDirection side, float targetDepth,
        IIcon icon) {
        ArrayList<ModelQuadView> quads = new ArrayList<>();
        float min = CableModel.MIN; // 0.375f
        float max = CableModel.MAX; // 0.625f

        float x0 = min, y0 = min, z0 = min;
        float x1 = max, y1 = max, z1 = max;

        switch (side) {
            case DOWN -> {
                y0 = 1.0f - targetDepth;
                y1 = min;
            }
            case UP -> {
                y0 = max;
                y1 = targetDepth;
            }
            case NORTH -> {
                z0 = 1.0f - targetDepth;
                z1 = min;
            }
            case SOUTH -> {
                z0 = max;
                z1 = targetDepth;
            }
            case WEST -> {
                x0 = 1.0f - targetDepth;
                x1 = min;
            }
            case EAST -> {
                x0 = max;
                x1 = targetDepth;
            }
            default -> {
                return quads;
            }
        }

        for (ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {
            if (face == side.getOpposite()) continue;

            float u0 = 0, v0 = 0, u1 = 16, v1 = 16;
            switch (face) {
                case DOWN, UP -> {
                    u0 = x0 * 16.0f;
                    v0 = z0 * 16.0f;
                    u1 = x1 * 16.0f;
                    v1 = z1 * 16.0f;
                }
                case NORTH, SOUTH -> {
                    u0 = x0 * 16.0f;
                    v0 = (1.0f - y1) * 16.0f;
                    u1 = x1 * 16.0f;
                    v1 = (1.0f - y0) * 16.0f;
                }
                case WEST, EAST -> {
                    u0 = z0 * 16.0f;
                    v0 = (1.0f - y1) * 16.0f;
                    u1 = z1 * 16.0f;
                    v1 = (1.0f - y0) * 16.0f;
                }
            }

            ModelQuad quad = QuadBuilderHelpers.buildFaceQuad(face, x0, y0, z0, x1, y1, z1, icon, u0, v0, u1, v1);
            quads.add(quad);
        }

        return quads;
    }
}
