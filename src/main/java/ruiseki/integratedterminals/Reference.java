package ruiseki.integratedterminals;

import ruiseki.integrateddynamics.Tags;

/**
 * Class that can hold basic static things that are better not hard-coded
 * like mod details, texture paths, ID's...
 *
 * @author rubensworks (aka kroeserr)
 *
 */
@SuppressWarnings("javadoc")
public class Reference {

    // Mod info
    public static final String MOD_ID = "integratedterminals";
    public static final String MOD_NAME = "Integrated Terminals";
    public static final String MOD_VERSION = Tags.VERSION;
    public static final String VERSION_URL = "";

    // Paths
    public static final String TEXTURE_PATH_GUI = "textures/gui/";
    public static final String TEXTURE_PATH_SKINS = "textures/skins/";
    public static final String TEXTURE_PATH_MODELS = "textures/models/";
    public static final String TEXTURE_PATH_ENTITIES = "textures/entities/";
    public static final String TEXTURE_PATH_GUIBACKGROUNDS = "textures/gui/title/background/";
    public static final String TEXTURE_PATH_ITEMS = "textures/items/";
    public static final String TEXTURE_PATH_PARTICLES = "textures/particles/";
    public static final String MODEL_PATH = "models/";

    // Dependencies
    public static final String MOD_DEPENDENCIES = "required-after:okcore;" + "required-after:commoncapabilities;"
        + "required-after:integrateddynamics;"
        + "required-after:integratedtunnels;"
        + "required-after:integratedcrafting;"
        + "after:jfmuy;"
        + "after:Waila;";
}
