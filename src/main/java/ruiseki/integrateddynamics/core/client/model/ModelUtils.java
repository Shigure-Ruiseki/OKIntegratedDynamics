package ruiseki.integrateddynamics.core.client.model;

import java.io.InputStreamReader;
import java.io.Reader;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ModelUtils {

    public static String getLayer0FromModel(ResourceLocation modelLocation) {
        try {
            ResourceLocation jsonLocation = new ResourceLocation(
                modelLocation.getResourceDomain(),
                "models/" + modelLocation.getResourcePath() + ".json");

            try (Reader reader = new InputStreamReader(
                Minecraft.getMinecraft()
                    .getResourceManager()
                    .getResource(jsonLocation)
                    .getInputStream())) {

                JsonObject jsonObject = new JsonParser().parse(reader)
                    .getAsJsonObject();
                if (jsonObject.has("textures")) {
                    JsonObject textures = jsonObject.getAsJsonObject("textures");
                    if (textures.has("layer0")) {
                        return textures.get("layer0")
                            .getAsString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
