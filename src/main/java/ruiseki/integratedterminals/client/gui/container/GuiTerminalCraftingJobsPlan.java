package ruiseki.integratedterminals.client.gui.container;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;

import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.Reference;
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

    private static final int BUTTON_CRAFTING_PLAN = 5;
    private static final int BUTTON_CRAFTING_PLAN_FLAT = 6;

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
                this.guiCraftingPlanFlat = null;

                if (this.getContainer()
                    .getCraftingPlanFlat() != null) {
                    String buttonText = EnumChatFormatting.ITALIC
                        + LangHelpers.localize("gui.integratedterminals.craftingplan.view.flat");
                    this.buttonList.add(
                        new GuiButton(BUTTON_CRAFTING_PLAN, this.guiLeft + 8, this.guiTop + 198, 80, 20, buttonText));
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
                this.guiCraftingPlan = null;

                if (this.getContainer()
                    .getCraftingPlan() != null) {
                    String buttonText = EnumChatFormatting.ITALIC
                        + LangHelpers.localize("gui.integratedterminals.craftingplan.view.tree");
                    this.buttonList.add(
                        new GuiButton(
                            BUTTON_CRAFTING_PLAN_FLAT,
                            this.guiLeft + 8,
                            this.guiTop + 198,
                            80,
                            20,
                            buttonText));
                }
            },
            () -> {
                this.guiCraftingPlan = null;
                this.guiCraftingPlanFlat = null;
            });
    }

    @Override
    protected ResourceLocation constructResourceLocation() {
        return new ResourceLocation(Reference.MOD_ID, this.getGuiTexture());
    }

    @Override
    public String getGuiTexture() {
        return this.guiCraftingPlanToggler.getCraftingPlanDisplayMode()
            == GuiCraftingPlanToggler.CraftingPlanDisplayMode.FLAT
                ? IntegratedTerminals._instance.getReferenceValue(ModBase.REFKEY_TEXTURE_PATH_GUI)
                    + "crafting_plan_flat.png"
                : IntegratedTerminals._instance.getReferenceValue(ModBase.REFKEY_TEXTURE_PATH_GUI)
                    + "crafting_plan.png";
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

        this.guiCraftingPlanToggler.initGui();

        if (this.guiCraftingPlan != null || this.guiCraftingPlanFlat != null) {
            this.buttonList.add(
                new GuiButtonText(
                    0,
                    guiLeft + 221 + 10 - 50,
                    guiTop + 198,
                    100,
                    20,
                    LangHelpers.localize("gui.integratedterminals.terminal_crafting_job.craftingplan.cancel"),
                    true));
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
    public void handleMouseInput() {
        super.handleMouseInput();
        if (this.guiCraftingPlan != null) {
            guiCraftingPlan.handleMouseInput();
        } else if (this.guiCraftingPlanFlat != null) {
            guiCraftingPlanFlat.handleMouseInput();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.guiCraftingPlan != null) {
            guiCraftingPlan.mouseClicked(mouseX, mouseY, mouseButton);
        } else if (this.guiCraftingPlanFlat != null) {
            guiCraftingPlanFlat.mouseClicked(mouseX, mouseY, mouseButton);
        }
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

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        if (button.id == BUTTON_CRAFTING_PLAN) {
            guiCraftingPlanToggler.setCraftingPlanDisplayMode(GuiCraftingPlanToggler.CraftingPlanDisplayMode.FLAT);
            initGui();
        }

        if (button.id == BUTTON_CRAFTING_PLAN_FLAT) {
            guiCraftingPlanToggler.setCraftingPlanDisplayMode(GuiCraftingPlanToggler.CraftingPlanDisplayMode.TREE);
            initGui();
        }
    }
}
