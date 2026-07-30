package ruiseki.integrateddynamics.client.model.json;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class BlockStateJson {

    @SerializedName("forge_marker")
    public int forgeMarker;

    @SerializedName("defaults")
    public Defaults defaults;

    @SerializedName("variants")
    public Map<String, Object> variants;

    public static class Defaults {

        @SerializedName("model")
        public String model;

        @SerializedName("textures")
        public Map<String, String> textures;

        @SerializedName("transform")
        public Object transform;
    }

    public static class Variant {

        @SerializedName("model")
        public String model;

        @SerializedName("textures")
        public Map<String, String> textures;

        @SerializedName("x")
        public Integer x;

        @SerializedName("y")
        public Integer y;

        @SerializedName("z")
        public Integer z;

        @SerializedName("uvlock")
        public Boolean uvlock;

        @SerializedName("weight")
        public Integer weight;
    }
}
