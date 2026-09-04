package ruiseki.integratedcompat;

import ruiseki.integrateddynamics.Tags;

public class Reference {

    public static final String MOD_ID = "integratedcompat";
    public static final String MOD_NAME = "IntegratedCompat";
    public static final String MOD_VERSION = Tags.VERSION;
    public static final String MOD_DEPENDENCIES = "required-after:okcore;" + "required-after:commoncapabilities;"
        + "required-after:integrateddynamics;"
        + "required-after:integratedcrafting;"
        + "required-after:integratedterminals;"
        + "required-after:integratedtunnels;"
        + "after:jfmuy;"
        + "after:NotEnoughItems;"
        + "after:Waila;";
    public static final String VERSION_URL = "";
    public static final String GUI_FACTORY = "ruiseki.integratedcompat.GuiConfigOverview$ExtendedConfigGuiFactory";
}
