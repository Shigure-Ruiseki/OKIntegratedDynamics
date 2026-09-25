package ruiseki.integratedcrafting.client.gui;

import java.util.Collections;
import java.util.Optional;

import net.minecraft.util.ResourceLocation;

import ruiseki.integratedcrafting.Reference;
import ruiseki.integratedcrafting.inventory.container.ContainerPartInterfaceCrafting;
import ruiseki.okcore.client.gui.container.GuiContainerExtended;
import ruiseki.okcore.client.gui.image.IImage;
import ruiseki.okcore.client.gui.image.Images;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.GuiHelpers;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Gui for the crafting interface.
 *
 * @author rubensworks
 */
public class GuiPartInterfaceCrafting extends GuiContainerExtended<ContainerPartInterfaceCrafting> {

    public GuiPartInterfaceCrafting(ContainerPartInterfaceCrafting container) {
        super(container);
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    protected ResourceLocation constructGuiTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/part_interface_crafting.png");
    }

    @Override
    protected int getBaseXSize() {
        return 176;
    }

    @Override
    protected int getBaseYSize() {
        return 141;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        GlStateManager.color(1, 1, 1, 1);
        int y = guiTop + 42;
        for (int i = 0; i < getContainer().getContainerInventory()
            .getSizeInventory(); i++) {
            int x = guiLeft + 10 + i * GuiHelpers.SLOT_SIZE;
            if (!ItemHelpers.isEmpty(
                getContainer().getContainerInventory()
                    .getStackInSlot(i))) {
                IImage image = container.isRecipeSlotValid(i) ? Images.OK : Images.ERROR;
                image.draw(this, x, y);
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        int y = 42;
        for (int i = 0; i < getContainer().getContainerInventory()
            .getSizeInventory(); i++) {
            int x = 10 + i * GuiHelpers.SLOT_SIZE;
            int slot = i;
            GuiHelpers.renderTooltipOptional(this, x, y, 14, 13, mouseX, mouseY, () -> {
                if (getContainer().getInventory()
                    .get(slot) != null) {
                    LangHelpers.UnlocalizedString unlocalizedMessage = container.getRecipeSlotUnlocalizedMessage(slot);
                    if (unlocalizedMessage != null) {
                        return Optional.of(Collections.singletonList(unlocalizedMessage.localize()));
                    }
                }
                return Optional.empty();
            });
        }
    }
}
