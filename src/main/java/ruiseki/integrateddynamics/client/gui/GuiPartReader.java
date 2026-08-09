package ruiseki.integrateddynamics.client.gui;

import java.awt.Rectangle;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.tuple.Pair;

import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.IAspectRead;
import ruiseki.integrateddynamics.api.part.read.IPartStateReader;
import ruiseki.integrateddynamics.api.part.read.IPartTypeReader;
import ruiseki.integrateddynamics.core.client.gui.container.GuiMultipartAspects;
import ruiseki.integrateddynamics.core.inventory.container.ContainerMultipartAspects;
import ruiseki.integrateddynamics.core.inventory.container.ContainerPartReader;
import ruiseki.integrateddynamics.item.ItemVariable;
import ruiseki.okcore.helper.RenderHelpers;
import ruiseki.okcore.inventory.IGuiContainerProvider;

/**
 * Gui for a reader part.
 *
 * @author rubensworks
 */
public class GuiPartReader<P extends IPartTypeReader<P, S> & IGuiContainerProvider, S extends IPartStateReader<P>>
    extends GuiMultipartAspects<P, S, IAspectRead> {

    /**
     * Make a new instance.
     *
     * @param partTarget    The target.
     * @param player        The player.
     * @param partContainer The part container.
     * @param partType      The targeted part type.
     */
    public GuiPartReader(EntityPlayer player, PartTarget partTarget, IPartContainer partContainer, P partType) {
        super(new ContainerPartReader<P, S>(player, partTarget, partContainer, partType));
    }

    @Override
    protected String getNameId() {
        return "part_reader";
    }

    @Override
    protected void drawAdditionalElementInfoForeground(ContainerMultipartAspects<P, S, IAspectRead> container,
        int index, IAspectRead aspect, int mouseX, int mouseY) {

    }

    @Override
    protected void drawAdditionalElementInfo(ContainerMultipartAspects container, int index, IAspectRead aspect) {
        FontRenderer fontRenderer = fontRendererObj;

        // Get current aspect value
        ContainerPartReader reader = (ContainerPartReader) container;

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
        ItemStack itemStack = container.writeAspectInfo(false, new ItemStack(ItemVariable.getInstance()), aspect);
        Rectangle pos = getElementPosition(container, index, true);
        itemRender.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemStack, pos.x, pos.y);
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
