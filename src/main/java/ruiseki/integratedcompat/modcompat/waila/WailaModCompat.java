package ruiseki.integratedcompat.modcompat.waila;

import cpw.mods.fml.common.event.FMLInterModComms;
import ruiseki.okcore.modcompat.IModCompat;

/**
 * Compatibility plugin for Waila.
 *
 * @author rubensworks
 *
 */
public class WailaModCompat implements IModCompat {

    @Override
    public String getModID() {
        return "Waila";
    }

    @Override
    public void onInit(Step step) {
        if (step == Step.INIT) {
            FMLInterModComms.sendMessage(getModID(), "register", Waila.class.getName() + ".callbackRegister");
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getComment() {
        return "WAILA tooltips for parts.";
    }

}
