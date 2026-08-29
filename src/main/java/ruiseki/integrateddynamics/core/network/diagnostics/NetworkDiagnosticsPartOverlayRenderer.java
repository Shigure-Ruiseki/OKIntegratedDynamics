package ruiseki.integrateddynamics.core.network.diagnostics;

import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.okcore.block.collidable.ImmutableAxisAlignedBB;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.datastructure.BlockPos;

/**
 * @author rubensworks
 */
@SideOnly(Side.CLIENT)
public class NetworkDiagnosticsPartOverlayRenderer {

    private static final NetworkDiagnosticsPartOverlayRenderer _INSTANCE = new NetworkDiagnosticsPartOverlayRenderer();
    private final Set<PartPos> partPositions = Sets.newHashSet();

    private NetworkDiagnosticsPartOverlayRenderer() {

    }

    public static NetworkDiagnosticsPartOverlayRenderer getInstance() {
        return _INSTANCE;
    }

    public synchronized void addPos(PartPos pos) {
        partPositions.add(pos);
    }

    public synchronized void removePos(PartPos pos) {
        partPositions.remove(pos);
    }

    public synchronized void clearPositions() {
        partPositions.clear();
    }

    public synchronized boolean hasPartPos(PartPos pos) {
        return partPositions.contains(pos);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (!partPositions.isEmpty()) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            World world = player.worldObj;
            if (world == null) return;

            float partialTicks = event.partialTicks;

            double offsetX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
            double offsetY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
            double offsetZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
            GL11.glLineWidth(6.0F);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);

            List<PartPos> partList = Lists.newArrayList(partPositions);
            for (PartPos partPos : partList) {
                BlockPos blockPos = partPos.getPos().getBlockPos();

                if (partPos.getPos().getDimensionId() != world.provider.dimensionId) {
                    continue;
                }

                if (player.getDistanceSq(blockPos.getX(), blockPos.getY(), blockPos.getZ()) >= 10000) {
                    continue;
                }

                int chunkX = blockPos.getX() >> 4;
                int chunkZ = blockPos.getZ() >> 4;
                if (!world.getChunkProvider().chunkExists(chunkX, chunkZ)) {
                    continue;
                }

                PartHelpers.PartStateHolder<?, ?> partStateHolder = PartHelpers.getPart(partPos);
                final ImmutableAxisAlignedBB localPartBB;
                if (partStateHolder != null) {
                    localPartBB = partStateHolder.getPart()
                        .getPartRenderPosition()
                        .getBoundingBox(partPos.getSide());
                } else {
                    localPartBB = new ImmutableAxisAlignedBB(0f, 0f, 0f, 1f, 1f, 1f);
                }

                final ImmutableAxisAlignedBB globalRenderBB = localPartBB.offset(blockPos)
                    .offset(-offsetX, -offsetY, -offsetZ)
                    .expand(0.05, 0.05, 0.05)
                    .expand(-0.05, -0.05, -0.05);
                RenderGlobal.drawOutlinedBoundingBox(globalRenderBB, -1);
            }

            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
    }
}
