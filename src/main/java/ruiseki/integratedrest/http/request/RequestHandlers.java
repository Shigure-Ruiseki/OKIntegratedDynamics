package ruiseki.integratedrest.http.request;

import ruiseki.integratedrest.IntegratedRest;
import ruiseki.integratedrest.api.http.request.IRequestHandlerRegistry;
import ruiseki.integratedrest.http.request.handler.ElementHttpRequestHandler;
import ruiseki.integratedrest.http.request.handler.ElementPartRequestHandler;
import ruiseki.integratedrest.http.request.handler.ElementRequestHandler;
import ruiseki.integratedrest.http.request.handler.IndexRequestHandler;
import ruiseki.integratedrest.http.request.handler.NetworkRequestHandler;
import ruiseki.integratedrest.http.request.handler.RegistryAspectRequestHandler;
import ruiseki.integratedrest.http.request.handler.RegistryBlockRequestHandler;
import ruiseki.integratedrest.http.request.handler.RegistryFluidRequestHandler;
import ruiseki.integratedrest.http.request.handler.RegistryItemRequestHandler;
import ruiseki.integratedrest.http.request.handler.RegistryModRequestHandler;
import ruiseki.integratedrest.http.request.handler.RegistryPartRequestHandler;
import ruiseki.integratedrest.http.request.handler.RegistryValueRequestHandler;

/**
 * Registration code for request handlers.
 *
 * @author rubensworks
 */
public class RequestHandlers {

    public static IRequestHandlerRegistry REGISTRY = IntegratedRest._instance.getRegistryManager()
        .getRegistry(IRequestHandlerRegistry.class);

    public static void load() {
        REGISTRY.registerHandler("", new IndexRequestHandler());
        REGISTRY.registerHandler("network", new NetworkRequestHandler());
        REGISTRY.registerHandler("networkElement", new ElementRequestHandler());
        REGISTRY.registerHandler("networkElement/integrateddynamics/part", new ElementPartRequestHandler());
        REGISTRY.registerHandler("networkElement/integratedrest/http", new ElementHttpRequestHandler());
        REGISTRY.registerHandler("networkElement/integrateddynamics/http", new ElementHttpRequestHandler());
        REGISTRY.registerHandler("registry/part", new RegistryPartRequestHandler());
        REGISTRY.registerHandler("registry/aspect", new RegistryAspectRequestHandler());
        REGISTRY.registerHandler("registry/value", new RegistryValueRequestHandler());
        REGISTRY.registerHandler("registry/item", new RegistryItemRequestHandler());
        REGISTRY.registerHandler("registry/block", new RegistryBlockRequestHandler());
        REGISTRY.registerHandler("registry/fluid", new RegistryFluidRequestHandler());
        REGISTRY.registerHandler("registry/mod", new RegistryModRequestHandler());
    }

}
