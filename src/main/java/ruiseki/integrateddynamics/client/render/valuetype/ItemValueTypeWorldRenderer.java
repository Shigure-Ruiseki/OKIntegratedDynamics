package ruiseki.integrateddynamics.client.render.valuetype;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import ruiseki.integrateddynamics.api.client.render.valuetype.IValueTypeWorldRenderer;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeItemStack;
import ruiseki.okcore.helper.Helpers;

/**
 * A value type world renderer for items (Minecraft 1.7.10 Port).
 *
 * @author rubensworks
 */
public class ItemValueTypeWorldRenderer implements IValueTypeWorldRenderer {

    private static final RenderItem RENDER_ITEM = RenderItem.getInstance();

    @Override
    public void renderValue(IPartContainer partContainer, double x, double y, double z, float partialTick,
        int destroyStage, ForgeDirection direction, IPartType partType, IValue value,
        TileEntityRendererDispatcher rendererDispatcher, float alpha) {

        ValueObjectTypeItemStack.ValueItemStack valueItemStack = (ValueObjectTypeItemStack.ValueItemStack) value;
        if (valueItemStack.getRawValue()
            .isPresent()) {
            ItemStack itemStack = valueItemStack.getRawValue()
                .get();

            // ItemStack
            renderItemStack(itemStack, alpha);

            // Stack size
            GL11.glPushMatrix();
            GL11.glTranslatef(7.0F, 8.5F, 0.3F);

            String stackSize = String.valueOf(itemStack.stackSize);
            float scale = 1.0F / ((float) stackSize.length() + 1.0F);
            GL11.glScalef(scale, scale, 1.0F);

            FontRenderer fontRenderer = rendererDispatcher.getFontRenderer();
            if (fontRenderer == null) {
                fontRenderer = Minecraft.getMinecraft().fontRenderer;
            }

            fontRenderer.drawString(stackSize, 0, 0, Helpers.RGBAToInt(200, 200, 200, (int) (alpha * 255.0F)));
            GL11.glPopMatrix();
        }
    }

    public static void renderItemStack(ItemStack itemStack, float alpha) {
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, -1.0F);
        GL11.glScalef(0.78F, 0.78F, 0.01F);

        GL11.glPushMatrix();
        GL11.glRotatef(40.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(95.0F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glPolygonOffset(-1.0F, -1.0F);

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopAttrib();

        Minecraft mc = Minecraft.getMinecraft();
        RENDER_ITEM.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemStack, 0, 0);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);

        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
    }
}
