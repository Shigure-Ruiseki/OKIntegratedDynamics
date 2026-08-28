package ruiseki.integratedterminals.core.client.gui;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;

import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.Reference;
import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalCraftingPlan;
import ruiseki.integratedterminals.client.gui.container.component.GuiCraftingPlan;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageCraftingPlanBase;
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

    @Nullable
    private GuiCraftingPlan guiCraftingPlan;

    private ITerminalCraftingPlan craftingPlan;
    private GuiButtonText buttonConfirm;

    public GuiTerminalStorageCraftingPlanBase(C container) {
        super(container);
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

        if (this.craftingPlan != null) {
            this.guiCraftingPlan = new GuiCraftingPlan(this, this.craftingPlan, guiLeft, guiTop, 9, 18, 10);
        } else {
            this.guiCraftingPlan = null;
        }

        this.buttonList.clear();
        this.buttonList.addAll(
            Lists.newArrayList(
                buttonConfirm = new GuiButtonText(
                    ContainerTerminalStorageCraftingPlanBase.BUTTON_START,
                    guiLeft + 95,
                    guiTop + 198,
                    50,
                    20,
                    EnumChatFormatting.YELLOW
                        + LangHelpers.localize("gui.integratedterminals.terminal_storage.step.craft"),
                    true)));
        buttonConfirm.enabled = this.guiCraftingPlan != null && this.guiCraftingPlan.isValid();
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

    private void returnToTerminalStorage() {
        CraftingOptionGuiData data = ((ContainerTerminalStorageCraftingPlanBase) getContainer())
            .getCraftingOptionGuiData();
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
    protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long time) {
        super.mouseClickMove(mouseX, mouseY, mouseButton, time);
        if (this.guiCraftingPlan != null) {
            guiCraftingPlan.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void onUpdate(int valueId, NBTTagCompound value) {
        super.onUpdate(valueId, value);

        if (((ContainerTerminalStorageCraftingPlanBase) getContainer()).getCraftingPlanNotifierId() == valueId) {
            this.craftingPlan = ((ContainerTerminalStorageCraftingPlanBase) getContainer()).getCraftingOptionGuiData()
                .getCraftingOption()
                .getHandler()
                .deserializeCraftingPlan(value);
            this.initGui();
        }
    }
}
