package ruiseki.integrateddynamics.core.network.diagnostics;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.okcore.client.renderer.GlStateManager;

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
            for (Iterator<PartPos> it = partList.iterator(); it.hasNext();) {
                PartPos partPos = it.next();
                if (partPos.getPos() != null && partPos.getPos()
                    .getWorld() == player.worldObj
                    && player.getDistanceSq(
                        partPos.getPos()
                            .getBlockPos()
                            .getX(),
                        partPos.getPos()
                            .getBlockPos()
                            .getY(),
                        partPos.getPos()
                            .getBlockPos()
                            .getZ())
                        < 10000) {
                    PartHelpers.PartStateHolder<?, ?> partStateHolder = PartHelpers.getPart(partPos);
                    if (partStateHolder != null) {
                        AxisAlignedBB bb = partStateHolder.getPart()
                            .getPartRenderPosition()
                            .getBoundingBox(partPos.getSide())
                            .offset(
                                partPos.getPos()
                                    .getBlockPos())
                            .offset(-offsetX, -offsetY, -offsetZ)
                            .expand(0.05, 0.05, 0.05);
                        RenderGlobal.drawOutlinedBoundingBox(bb, -1);
                    } else {
                        it.remove();
                    }
                }
            }

            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
    }
}
