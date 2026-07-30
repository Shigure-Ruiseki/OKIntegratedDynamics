package ruiseki.integrateddynamics.network;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.network.IEventListenableNetworkElement;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.core.network.TileNetworkElement;
import ruiseki.integrateddynamics.tileentity.TileProxy;
import ruiseki.okcore.datastructure.DimPos;

/**
 * Network element for coal generators.
 *
 * @author rubensworks
 */
public class ProxyNetworkElement extends TileNetworkElement<TileProxy>
    implements IEventListenableNetworkElement<IPartNetwork, TileProxy> {

    public ProxyNetworkElement(DimPos pos) {
        super(pos);
    }

    protected int getId() {
        return getTile().getProxyId();
    }

    @Override
    public boolean onNetworkAddition(IPartNetwork network) {
        if (super.onNetworkAddition(network)) {
            if (!network.addProxy(getId(), getPos())) {
                IntegratedDynamics.clog(
                    Level.WARN,
                    "A proxy already existed in the network, this is possibly a " + "result from item duplication.");
                getTile().generateNewProxyId();
                return network.addProxy(getId(), getPos());
            }
            return true;
        }
        return false;
    }

    @Override
    public void onNetworkRemoval(IPartNetwork network) {
        super.onNetworkRemoval(network);
        network.removeProxy(getId());
    }

    @Override
    public int getConsumptionRate() {
        return 2;
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
