package ruiseki.integrateddynamics.api.network;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * A network element that exists at a certain side.
 * 
 * @author rubensworks
 */
public interface ISidedNetworkElement extends INetworkElement {

    public ForgeDirection getSide();

}
