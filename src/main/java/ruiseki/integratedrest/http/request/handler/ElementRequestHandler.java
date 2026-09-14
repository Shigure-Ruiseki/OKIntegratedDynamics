package ruiseki.integratedrest.http.request.handler;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.network.IIdentifiableNetworkElement;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.core.persist.world.NetworkWorldStorage;
import ruiseki.integratedrest.api.http.request.IRequestHandler;
import ruiseki.integratedrest.json.JsonUtil;

/**
 * Request handler for /part requests.
 * 
 * @author rubensworks
 */
public class ElementRequestHandler implements IRequestHandler {

    @Override
    public HttpResponseStatus handle(String[] path, HttpRequest request, JsonObject responseObject) {
        NetworkWorldStorage worldStorage = NetworkWorldStorage.getInstance(IntegratedDynamics._instance);
        if (path.length == 0) {
            if (!request.getMethod()
                .equals(HttpMethod.GET)) {
                return HttpResponseStatus.BAD_REQUEST;
            }

            // All parts
            JsonArray jsonParts = new JsonArray();
            for (INetwork network : worldStorage.getNetworks()) {
                for (INetworkElement element : network.getElements()) {
                    JsonObject jsonElement = new JsonObject();
                    JsonUtil.addNetworkElementInfo(jsonElement, element, network);
                    jsonParts.add(jsonElement);
                }
            }
            responseObject.addProperty("@id", JsonUtil.absolutizePath("/"));
            responseObject.add("networkElements", jsonParts);
            return HttpResponseStatus.OK;
        } else {
            if (path.length >= 2) {
                String groupPath = String.join("/", ArrayUtils.subarray(path, 0, path.length - 1));
                try {
                    int id = Integer.parseInt(path[path.length - 1]);

                    for (INetwork network : worldStorage.getNetworks()) {
                        for (INetworkElement element : network.getElements()) {
                            HttpResponseStatus status = handleElement(
                                groupPath,
                                id,
                                network,
                                element,
                                request,
                                responseObject);
                            if (status != null) {
                                return status;
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    return HttpResponseStatus.BAD_REQUEST;
                }
            }
        }
        return HttpResponseStatus.NOT_FOUND;
    }

    @Nullable
    protected HttpResponseStatus handleElement(String groupPath, int id, INetwork network,
        INetworkElement networkElement, HttpRequest request, JsonObject responseObject) {
        if (networkElement instanceof IIdentifiableNetworkElement
            && ((IIdentifiableNetworkElement) networkElement).getId() == id
            && JsonUtil.resourceLocationToPath(((IIdentifiableNetworkElement) networkElement).getGroup())
                .equals(groupPath)) {
            JsonUtil.addNetworkElementInfo(responseObject, networkElement, network);
            return HttpResponseStatus.OK;
        }
        return null;
    }
}
