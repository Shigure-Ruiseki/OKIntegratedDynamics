package ruiseki.integrateddynamics.client.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import io.netty.buffer.Unpooled;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import ruiseki.integrateddynamics.core.persist.world.LabelsWorldStorage;
import ruiseki.integrateddynamics.inventory.container.ContainerLabeller;
import ruiseki.integrateddynamics.network.packet.ItemStackRenamePacket;
import ruiseki.okcore.client.gui.component.button.GuiButtonText;
import ruiseki.okcore.client.gui.component.input.GuiTextFieldExtended;
import ruiseki.okcore.client.gui.container.GuiContainerConfigurable;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Gui for the labeller.
 *
 * @author rubensworks
 */
public class GuiLabeller extends GuiContainerConfigurable<ContainerLabeller> {

    public static final int BUTTON_WRITE = 1;

    private GuiTextFieldExtended searchField;

    /**
     * Make a new instance.
     *
     * @param player    The player.
     * @param itemIndex The index of the item in use inside the player inventory.
     */
    public GuiLabeller(EntityPlayer player, int itemIndex) {
        super(new ContainerLabeller(player, itemIndex));
        ContainerLabeller container = getContainer();
        container.setGui(this);
    }

    @Override
    protected ResourceLocation constructGuiTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/labeller.png");
    }

    @Override
    public void initGui() {
        super.initGui();
        addRenderableWidget(
            new GuiButtonText(
                this.guiLeft + 133,
                this.guiTop + 8,
                LangHelpers.localize("item.items.integrateddynamics.labeller.button.write"),
                button -> {
                    ItemStack itemStack = getContainer().getItemStack();
                    IVariableFacadeHandlerRegistry registry = IntegratedDynamics._instance.getRegistryManager()
                        .getRegistry(IVariableFacadeHandlerRegistry.class);
                    IVariableFacade variableFacade = registry.handle(itemStack);
                    if (variableFacade.isValid()) {
                        int variableId = variableFacade.getId();
                        String label = StringUtils.isBlank(searchField.getText()) ? "" : searchField.getText();
                        LabelsWorldStorage.getInstance(IntegratedDynamics._instance)
                            .put(variableId, label);
                    } else if (!ItemHelpers.isEmpty(itemStack)) {
                        String name = searchField.getText();
                        IntegratedDynamics._instance.getPacketHandler()
                            .sendToServer(new ItemStackRenamePacket(name));
                        getContainer().setItemStackName(name);
                    }
                }));

        Keyboard.enableRepeatEvents(true);
        int searchWidth = 87;
        int searchX = 36;
        int searchY = 11;
        this.searchField = new GuiTextFieldExtended(
            this.fontRendererObj,
            this.guiLeft + searchX,
            this.guiTop + searchY,
            searchWidth,
            this.fontRendererObj.FONT_HEIGHT,
            LangHelpers.localize("gui.okcore.search"),
            true);
        this.searchField.setMaxStringLength(64);
        this.searchField.setEnableBackgroundDrawing(false);
        this.searchField.setVisible(true);
        this.searchField.setFocused(true);
        this.searchField.setTextColor(16777215);
        this.searchField.setCanLoseFocus(false);
        this.searchField.setText("");
        this.searchField.width = searchWidth;
        this.searchField.xPosition = this.guiLeft + (searchX + searchWidth) - this.searchField.width;
    }

    @Override
    protected int getBaseYSize() {
        return 113;
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        if (!this.searchField.charTyped(typedChar, keyCode)) {
            return super.charTyped(typedChar, keyCode);
        }
        return true;
    }

    @Override
    public boolean keyPressed(int typedChar, int keyCode, int modifiers) {
        if (typedChar != Keyboard.KEY_ESCAPE) {
            this.searchField.keyPressed(typedChar, keyCode, modifiers);
            return true;
        }
        return super.keyPressed(typedChar, keyCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return this.searchField.mouseClicked(mouseX, mouseY, mouseButton)
            || super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        this.searchField.drawTextBox();
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.id == BUTTON_WRITE) {
            ItemStack itemStack = getContainer().getItemStack();
            IVariableFacadeHandlerRegistry registry = IntegratedDynamics._instance.getRegistryManager()
                .getRegistry(IVariableFacadeHandlerRegistry.class);
            IVariableFacade variableFacade = registry.handle(itemStack);
            if (variableFacade.isValid()) {
                int variableId = variableFacade.getId();
                String label = StringUtils.isBlank(searchField.getText()) ? "" : searchField.getText();
                LabelsWorldStorage.getInstance(IntegratedDynamics._instance)
                    .put(variableId, label);
            } else if (itemStack != null) {
                try {
                    PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
                    buffer.writeStringToBuffer(searchField.getText());

                    this.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("MC|ItemName", buffer));
                } catch (IOException ignore) {}
                String name = searchField.getText();
                IntegratedDynamics._instance.getPacketHandler()
                    .sendToServer(new ItemStackRenamePacket(name));
                getContainer().setItemStackName(name);
            }
        }
    }

    public void setText(String text) {
        this.searchField.setText(text);
    }

}
