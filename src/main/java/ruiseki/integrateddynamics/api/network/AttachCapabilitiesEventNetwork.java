package ruiseki.integrateddynamics.api.network;

import java.util.List;

import com.google.common.collect.Lists;

import ruiseki.okcore.event.capabilities.AttachCapabilitiesEvent;

/**
 * Event for when an {@link INetwork} is being constructed.
 * Next to capabilities, also {@link IFullNetworkListener}'s can be added to the network.
 * 
 * @author rubensworks
 */
public class AttachCapabilitiesEventNetwork extends AttachCapabilitiesEvent<INetwork> {

    private final List<IFullNetworkListener> fullNetworkListeners;

    public AttachCapabilitiesEventNetwork(INetwork network) {
        super(INetwork.class, network);
        this.fullNetworkListeners = Lists.newArrayList();
    }

    public INetwork getNetwork() {
        return getObject();
    }

    public void addFullNetworkListener(IFullNetworkListener fullNetworkListener) {
        this.fullNetworkListeners.add(fullNetworkListener);
    }

    public List<IFullNetworkListener> getFullNetworkListeners() {
        return fullNetworkListeners;
    }
}
