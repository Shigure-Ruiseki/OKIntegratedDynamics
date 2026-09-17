package ruiseki.integratedterminals.client.gui.container;

import java.util.List;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.ingredient.IPrototypedIngredient;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.Reference;
import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalCraftingPlanFlat;
import ruiseki.integratedterminals.capability.ingredient.IngredientComponentTerminalStorageHandlerConfig;
import ruiseki.integratedterminals.client.gui.container.component.GuiCraftingPlan;
import ruiseki.integratedterminals.core.client.gui.CraftingJobGuiData;
import ruiseki.integratedterminals.core.client.gui.GuiTerminalStorage;
import ruiseki.integratedterminals.core.terminalstorage.crafting.HandlerWrappedTerminalCraftingPlan;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalCraftingJobs;
import ruiseki.integratedterminals.network.packet.CancelCraftingJobPacket;
import ruiseki.integratedterminals.network.packet.OpenCraftingJobsPlanGuiPacket;
import ruiseki.okcore.client.gui.component.GuiScrollBar;
import ruiseki.okcore.client.gui.component.button.GuiButtonText;
import ruiseki.okcore.client.gui.container.GuiContainerExtended;
import ruiseki.okcore.helper.GuiHelpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.RenderHelpers;

/**
 * The crafting jobs overview gui.
 *
 * @author rubensworks
 */
public class GuiTerminalCraftingJobs extends GuiContainerExtended<ContainerTerminalCraftingJobs> {

    public static int OUTPUT_SLOT_X = 8;
    public static int OUTPUT_SLOT_Y = 17;

    public static int LINE_WIDTH = 221;

    private final EntityPlayer player;

    private GuiScrollBar scrollBar;
    private int firstRow;

    public GuiTerminalCraftingJobs(EntityPlayer player, PartTarget target, IPartContainer partContainer,
        IPartType partType) {
        super(new ContainerTerminalCraftingJobs(player, target, partContainer, partType));
        this.player = player;
    }

    @Override
    public ContainerTerminalCraftingJobs getContainer() {
        return super.getContainer();
    }

    @Override
    public void initGui() {
        super.initGui();

        scrollBar = new GuiScrollBar(
            guiLeft + 236,
            guiTop + 18,
            178,
            LangHelpers.localize("gui.okcore.scrollbar"),
            this::setFirstRow,
            10);
        scrollBar.setTotalRows(
            getContainer().getCraftingJobs()
                .size() - 1);

        addRenderableWidget(
            new GuiButtonText(
                guiLeft + 70,
                guiTop + 198,
                120,
                20,
                LangHelpers.localize("gui.integratedterminals.terminal_crafting_job.craftingplan.cancel_all"),
                (b) -> cancelCraftingJobs(),
                true));
    }

    @Override
    protected ResourceLocation constructGuiTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/crafting_plan.png");
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
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        scrollBar.drawWidget(mouseX, mouseY, partialTicks);
        RenderHelpers.bindTexture(this.texture);
        drawCraftingPlans(
            guiLeft,
            guiTop,
            partialTicks,
            mouseX - guiLeft,
            mouseY - guiTop,
            GuiTerminalStorage.DrawLayer.BACKGROUND);

        // Draw plan label
        drawString(
            Minecraft.getMinecraft().fontRenderer,
            LangHelpers.localize("parttype.parttypes.integratedterminals.terminal_crafting_job.name"),
            guiLeft + 8,
            guiTop + 5,
            16777215);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawCraftingPlans(0, 0, 0, mouseX, mouseY, GuiTerminalStorage.DrawLayer.FOREGROUND);
    }

    protected List<HandlerWrappedTerminalCraftingPlan> getVisiblePlans() {
        return this.getContainer()
            .getCraftingJobs()
            .subList(
                firstRow,
                Math.min(
                    this.getContainer()
                        .getCraftingJobs()
                        .size(),
                    firstRow + scrollBar.getVisibleRows()));
    }

    protected void drawCraftingPlans(int x, int y, float partialTicks, int mouseX, int mouseY,
        GuiTerminalStorage.DrawLayer layer) {
        int offsetY = OUTPUT_SLOT_Y;
        for (HandlerWrappedTerminalCraftingPlan craftingPlan : getVisiblePlans()) {
            drawCraftingPlan(craftingPlan, x + OUTPUT_SLOT_X, y + offsetY, layer, partialTicks, mouseX, mouseY);
            offsetY += GuiHelpers.SLOT_SIZE;
        }
    }

    protected void drawCraftingPlan(HandlerWrappedTerminalCraftingPlan craftingPlan, int x, int y,
        GuiTerminalStorage.DrawLayer layer, float partialTick, int mouseX, int mouseY) {
        int xOriginal = x;
        ITerminalCraftingPlanFlat<?> plan = craftingPlan.getCraftingPlanFlat();

        // Draw background color if hovering
        if (layer == GuiTerminalStorage.DrawLayer.BACKGROUND && RenderHelpers
            .isPointInRegion(x - guiLeft, y - guiTop, LINE_WIDTH, GuiHelpers.SLOT_SIZE, mouseX, mouseY)) {
            drawRect(x + 1, y + 1, x + LINE_WIDTH + 1, y + GuiHelpers.SLOT_SIZE, -2130706433);
        }

        // Draw outputs
        x += 4;
        for (IPrototypedIngredient<?, ?> output : plan.getOutputs()) {
            IngredientComponent<?, ?> ingredientComponent = output.getComponent();
            long quantity = ((IngredientComponent) ingredientComponent).getMatcher()
                .getQuantity(output.getPrototype());
            ingredientComponent.getCapability(IngredientComponentTerminalStorageHandlerConfig.CAPABILITY)
                .getOrNull()
                .drawInstance(
                    output.getPrototype(),
                    quantity,
                    GuiHelpers.quantityToScaledString(quantity),
                    this,
                    layer,
                    partialTick,
                    x,
                    y + 1,
                    mouseX,
                    mouseY,
                    null);
            x += GuiHelpers.SLOT_SIZE_INNER;
        }

        // Draw dependency count
        if (layer == GuiTerminalStorage.DrawLayer.BACKGROUND) {
            String statusString = LangHelpers.localize(
                "gui.integratedterminals.craftingplan.status",
                LangHelpers.localize(
                    "gui.integratedterminals.craftingplan.status." + plan.getStatus()
                        .name()
                        .toLowerCase(Locale.ENGLISH) + ".name"));
            RenderHelpers.drawScaledString(
                fontRendererObj,
                statusString,
                xOriginal + LINE_WIDTH - 80,
                y + 1,
                0.5f,
                16777215,
                true);

            int dependencies = plan.getEntries()
                .size();
            String dependenciesString = LangHelpers
                .localize("gui.integratedterminals.terminal_crafting_job.craftingplan.dependencies", dependencies);
            RenderHelpers.drawScaledString(
                fontRendererObj,
                dependenciesString,
                xOriginal + LINE_WIDTH - 80,
                y + 7,
                0.5f,
                16777215,
                true);

            if (plan.getChannel() != -1) {
                String channelString = LangHelpers.localize(
                    "gui.integratedterminals.terminal_crafting_job.craftingplan.crafting_channel",
                    plan.getChannel());
                RenderHelpers.drawScaledString(
                    fontRendererObj,
                    channelString,
                    xOriginal + LINE_WIDTH - 40,
                    y + 7,
                    0.5f,
                    16777215,
                    true);
            }

            long tickDuration = plan.getTickDuration();
            if (tickDuration >= 0) {
                String durationString = GuiCraftingPlan.getDurationString(tickDuration);
                RenderHelpers.drawScaledString(
                    fontRendererObj,
                    durationString,
                    xOriginal + LINE_WIDTH - 80,
                    y + 13,
                    0.5f,
                    16777215,
                    true);
            }
        }
    }

    private void cancelCraftingJobs() {
        // Send packets to cancel crafting jobs
        for (HandlerWrappedTerminalCraftingPlan craftingJob : getContainer().getCraftingJobs()) {
            PartPos center = getContainer().getTarget()
                .getCenter();
            CraftingJobGuiData data = new CraftingJobGuiData(
                center.getPos()
                    .getBlockPos(),
                center.getSide(),
                getContainer().getChannel(),
                craftingJob.getHandler(),
                craftingJob.getCraftingPlanFlat()
                    .getId());
            IntegratedTerminals._instance.getPacketHandler()
                .sendToServer(new CancelCraftingJobPacket(data));
        }

        // Close the gui
        this.player.closeScreen();
    }

    @Nullable
    protected HandlerWrappedTerminalCraftingPlan getHoveredPlan(double mouseX, double mouseY) {
        mouseX -= guiLeft;
        mouseY -= guiTop;
        if (mouseX > OUTPUT_SLOT_X && mouseX < OUTPUT_SLOT_X + LINE_WIDTH
            && mouseY > OUTPUT_SLOT_Y
            && mouseY < OUTPUT_SLOT_Y + GuiHelpers.SLOT_SIZE * scrollBar.getVisibleRows()) {
            int index = (((int) mouseY) - OUTPUT_SLOT_Y) / GuiHelpers.SLOT_SIZE;
            List<HandlerWrappedTerminalCraftingPlan> plans = getVisiblePlans();
            if (index >= 0 && index < plans.size()) {
                return plans.get(index);
            }
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        HandlerWrappedTerminalCraftingPlan plan = getHoveredPlan(mouseX, mouseY);
        if (plan != null) {
            PartPos pos = getContainer().getTarget()
                .getCenter();
            OpenCraftingJobsPlanGuiPacket.send(
                pos.getPos()
                    .getBlockPos(),
                pos.getSide(),
                getContainer().getChannel(),
                plan);
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double mouseXPrev, double mouseYPrev) {
        return this.getFocused() != null && this.isDragging()
            && mouseButton == 0
            && this.getFocused()
                .mouseDragged(mouseX, mouseY, mouseButton, mouseXPrev, mouseYPrev) ? true
                    : super.mouseDragged(mouseX, mouseY, mouseButton, mouseXPrev, mouseYPrev);
    }

    public void setFirstRow(int firstRow) {
        this.firstRow = firstRow;
    }

    @Override
    public void onUpdate(int valueId, NBTTagCompound value) {
        super.onUpdate(valueId, value);

        if (valueId == this.getContainer()
            .getValueIdCraftingJobs()) {
            this.initGui();
        }
    }
}
