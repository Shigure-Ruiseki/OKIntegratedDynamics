package ruiseki.integratedterminals.client.gui.container;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;

import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.Reference;
import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalCraftingPlan;
import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalCraftingPlanFlat;
import ruiseki.integratedterminals.client.gui.container.component.GuiCraftingPlan;
import ruiseki.integratedterminals.client.gui.container.component.GuiCraftingPlanFlat;
import ruiseki.integratedterminals.client.gui.container.component.GuiCraftingPlanToggler;
import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageCraftingPlanBase;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientOpenCraftingJobAmountGuiPacket;
import ruiseki.okcore.client.gui.component.button.GuiButtonText;
import ruiseki.okcore.client.gui.container.GuiContainerExtended;
import ruiseki.okcore.helper.LangHelpers;

/**
 * A gui for previewing a crafting plan.
 *
 * @author rubensworks
 */
public class GuiTerminalStorageCraftingPlan<L, C extends ContainerTerminalStorageCraftingPlanBase<L>>
    extends GuiContainerExtended<C> {

    private GuiCraftingPlanToggler guiCraftingPlanToggler;

    @Nullable
    private GuiCraftingPlan guiCraftingPlan;
    @Nullable
    private GuiCraftingPlanFlat guiCraftingPlanFlat;

    private ITerminalCraftingPlan craftingPlan;
    private ITerminalCraftingPlanFlat craftingPlanFlat;
    private GuiButtonText buttonConfirm;

    public GuiTerminalStorageCraftingPlan(C container) {
        super(container);

        this.guiCraftingPlanToggler = new GuiCraftingPlanToggler(
            () -> this.craftingPlan,
            () -> this.craftingPlanFlat,
            () -> {
                this.guiCraftingPlan = new GuiCraftingPlan(this, this.craftingPlan, guiLeft, guiTop, 9, 18, 10);
                addRenderableWidget(this.guiCraftingPlan);

                if (this.craftingPlanFlat != null) {
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
                this.guiCraftingPlanFlat = new GuiCraftingPlanFlat(
                    this,
                    this.craftingPlanFlat,
                    guiLeft,
                    guiTop,
                    9,
                    18,
                    10);
                addRenderableWidget(this.guiCraftingPlanFlat);

                if (this.craftingPlan != null) {
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
    public C getContainer() {
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
        this.guiCraftingPlan = null;
        this.guiCraftingPlanFlat = null;

        this.guiCraftingPlanToggler.initGui();

        addRenderableWidget(
            new GuiButtonText(
                guiLeft + 221 + 10 - 50 - 55,
                guiTop + 198,
                50,
                20,
                LangHelpers.localize("gui.integratedterminals.terminal_storage.step.back"),
                (b) -> returnToCraftingOptionAmount(),
                true));

        addRenderableWidget(
            buttonConfirm = new GuiButtonText(
                guiLeft + 221 + 10 - 50,
                guiTop + 198,
                50,
                20,
                EnumChatFormatting.YELLOW + LangHelpers.localize("gui.integratedterminals.terminal_storage.step.craft"),
                createServerPressable(ContainerTerminalStorageCraftingPlanBase.BUTTON_START, (b) -> {}),
                true));
        buttonConfirm.enabled = (this.guiCraftingPlan != null && this.guiCraftingPlan.isValid())
            || (this.guiCraftingPlanFlat != null && this.guiCraftingPlanFlat.isValid());
    }

    @Override
    public boolean keyPressed(int typedChar, int keyCode, int modifiers) {
        if (typedChar == Keyboard.KEY_ESCAPE) {
            returnToCraftingOptionAmount();
            return true;
        }
        if (this.guiCraftingPlan != null && this.guiCraftingPlan.isValid()
            && (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER)) {
            buttonConfirm.onPress();
            return true;
        }
        return super.keyPressed(typedChar, keyCode, modifiers);
    }

    /**
     * Go back to the gui in which the crafting amount can be set.
     * If no crafting option is known, the terminal itself is opened again.
     */
    private void returnToCraftingOptionAmount() {
        CraftingOptionGuiData data = getContainer().getCraftingOptionGuiData();
        if (data.getCraftingOption() == null) {
            returnToTerminalStorage();
        } else {
            IntegratedTerminals._instance.getPacketHandler()
                .sendToServer(new TerminalStorageIngredientOpenCraftingJobAmountGuiPacket(data));
        }
    }

    private void returnToTerminalStorage() {
        CraftingOptionGuiData data = getContainer().getCraftingOptionGuiData();
        data.getLocation()
            .openContainerFromClient(data);
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
                LangHelpers.localize("gui.integratedterminals.terminal_storage.step.crafting_plan_calculating"),
                guiLeft + getBaseXSize() / 2,
                guiTop + 23,
                16777215);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // super.drawGuiContainerForegroundLayer(mouseX, mouseY);
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

        if (getContainer().getCraftingPlanNotifierId() == valueId) {
            this.craftingPlan = getContainer().getCraftingOptionGuiData()
                .getCraftingOption()
                .getHandler()
                .deserializeCraftingPlan(value);
            this.guiCraftingPlanToggler.setCraftingPlanDisplayMode(null);
            this.initGui();
        }
        if (getContainer().getCraftingPlanFlatNotifierId() == valueId) {
            this.craftingPlanFlat = getContainer().getCraftingOptionGuiData()
                .getCraftingOption()
                .getHandler()
                .deserializeCraftingPlanFlat(value);
            this.guiCraftingPlanToggler.setCraftingPlanDisplayMode(null);
            this.initGui();
        }

        super.onUpdate(valueId, value);
    }
}
