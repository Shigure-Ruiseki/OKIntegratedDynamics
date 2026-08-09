package ruiseki.integrateddynamics.modcompat.nei;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.modcompat.IModCompat;

public class NEIModCompat implements IModCompat {

    /**
     * If the modcompat can be used.
     */
    public static boolean canBeUsed = false;

    @Override
    public void onInit(Step initStep) {
        if (initStep == Step.PREINIT) {
            canBeUsed = IntegratedDynamics._instance.getModCompatLoader()
                .shouldLoadModCompat(this);
        }
    }

    @Override
    public String getModID() {
        return "NotEnoughItems";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getComment() {
        return "Integration for Integrated Dynamics recipes.";
    }

}
