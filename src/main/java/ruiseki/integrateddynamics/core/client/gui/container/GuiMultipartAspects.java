package ruiseki.integrateddynamics.core.client.gui.container;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
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
import ruiseki.okcore.client.gui.container.GuiContainerScrolling;
import ruiseki.okcore.client.gui.image.IImage;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.RenderHelpers;
import ruiseki.okcore.inventory.IGuiContainerProvider;

/**
 * Gui for parts.
 *
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = false)
@Data
public abstract class GuiMultipartAspects<P extends IPartType<P, S> & IGuiContainerProvider, S extends IPartState<P>, A extends IAspect, C extends ContainerMultipartAspects<P, S, A>>
    extends GuiContainerScrolling<C> {

    private static final Rectangle ITEM_POSITION = new Rectangle(8, 17, 18, 18);
    /**
     * The maximum number of characters that are shown for a modified aspect property value in tooltips.
     */
    private static final int MAX_PROPERTY_VALUE_LENGTH = 20;

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
    public GuiMultipartAspects(C container) {
        super(container);
        this.target = container.getTarget();
        this.partContainer = container.getPartContainer();
        this.partType = container.getPartType();
    }

    @Override
    protected Rectangle getScrollRegion() {
        return new Rectangle(this.guiLeft + 9, this.guiTop + 18, 160, 105);
    }

    @Override
    public void initGui() {
        clearWidgets();
        super.initGui();
        if (getPartType() instanceof PartTypeConfigurable<?, ?>configurable) {
            if (configurable.hasSettings()) {
                addRenderableWidget(
                    new GuiButtonImage(
                        this.guiLeft - 20,
                        this.guiTop + 0,
                        18,
                        18,
                        LangHelpers.localize("gui.integrateddynamics.part_settings"),
                        createServerPressable(ContainerMultipartAspects.BUTTON_SETTINGS, (button) -> {
                            IntegratedDynamics._instance.getGuiHandler()
                                .setTemporaryData(
                                    ExtendedGuiHandler.PART,
                                    getTarget().getCenter()
                                        .getSide()); // Pass the side as extra data to the gui
                        }),
                        new IImage[] { ruiseki.integrateddynamics.client.gui.image.Images.BUTTON_BACKGROUND_INACTIVE,
                            ruiseki.integrateddynamics.client.gui.image.Images.BUTTON_MIDDLE_SETTINGS },
                        false,
                        0,
                        0));
            }
            if (configurable.supportsOffsets()) {
                addRenderableWidget(
                    new GuiButtonImage(
                        this.guiLeft - 20,
                        this.guiTop + 20,
                        18,
                        18,
                        LangHelpers.localize("gui.integrateddynamics.part_offsets"),
                        createServerPressable(ContainerMultipartAspects.BUTTON_OFFSETS, (button) -> {
                            IntegratedDynamics._instance.getGuiHandler()
                                .setTemporaryData(
                                    ExtendedGuiHandler.PART,
                                    getTarget().getCenter()
                                        .getSide()); // Pass the side as extra data to the gui
                        }),
                        new IImage[] { ruiseki.integrateddynamics.client.gui.image.Images.BUTTON_BACKGROUND_INACTIVE,
                            ruiseki.integrateddynamics.client.gui.image.Images.BUTTON_MIDDLE_OFFSET },
                        false,
                        0,
                        0));
            }
        }
        for (Map.Entry<IAspect, String> entry : getContainer().getAspectPropertyButtons()
            .entrySet()) {
            GuiButtonText button = new GuiButtonText(
                -20,
                -20,
                10,
                10,
                "+",
                createServerPressable(entry.getValue(), b -> {
                    IntegratedDynamics._instance.getGuiHandler()
                        .setTemporaryData(
                            ExtendedGuiHandler.PART,
                            getTarget().getCenter()
                                .getSide()); // Pass the side as extra data to the gui
                }),
                true);
            aspectPropertyButtons.put(entry.getKey(), button);
            addRenderableWidget(button);
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
        C container = getContainer();
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

    protected abstract void drawAdditionalElementInfo(C container, int index, A aspect);

    protected Rectangle getElementPosition(C container, int i, boolean absolute) {
        return new Rectangle(
            ITEM_POSITION.x + offsetX + (absolute ? this.guiLeft : 0),
            ITEM_POSITION.y + container.getAspectBoxHeight() * i + offsetY + (absolute ? this.guiTop : 0),
            ITEM_POSITION.width,
            ITEM_POSITION.height);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        C container = getContainer();
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
                        List<String> propertyValues = container.getModifiedAspectPropertyValuesSynced(aspect);
                        int propertyIndex = 0;
                        for (IAspectPropertyTypeInstance property : ((IAspect<?, ?>) aspect).getPropertyTypes()) {
                            String line = "-" + EnumChatFormatting.YELLOW
                                + LangHelpers.localize(property.getUnlocalizedName());

                            String value = propertyValues != null && propertyIndex < propertyValues.size()
                                ? propertyValues.get(propertyIndex)
                                : null;

                            if (value != null && !value.trim()
                                .isEmpty()) {
                                line += EnumChatFormatting.YELLOW + ": "
                                    + EnumChatFormatting.RESET
                                    + compactPropertyValue(value);
                            }
                            lines.add(line);
                            propertyIndex++;
                        }
                        drawTooltip(lines, mouseX - this.guiLeft, mouseY - this.guiTop);
                    }
                }
            }
        }

        if (getPartType() instanceof PartTypeConfigurable<?, ?>configurable) {
            if (isPointInRegion(-20, 0, 18, 18, mouseX, mouseY)) {
                drawTooltip(
                    Lists.newArrayList(LangHelpers.localize("gui.integrateddynamics.part_settings")),
                    mouseX - guiLeft,
                    mouseY - guiTop);
            }
            if (isPointInRegion(-20, 20, 18, 18, mouseX, mouseY)) {
                drawTooltip(
                    Lists.newArrayList(LangHelpers.localize("gui.integrateddynamics.part_offsets")),
                    mouseX - guiLeft,
                    mouseY - guiTop);
            }
        }
    }

    protected abstract void drawAdditionalElementInfoForeground(C container, int index, A aspect, int mouseX,
        int mouseY);

    public int getMaxLabelWidth() {
        return 63;
    }

    /**
     * Create a compact single-line representation of the given aspect property value.
     *
     * @param value An aspect property value.
     * @return A compact representation of the given value.
     */
    protected static String compactPropertyValue(String value) {
        if (value == null) return "";
        String string = value.replaceAll("\\s+", " ");
        if (string.length() > MAX_PROPERTY_VALUE_LENGTH) {
            string = string.substring(0, MAX_PROPERTY_VALUE_LENGTH) + "...";
        }
        return string;
    }
}
