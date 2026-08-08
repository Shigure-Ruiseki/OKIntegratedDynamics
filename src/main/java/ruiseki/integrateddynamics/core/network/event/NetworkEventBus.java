package ruiseki.integrateddynamics.core.network.event;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

import ruiseki.integrateddynamics.api.network.IEventListenableNetworkElement;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.event.ICancelableNetworkEvent;
import ruiseki.integrateddynamics.api.network.event.INetworkEvent;
import ruiseki.integrateddynamics.api.network.event.INetworkEventBus;
import ruiseki.okcore.helper.CollectionHelpers;

/**
 * An event bus for {@link INetwork} events where
 * {@link INetworkElement} instances can listen to.
 *
 * Partially based on Minecraft Forge's {@link EventBus} implementation.
 *
 * @author rubensworks
 */
public class NetworkEventBus implements INetworkEventBus {

    private final Map<Class<? extends INetworkEvent>, Set<IEventListenableNetworkElement<?>>> listeners = Collections
        .synchronizedMap(Maps.<Class<? extends INetworkEvent>, Set<IEventListenableNetworkElement<?>>>newHashMap());

    @Override
    public void register(IEventListenableNetworkElement<?> target, Class<? extends INetworkEvent> eventType) {
        CollectionHelpers.addToMapSet(this.listeners, eventType, target);
    }

    @Override
    public void unregister(IEventListenableNetworkElement<?> target, Class<? extends INetworkEvent> eventType) {
        Set<IEventListenableNetworkElement<?>> listeners = this.listeners.get(eventType);
        if (listeners != null) {
            listeners.remove(target);
        }
    }

    @Override
    public void unregister(IEventListenableNetworkElement<?> target) {
        for (Class<? extends INetworkEvent> eventType : target.getNetworkEventListener()
            .getSubscribedEvents()) {
            unregister(target, eventType);
        }
    }

    @Override
    public void post(INetworkEvent event) {
        Set<IEventListenableNetworkElement<?>> listeners = this.listeners.get(event.getClass());
        if (listeners != null) {
            for (IEventListenableNetworkElement listener : listeners) {
                listener.getNetworkEventListener()
                    .onEvent(event, listener);
            }
        }
    }

    @Override
    public boolean postCancelable(ICancelableNetworkEvent event) {
        post(event);
        return !event.isCanceled();
    }

}
