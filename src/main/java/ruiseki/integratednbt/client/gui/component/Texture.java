package ruiseki.integratednbt.client.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class Texture {

    private ResourceLocation resourceLocation;

    public Texture(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    public Texture(String namespace, String path) {
        this(new ResourceLocation(namespace, path));
    }

    public void bind() {
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(this.resourceLocation);
    }

    public ResourceLocation getResourceLocation() {
        return this.resourceLocation;
    }

    public TexturePart createPart(int x, int y, int width, int height) {
        return new TexturePart(this, x, y, width, height);
    }
}
