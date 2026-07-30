package ruiseki.integrateddynamics.client.render.part;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.client.render.part.IPartOverlayRendererRegistry;
import ruiseki.integrateddynamics.core.part.PartTypes;

/**
 * A collection of all part overlay renderers
 *
 * @author rubensworks
 */
public class PartOverlayRenderers {

    public static final IPartOverlayRendererRegistry REGISTRY = IntegratedDynamics._instance.getRegistryManager()
        .getRegistry(IPartOverlayRendererRegistry.class);

    public static final DisplayPartOverlayRenderer DISPLAY = REGISTRY
        .register(PartTypes.DISPLAY_PANEL, new DisplayPartOverlayRenderer());

    public static void load() {}

}
