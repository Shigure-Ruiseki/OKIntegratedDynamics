package ruiseki.integrateddynamics.client.render.valuetype;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.base.Optional;

import ruiseki.integrateddynamics.api.client.render.valuetype.IValueTypeWorldRenderer;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeItemStack;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.RenderHelpers;

/**
 * A value type world renderer for items (Minecraft 1.7.10 Port).
 *
 * @author rubensworks
 */
public class ItemValueTypeWorldRenderer implements IValueTypeWorldRenderer {

    @Override
    public void renderValue(IPartContainer partContainer, double x, double y, double z, float partialTick,
        int destroyStage, ForgeDirection direction, IPartType partType, IValue value,
        TileEntityRendererDispatcher rendererDispatcher, float alpha) {

        Optional<ItemStack> itemStackOptional = ((ValueObjectTypeItemStack.ValueItemStack) value).getRawValue();
        if (itemStackOptional.isPresent() && itemStackOptional.get() != null) {
            ItemStack itemStack = itemStackOptional.get();

            renderItemStack(itemStack, alpha);

            GlStateManager.pushMatrix();
            GlStateManager.translate(6.0F, 8.5F, 0.2F);
            String stackSize = String.valueOf(itemStack.stackSize);

            float scale = 0.5F / Math.max(1.0F, ((float) stackSize.length() * 0.5F));
            GlStateManager.scale(scale, scale, 1.0F);

            FontRenderer fontRenderer = rendererDispatcher != null ? rendererDispatcher.getFontRenderer()
                : Minecraft.getMinecraft().fontRenderer;
            if (fontRenderer == null) {
                fontRenderer = Minecraft.getMinecraft().fontRenderer;
            }

            if (fontRenderer != null) {
                fontRenderer.drawString(stackSize, 0, 0, Helpers.RGBAToInt(220, 220, 220, (int) (alpha * 255F)));
            }
            GlStateManager.popMatrix();
        }
    }

    public static void renderItemStack(ItemStack itemStack, float alpha) {
        if (itemStack == null || itemStack.getItem() == null) return;

        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.pushMatrix();

        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.78125F, 0.78125F, 0.01F);

        // Render Item chuẩn theo OKCore / Forge 1.7.10
        RenderHelpers.renderItem(Minecraft.getMinecraft().theWorld, itemStack, 0.0D, 0.0D, 0.0D);

        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
    }
}
