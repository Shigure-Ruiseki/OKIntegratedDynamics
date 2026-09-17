package ruiseki.integratedterminals.client.gui;

import ruiseki.integratedterminals.client.gui.image.Images;
import ruiseki.okcore.client.gui.component.button.GuiButtonImage;
import ruiseki.okcore.client.gui.image.IImage;

/**
 * A gui button for toggling sorting modes.
 *
 * @author rubensworks
 */
public class GuiButtonSort extends GuiButtonImage {

    private final boolean active;
    private final boolean descending;

    public GuiButtonSort(int x, int y, String narrationMessage, OnPress pressCallback, IImage image, boolean active,
        boolean descending) {
        super(x, y, narrationMessage, pressCallback, image);
        this.active = active;
        this.descending = descending;
    }

    @Override
    protected void drawButtonInner(int mouseX, int mouseY, boolean mouseOver) {
        (active ? Images.BUTTON_BACKGROUND_ACTIVE : Images.BUTTON_BACKGROUND_INACTIVE).draw(this, xPosition, yPosition);
        super.drawButtonInner(mouseX, mouseY, mouseOver);
        if (active) {
            (descending ? Images.BUTTON_OVERLAY_DESCENDING : Images.BUTTON_OVERLAY_ASCENDING)
                .draw(this, xPosition, yPosition);
        }
    }
}
