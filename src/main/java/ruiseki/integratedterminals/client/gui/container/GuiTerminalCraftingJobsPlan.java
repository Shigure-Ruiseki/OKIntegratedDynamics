package ruiseki.integratedterminals.client.gui.container;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;

import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.Reference;
import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalCraftingPlan;
import ruiseki.integratedterminals.client.gui.container.component.GuiCraftingPlan;
import ruiseki.integratedterminals.core.client.gui.CraftingJobGuiData;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalCraftingJobsPlan;
import ruiseki.integratedterminals.network.packet.CancelCraftingJobPacket;
import ruiseki.integratedterminals.network.packet.OpenCraftingJobsGuiPacket;
import ruiseki.okcore.client.gui.component.button.GuiButtonText;
import ruiseki.okcore.client.gui.container.GuiContainerExtended;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.init.ModBase;

/**
 * A gui for visualizing a live crafting plan.
 *
 * @author rubensworks
 */
public class GuiTerminalCraftingJobsPlan extends GuiContainerExtended {

    private final EntityPlayer player;

    @Nullable
    private GuiCraftingPlan guiCraftingPlan;

    public GuiTerminalCraftingJobsPlan(EntityPlayer player, PartTarget target, IPartContainer partContainer,
        IPartType partType, CraftingJobGuiData craftingPlanGuiData) {
        super(new ContainerTerminalCraftingJobsPlan(player, target, partContainer, partType, craftingPlanGuiData));

        this.player = player;
    }

    @Override
    protected ResourceLocation constructResourceLocation() {
        return new ResourceLocation(Reference.MOD_ID, this.getGuiTexture());
    }

    @Override
    public String getGuiTexture() {
        return IntegratedTerminals._instance.getReferenceValue(ModBase.REFKEY_TEXTURE_PATH_GUI) + "crafting_plan.png";
    }

    @Override
    public int getBaseXSize() {
        return 256;
    }

    @Override
    public int getBaseYSize() {
        return 222;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.buttonList.clear();

        ITerminalCraftingPlan craftingPlan = getContainer().getCraftingPlan();
        if (craftingPlan != null) {
            GuiCraftingPlan previousGuiCraftingPlan = this.guiCraftingPlan;
            this.guiCraftingPlan = new GuiCraftingPlan(this, craftingPlan, guiLeft, guiTop, 9, 18, 10);
            if (previousGuiCraftingPlan != null) {
                this.guiCraftingPlan.inheritVisualizationState(previousGuiCraftingPlan);
            }

            this.buttonList.add(
                new GuiButtonText(
                    0,
                    guiLeft + 70,
                    guiTop + 198,
                    100,
                    20,
                    LangHelpers.localize("gui.integratedterminals.terminal_crafting_job.craftingplan.cancel"),
                    true));
        } else {
            this.guiCraftingPlan = null;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (!this.checkHotbarKeys(keyCode)) {
            if (keyCode == Keyboard.KEY_ESCAPE) {
                returnToOverview();
            } else {
                super.keyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    protected ContainerTerminalCraftingJobsPlan getContainer() {
        return (ContainerTerminalCraftingJobsPlan) super.getContainer();
    }

    private void returnToOverview() {
        PartPos center = getContainer().getTarget()
            .getCenter();
        OpenCraftingJobsGuiPacket.send(
            center.getPos()
                .getBlockPos(),
            center.getSide());
    }

    @Override
    public boolean requiresAction(int buttonId) {
        return true;
    }

    @Override
    public void onButtonClick(int buttonId) {
        super.onButtonClick(buttonId);
        GuiButton button = buttonList.get(buttonId);
        if (button instanceof GuiButtonText) {
            cancelCraftingJob();
        }
    }

    private void cancelCraftingJob() {
        // Send packet to cancel crafting job
        IntegratedTerminals._instance.getPacketHandler()
            .sendToServer(new CancelCraftingJobPacket(getContainer().getCraftingJobGuiData()));

        // Return to overview
        returnToOverview();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        if (this.guiCraftingPlan != null) {
            guiCraftingPlan.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        } else {
            drawCenteredString(
                fontRendererObj,
                LangHelpers.localize("gui.integratedterminals.terminal_crafting_job.craftingplan.empty"),
                guiLeft + getBaseXSize() / 2,
                guiTop + 23,
                16777215);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        if (this.guiCraftingPlan != null) {
            guiCraftingPlan.drawGuiContainerForegroundLayer(mouseX, mouseY);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.guiCraftingPlan != null) {
            guiCraftingPlan.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        if (this.guiCraftingPlan != null) {
            guiCraftingPlan.handleMouseInput();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.guiCraftingPlan != null) {
            guiCraftingPlan.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void onUpdate(int valueId, NBTTagCompound value) {
        super.onUpdate(valueId, value);

        if (getContainer().getCraftingPlanNotifierId() == valueId) {
            this.initGui();
        }
    }
}
