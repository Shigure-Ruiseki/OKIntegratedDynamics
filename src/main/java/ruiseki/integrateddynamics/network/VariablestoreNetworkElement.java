package ruiseki.integrateddynamics.network;

import ruiseki.integrateddynamics.api.network.IChanneledNetwork;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.network.TileNetworkElement;
import ruiseki.integrateddynamics.tileentity.TileVariablestore;
import ruiseki.okcore.datastructure.DimPos;

/**
 * Network element for variable stores.
 *
 * @author rubensworks
 */
public class VariablestoreNetworkElement extends TileNetworkElement<TileVariablestore> {

    public VariablestoreNetworkElement(DimPos pos) {
        super(pos);
    }

    @Override
    public boolean onNetworkAddition(INetwork network) {
        return NetworkHelpers.getPartNetwork(network)
            .addVariableContainer(getPos());
    }

    @Override
    public void onNetworkRemoval(INetwork network) {
        NetworkHelpers.getPartNetwork(network)
            .removeVariableContainer(getPos());
    }

    @Override
    public void setPriority(INetwork network, int priority) {

    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void setChannel(INetwork network, int channel) {

    }

    @Override
    public int getChannel() {
        return IChanneledNetwork.DEFAULT_CHANNEL;
    }

    @Override
    public int getConsumptionRate() {
        return 4;
    }

    @Override
    protected Class<TileVariablestore> getTileClass() {
        return TileVariablestore.class;
    }
}
