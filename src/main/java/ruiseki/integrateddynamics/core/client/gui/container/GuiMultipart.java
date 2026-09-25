package ruiseki.integrateddynamics.core.client.gui.container;

import java.awt.Rectangle;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

import com.google.common.collect.Lists;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.core.inventory.container.ContainerMultipart;
import ruiseki.integrateddynamics.core.part.PartTypeConfigurable;
import ruiseki.okcore.client.gui.component.button.GuiButtonImage;
import ruiseki.okcore.client.gui.container.GuiContainerExtended;
import ruiseki.okcore.client.gui.image.IImage;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Gui for parts.
 *
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = false)
@Data
public abstract class GuiMultipart<P extends IPartType<P, S>, S extends IPartState<P>, C extends ContainerMultipart<P, S>>
    extends GuiContainerExtended<C> {

    private static final Rectangle ITEM_POSITION = new Rectangle(8, 17, 18, 18);

    protected final DisplayErrorsComponent displayErrors = new DisplayErrorsComponent();

    /**
     * Make a new instance.
     *
     * @param container The container to make the GUI for.
     */
    public GuiMultipart(C container) {
        super(container);
    }

    @Override
    public void initGui() {
        clearWidgets();
        super.initGui();
        P partType = getContainer().getPartType();
        if (partType instanceof PartTypeConfigurable && partType.getContainerProviderSettings(null)
            .isPresent()) {
            addRenderableWidget(
                new GuiButtonImage(
                    this.guiLeft - 20,
                    this.guiTop + 0,
                    18,
                    18,
                    LangHelpers.localize("gui.integrateddynamics.part_settings"),
                    createServerPressable(ContainerMultipart.BUTTON_SETTINGS, (button) -> {}),
                    new IImage[] { ruiseki.integrateddynamics.client.gui.image.Images.BUTTON_BACKGROUND_INACTIVE,
                        ruiseki.integrateddynamics.client.gui.image.Images.BUTTON_MIDDLE_SETTINGS },
                    false,
                    0,
                    0));
            if (getContainer().getPartType()
                .supportsOffsets()) {
                addRenderableWidget(
                    new GuiButtonImage(
                        this.guiLeft - 20,
                        this.guiTop + 20,
                        18,
                        18,
                        LangHelpers.localize("gui.integrateddynamics.part_offsets"),
                        createServerPressable(ContainerMultipart.BUTTON_OFFSETS, (button) -> {}),
                        new IImage[] { ruiseki.integrateddynamics.client.gui.image.Images.BUTTON_BACKGROUND_INACTIVE,
                            ruiseki.integrateddynamics.client.gui.image.Images.BUTTON_MIDDLE_OFFSET },
                        false,
                        0,
                        0));
            }
        }
    }

    protected abstract String getNameId();

    @Override
    protected ResourceLocation constructGuiTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/" + getNameId() + ".png");
    }

    protected float colorSmoothener(float color) {
        return 1F - ((1F - color) / 4F);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        FontRenderer fontRenderer = fontRendererObj;

        // Draw part name
        fontRenderer.drawString(
            LangHelpers.localize(
                getContainer().getPartType()
                    .getUnlocalizedName()),
            guiLeft + 8,
            guiTop + 6,
            Helpers.RGBToInt(0, 0, 0));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        if (getContainer().getPartType() instanceof PartTypeConfigurable<?, ?>configurable) {
            drawTooltip(
                Lists.newArrayList(LangHelpers.localize("gui.integrateddynamics.part_settings")),
                mouseX - guiLeft,
                mouseY - guiTop);
            if (configurable.supportsOffsets() && isPointInRegion(-20, 20, 18, 18, mouseX, mouseY)) {
                drawTooltip(
                    Lists.newArrayList(LangHelpers.localize("gui.integrateddynamics.part_offsets")),
                    mouseX - guiLeft,
                    mouseY - guiTop);
            }
        }
    }
}
