package ruiseki.integrateddynamics.core.evaluate.variable;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Data;
import ruiseki.integrateddynamics.api.client.gui.subgui.IGuiInputElement;
import ruiseki.integrateddynamics.api.client.gui.subgui.ISubGuiBox;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import ruiseki.integrateddynamics.core.client.gui.IDropdownEntry;
import ruiseki.integrateddynamics.core.client.gui.IDropdownEntryListener;
import ruiseki.integrateddynamics.core.client.gui.subgui.SubGuiBox;
import ruiseki.integrateddynamics.core.logicprogrammer.SubGuiConfigRenderPattern;
import ruiseki.okcore.client.gui.container.GuiContainerExtended;
import ruiseki.okcore.client.gui.image.Images;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.StringHelpers;

/**
 * Element for value type.
 *
 * @author rubensworks
 */
@Data
public class ValueTypeGuiElement<G extends Gui, C extends Container>
    implements IGuiInputElement<SubGuiConfigRenderPattern, G, C>, IDropdownEntryListener {

    private final IValueType valueType;
    private final IConfigRenderPattern renderPattern;
    private String defaultInputString;
    private String inputString;
    private Set<IDropdownEntry<?>> dropdownPossibilities = Collections.emptySet();
    private IDropdownEntryListener dropdownEntryListener = null;

    public ValueTypeGuiElement(IValueType valueType, IConfigRenderPattern renderPattern) {
        this.valueType = valueType;
        this.renderPattern = renderPattern;
        defaultInputString = getValueType().toCompactString(getValueType().getDefault());
    }

    public void setInputString(String inputString, ValueTypeSubGuiRenderPattern subGui) {
        this.inputString = inputString;
        if (subGui != null) {
            subGui.getSearchField()
                .setText(inputString);
        }
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
        this.inputString = new String(defaultInputString);
    }

    @Override
    public void deactivate() {
        this.inputString = null;
    }

    @Override
    public LangHelpers.UnlocalizedString validate() {
        return getValueType().canDeserialize(inputString);
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
    public void onSetDropdownPossiblity(IDropdownEntry<?> dropdownEntry) {
        if (dropdownEntryListener != null) {
            dropdownEntryListener.onSetDropdownPossiblity(dropdownEntry);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ValueTypeSubGuiRenderPattern createSubGui(int baseX, int baseY, int maxWidth, int maxHeight, G gui,
        C container) {
        return new ValueTypeSubGuiRenderPattern<ValueTypeSubGuiRenderPattern, G, C>(
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

        protected abstract LangHelpers.UnlocalizedString getLastError();

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

            fontRenderer.drawString(element.getLocalizedNameFull(), x + 2, y + 6, Helpers.RGBToInt(240, 240, 240));

            if (showError()) {
                LangHelpers.UnlocalizedString lastError = getLastError();
                if (lastError != null) {
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
                LangHelpers.UnlocalizedString lastError = getLastError();
                if (lastError != null && gui.func_146978_c(
                    x + getSignalX(),
                    y + getSignalY() - 1,
                    Images.ERROR.getSheetWidth(),
                    Images.ERROR.getSheetHeight(),
                    mouseX,
                    mouseY)) {
                    List<String> lines = Lists.newLinkedList();
                    lines.addAll(
                        StringHelpers.splitLines(
                            lastError.localize(),
                            LangHelpers.MAX_TOOLTIP_LINE_LENGTH,
                            EnumChatFormatting.RED.toString()));
                    gui.drawTooltip(lines, mouseX - guiLeft, mouseY - guiTop);
                }
            }
        }

    }

}
