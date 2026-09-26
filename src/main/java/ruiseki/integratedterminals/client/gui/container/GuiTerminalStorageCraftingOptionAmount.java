package ruiseki.integratedterminals.client.gui.container;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;

import ruiseki.commoncapabilities.api.ingredient.IPrototypedIngredient;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.PrototypedIngredient;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.Reference;
import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalCraftingOption;
import ruiseki.integratedterminals.capability.ingredient.IngredientComponentTerminalStorageHandlerConfig;
import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.integratedterminals.core.client.gui.GuiTerminalStorage;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageCraftingOptionAmountBase;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientOpenCraftingPlanGuiPacket;
import ruiseki.okcore.client.gui.component.GuiScrollBar;
import ruiseki.okcore.client.gui.component.button.GuiButtonExtended;
import ruiseki.okcore.client.gui.component.button.GuiButtonText;
import ruiseki.okcore.client.gui.component.input.GuiNumberField;
import ruiseki.okcore.client.gui.container.GuiContainerExtended;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.GuiHelpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.RenderHelpers;

/**
 * A gui for setting the amount for a given crafting option.
 *
 * @author rubensworks
 */
public class GuiTerminalStorageCraftingOptionAmount<L, C extends ContainerTerminalStorageCraftingOptionAmountBase<L>>
    extends GuiContainerExtended<C> {

    public static int OUTPUT_SLOT_X = 135;
    public static int OUTPUT_SLOT_Y = 15;

    private final List<IPrototypedIngredient<?, ?>> outputs;

    private GuiNumberField numberField = null;
    private GuiScrollBar scrollBar;
    private int firstRow;
    private GuiButtonText nextButton;

    public GuiTerminalStorageCraftingOptionAmount(C container) {
        super(container);

        this.outputs = Lists.newArrayList();
        ITerminalCraftingOption<?> option = getContainer().getCraftingOptionGuiData()
            .getCraftingOption()
            .getCraftingOption();
        for (IngredientComponent<?, ?> outputComponent : option.getOutputComponents()) {
            for (Object output : option.getOutputs(outputComponent)) {
                this.outputs.add(new PrototypedIngredient(outputComponent, output, null));
            }
        }
    }

    @Override
    public C getContainer() {
        return super.getContainer();
    }

    @Override
    protected ResourceLocation constructGuiTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/crafting_option_amount.png");
    }

    @Override
    public int getBaseXSize() {
        return 178;
    }

    @Override
    public int getBaseYSize() {
        return 162;
    }

    @Override
    public void initGui() {
        super.initGui();

        numberField = new GuiNumberField(
            Minecraft.getMinecraft().fontRenderer,
            guiLeft + 25,
            guiTop + 36,
            53,
            14,
            true,
            LangHelpers.localize("gui.integratedterminals.amount"),
            true);
        numberField.setPositiveOnly(true);
        numberField.setMaxStringLength(5);
        numberField.setMaxValue(10000);
        numberField.setMinValue(1);
        numberField.setVisible(true);
        numberField.setTextColor(16777215);
        numberField.setCanLoseFocus(true);
        numberField.setText(
            Integer.toString(
                numberField.validateNumber(
                    getContainer().getCraftingOptionGuiData()
                        .getAmount())));
        addRenderableWidget(numberField);

        scrollBar = new GuiScrollBar(
            guiLeft + 153,
            guiTop + 15,
            54,
            LangHelpers.localize("gui.okcore.scrollbar"),
            this::setFirstRow,
            3);
        scrollBar.setTotalRows(outputs.size() - 1);
        addWidget(scrollBar);

        addRenderableWidget(new GuiButtonChangeQuantity(guiLeft + 5, guiTop + 10, +10, this::buttonChangeQuantity));
        addRenderableWidget(new GuiButtonChangeQuantity(guiLeft + 5, guiTop + 55, -10, this::buttonChangeQuantity));

        addRenderableWidget(new GuiButtonChangeQuantity(guiLeft + 48, guiTop + 10, +100, this::buttonChangeQuantity));
        addRenderableWidget(new GuiButtonChangeQuantity(guiLeft + 48, guiTop + 55, -100, this::buttonChangeQuantity));

        addRenderableWidget(new GuiButtonChangeQuantity(guiLeft + 91, guiTop + 10, +1000, this::buttonChangeQuantity));
        addRenderableWidget(new GuiButtonChangeQuantity(guiLeft + 91, guiTop + 55, -1000, this::buttonChangeQuantity));

        addRenderableWidget(
            nextButton = new GuiButtonText(
                guiLeft + 81,
                guiTop + 33,
                50,
                20,
                LangHelpers.localize("gui.integratedterminals.terminal_storage.step.next"),
                (bb) -> calculateCraftingJob(),
                true));

        GuiButtonText backButton = new GuiButtonText(
            guiLeft + 5,
            guiTop + 33,
            15,
            20,
            "<",
            (bb) -> returnToTerminalStorage(),
            true);
        addRenderableWidget(backButton);
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        return this.numberField.charTyped(typedChar, keyCode) || super.charTyped(typedChar, keyCode);
    }

    @Override
    public boolean keyPressed(int typedChar, int keyCode, int modifiers) {
        if (typedChar == Keyboard.KEY_ESCAPE) {
            returnToTerminalStorage();
            return true;
        } else if (typedChar == Keyboard.KEY_RETURN || typedChar == Keyboard.KEY_NUMPADENTER) {
            calculateCraftingJob();
            return true;
        }
        return this.numberField.keyPressed(typedChar, keyCode, modifiers)
            || super.keyPressed(typedChar, keyCode, modifiers);
    }

    private void returnToTerminalStorage() {
        CraftingOptionGuiData data = ((ContainerTerminalStorageCraftingOptionAmountBase) getContainer())
            .getCraftingOptionGuiData();
        data.getLocation()
            .openContainerFromClient(data);
    }

    public void buttonChangeQuantity(GuiButtonExtended button) {
        if (button instanceof GuiButtonChangeQuantity) {
            int diff = ((GuiButtonChangeQuantity) button).getDiff();
            setAmount(getAmount() + diff);
        }
    }

    private void calculateCraftingJob() {
        CraftingOptionGuiData craftingOptionData = ((ContainerTerminalStorageCraftingOptionAmountBase) getContainer())
            .getCraftingOptionGuiData()
            .copyWithAmount(getAmount());
        IntegratedTerminals._instance.getPacketHandler()
            .sendToServer(new TerminalStorageIngredientOpenCraftingPlanGuiPacket(craftingOptionData));
    }

    protected <T, M> void drawInstance(IngredientComponent<T, M> ingredientComponent, T instance, int x, int y,
        GuiTerminalStorage.DrawLayer layer, float partialTick, int mouseX, int mouseY) {
        long quantity = ingredientComponent.getMatcher()
            .getQuantity(instance) * getAmount();
        ingredientComponent.getCapability(IngredientComponentTerminalStorageHandlerConfig.CAPABILITY)
            .getOrNull()
            .drawInstance(
                ingredientComponent.getMatcher()
                    .withQuantity(instance, quantity),
                quantity,
                GuiHelpers.quantityToScaledString(quantity),
                this,
                layer,
                partialTick,
                x,
                y,
                mouseX,
                mouseY,
                null);
    }

    private int getAmount() {
        try {
            return this.numberField.getInt();
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private void setAmount(int amount) {
        this.numberField.setText(Integer.toString(this.numberField.validateNumber(amount)));
    }

    protected void drawOutputSlots(int x, int y, float partialTicks, int mouseX, int mouseY,
        GuiTerminalStorage.DrawLayer layer) {
        int offsetY = OUTPUT_SLOT_Y;
        for (IPrototypedIngredient output : this.outputs
            .subList(firstRow, Math.min(this.outputs.size(), firstRow + scrollBar.getVisibleRows()))) {
            drawInstance(
                output.getComponent(),
                output.getPrototype(),
                x + OUTPUT_SLOT_X,
                y + offsetY,
                layer,
                partialTicks,
                mouseX,
                mouseY);
            offsetY += GuiHelpers.SLOT_SIZE;
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        numberField.drawScreen(mouseX - guiLeft, mouseY - guiTop, partialTicks);
        scrollBar.drawWidget(mouseX, mouseY, partialTicks);

        RenderHelpers.bindTexture(this.texture);
        drawOutputSlots(
            guiLeft,
            guiTop,
            partialTicks,
            mouseX - guiLeft,
            mouseY - guiTop,
            GuiTerminalStorage.DrawLayer.BACKGROUND);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        drawOutputSlots(0, 0, 0, mouseX, mouseY, GuiTerminalStorage.DrawLayer.FOREGROUND);
    }

    public void setFirstRow(int firstRow) {
        this.firstRow = firstRow;
    }

    public static class GuiButtonChangeQuantity extends GuiButtonExtended {

        private final int diff;

        public GuiButtonChangeQuantity(int x, int y, int diff, OnPress onPress) {
            super(x, y, 40, 20, (diff < 0 ? "- " : "+ ") + Integer.toString(Math.abs(diff)), onPress, true);
            this.diff = diff;
        }

        @Override
        protected void drawButtonInner(int i, int i1, boolean b) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            int color = 14737632;
            if (!this.active) {
                color = 10526880;
            } else if (this.isHovered()) {
                color = 16777120;
            }
            this.drawCenteredString(
                Minecraft.getMinecraft().fontRenderer,
                this.narrationMessage,
                this.x + this.width / 2,
                this.y + (this.height - 8) / 2,
                color);
        }

        public int getDiff() {
            return diff;
        }
    }

}
