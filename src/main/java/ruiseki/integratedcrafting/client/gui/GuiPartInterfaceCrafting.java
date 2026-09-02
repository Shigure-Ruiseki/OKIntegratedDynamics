package ruiseki.integratedcrafting.client.gui;

import java.util.Collections;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import ruiseki.integratedcrafting.IntegratedCrafting;
import ruiseki.integratedcrafting.Reference;
import ruiseki.integratedcrafting.inventory.container.ContainerPartInterfaceCrafting;
import ruiseki.integratedcrafting.part.PartTypeInterfaceCrafting;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.client.gui.ExtendedGuiHandler;
import ruiseki.integrateddynamics.core.client.gui.container.GuiMultipart;
import ruiseki.okcore.client.gui.component.button.GuiButtonImage;
import ruiseki.okcore.client.gui.image.IImage;
import ruiseki.okcore.client.gui.image.Images;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.GuiHelpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.init.ModBase;

/**
 * Gui for the crafting interface.
 *
 * @author rubensworks
 */
public class GuiPartInterfaceCrafting extends GuiMultipart<PartTypeInterfaceCrafting, PartTypeInterfaceCrafting.State> {

    public static final int BUTTON_SETTINGS = 1;

    /**
     * Make a new instance.
     *
     * @param partTarget    The target.
     * @param player        The player.
     * @param partContainer The part container.
     * @param partType      The targeted part type.
     */
    public GuiPartInterfaceCrafting(EntityPlayer player, PartTarget partTarget, IPartContainer partContainer,
        IPartType partType) {
        super(new ContainerPartInterfaceCrafting(player, partTarget, partContainer, partType));

        putButtonAction(BUTTON_SETTINGS, (buttonId, gui, container) -> {
            IntegratedDynamics._instance.getGuiHandler()
                .setTemporaryData(
                    ExtendedGuiHandler.PART,
                    getTarget().getCenter()
                        .getSide()); // Pass the side as extra data to the gui
        });
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(
            new GuiButtonImage(
                BUTTON_SETTINGS,
                this.guiLeft + 155,
                this.guiTop + 4,
                15,
                15,
                Images.CONFIG_BOARD,
                -2,
                -3,
                true));
    }

    @Override
    protected ResourceLocation constructResourceLocation() {
        return new ResourceLocation(Reference.MOD_ID, getGuiTexture());
    }

    @Override
    public String getGuiTexture() {
        return IntegratedCrafting._instance.getReferenceValue(ModBase.REFKEY_TEXTURE_PATH_GUI) + getNameId() + ".png";
    }

    @Override
    protected String getNameId() {
        return "part_interface_crafting";
    }

    @Override
    protected int getBaseXSize() {
        return 176;
    }

    @Override
    protected int getBaseYSize() {
        return 141;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        PartTypeInterfaceCrafting.State state;
        try {
            state = getPartState();
        } catch (Exception e) {
            return;
        }

        if (state == null) {
            return;
        }

        GlStateManager.color(1, 1, 1, 1);
        int y = guiTop + 42;
        for (int i = 0; i < state.getInventoryVariables()
            .getSizeInventory(); i++) {
            int x = guiLeft + 10 + i * GuiHelpers.SLOT_SIZE;
            if (state.getInventoryVariables()
                .getStackInSlot(i) != null) {
                IImage image = state.isRecipeSlotValid(i) ? Images.OK : Images.ERROR;
                image.draw(this, x, y);
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        PartTypeInterfaceCrafting.State state;
        try {
            state = getPartState();
        } catch (Exception e) {
            return;
        }

        if (state == null) {
            return;
        }

        int y = 42;
        for (int i = 0; i < state.getInventoryVariables()
            .getSizeInventory(); i++) {
            int x = 10 + i * GuiHelpers.SLOT_SIZE;
            int slot = i;
            GuiHelpers.renderTooltipOptional(this, x, y, 14, 13, mouseX, mouseY, () -> {
                if (getContainer().getInventory()
                    .get(slot) != null) {
                    LangHelpers.UnlocalizedString unlocalizedMessage = state.getRecipeSlotUnlocalizedMessage(slot);
                    if (unlocalizedMessage != null) {
                        return Optional.of(Collections.singletonList(unlocalizedMessage.localize()));
                    }
                }
                return Optional.empty();
            });
        }
    }
}
