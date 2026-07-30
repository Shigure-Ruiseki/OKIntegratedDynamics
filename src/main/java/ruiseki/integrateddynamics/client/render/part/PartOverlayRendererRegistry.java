package ruiseki.integrateddynamics.client.render.part;

import java.util.Collection;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.client.render.part.IPartOverlayRenderer;
import ruiseki.integrateddynamics.api.client.render.part.IPartOverlayRendererRegistry;
import ruiseki.integrateddynamics.api.part.IPartType;

/**
 * Registry for {@link IPartOverlayRenderer}.
 * 
 * @author rubensworks
 */
@SideOnly(Side.CLIENT)
public final class PartOverlayRendererRegistry implements IPartOverlayRendererRegistry {

    private static PartOverlayRendererRegistry INSTANCE = new PartOverlayRendererRegistry();

    private final Multimap<IPartType<?, ?>, IPartOverlayRenderer> renderers = HashMultimap.create();

    private PartOverlayRendererRegistry() {

    }

    /**
     * @return The unique instance.
     */
    public static PartOverlayRendererRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public <R extends IPartOverlayRenderer> R register(IPartType<?, ?> partType, R renderer) {
        renderers.put(partType, renderer);
        return renderer;
    }

    @Override
    public Collection<IPartOverlayRenderer> getRenderers(IPartType<?, ?> partType) {
        return renderers.get(partType);
    }
}
