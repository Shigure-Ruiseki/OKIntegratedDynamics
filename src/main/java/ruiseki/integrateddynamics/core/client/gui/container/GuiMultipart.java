package ruiseki.integrateddynamics.core.client.gui.container;

import java.awt.Rectangle;

import net.minecraft.client.gui.FontRenderer;

import com.google.common.collect.Lists;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.client.gui.ExtendedGuiHandler;
import ruiseki.integrateddynamics.core.inventory.container.ContainerMultipart;
import ruiseki.integrateddynamics.core.part.PartTypeConfigurable;
import ruiseki.okcore.client.gui.component.button.GuiButtonImage;
import ruiseki.okcore.client.gui.container.GuiContainerExtended;
import ruiseki.okcore.client.gui.image.IImage;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.inventory.IGuiContainerProvider;
import ruiseki.okcore.inventory.container.ExtendedInventoryContainer;
import ruiseki.okcore.inventory.container.button.IButtonActionClient;

/**
 * Gui for parts.
 *
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = false)
@Data
public abstract class GuiMultipart<P extends IPartType<P, S> & IGuiContainerProvider, S extends IPartState<P>>
    extends GuiContainerExtended {

    public static final int BUTTON_SETTINGS = 1;
    public static final int BUTTON_OFFSETS = 2;
    private static final Rectangle ITEM_POSITION = new Rectangle(8, 17, 18, 18);

    protected final DisplayErrorsComponent displayErrors = new DisplayErrorsComponent();
    private final PartTarget target;
    private final IPartContainer partContainer;
    private final P partType;

    /**
     * Make a new instance.
     *
     * @param container The container to make the GUI for.
     */
    public GuiMultipart(ContainerMultipart<P, S> container) {
        super(container);
        this.target = container.getTarget();
        this.partContainer = container.getPartContainer();
        this.partType = container.getPartType();

        putButtonAction(BUTTON_SETTINGS, new IButtonActionClient<GuiContainerExtended, ExtendedInventoryContainer>() {

            @Override
            public void onAction(int buttonId, GuiContainerExtended gui, ExtendedInventoryContainer container) {
                IntegratedDynamics._instance.getGuiHandler()
                    .setTemporaryData(
                        ExtendedGuiHandler.PART,
                        getTarget().getCenter()
                            .getSide()); // Pass the side as extra data to the gui
            }
        });
        putButtonAction(BUTTON_OFFSETS, new IButtonActionClient<GuiContainerExtended, ExtendedInventoryContainer>() {

            @Override
            public void onAction(int buttonId, GuiContainerExtended gui, ExtendedInventoryContainer container) {
                IntegratedDynamics._instance.getGuiHandler()
                    .setTemporaryData(
                        ExtendedGuiHandler.PART,
                        getTarget().getCenter()
                            .getSide()); // Pass the side as extra data to the gui
            }
        });
    }

    @Override
    public void initGui() {
        buttonList.clear();
        super.initGui();
        if (getPartType() instanceof PartTypeConfigurable<?, ?>configurable) {
            if (configurable.hasSettings()) {
                buttonList.add(
                    new GuiButtonImage(
                        ContainerMultipart.BUTTON_SETTINGS,
                        this.guiLeft - 20,
                        this.guiTop + 0,
                        18,
                        18,
                        new IImage[] { ruiseki.integrateddynamics.client.gui.image.Images.BUTTON_BACKGROUND_INACTIVE,
                            ruiseki.integrateddynamics.client.gui.image.Images.BUTTON_MIDDLE_SETTINGS },
                        0,
                        0,
                        false));
            }
            if (configurable.supportsOffsets()) {
                buttonList.add(
                    new GuiButtonImage(
                        ContainerMultipart.BUTTON_OFFSETS,
                        this.guiLeft - 20,
                        this.guiTop + 20,
                        18,
                        18,
                        new IImage[] { ruiseki.integrateddynamics.client.gui.image.Images.BUTTON_BACKGROUND_INACTIVE,
                            ruiseki.integrateddynamics.client.gui.image.Images.BUTTON_MIDDLE_OFFSET },
                        0,
                        0,
                        false));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public S getPartState() {
        return ((ContainerMultipart<P, S>) container).getPartState();
    }

    protected abstract String getNameId();

    @Override
    public String getGuiTexture() {
        return getContainer().getGuiProvider()
            .getModGui()
            .getReferenceValue(ModBase.REFKEY_TEXTURE_PATH_GUI) + getNameId() + ".png";
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
            LangHelpers.localize(getPartType().getUnlocalizedName()),
            guiLeft + 8,
            guiTop + 6,
            Helpers.RGBToInt(0, 0, 0));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        if (func_146978_c(-20, 0, 18, 18, mouseX, mouseY)) {
            drawTooltip(
                Lists.newArrayList(LangHelpers.localize("gui.integrateddynamics.part_settings")),
                mouseX - guiLeft,
                mouseY - guiTop);
        }
        if (func_146978_c(-20, 20, 18, 18, mouseX, mouseY)) {
            drawTooltip(
                Lists.newArrayList(LangHelpers.localize("gui.integrateddynamics.part_offsets")),
                mouseX - guiLeft,
                mouseY - guiTop);
        }
    }
}
