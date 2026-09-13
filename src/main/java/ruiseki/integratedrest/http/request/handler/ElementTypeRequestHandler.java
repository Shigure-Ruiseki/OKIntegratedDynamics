package ruiseki.integratedrest.http.request.handler;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.core.persist.world.NetworkWorldStorage;
import ruiseki.integratedrest.api.http.request.IRequestHandler;

/**
 * Request handler for /part requests.
 * 
 * @author rubensworks
 */
public abstract class ElementTypeRequestHandler implements IRequestHandler {

    @Nullable
    protected abstract HttpResponseStatus handleElement(int id, INetwork network, INetworkElement networkElement,
        HttpRequest request, JsonObject responseObject);

    @Override
    public HttpResponseStatus handle(String[] path, HttpRequest request, JsonObject responseObject) {
        NetworkWorldStorage worldStorage = NetworkWorldStorage.getInstance(IntegratedDynamics._instance);
        if (path.length == 1) {
            // A single part
            try {
                int id = Integer.parseInt(path[0]);

                for (INetwork network : worldStorage.getNetworks()) {
                    for (INetworkElement element : network.getElements()) {
                        HttpResponseStatus status = handleElement(id, network, element, request, responseObject);
                        if (status != null) {
                            return status;
                        }
                    }
                }
            } catch (NumberFormatException e) {
                return HttpResponseStatus.BAD_REQUEST;
            }
        }
        return HttpResponseStatus.NOT_FOUND;
    }
}
