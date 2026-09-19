package ruiseki.integrateddynamics.client.render.valuetype;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
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
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.ItemHelpers;

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
        ItemStack itemStack = valueItemStack.getRawValue();

        if (!ItemHelpers.isEmpty(itemStack)) {
            // ItemStack
            renderItemStack(itemStack, alpha);

            // Stack size
            GL11.glPushMatrix();
            GL11.glTranslatef(7.0F, 8.5F, 0.3F);

            String stackSize = String.valueOf(itemStack.stackSize);
            float scale = 1.0F / ((float) stackSize.length() + 1.0F);
            GL11.glScalef(scale, scale, 1.0F);

            FontRenderer fontRenderer = rendererDispatcher.getFontRenderer();
            fontRenderer.drawString(stackSize, 0, 0, Helpers.RGBAToInt(200, 200, 200, (int) (alpha * 255.0F)));
            GL11.glPopMatrix();
        }
    }

    public static void renderItemStack(ItemStack itemStack, float alpha) {
        Minecraft mc = Minecraft.getMinecraft();

        GL11.glPushMatrix();

        GL11.glScalef(0.75F, -0.75F, -0.001F);
        GL11.glRotatef(180,1F, 0F, 0F);
        GL11.glTranslatef(0.0F, 0.0F, 0.0F);

        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
        RENDER_ITEM.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemStack, 0, 0);
        RenderHelper.disableStandardItemLighting();

        GL11.glPopMatrix();
    }
}
