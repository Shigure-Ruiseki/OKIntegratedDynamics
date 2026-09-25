package ruiseki.integratedcrafting.client.gui;

import java.util.Collections;
import java.util.Optional;

import net.minecraft.util.ResourceLocation;

import ruiseki.integratedcrafting.core.part.PartTypeInterfaceCraftingVariableBase;
import ruiseki.integratedcrafting.inventory.container.ContainerPartInterfaceCrafting;
import ruiseki.integrateddynamics.core.inventory.container.ContainerMultipartAspects;
import ruiseki.okcore.client.gui.component.button.GuiButtonImage;
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

    private static final int BUTTON_SETTINGS_X = 155;

    public GuiPartInterfaceCrafting(ContainerPartInterfaceCrafting container) {
        super(container);
    }

    @Override
    public void initGui() {
        super.initGui();
        addRenderableWidget(
            new GuiButtonImage(
                this.guiLeft + BUTTON_SETTINGS_X,
                this.guiTop + 4,
                15,
                15,
                LangHelpers.localize("gui.integrateddynamics.part_settings"),
                createServerPressable(ContainerMultipartAspects.BUTTON_SETTINGS, b -> {}),
                true,
                Images.CONFIG_BOARD,
                -2,
                -3));
    }

    @Override
    protected ResourceLocation constructGuiTexture() {
        return ((PartTypeInterfaceCraftingVariableBase<?, ?>) getContainer().getPartType()).getGuiTexture();
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
        int slotsX = ContainerPartInterfaceCrafting.getVariableSlotsX(
            getContainer().getContainerInventory()
                .getSizeInventory());
        for (int i = 0; i < getContainer().getContainerInventory()
            .getSizeInventory(); i++) {
            int x = guiLeft + slotsX + 2 + i * GuiHelpers.SLOT_SIZE;
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
        int slotsX = ContainerPartInterfaceCrafting.getVariableSlotsX(
            getContainer().getContainerInventory()
                .getSizeInventory());
        for (int i = 0; i < getContainer().getContainerInventory()
            .getSizeInventory(); i++) {
            int x = slotsX + 2 + i * GuiHelpers.SLOT_SIZE;
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
