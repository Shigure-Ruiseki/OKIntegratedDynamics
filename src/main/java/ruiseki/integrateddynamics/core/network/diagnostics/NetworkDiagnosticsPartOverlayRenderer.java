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
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return;

        float partialTicks = event.partialTicks;

        double offsetX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
        double offsetY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
        double offsetZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 0.2F, 0.1F, 0.8F);
        GL11.glLineWidth(6.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(false);

        List<PartPos> partList;
        synchronized (this) {
            partList = Lists.newArrayList(partPositions);
        }

        for (Iterator<PartPos> it = partList.iterator(); it.hasNext();) {
            PartPos partPos = it.next();
            if (partPos.getPos()
                .getWorld() == player.worldObj) {
                BlockPos bPos = partPos.getPos()
                    .getBlockPos();

                double dx = bPos.getX() - player.posX;
                double dy = bPos.getY() - player.posY;
                double dz = bPos.getZ() - player.posZ;
                double distanceSq = dx * dx + dy * dy + dz * dz;

                if (distanceSq < 10000) {
                    PartHelpers.PartStateHolder<?, ?> partStateHolder = PartHelpers.getPart(partPos);
                    if (partStateHolder != null) {
                        AxisAlignedBB bb = partStateHolder.getPart()
                            .getRenderPosition()
                            .getBoundingBox(partPos.getSide())
                            .offset(bPos)
                            .offset(-offsetX, -offsetY, -offsetZ)
                            .expand(0.05, 0.05, 0.05);

                        RenderGlobal.drawOutlinedBoundingBox(bb, -1);
                    } else {
                        synchronized (this) {
                            removePos(partPos);
                        }
                    }
                }
            }
        }

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
