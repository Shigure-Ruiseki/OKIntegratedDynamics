package ruiseki.integrateddynamics.client.gui.image;

import net.minecraft.util.ResourceLocation;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.client.gui.image.Image;
import ruiseki.okcore.init.ModBase;

/**
 * Default images provided by this mod.
 * 
 * @author rubensworks
 */
public class Images {

    public static final ResourceLocation ICONS = new ResourceLocation(
        IntegratedDynamics._instance.getModId(),
        IntegratedDynamics._instance.getReferenceValue(ModBase.REFKEY_TEXTURE_PATH_GUI) + "icons.png");

    public static final Image BUTTON_BACKGROUND_INACTIVE = new Image(ICONS, 0, 0, 18, 18);
    public static final Image BUTTON_BACKGROUND_ACTIVE = new Image(ICONS, 18, 0, 18, 18);

    public static final Image BUTTON_MIDDLE_OFFSET = new Image(ICONS, 0, 18, 18, 18);
    public static final Image BUTTON_MIDDLE_SETTINGS = new Image(ICONS, 18, 18, 18, 18);

}
