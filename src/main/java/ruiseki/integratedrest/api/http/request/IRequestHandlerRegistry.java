package ruiseki.integratedrest.api.http.request;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.init.IRegistry;

/**
 * Registry for {@link IRequestHandler}s.
 *
 * @author rubensworks
 */
public interface IRequestHandlerRegistry extends IRegistry {

    /**
     * Register the given handler for the given path.
     *
     * @param path    A path element.
     * @param handler A request handler.
     */
    public void registerHandler(String path, IRequestHandler handler);

    /**
     * Get a request handler for the given path.
     *
     * @param path A path element.
     * @return A request handler, or null if not found.
     */
    @Nullable
    public IRequestHandler getHandler(String path);

}
