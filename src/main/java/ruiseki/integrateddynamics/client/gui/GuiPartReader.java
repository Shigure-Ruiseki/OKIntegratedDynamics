package ruiseki.integrateddynamics.client.gui;

import java.awt.Rectangle;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.tuple.Pair;

import ruiseki.integrateddynamics.api.part.aspect.IAspectRead;
import ruiseki.integrateddynamics.api.part.read.IPartStateReader;
import ruiseki.integrateddynamics.api.part.read.IPartTypeReader;
import ruiseki.integrateddynamics.core.client.gui.container.GuiMultipartAspects;
import ruiseki.integrateddynamics.inventory.container.ContainerPartReader;
import ruiseki.integrateddynamics.item.ItemVariableConfig;
import ruiseki.okcore.helper.RenderHelpers;

/**
 * Gui for a reader part.
 *
 * @author rubensworks
 */
public class GuiPartReader<P extends IPartTypeReader<P, S>, S extends IPartStateReader<P>>
    extends GuiMultipartAspects<P, S, IAspectRead, ContainerPartReader<P, S>> {

    public GuiPartReader(ContainerPartReader<P, S> container) {
        super(container);
    }

    @Override
    protected String getNameId() {
        return "part_reader";
    }

    @Override
    protected void drawAdditionalElementInfoForeground(ContainerPartReader<P, S> container, int index,
        IAspectRead aspect, int mouseX, int mouseY) {

    }

    @Override
    protected void drawAdditionalElementInfo(ContainerPartReader<P, S> container, int index, IAspectRead aspect) {

        // Get current aspect value
        ContainerPartReader<P, S> reader = container;

        Pair<String, Integer> readValues = reader.getReadValue(aspect);
        if (readValues != null) {
            RenderHelpers.drawScaledCenteredString(
                fontRendererObj,
                readValues.getLeft(),
                this.guiLeft + offsetX + 16,
                this.guiTop + offsetY + 39 + container.getAspectBoxHeight() * index,
                70,
                readValues.getRight());
        }

        // Render target item
        // This could be cached if this would prove to be a bottleneck
        ItemStack itemStack = container
            .writeAspectInfo(false, new ItemStack(ItemVariableConfig._instance.getInstance()), aspect);
        Rectangle pos = getElementPosition(container, index, true);
        RenderHelper.enableGUIStandardItemLighting();
        itemRender.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemStack, pos.x, pos.y);
        RenderHelper.disableStandardItemLighting();
    }

    @Override
    protected int getBaseXSize() {
        return 195;
    }

    @Override
    protected int getBaseYSize() {
        return 213;
    }
}
