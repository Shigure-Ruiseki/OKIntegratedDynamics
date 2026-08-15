package ruiseki.integrateddynamics.client.render.part;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Triple;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.part.PartTypeConnectorOmniDirectional;
import ruiseki.okcore.client.particle.ParticleBlur;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.Helpers;

/**
 * Overlay renderer for the omni-directional connector for rendering particle effects.
 *
 * @author rubensworks
 */
@SideOnly(Side.CLIENT)
public class ConnectorOmniPartOverlayRenderer extends PartOverlayRendererBase {

    private static final Int2IntMap CACHED_GROUP_COLORS = new Int2IntOpenHashMap();

    private final Random rand = new Random();

    protected static int getGroupColor(int group) {
        if (!CACHED_GROUP_COLORS.containsKey(group)) {
            Random rand = new Random(group);
            int color = rand.nextInt(1 << 23) | (255 << 24);
            CACHED_GROUP_COLORS.put(group, color);
            return color;
        }
        return CACHED_GROUP_COLORS.get(group);
    }

    @Override
    public void renderPartOverlay(IPartContainer partContainer, double x, double y, double z, float partialTick,
        int destroyStage, ForgeDirection direction, IPartType partType,
        TileEntityRendererDispatcher rendererDispatcher) {

        BlockPos pos = partContainer.getPosition()
            .getBlockPos();

        if (!shouldRender(pos)) return;

        if (rand.nextInt(20) == 0 && !Minecraft.getMinecraft()
            .isGamePaused()) {
            IPartState partStateUnsafe = partContainer.getPartState(direction);
            if (partStateUnsafe instanceof PartTypeConnectorOmniDirectional.State) {
                PartTypeConnectorOmniDirectional.State partState = (PartTypeConnectorOmniDirectional.State) partStateUnsafe;
                if (partState.hasConnectorId()) {

                    boolean isXAxis = (direction == ForgeDirection.WEST || direction == ForgeDirection.EAST);
                    boolean isYAxis = (direction == ForgeDirection.DOWN || direction == ForgeDirection.UP);
                    boolean isZAxis = (direction == ForgeDirection.NORTH || direction == ForgeDirection.SOUTH);

                    double tx = pos.getX() + 0.5F
                        + direction.offsetX * 1.15F
                        - 0.03F
                        + rand.nextFloat() * 0.04F
                        + (!isXAxis ? 0.25F - rand.nextFloat() * 0.5F : 0F);
                    double ty = pos.getY() + 0.5F
                        + direction.offsetY * 1.15F
                        - 0.03F
                        + rand.nextFloat() * 0.04F
                        + (!isYAxis ? 0.25F - rand.nextFloat() * 0.5F : 0F);
                    double tz = pos.getZ() + 0.5F
                        + direction.offsetZ * 1.15F
                        - 0.03F
                        + rand.nextFloat() * 0.04F
                        + (!isZAxis ? 0.25F - rand.nextFloat() * 0.5F : 0F);

                    float scale = 0.15F;
                    Triple<Float, Float, Float> colors = Helpers.intToRGB(getGroupColor(partState.getGroupId()));
                    float red = colors.getLeft() + rand.nextFloat() * 0.1F - 0.05F;
                    float green = colors.getMiddle() + rand.nextFloat() * 0.1F - 0.05F;
                    float blue = colors.getRight() + rand.nextFloat() * 0.1F - 0.05F;
                    float ageMultiplier = 17F;

                    ParticleBlur blur = new ParticleBlur(
                        Minecraft.getMinecraft().theWorld,
                        tx,
                        ty,
                        tz,
                        scale,
                        -(direction.offsetX * 0.05F + rand.nextFloat() * 0.02F - 0.01F),
                        -(direction.offsetY * 0.05F + rand.nextFloat() * 0.02F - 0.01F),
                        -(direction.offsetZ * 0.05F + rand.nextFloat() * 0.02F - 0.01F),
                        red,
                        green,
                        blue,
                        ageMultiplier);

                    Minecraft.getMinecraft().effectRenderer.addEffect(blur);
                }
            }
        }
    }
}
