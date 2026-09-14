package ruiseki.integratedrest.http.request.handler;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cpw.mods.fml.common.registry.GameData;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import ruiseki.integratedrest.api.http.request.IRequestHandler;
import ruiseki.integratedrest.json.JsonUtil;

/**
 * Request handler for registry/item requests.
 * 
 * @author rubensworks
 */
public class RegistryItemRequestHandler implements IRequestHandler {

    @Override
    public HttpResponseStatus handle(String[] path, HttpRequest request, JsonObject responseObject) {
        if (path.length == 0) {
            JsonArray array = new JsonArray();
            for (Object object : GameData.getItemRegistry()) {
                if (object instanceof Item) {
                    Item item = (Item) object;
                    JsonObject itemObject = new JsonObject();
                    JsonUtil.addItemInfo(itemObject, item);
                    array.add(itemObject);
                }
            }
            responseObject.addProperty("@id", JsonUtil.absolutizePath("registry/item"));
            responseObject.add("items", array);
            return HttpResponseStatus.OK;
        } else {
            ResourceLocation resourceLocation = RegistryNamespacedRequestHandler.pathToResourceLocation(path);
            Item item = (Item) GameData.getItemRegistry()
                .getObject(resourceLocation.toString());

            if (item != null) {
                JsonUtil.addItemInfo(responseObject, item);
                return HttpResponseStatus.OK;
            }
        }
        return HttpResponseStatus.NOT_FOUND;
    }
}
