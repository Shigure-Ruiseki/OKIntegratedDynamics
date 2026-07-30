package ruiseki.integrateddynamics.api.network.event;

import ruiseki.integrateddynamics.api.network.IEventListenableNetworkElement;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.IPartNetwork;

/**
 * An event bus for {@link IPartNetwork} events where
 * {@link INetworkElement} instances can listen to.
 *
 * @author rubensworks
 */
public interface INetworkEventBus<N extends INetwork<N>> {

    /**
     * Register a network element for the given event type.
     *
     * @param target    The element that will be called once the event bus receives the given event.
     * @param eventType The event type.
     */
    public void register(IEventListenableNetworkElement<N, ?> target, Class<? extends INetworkEvent<N>> eventType);

    /**
     * Unregister a network element for the given event type.
     *
     * @param target    The element that would be called once the event bus receives the given event.
     * @param eventType The event type.
     */
    public void unregister(IEventListenableNetworkElement<N, ?> target, Class<? extends INetworkEvent<N>> eventType);

    /**
     * Unregister all events for the given network element.
     *
     * @param target The element that would be called once the event bus receives events.
     */
    public void unregister(IEventListenableNetworkElement<N, ?> target);

    /**
     * Post the given event to the events bus.
     *
     * @param event The event to post.
     */
    public void post(INetworkEvent<N> event);

    /**
     * Post the given cancelable event to the events bus.
     *
     * @param event The event to post.
     * @return If the event was not canceled.
     */
    public boolean postCancelable(ICancelableNetworkEvent<N> event);

}
