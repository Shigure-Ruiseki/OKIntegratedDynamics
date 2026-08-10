package ruiseki.integrateddynamics.api.network;

import ruiseki.okcore.datastructure.DimPos;

/**
 * A network element that exists at a certain position.
 * 
 * @author rubensworks
 */
public interface IPositionedNetworkElement extends INetworkElement {

    public DimPos getPosition();

}
