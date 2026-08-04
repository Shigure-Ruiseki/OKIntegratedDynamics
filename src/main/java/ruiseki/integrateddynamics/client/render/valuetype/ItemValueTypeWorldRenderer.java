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
 * A value type world renderer for items.
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

            // Render Item
            renderItemStack(itemStack, alpha);

            // Render Stack size text
            GlStateManager.pushMatrix();
            GlStateManager.translate(7F, 8.5F, 0.1F);
            GlStateManager.scale(0.5F, 0.5F, 1F);

            FontRenderer fontRenderer = rendererDispatcher != null ? rendererDispatcher.getFontRenderer()
                : Minecraft.getMinecraft().fontRenderer;
            if (fontRenderer == null) {
                fontRenderer = Minecraft.getMinecraft().fontRenderer;
            }

            fontRenderer.drawString(
                String.valueOf(itemStack.stackSize),
                0,
                0,
                Helpers.RGBAToInt(200, 200, 200, (int) (alpha * 255F)));
            GlStateManager.popMatrix();
        }
    }

    public static void renderItemStack(ItemStack itemStack, float alpha) {
        if (itemStack == null || itemStack.getItem() == null) return;

        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.pushMatrix();

        GlStateManager.translate(3F, 3F, 0F);
        GlStateManager.scale(0.4F, 0.4F, 0.01F);

        // Use MC 1.7.10 RenderHelpers.renderItem
        RenderHelpers.renderItem(Minecraft.getMinecraft().theWorld, itemStack, 0.0D, 0.0D, 0.0D);

        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
    }
}
