package ruiseki.integratedrest.http.request.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import ruiseki.integratedrest.api.http.request.IRequestHandler;
import ruiseki.integratedrest.json.JsonUtil;

/**
 * Request handler for registry/mod requests.
 * 
 * @author rubensworks
 */
public class RegistryModRequestHandler implements IRequestHandler {

    @Override
    public HttpResponseStatus handle(String[] path, HttpRequest request, JsonObject responseObject) {
        if (path.length == 0) {
            JsonArray array = new JsonArray();
            for (ModContainer modContainer : Loader.instance()
                .getActiveModList()) {
                JsonObject modObject = new JsonObject();
                JsonUtil.addModInfo(modObject, modContainer);
                array.add(modObject);
            }
            responseObject.addProperty("@id", JsonUtil.absolutizePath("registry/mod"));
            responseObject.add("mods", array);
            return HttpResponseStatus.OK;
        } else {
            String modId = String.join("/", path);
            for (ModContainer modContainer : Loader.instance()
                .getActiveModList()) {
                if (modContainer.getModId()
                    .equalsIgnoreCase(modId)) {
                    JsonUtil.addModInfo(responseObject, modContainer);
                    return HttpResponseStatus.OK;
                }
            }
        }
        return HttpResponseStatus.NOT_FOUND;
    }
}
