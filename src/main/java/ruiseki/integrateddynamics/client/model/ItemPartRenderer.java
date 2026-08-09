package ruiseki.integrateddynamics.client.model;

import java.util.List;
import java.util.Random;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;
import com.gtnewhorizon.gtnhlib.client.model.ItemContext;
import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;
import com.gtnewhorizon.gtnhlib.client.model.baked.BakedModel;
import com.gtnewhorizon.gtnhlib.client.model.loading.ModelRegistry;
import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.ModelQuadView;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.properties.ModelQuadFacing;
import com.gtnewhorizon.gtnhlib.util.StdLCG;

import ruiseki.integrateddynamics.core.item.ItemPart;
import ruiseki.okcore.helper.RenderHelpers;

public class ItemPartRenderer implements IItemRenderer {

    private final ItemContext itemContext = new ItemContext();
    private final Random rand = new StdLCG();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        if (!(stack.getItem() instanceof ItemPart<?, ?>itemPart)) return;

        BlockState state = itemPart.getPart()
            .getBlockState(null, ForgeDirection.EAST);
        BakedModel model = ModelRegistry.getBakedModel(state);
        if (model == null) return;

        itemContext.stack = stack;
        itemContext.random = rand;

        Tessellator tessellator = TessellatorManager.get();
        ModelISBRH isbrh = ModelISBRH.INSTANCE.get();

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);
        RenderHelpers.bindTexture(TextureMap.locationBlocksTexture);
        tessellator.startDrawingQuads();

        for (ModelQuadFacing facing : ModelQuadFacing.VALUES) {
            itemContext.quadFacing = facing;
            List<ModelQuadView> quads = model.getQuads(itemContext);
            if (quads == null || quads.isEmpty()) continue;

            for (ModelQuadView quad : quads) {
                float shade = ModelISBRH.diffuseLight(quad.getComputedFaceNormal());

                tessellator.setColorOpaque_F(shade, shade, shade);
                isbrh.renderQuad(quad, 0f, 0f, 0f, tessellator, null);
            }
        }

        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);

        tessellator.draw();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        itemContext.reset();
    }
}
