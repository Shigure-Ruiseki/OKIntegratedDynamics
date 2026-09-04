package ruiseki.integrateddynamics.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumChatFormatting;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.core.client.gui.GuiMechanicalMachine;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.inventory.container.ContainerMechanicalSqueezer;
import ruiseki.integrateddynamics.tileentity.TileMechanicalSqueezer;
import ruiseki.okcore.client.gui.component.button.GuiButtonImage;
import ruiseki.okcore.client.gui.image.IImage;
import ruiseki.okcore.client.gui.image.Image;
import ruiseki.okcore.helper.GuiHelpers;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Gui for the mechanical squeezer.
 *
 * @author rubensworks
 */
public class GuiMechanicalSqueezer extends GuiMechanicalMachine<ContainerMechanicalSqueezer> {

    private final IImage imageArrowDownEnabled;
    private final IImage imageArrowDownDisabled;
    private GuiButtonImage buttonToggleFluidEject;

    /**
     * Make a new instance.
     *
     * @param inventory The player inventory.
     * @param tile      The part.
     */
    public GuiMechanicalSqueezer(InventoryPlayer inventory, TileMechanicalSqueezer tile) {
        super(new ContainerMechanicalSqueezer(inventory, tile));
        imageArrowDownEnabled = new Image(texture, 176, 138, 20, 10);
        imageArrowDownDisabled = new Image(texture, 176, 148, 20, 10);
    }

    @Override
    public void initGui() {
        super.initGui();

        this.buttonList.add(
            buttonToggleFluidEject = new GuiButtonImage(
                ContainerMechanicalSqueezer.BUTTON_TOGGLE_FLUID_EJECT,
                getGuiLeftTotal() + 149,
                getGuiTopTotal() + 71,
                imageArrowDownDisabled));
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        // Update the image in the fluid eject toggle button
        buttonToggleFluidEject.setImage(
            getContainer().getTile()
                .isAutoEjectFluids() ? imageArrowDownEnabled : imageArrowDownDisabled);

        // Render progress
        GuiHelpers.renderProgressBar(
            this,
            getGuiLeftTotal() + 73,
            getGuiTopTotal() + 36,
            12,
            18,
            176,
            120,
            GuiHelpers.ProgressDirection.DOWN,
            getContainer().getProgress(),
            getContainer().getMaxProgress());

        // Render energy level
        GuiHelpers.renderProgressBar(
            this,
            getGuiLeftTotal() + 8,
            getGuiTopTotal() + 16,
            18,
            60,
            176,
            60,
            GuiHelpers.ProgressDirection.UP,
            getContainer().getEnergy(),
            getContainer().getMaxEnergy());

        // Render fluid tank
        GuiHelpers.renderOverlayedFluidTank(
            this,
            getContainer().getFluidStack(),
            getContainer().getFluidCapacity(),
            getGuiLeftTotal() + 150,
            getGuiTopTotal() + 10,
            18,
            60,
            texture,
            176,
            0);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        drawEnergyBarTooltip(8, 16, 18, 60, mouseX, mouseY);
        drawFluidTankTooltip(
            getContainer().getFluidStack(),
            getContainer().getFluidCapacity(),
            150,
            10,
            18,
            60,
            mouseX,
            mouseY);

        // Draw fluid auto-eject toggle
        GuiHelpers.renderTooltip(
            this,
            150,
            70,
            18,
            10,
            mouseX,
            mouseY,
            () -> Lists.newArrayList(
                LangHelpers.localize(
                    L10NValues.GUI_MECHANICAL_SQUEEZER_TOGGLEFLUIDAUTOEJECT,
                    EnumChatFormatting.AQUA + LangHelpers.localize(
                        getContainer().getTile()
                            .isAutoEjectFluids() ? L10NValues.GENERAL_TRUE : L10NValues.GENERAL_FALSE)),
                LangHelpers.localize(L10NValues.GUI_MECHANICAL_SQUEEZER_TOGGLEFLUIDAUTOEJECT + ".info")));
    }
}
