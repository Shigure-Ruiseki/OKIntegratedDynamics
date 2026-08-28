package ruiseki.integrateddynamics.network;

import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.api.network.IEventListenableNetworkElement;
import ruiseki.integrateddynamics.api.network.IIdentifiableNetworkElement;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetwork;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.network.TileNetworkElement;
import ruiseki.integrateddynamics.tileentity.TileProxy;
import ruiseki.okcore.datastructure.DimPos;

/**
 * Network element for coal generators.
 *
 * @author rubensworks
 */
public class ProxyNetworkElement extends TileNetworkElement<TileProxy>
    implements IEventListenableNetworkElement<TileProxy>, IIdentifiableNetworkElement {

    public static final ResourceLocation GROUP = new ResourceLocation(Reference.MOD_ID, "proxy");

    public ProxyNetworkElement(DimPos pos) {
        super(pos);
    }

    @Override
    public int getId() {
        return getTile().getProxyId();
    }

    @Override
    public ResourceLocation getGroup() {
        return ProxyNetworkElement.GROUP;
    }

    @Override
    public boolean onNetworkAddition(INetwork network) {
        if (super.onNetworkAddition(network)) {
            return NetworkHelpers.getPartNetwork(network)
                .map(partNetwork -> {
                    if (!partNetwork.addProxy(getId(), getPos())) {
                        IntegratedDynamics.clog(
                            Level.WARN,
                            "A proxy already existed in the network, this is possibly a "
                                + "result from item duplication.");
                        getTile().generateNewProxyId();
                        return partNetwork.addProxy(getId(), getPos());
                    }
                    return true;
                })
                .orElse(false);
        }
        return false;
    }

    @Override
    public void onNetworkRemoval(INetwork network) {
        super.onNetworkRemoval(network);
        NetworkHelpers.getPartNetwork(network)
            .ifPresent(partNetwork -> partNetwork.removeProxy(getId()));
    }

    @Override
    public void setPriorityAndChannel(INetwork network, int priority, int channel) {

    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public int getChannel() {
        return IPositionedAddonsNetwork.DEFAULT_CHANNEL;
    }

    @Override
    public int getConsumptionRate() {
        return GeneralConfig.proxyBaseConsumption;
    }

    @Nullable
    @Override
    public TileProxy getNetworkEventListener() {
        return getTile();
    }

    @Override
    protected Class<TileProxy> getTileClass() {
        return TileProxy.class;
    }
}
