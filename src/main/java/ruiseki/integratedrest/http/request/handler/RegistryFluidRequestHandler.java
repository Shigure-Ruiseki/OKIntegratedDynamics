package ruiseki.integratedrest.http.request.handler;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import ruiseki.integratedrest.api.http.request.IRequestHandler;
import ruiseki.integratedrest.json.JsonUtil;

/**
 * Request handler for registry/fluid requests.
 * 
 * @author rubensworks
 */
public class RegistryFluidRequestHandler implements IRequestHandler {

    @Override
    public HttpResponseStatus handle(String[] path, HttpRequest request, JsonObject responseObject) {
        if (path.length == 0) {
            JsonArray array = new JsonArray();
            for (Fluid fluid : FluidRegistry.getRegisteredFluids()
                .values()) {
                JsonObject fluidObject = new JsonObject();
                JsonUtil.addFluidInfo(fluidObject, fluid);
                array.add(fluidObject);
            }
            responseObject.addProperty("@id", JsonUtil.absolutizePath("registry/fluid"));
            responseObject.add("fluids", array);
            return HttpResponseStatus.OK;
        } else {
            String fluidName = String.join("/", path);
            Fluid fluid = FluidRegistry.getFluid(fluidName);

            if (fluid != null) {
                JsonUtil.addFluidInfo(responseObject, fluid);
                return HttpResponseStatus.OK;
            }
        }
        return HttpResponseStatus.NOT_FOUND;
    }
}
