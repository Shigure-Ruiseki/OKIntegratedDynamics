package ruiseki.integrateddynamics.network;

import ruiseki.integrateddynamics.api.network.IPartNetwork;
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
    public boolean onNetworkAddition(IPartNetwork network) {
        return network.addVariableContainer(getPos());
    }

    @Override
    public void onNetworkRemoval(IPartNetwork network) {
        network.removeVariableContainer(getPos());
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
