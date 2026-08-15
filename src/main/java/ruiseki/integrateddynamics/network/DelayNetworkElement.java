package ruiseki.integrateddynamics.network;

import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.Reference;
import ruiseki.okcore.datastructure.DimPos;

/**
 * Network element for delays.
 * 
 * @author rubensworks
 */
public class DelayNetworkElement extends ProxyNetworkElement {

    public static final ResourceLocation GROUP = new ResourceLocation(Reference.MOD_ID, "delay");

    public DelayNetworkElement(DimPos pos) {
        super(pos);
    }

    @Override
    public ResourceLocation getGroup() {
        return DelayNetworkElement.GROUP;
    }

}
