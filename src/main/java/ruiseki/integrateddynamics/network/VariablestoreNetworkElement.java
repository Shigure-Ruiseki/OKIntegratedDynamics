package ruiseki.integrateddynamics.network;

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
    public int getConsumptionRate() {
        return 4;
    }

    @Override
    protected Class<TileVariablestore> getTileClass() {
        return TileVariablestore.class;
    }
}
