package ruiseki.integrateddynamics.client.gui;

import net.minecraft.util.ResourceLocation;

import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.core.client.gui.GuiMechanicalMachine;
import ruiseki.integrateddynamics.inventory.container.ContainerMechanicalDryingBasin;
import ruiseki.okcore.helper.GuiHelpers;

/**
 * Gui for the mechanical squeezer.
 *
 * @author rubensworks
 */
public class GuiMechanicalDryingBasin extends GuiMechanicalMachine<ContainerMechanicalDryingBasin> {

    public GuiMechanicalDryingBasin(ContainerMechanicalDryingBasin container) {
        super(container);
    }

    @Override
    protected ResourceLocation constructGuiTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/mechanical_drying_basin.png");
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        // Render progress
        GuiHelpers.renderProgressBar(
            this,
            getGuiLeftTotal() + 84,
            getGuiTopTotal() + 31,
            11,
            28,
            176,
            120,
            GuiHelpers.ProgressDirection.UP,
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

        // Render input fluid tank
        GuiHelpers.renderOverlayedFluidTank(
            this,
            getContainer().getInputFluidStack(),
            getContainer().getInputFluidCapacity(),
            getGuiLeftTotal() + 28,
            getGuiTopTotal() + 16,
            18,
            60,
            texture,
            176,
            0);

        // Render output fluid tank
        GuiHelpers.renderOverlayedFluidTank(
            this,
            getContainer().getOutputFluidStack(),
            getContainer().getOutputFluidCapacity(),
            getGuiLeftTotal() + 150,
            getGuiTopTotal() + 16,
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
            getContainer().getInputFluidStack(),
            getContainer().getInputFluidCapacity(),
            28,
            16,
            18,
            60,
            mouseX,
            mouseY);
        drawFluidTankTooltip(
            getContainer().getOutputFluidStack(),
            getContainer().getOutputFluidCapacity(),
            150,
            16,
            18,
            60,
            mouseX,
            mouseY);
    }
}
