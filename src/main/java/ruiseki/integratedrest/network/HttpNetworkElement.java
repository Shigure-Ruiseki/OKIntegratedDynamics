package ruiseki.integratedrest.network;

import net.minecraft.util.ResourceLocation;

import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.network.ProxyNetworkElement;
import ruiseki.okcore.datastructure.DimPos;

/**
 * Network element for http proxies.
 * 
 * @author rubensworks
 */
public class HttpNetworkElement extends ProxyNetworkElement {

    public static final ResourceLocation GROUP = new ResourceLocation(Reference.MOD_ID, "http");

    public HttpNetworkElement(DimPos pos) {
        super(pos);
    }

    @Override
    public ResourceLocation getGroup() {
        return HttpNetworkElement.GROUP;
    }

}
