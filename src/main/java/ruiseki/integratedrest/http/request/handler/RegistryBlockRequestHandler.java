package ruiseki.integratedrest.http.request.handler;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cpw.mods.fml.common.registry.GameData;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import ruiseki.integratedrest.api.http.request.IRequestHandler;
import ruiseki.integratedrest.json.JsonUtil;

/**
 * Request handler for registry/block requests.
 * 
 * @author rubensworks
 */
public class RegistryBlockRequestHandler implements IRequestHandler {

    @Override
    public HttpResponseStatus handle(String[] path, HttpRequest request, JsonObject responseObject) {
        if (path.length == 0) {
            JsonArray array = new JsonArray();
            for (Object object : GameData.getBlockRegistry()) {
                if (object instanceof Block) {
                    Block block = (Block) object;
                    JsonObject blockObject = new JsonObject();
                    JsonUtil.addBlockInfo(blockObject, block);
                    array.add(blockObject);
                }
            }
            responseObject.addProperty("@id", JsonUtil.absolutizePath("registry/block"));
            responseObject.add("blocks", array);
            return HttpResponseStatus.OK;
        } else {
            ResourceLocation resourceLocation = RegistryNamespacedRequestHandler.pathToResourceLocation(path);
            Block block = (Block) GameData.getBlockRegistry()
                .getObject(resourceLocation.toString());

            if (block != null) {
                JsonUtil.addBlockInfo(responseObject, block);
                return HttpResponseStatus.OK;
            }
        }
        return HttpResponseStatus.NOT_FOUND;
    }
}
