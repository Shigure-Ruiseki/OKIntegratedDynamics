package ruiseki.integrateddynamics.core.evaluate.variable.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Data;
import ruiseki.integrateddynamics.api.client.gui.subgui.IGuiInputElement;
import ruiseki.integrateddynamics.api.client.gui.subgui.IGuiInputElementValueType;
import ruiseki.integrateddynamics.api.client.gui.subgui.ISubGuiBox;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import ruiseki.integrateddynamics.core.client.gui.IDropdownEntry;
import ruiseki.integrateddynamics.core.client.gui.IDropdownEntryListener;
import ruiseki.integrateddynamics.core.client.gui.subgui.SubGuiBox;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.logicprogrammer.RenderPattern;
import ruiseki.okcore.client.gui.container.GuiContainerExtended;
import ruiseki.okcore.client.gui.image.Images;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.RenderHelpers;
import ruiseki.okcore.helper.StringHelpers;

/**
 * GUI element for value type that are displayed using a dropdown list.
 *
 * @author rubensworks
 */
@Data
public class GuiElementValueTypeDropdownList<T, G extends Gui, C extends Container>
    implements IGuiInputElementValueType<RenderPattern, G, C>, IDropdownEntryListener<T> {

    private final IValueType valueType;
    private Predicate<IValue> validator;
    private final IConfigRenderPattern renderPattern;
    private String inputString;
    private Set<IDropdownEntry<T>> dropdownPossibilities = Collections.emptySet();
    private IDropdownEntryListener<T> dropdownEntryListener = null;

    public GuiElementValueTypeDropdownList(IValueType valueType, IConfigRenderPattern renderPattern) {
        this.valueType = valueType;
        this.validator = Predicates.alwaysTrue();
        this.renderPattern = renderPattern;
    }

    @Override
    public void setValidator(Predicate<IValue> validator) {
        this.validator = validator;
    }

    @Override
    public void setValue(IValue value, RenderPattern propertyConfigPattern) {
        throw new UnsupportedOperationException("This method has not been implemented yet");
    }

    @Override
    public IValue getValue() {
        throw new UnsupportedOperationException("This method has not been implemented yet");
    }

    @Override
    public String getLocalizedNameFull() {
        return LangHelpers.localize(getValueType().getUnlocalizedName());
    }

    @Override
    public void loadTooltip(List<String> lines) {
        getValueType().loadTooltip(lines, true, null);
    }

    @Override
    public IConfigRenderPattern getRenderPattern() {
        return renderPattern;
    }

    @Override
    public void activate() {
        this.inputString = "";
    }

    @Override
    public void deactivate() {
        this.inputString = null;
    }

    @Override
    public LangHelpers.UnlocalizedString validate() {
        IValue value = ValueHelpers.deserializeRaw(getValueType(), inputString);
        if (!this.validator.apply(value)) {
            return new LangHelpers.UnlocalizedString(L10NValues.VALUE_ERROR);
        }
        return null;
    }

    @Override
    public int getColor() {
        return getValueType().getDisplayColor();
    }

    @Override
    public String getSymbol() {
        return LangHelpers.localize(getValueType().getUnlocalizedName());
    }

    @Override
    public void onSetDropdownPossiblity(IDropdownEntry dropdownEntry) {
        if (dropdownEntryListener != null) {
            dropdownEntryListener.onSetDropdownPossiblity(dropdownEntry);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiElementValueTypeDropdownListRenderPattern<T, ?, G, C> createSubGui(int baseX, int baseY, int maxWidth,
        int maxHeight, G gui, C container) {
        return new GuiElementValueTypeDropdownListRenderPattern<>(
            this,
            baseX,
            baseY,
            maxWidth,
            maxHeight,
            gui,
            container);
    }

    @SideOnly(Side.CLIENT)
    public abstract static class SubGuiValueTypeInfo<S extends ISubGuiBox, G extends GuiContainerExtended, C extends Container>
        extends SubGuiBox.Base {

        private final IGuiInputElement element;
        protected final G gui;
        protected final C container;

        public SubGuiValueTypeInfo(G gui, C container, IGuiInputElement<S, G, C> element, int x, int y, int width,
            int height) {
            super(Box.DARK, x, y, width, height);
            this.gui = gui;
            this.container = container;
            this.element = element;
        }

        protected abstract boolean showError();

        protected abstract String getLastError();

        protected abstract ResourceLocation getTexture();

        protected int getSignalX() {
            return getWidth() - 22;
        }

        protected int getSignalY() {
            return (getHeight() - 12) / 2;
        }

        @Override
        public void drawGuiContainerBackgroundLayer(int guiLeft, int guiTop, TextureManager textureManager,
            FontRenderer fontRenderer, float partialTicks, int mouseX, int mouseY) {
            super.drawGuiContainerBackgroundLayer(
                guiLeft,
                guiTop,
                textureManager,
                fontRenderer,
                partialTicks,
                mouseX,
                mouseY);

            int x = guiLeft + getX();
            int y = guiTop + getY();

            RenderHelpers.drawScaledCenteredString(
                fontRenderer,
                String.valueOf(element.getLocalizedNameFull()),
                x + 2,
                y + 6,
                10,
                Helpers.RGBToInt(240, 240, 240));

            if (showError()) {
                String lastError = getLastError();
                if (lastError.isEmpty()) {
                    Images.ERROR.draw(this, x + getSignalX(), y + getSignalY() - 1);
                } else {
                    Images.OK.draw(this, x + getSignalX(), y + getSignalY() + 1);
                }
            }
        }

        @Override
        public void drawGuiContainerForegroundLayer(int guiLeft, int guiTop, TextureManager textureManager,
            FontRenderer fontRenderer, int mouseX, int mouseY) {
            super.drawGuiContainerForegroundLayer(guiLeft, guiTop, textureManager, fontRenderer, mouseX, mouseY);

            int x = getX();
            int y = getY();

            if (showError()) {
                String lastError = getLastError();
                if (lastError != null && gui.func_146978_c(
                    x + getSignalX(),
                    y + getSignalY() - 1,
                    Images.ERROR.getSheetWidth(),
                    Images.ERROR.getSheetHeight(),
                    mouseX,
                    mouseY)) {
                    List<String> lines = new ArrayList<>(
                        StringHelpers.splitLines(
                            lastError,
                            LangHelpers.MAX_TOOLTIP_LINE_LENGTH,
                            EnumChatFormatting.RED.toString()));
                    gui.drawTooltip(lines, mouseX - guiLeft, mouseY - guiTop);
                }
            }
        }

    }

}
