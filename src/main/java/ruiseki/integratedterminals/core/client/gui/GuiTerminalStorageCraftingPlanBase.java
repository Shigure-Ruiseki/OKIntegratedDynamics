package ruiseki.integratedterminals.core.client.gui;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;

import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.Reference;
import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalCraftingPlan;
import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalCraftingPlanFlat;
import ruiseki.integratedterminals.client.gui.container.GuiCraftingPlanFlat;
import ruiseki.integratedterminals.client.gui.container.GuiCraftingPlanToggler;
import ruiseki.integratedterminals.client.gui.container.component.GuiCraftingPlan;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageCraftingPlanBase;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientOpenCraftingJobAmountGuiPacket;
import ruiseki.okcore.client.gui.component.button.GuiButtonText;
import ruiseki.okcore.client.gui.container.GuiContainerExtended;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.init.ModBase;

/**
 * A gui for previewing a crafting plan.
 *
 * @author rubensworks
 */
public class GuiTerminalStorageCraftingPlanBase<L, C extends ContainerTerminalStorageCraftingPlanBase<L>>
    extends GuiContainerExtended {

    private static final int BUTTON_CRAFTING_PLAN = 5;
    private static final int BUTTON_CRAFTING_PLAN_FLAT = 6;

    private GuiCraftingPlanToggler guiCraftingPlanToggler;

    @Nullable
    private GuiCraftingPlan guiCraftingPlan;
    @Nullable
    private GuiCraftingPlanFlat guiCraftingPlanFlat;

    private ITerminalCraftingPlan craftingPlan;
    private ITerminalCraftingPlanFlat craftingPlanFlat;
    private GuiButtonText buttonConfirm;

    public GuiTerminalStorageCraftingPlanBase(C container) {
        super(container);

        this.guiCraftingPlanToggler = new GuiCraftingPlanToggler(
            () -> this.craftingPlan,
            () -> this.craftingPlanFlat,
            () -> {
                this.guiCraftingPlan = new GuiCraftingPlan(this, this.craftingPlan, guiLeft, guiTop, 9, 18, 10);

                if (this.craftingPlanFlat != null) {
                    String buttonText = EnumChatFormatting.ITALIC
                        + LangHelpers.localize("gui.integratedterminals.craftingplan.view.flat");
                    this.buttonList.add(
                        new GuiButtonText(
                            BUTTON_CRAFTING_PLAN,
                            this.guiLeft + 8,
                            this.guiTop + 198,
                            80,
                            20,
                            buttonText,
                            true));
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

                if (this.craftingPlan != null) {
                    String buttonText = EnumChatFormatting.ITALIC
                        + LangHelpers.localize("gui.integratedterminals.craftingplan.view.tree");
                    this.buttonList.add(
                        new GuiButtonText(
                            BUTTON_CRAFTING_PLAN_FLAT,
                            this.guiLeft + 8,
                            this.guiTop + 198,
                            80,
                            20,
                            buttonText,
                            true));
                }
            },
            () -> {
                this.guiCraftingPlan = null;
                this.guiCraftingPlanFlat = null;
            });
    }

    @Override
    protected ContainerTerminalStorageCraftingPlanBase getContainer() {
        return (ContainerTerminalStorageCraftingPlanBase) super.getContainer();
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

        // Reset states
        this.buttonList.clear();
        this.guiCraftingPlan = null;
        this.guiCraftingPlanFlat = null;

        this.guiCraftingPlanToggler.initGui();

        this.buttonList.add(
            new GuiButtonText(
                ContainerTerminalStorageCraftingPlanBase.BUTTON_BACK,
                guiLeft + 221 + 10 - 50 - 55,
                guiTop + 198,
                50,
                20,
                LangHelpers.localize("gui.integratedterminals.terminal_storage.step.back"),
                true));

        this.buttonList.add(
            buttonConfirm = new GuiButtonText(
                ContainerTerminalStorageCraftingPlanBase.BUTTON_START,
                guiLeft + 221 + 10 - 50,
                guiTop + 198,
                50,
                20,
                EnumChatFormatting.YELLOW + LangHelpers.localize("gui.integratedterminals.terminal_storage.step.craft"),
                true));
        buttonConfirm.enabled = (this.guiCraftingPlan != null && this.guiCraftingPlan.isValid())
            || (this.guiCraftingPlanFlat != null && this.guiCraftingPlanFlat.isValid());
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (!this.checkHotbarKeys(keyCode)) {
            if (keyCode == Keyboard.KEY_ESCAPE) {
                returnToTerminalStorage();
            } else if (this.guiCraftingPlan != null && this.guiCraftingPlan.isValid()
                && (keyCode == Keyboard.KEY_NUMPADENTER || keyCode == Keyboard.KEY_RETURN)) {
                    actionPerformed(this.buttonConfirm);
                } else {
                    super.keyTyped(typedChar, keyCode);
                }
        }
    }

    @Override
    public void onButtonClick(int buttonId) {
        super.onButtonClick(buttonId);
        if (buttonId == ContainerTerminalStorageCraftingPlanBase.BUTTON_BACK) {
            returnToCraftingOptionAmount();
        }
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
    public boolean requiresAction(int buttonId) {
        return true;
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
    protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long time) {
        super.mouseClickMove(mouseX, mouseY, mouseButton, time);
        if (this.guiCraftingPlan != null) {
            guiCraftingPlan.mouseClicked(mouseX, mouseY, mouseButton);
        } else if (this.guiCraftingPlanFlat != null) {
            guiCraftingPlanFlat.mouseClicked(mouseX, mouseY, mouseButton);
        }
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
