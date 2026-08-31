package ruiseki.integrateddynamics.core.client.gui.container;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.EnumChatFormatting;

import org.apache.commons.lang3.tuple.Triple;

import com.cleanroommc.modularui.utils.GlStateManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectPropertyTypeInstance;
import ruiseki.integrateddynamics.core.client.gui.ExtendedGuiHandler;
import ruiseki.integrateddynamics.core.inventory.container.ContainerMultipartAspects;
import ruiseki.integrateddynamics.core.part.PartTypeConfigurable;
import ruiseki.okcore.client.gui.component.button.GuiButtonImage;
import ruiseki.okcore.client.gui.component.button.GuiButtonText;
import ruiseki.okcore.client.gui.container.GuiContainerExtended;
import ruiseki.okcore.client.gui.container.ScrollingGuiContainer;
import ruiseki.okcore.client.gui.image.IImage;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.RenderHelpers;
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
public abstract class GuiMultipartAspects<P extends IPartType<P, S> & IGuiContainerProvider, S extends IPartState<P>, A extends IAspect>
    extends ScrollingGuiContainer {

    public static final int BUTTON_SETTINGS = 1;
    public static final int BUTTON_OFFSETS = 2;
    private static final Rectangle ITEM_POSITION = new Rectangle(8, 17, 18, 18);

    protected final DisplayErrorsComponent displayErrors = new DisplayErrorsComponent();
    private final PartTarget target;
    private final IPartContainer partContainer;
    private final P partType;

    private Map<IAspect, GuiButtonText> aspectPropertyButtons = Maps.newHashMap();

    /**
     * Make a new instance.
     *
     * @param container The container to make the GUI for.
     */
    public GuiMultipartAspects(ContainerMultipartAspects<P, S, A> container) {
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
                        ContainerMultipartAspects.BUTTON_SETTINGS,
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
                        ContainerMultipartAspects.BUTTON_OFFSETS,
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
        for (Map.Entry<IAspect, Integer> entry : (Set<Map.Entry<IAspect, Integer>>) ((ContainerMultipartAspects) getContainer())
            .getAspectPropertyButtons()
            .entrySet()) {
            GuiButtonText button = new GuiButtonText(entry.getValue(), -20, -20, 10, 10, "+", true);
            aspectPropertyButtons.put(entry.getKey(), button);
            buttonList.add(button);
        }
    }

    @SuppressWarnings("unchecked")
    public S getPartState() {
        return ((ContainerMultipartAspects<P, S, A>) container).getPartState();
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

        // Reset button positions
        for (Map.Entry<IAspect, GuiButtonText> entry : this.aspectPropertyButtons.entrySet()) {
            entry.getValue().xPosition = -20;
            entry.getValue().yPosition = -20;
        }

        // Draw part name
        RenderHelpers.drawScaledCenteredString(
            fontRenderer,
            LangHelpers.localize(getPartType().getUnlocalizedName()),
            this.guiLeft + offsetX + 6,
            this.guiTop + offsetY + 10,
            70,
            Helpers.RGBToInt(0, 0, 0));

        // Draw aspects
        ContainerMultipartAspects<P, S, A> container = (ContainerMultipartAspects) getScrollingInventoryContainer();
        int aspectBoxHeight = container.getAspectBoxHeight();
        for (int i = 0; i < container.getPageSize(); i++) {
            if (container.isElementVisible(i)) {
                A aspect = container.getVisibleElement(i);

                GlStateManager.disableAlpha();
                Triple<Float, Float, Float> rgb = Helpers.intToRGB(
                    aspect.getValueType()
                        .getDisplayColor());
                GlStateManager.color(
                    colorSmoothener(rgb.getLeft()),
                    colorSmoothener(rgb.getMiddle()),
                    colorSmoothener(rgb.getRight()),
                    1);

                // Background
                mc.renderEngine.bindTexture(texture);
                drawTexturedModalRect(
                    guiLeft + offsetX + 9,
                    guiTop + offsetY + 18 + aspectBoxHeight * i,
                    0,
                    getBaseYSize(),
                    160,
                    aspectBoxHeight - 1);

                // Aspect type info
                String aspectName = LangHelpers.localize(aspect.getUnlocalizedName());
                RenderHelpers.drawScaledCenteredString(
                    fontRenderer,
                    aspectName,
                    this.guiLeft + offsetX + 26,
                    this.guiTop + offsetY + 25 + aspectBoxHeight * i,
                    getMaxLabelWidth(),
                    Helpers.RGBToInt(40, 40, 40));

                drawAdditionalElementInfo(container, i, aspect);

                if (aspectPropertyButtons.containsKey(aspect)) {
                    GuiButtonText button = aspectPropertyButtons.get(aspect);
                    button.xPosition = this.guiLeft + offsetX + 116;
                    button.yPosition = this.guiTop + offsetY + 20 + aspectBoxHeight * i;
                }
            }
        }
    }

    protected abstract void drawAdditionalElementInfo(ContainerMultipartAspects<P, S, A> container, int index,
        A aspect);

    protected Rectangle getElementPosition(ContainerMultipartAspects<P, S, A> container, int i, boolean absolute) {
        return new Rectangle(
            ITEM_POSITION.x + offsetX + (absolute ? this.guiLeft : 0),
            ITEM_POSITION.y + container.getAspectBoxHeight() * i + offsetY + (absolute ? this.guiTop : 0),
            ITEM_POSITION.width,
            ITEM_POSITION.height);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        ContainerMultipartAspects<P, S, A> container = (ContainerMultipartAspects) getScrollingInventoryContainer();
        for (int i = 0; i < container.getPageSize(); i++) {
            if (container.isElementVisible(i)) {
                // Item icon tooltip
                if (isPointInRegion(getElementPosition(container, i, false), new Point(mouseX, mouseY))) {
                    List<String> lines = Lists.newLinkedList();
                    container.getVisibleElement(i)
                        .loadTooltip(lines, true);
                    drawTooltip(lines, mouseX - this.guiLeft, mouseY - this.guiTop);
                }
                drawAdditionalElementInfoForeground(container, i, container.getVisibleElement(i), mouseX, mouseY);

                // Optional aspect properties tooltip
                IAspect aspect = container.getVisibleElement(i);
                if (aspectPropertyButtons.containsKey(aspect)) {
                    GuiButtonText button = aspectPropertyButtons.get(aspect);
                    int x = button.xPosition - guiLeft;
                    int y = button.yPosition - guiTop;
                    if (func_146978_c(x, y, button.width, button.height, mouseX, mouseY)) {
                        List<String> lines = Lists.newLinkedList();
                        lines.add(
                            EnumChatFormatting.WHITE + LangHelpers.localize("gui.integrateddynamics.part.properties"));
                        for (IAspectPropertyTypeInstance property : ((IAspect<?, ?>) aspect).getPropertyTypes()) {
                            lines.add(
                                "-" + EnumChatFormatting.YELLOW + LangHelpers.localize(property.getUnlocalizedName()));
                        }
                        drawTooltip(lines, mouseX - this.guiLeft, mouseY - this.guiTop);
                    }
                }
            }
        }
    }

    protected abstract void drawAdditionalElementInfoForeground(ContainerMultipartAspects<P, S, A> container, int index,
        A aspect, int mouseX, int mouseY);

    public int getMaxLabelWidth() {
        return 63;
    }
}
