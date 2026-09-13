package ruiseki.integratedrest.http.request.handler;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.network.IPartNetworkElement;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integratedrest.json.JsonUtil;

/**
 * Request handler for /element/part requests.
 *
 * @author rubensworks
 */
public class ElementPartRequestHandler extends ElementTypeRequestHandler {

    @Nullable
    @Override
    protected HttpResponseStatus handleElement(int id, INetwork network, INetworkElement networkElement,
        HttpRequest request, JsonObject responseObject) {
        if (networkElement instanceof IPartNetworkElement) {
            IPartNetworkElement partNetworkElement = (IPartNetworkElement) networkElement;
            IPartNetwork partNetwork = NetworkHelpers.getPartNetwork(network)
                .getOrNull();
            if (partNetwork != null) {
                if (partNetworkElement.getPartState()
                    .getId() == id) {
                    if (request.getMethod()
                        .equals(HttpMethod.GET)) {
                        JsonUtil.addNetworkElementInfo(responseObject, networkElement, network);
                        return HttpResponseStatus.OK;
                    } else {
                        return HttpResponseStatus.BAD_REQUEST;
                    }
                }
            } else {
                return HttpResponseStatus.INTERNAL_SERVER_ERROR;
            }
        }
        return null;
    }
}
