package ruiseki.integrateddynamics.core.client.gui.container;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.api.client.gui.subgui.IGuiInputElement;
import ruiseki.integrateddynamics.api.client.gui.subgui.IGuiInputElementValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.logicprogrammer.IValueTypeLogicProgrammerElement;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectPropertyTypeInstance;
import ruiseki.integrateddynamics.core.client.gui.ExtendedGuiHandler;
import ruiseki.integrateddynamics.core.client.gui.subgui.SubGuiHolder;
import ruiseki.integrateddynamics.core.evaluate.variable.gui.GuiElementValueTypeString;
import ruiseki.integrateddynamics.core.inventory.container.ContainerAspectSettings;
import ruiseki.integrateddynamics.core.logicprogrammer.RenderPattern;
import ruiseki.okcore.client.gui.component.button.GuiButtonText;
import ruiseki.okcore.client.gui.container.GuiContainerExtended;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.RenderHelpers;

/**
 * Gui for aspect settings.
 *
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class GuiAspectSettings extends GuiContainerExtended<ContainerAspectSettings> {

    private static final int ERROR_WIDTH = 13;
    private static final int ERROR_HEIGHT = 13;
    private static final int OK_WIDTH = 14;
    private static final int OK_HEIGHT = 12;

    private static final int BUTTON_LEFT = 0;
    private static final int BUTTON_RIGHT = 1;
    public static final int BUTTON_EXIT = 2;

    private final PartTarget target;
    private final IPartContainer partContainer;
    private final IPartType partType;
    private final IAspect aspect;

    private final List<IAspectPropertyTypeInstance> propertyTypes;
    protected final SubGuiHolder subGuiHolder = new SubGuiHolder();
    protected IGuiInputElementValueType<RenderPattern, GuiAspectSettings, ContainerAspectSettings> guiElement = null;
    protected int activePropertyIndex = 0;
    protected RenderPattern propertyConfigPattern = null;
    protected SubGuiValueTypeInfo propertyInfo = null;
    private GuiButtonText buttonLeft = null;
    private GuiButtonText buttonRight = null;
    private GuiButtonText buttonExit = null;
    private LangHelpers.UnlocalizedString lastError;

    /**
     * Make a new instance.
     *
     * @param target        The target.
     * @param player        The player.
     * @param partContainer The part container.
     * @param partType      The part type.
     * @param aspect        The aspect.
     */
    public GuiAspectSettings(EntityPlayer player, PartTarget target, IPartContainer partContainer, IPartType partType,
        IAspect aspect) {
        super(new ContainerAspectSettings(player, target, partContainer, partType, aspect));
        this.target = target;
        this.partContainer = partContainer;
        this.partType = partType;
        this.aspect = aspect;

        // noinspection deprecation
        this.propertyTypes = Lists.newArrayList(
            container.getAspect()
                .getDefaultProperties()
                .getTypes());
    }

    @Override
    protected ResourceLocation constructGuiTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/aspect_settings.png");
    }

    protected void saveSetting() {
        if (guiElement != null && lastError == null) {
            container.setValue(getActiveProperty(), guiElement.getValue());
        }
    }

    protected void refreshButtonEnabled() {
        buttonLeft.enabled = getActivePropertyIndex() > 0;
        buttonRight.enabled = getActivePropertyIndex() < propertyTypes.size() - 1;
    }

    @Override
    protected int getBaseYSize() {
        return 213;
    }

    @Override
    public void initGui() {
        super.initGui();
        subGuiHolder.initGui(this.guiLeft, this.guiTop);
        addRenderableWidget(
            buttonExit = new GuiButtonText(
                guiLeft + 7,
                guiTop + 5,
                12,
                10,
                "<<",
                createServerPressable(ContainerAspectSettings.BUTTON_EXIT, (button) -> {
                    saveSetting();
                    IntegratedDynamics._instance.getGuiHandler()
                        .setTemporaryData(
                            ExtendedGuiHandler.PART,
                            getTarget().getCenter()
                                .getSide());
                }),
                true));
        addRenderableWidget(buttonLeft = new GuiButtonText(guiLeft + 21, guiTop + 5, 10, 10, "<", (button) -> {
            saveSetting();
            if (getActivePropertyIndex() > 0) {
                setActiveProperty(getActivePropertyIndex() - 1);
                refreshButtonEnabled();
            }
        }, true));
        addRenderableWidget(buttonRight = new GuiButtonText(guiLeft + 159, guiTop + 5, 10, 10, ">", (button) -> {
            saveSetting();
            if (getActivePropertyIndex() < propertyTypes.size()) {
                setActiveProperty(getActivePropertyIndex() + 1);
                refreshButtonEnabled();
            }
        }, true));
        refreshButtonEnabled();

        setActiveProperty(activePropertyIndex);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        subGuiHolder.drawGuiContainerBackgroundLayer(
            this.guiLeft,
            this.guiTop,
            mc.renderEngine,
            fontRendererObj,
            partialTicks,
            mouseX,
            mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        subGuiHolder.drawGuiContainerForegroundLayer(
            this.guiLeft,
            this.guiTop,
            mc.renderEngine,
            fontRendererObj,
            mouseX,
            mouseY);

        IAspectPropertyTypeInstance activeProperty = getActiveProperty();
        if (activeProperty != null) {
            String label = LangHelpers.localize(activeProperty.getUnlocalizedName());
            RenderHelpers
                .drawScaledCenteredString(fontRendererObj, label, 88, 10, 0, 1.0F, 140, Helpers.RGBToInt(10, 10, 10));
            if (RenderHelpers.isPointInRegion(this.guiLeft + 40, this.guiTop, 110, 20, mouseX, mouseY)) {
                String unlocalizedInfo = activeProperty.getUnlocalizedName()
                    .replaceFirst("\\.name$", ".info");
                if (StatCollector.canTranslate(unlocalizedInfo)) {
                    drawTooltip(
                        Lists.newArrayList(EnumChatFormatting.GRAY.toString() + LangHelpers.localize(unlocalizedInfo)),
                        mouseX - this.guiLeft,
                        mouseY - this.guiTop + 20);
                }
            }
        }
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        if (!subGuiHolder.charTyped(typedChar, keyCode)) {
            if (keyCode == 1 || this.mc.gameSettings.keyBindInventory.getKeyCode() == keyCode) {
                saveSetting();
                this.mc.thePlayer.closeScreen();
            } else {
                return super.charTyped(typedChar, keyCode);
            }
        } else {
            if (guiElement != null) {
                onValueChanged();
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int typedChar, int keyCode, int modifiers) {
        if (typedChar != Keyboard.KEY_ESCAPE) {
            if (this.subGuiHolder.keyPressed(typedChar, keyCode, modifiers)) {
                if (guiElement != null) {
                    onValueChanged();
                }
                return true;
            } else {
                return false;
            }
        } else {
            saveSetting();
            return super.keyPressed(typedChar, keyCode, modifiers);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return subGuiHolder.mouseClicked(mouseX, mouseY, mouseButton)
            || super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void onValueChanged() {
        lastError = guiElement.validate();
    }

    protected IAspectPropertyTypeInstance getActiveProperty() {
        return propertyTypes.get(Math.max(0, Math.min(propertyTypes.size() - 1, activePropertyIndex)));
    }

    protected void setActiveProperty(int index) {
        onActivateElement(propertyTypes.get(activePropertyIndex = index));
    }

    protected void onActivateElement(IAspectPropertyTypeInstance property) {
        // Deactivate old element
        if (guiElement != null) {
            guiElement.deactivate();
            subGuiHolder.removeSubGui(propertyConfigPattern);
            subGuiHolder.removeSubGui(propertyInfo);
        }

        // Determine element type
        IValueTypeLogicProgrammerElement lpElement = property.getType()
            .createLogicProgrammerElement();
        guiElement = lpElement.createInnerGuiElement();
        if (guiElement == null) {
            throw new UnsupportedOperationException(
                "Tried to invoke createInnerGuiElement on a value type that does not have an inner gui element: "
                    + property.getType()
                        .getTypeName());
        }

        // Create new element
        guiElement.setValidator(property.getValidator());
        subGuiHolder.addSubGui(
            propertyConfigPattern = guiElement
                .createSubGui(8, 17, 160, 91, this, (ContainerAspectSettings) getContainer()));
        subGuiHolder.addSubGui(propertyInfo = new SubGuiValueTypeInfo(guiElement));
        propertyConfigPattern.initGui(guiLeft, guiTop);
        guiElement.activate();
        syncInputValue();
        lastError = guiElement.validate();
    }

    protected void syncInputValue() {
        IAspectPropertyTypeInstance property = getActiveProperty();
        IValue value = getContainer().getPropertyValue(property);
        if (value != null) {
            guiElement.setValue(value);
            guiElement.setValueInGui(propertyConfigPattern, false);
        }
        onValueChanged();
    }

    @Override
    public void onUpdate(int valueId, NBTTagCompound value) {
        super.onUpdate(valueId, value);
        IAspectPropertyTypeInstance property = getContainer().getPropertyIds()
            .get(valueId);
        if (property != null && getActiveProperty() == property) {
            syncInputValue();
        }
    }

    public class SubGuiValueTypeInfo extends
        GuiElementValueTypeString.SubGuiValueTypeInfo<RenderPattern, GuiAspectSettings, ContainerAspectSettings> {

        public SubGuiValueTypeInfo(
            IGuiInputElement<RenderPattern, GuiAspectSettings, ContainerAspectSettings> element) {
            super(
                GuiAspectSettings.this,
                (ContainerAspectSettings) GuiAspectSettings.this.container,
                element,
                8,
                105,
                160,
                20);
        }

        @Override
        protected boolean showError() {
            return true;
        }

        @Override
        protected LangHelpers.UnlocalizedString getLastError() {
            return lastError;
        }

        @Override
        protected ResourceLocation getTexture() {
            return texture;
        }

    }

}
