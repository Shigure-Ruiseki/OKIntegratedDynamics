package ruiseki.integratedrest.http.request;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Maps;

import ruiseki.integratedrest.api.http.request.IRequestHandler;
import ruiseki.integratedrest.api.http.request.IRequestHandlerRegistry;

/**
 * Implementation for {@link IRequestHandlerRegistry}.
 * 
 * @author rubensworks
 */
public class RequestHandlerRegistry implements IRequestHandlerRegistry {

    private static RequestHandlerRegistry INSTANCE = new RequestHandlerRegistry();

    private RequestHandlerRegistry() {

    }

    public static RequestHandlerRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<String, IRequestHandler> handlers = Maps.newHashMap();

    @Override
    public void registerHandler(String path, IRequestHandler handler) {
        handlers.put(path, handler);
    }

    @Override
    @Nullable
    public IRequestHandler getHandler(String path) {
        return handlers.get(path);
    }
}
