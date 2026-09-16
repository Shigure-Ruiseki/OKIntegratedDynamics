package ruiseki.integratedterminals.client.gui.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;

import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.Reference;
import ruiseki.integratedterminals.client.gui.container.component.GuiCraftingPlan;
import ruiseki.integratedterminals.client.gui.container.component.GuiCraftingPlanFlat;
import ruiseki.integratedterminals.client.gui.container.component.GuiCraftingPlanToggler;
import ruiseki.integratedterminals.core.client.gui.CraftingJobGuiData;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalCraftingJobsPlan;
import ruiseki.integratedterminals.network.packet.CancelCraftingJobPacket;
import ruiseki.integratedterminals.network.packet.OpenCraftingJobsGuiPacket;
import ruiseki.okcore.client.gui.component.button.GuiButtonText;
import ruiseki.okcore.client.gui.container.GuiContainerExtended;
import ruiseki.okcore.helper.LangHelpers;

/**
 * A gui for visualizing a live crafting plan.
 *
 * @author rubensworks
 */
public class GuiTerminalCraftingJobsPlan extends GuiContainerExtended<ContainerTerminalCraftingJobsPlan> {

    private final EntityPlayer player;
    private GuiCraftingPlanToggler guiCraftingPlanToggler;

    @Nullable
    private GuiCraftingPlan guiCraftingPlan;
    @Nullable
    private GuiCraftingPlanFlat guiCraftingPlanFlat;

    private boolean craftingPlanInitialized = false;
    private boolean craftingPlanFlatInitialized = false;

    public GuiTerminalCraftingJobsPlan(EntityPlayer player, PartTarget target, IPartContainer partContainer,
        IPartType partType, CraftingJobGuiData craftingPlanGuiData) {
        super(new ContainerTerminalCraftingJobsPlan(player, target, partContainer, partType, craftingPlanGuiData));

        this.player = player;
        this.guiCraftingPlanToggler = new GuiCraftingPlanToggler(
            () -> this.getContainer()
                .getCraftingPlan(),
            () -> this.getContainer()
                .getCraftingPlanFlat(),
            () -> {
                GuiCraftingPlan previousGuiCraftingPlan = this.guiCraftingPlan;
                this.guiCraftingPlan = new GuiCraftingPlan(
                    this,
                    this.getContainer()
                        .getCraftingPlan(),
                    guiLeft,
                    guiTop,
                    9,
                    18,
                    10);
                if (previousGuiCraftingPlan != null) {
                    this.guiCraftingPlan.inheritVisualizationState(previousGuiCraftingPlan);
                }
                addRenderableWidget(this.guiCraftingPlan);
                this.guiCraftingPlanFlat = null;

                if (this.getContainer()
                    .getCraftingPlanFlat() != null) {
                    String buttonText = EnumChatFormatting.ITALIC
                        + LangHelpers.localize("gui.integratedterminals.craftingplan.view.flat");
                    addRenderableWidget(
                        new GuiButtonText(this.guiLeft + 8, this.guiTop + 198, 80, 20, buttonText, (b) -> {
                            this.guiCraftingPlanToggler
                                .setCraftingPlanDisplayMode(GuiCraftingPlanToggler.CraftingPlanDisplayMode.FLAT);
                            this.initGui();
                        }, true));
                }
            },
            () -> {
                GuiCraftingPlanFlat previousGuiCraftingPlan = this.guiCraftingPlanFlat;
                this.guiCraftingPlanFlat = new GuiCraftingPlanFlat(
                    this,
                    this.getContainer()
                        .getCraftingPlanFlat(),
                    guiLeft,
                    guiTop,
                    9,
                    18,
                    10);
                if (previousGuiCraftingPlan != null) {
                    this.guiCraftingPlanFlat.inheritVisualizationState(previousGuiCraftingPlan);
                }
                addRenderableWidget(this.guiCraftingPlanFlat);
                this.guiCraftingPlan = null;

                if (this.getContainer()
                    .getCraftingPlan() != null) {
                    String buttonText = EnumChatFormatting.ITALIC
                        + LangHelpers.localize("gui.integratedterminals.craftingplan.view.tree");
                    addRenderableWidget(
                        new GuiButtonText(this.guiLeft + 8, this.guiTop + 198, 80, 20, buttonText, (b) -> {
                            this.guiCraftingPlanToggler
                                .setCraftingPlanDisplayMode(GuiCraftingPlanToggler.CraftingPlanDisplayMode.TREE);
                            this.initGui();
                        }, true));
                }
            },
            () -> {
                this.guiCraftingPlan = null;
                this.guiCraftingPlanFlat = null;
            });
    }

    @Override
    public ContainerTerminalCraftingJobsPlan getContainer() {
        return super.getContainer();
    }

    @Override
    protected ResourceLocation constructGuiTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/crafting_plan.png");
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return this.guiCraftingPlanToggler.getCraftingPlanDisplayMode()
            == GuiCraftingPlanToggler.CraftingPlanDisplayMode.FLAT
                ? new ResourceLocation(Reference.MOD_ID, "textures/gui/crafting_plan_flat.png")
                : super.getGuiTexture();
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

        // Reset states
        this.renderables.clear();
        this.getChildren()
            .clear();

        this.guiCraftingPlanToggler.initGui();

        if (this.guiCraftingPlan != null || this.guiCraftingPlanFlat != null) {
            addRenderableWidget(
                new GuiButtonText(
                    guiLeft + 221 + 10 - 50,
                    guiTop + 198,
                    100,
                    20,
                    LangHelpers.localize("gui.integratedterminals.terminal_crafting_job.craftingplan.cancel"),
                    (b) -> cancelCraftingJob(),
                    true));
        }
    }

    @Override
    public boolean keyPressed(int typedChar, int keyCode, int modifiers) {
        if (typedChar == Keyboard.KEY_ESCAPE) {
            returnToOverview();
            return true;
        } else {
            return super.keyPressed(typedChar, keyCode, modifiers);
        }
    }

    private void returnToOverview() {
        PartPos center = getContainer().getTarget()
            .getCenter();
        OpenCraftingJobsGuiPacket.send(
            center.getPos()
                .getBlockPos(),
            center.getSide());
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
        } else if (this.guiCraftingPlanFlat != null) {
            guiCraftingPlanFlat.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
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
        } else if (this.guiCraftingPlanFlat != null) {
            guiCraftingPlanFlat.drawGuiContainerForegroundLayer(mouseX, mouseY);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.guiCraftingPlan != null) {
            guiCraftingPlan.drawScreen(mouseX, mouseY, partialTicks);
        } else if (this.guiCraftingPlanFlat != null) {
            guiCraftingPlanFlat.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (this.guiCraftingPlan != null) {
            return guiCraftingPlan.mouseScrolled(mouseX, mouseY, delta);
        } else if (this.guiCraftingPlanFlat != null) {
            return guiCraftingPlanFlat.mouseScrolled(mouseX, mouseY, delta);
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double mouseXPrev, double mouseYPrev) {
        if (this.guiCraftingPlan != null) {
            return guiCraftingPlan.mouseDragged(mouseX, mouseY, mouseButton, mouseXPrev, mouseYPrev);
        } else if (this.guiCraftingPlanFlat != null) {
            return guiCraftingPlanFlat.mouseDragged(mouseX, mouseY, mouseButton, mouseXPrev, mouseYPrev);
        }
        return super.mouseDragged(mouseX, mouseY, mouseButton, mouseXPrev, mouseYPrev);
    }

    @Override
    public void onUpdate(int valueId, NBTTagCompound value) {

        if (getContainer().getCraftingPlanNotifierId() == valueId
            || getContainer().getCraftingPlanFlatNotifierId() == valueId) {
            if (!craftingPlanInitialized || !craftingPlanFlatInitialized) {
                this.guiCraftingPlanToggler.setCraftingPlanDisplayMode(null);
            }
            this.initGui();
        }

        if (getContainer().getCraftingPlanNotifierId() == valueId) {
            craftingPlanInitialized = true;
        }
        if (getContainer().getCraftingPlanFlatNotifierId() == valueId) {
            craftingPlanFlatInitialized = true;
        }
        super.onUpdate(valueId, value);
    }
}
