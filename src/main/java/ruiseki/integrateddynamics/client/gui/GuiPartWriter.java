package ruiseki.integrateddynamics.client.gui;

import java.awt.Rectangle;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.IAspectWrite;
import ruiseki.integrateddynamics.api.part.write.IPartStateWriter;
import ruiseki.integrateddynamics.api.part.write.IPartTypeWriter;
import ruiseki.integrateddynamics.core.client.gui.container.GuiMultipartAspects;
import ruiseki.integrateddynamics.core.inventory.container.ContainerMultipartAspects;
import ruiseki.integrateddynamics.inventory.container.ContainerPartWriter;
import ruiseki.integrateddynamics.item.ItemVariable;
import ruiseki.okcore.inventory.IGuiContainerProvider;

/**
 * Gui for a writer part.
 *
 * @author rubensworks
 */
public class GuiPartWriter<P extends IPartTypeWriter<P, S> & IGuiContainerProvider, S extends IPartStateWriter<P>>
    extends GuiMultipartAspects<P, S, IAspectWrite> {

    private static final int ERROR_X = 152;
    private static final int ERROR_Y = 20;
    private static final int OK_X = 152;
    private static final int OK_Y = 20;

    /**
     * Make a new instance.
     *
     * @param partTarget    The target.
     * @param player        The player.
     * @param partContainer The part container.
     * @param partType      The targeted part type.
     */
    public GuiPartWriter(EntityPlayer player, PartTarget partTarget, IPartContainer partContainer, P partType) {
        super(new ContainerPartWriter<P, S>(player, partTarget, partContainer, partType));
    }

    @Override
    protected String getNameId() {
        return "partWriter";
    }

    @Override
    protected void drawAdditionalElementInfoForeground(ContainerMultipartAspects<P, S, IAspectWrite> container,
        int index, IAspectWrite aspect, int mouseX, int mouseY) {
        // Render error tooltip
        displayErrors.drawForeground(
            getPartState().getErrors(aspect),
            ERROR_X,
            ERROR_Y + container.getAspectBoxHeight() * index,
            mouseX,
            mouseY,
            this,
            this.guiLeft,
            this.guiTop);
    }

    @Override
    protected void drawAdditionalElementInfo(ContainerMultipartAspects container, int index, IAspectWrite aspect) {
        int aspectBoxHeight = container.getAspectBoxHeight();

        // Render dummy target item
        // This could be cached if this would prove to be a bottleneck
        ItemStack itemStack = container.writeAspectInfo(false, new ItemStack(ItemVariable.getInstance()), aspect);
        Rectangle pos = getElementPosition(container, index, true);
        itemRender.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemStack, pos.x, pos.y);

        // Render error symbol
        displayErrors.drawBackground(
            getPartState().getErrors(aspect),
            ERROR_X,
            ERROR_Y + aspectBoxHeight * index,
            OK_X,
            OK_Y + aspectBoxHeight * index,
            this,
            this.guiLeft,
            this.guiTop,
            getPartState().getActiveAspect() == aspect);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        ContainerPartWriter container = (ContainerPartWriter) getContainer();
        fontRendererObj.drawString(
            container.getWriteValue(),
            this.guiLeft + offsetX + 53,
            this.guiTop + offsetY + 128,
            container.getWriteValueColor());
    }

    @Override
    protected int getBaseXSize() {
        return 195;
    }

    @Override
    protected int getBaseYSize() {
        return 222;
    }
}
