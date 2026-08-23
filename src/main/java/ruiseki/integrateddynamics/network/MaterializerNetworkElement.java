package ruiseki.integrateddynamics.network;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.api.network.IEventListenableNetworkElement;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetwork;
import ruiseki.integrateddynamics.core.network.TileNetworkElement;
import ruiseki.integrateddynamics.tileentity.TileMaterializer;
import ruiseki.okcore.datastructure.DimPos;

/**
 * Network element for materializers.
 * 
 * @author rubensworks
 */
public class MaterializerNetworkElement extends TileNetworkElement<TileMaterializer>
    implements IEventListenableNetworkElement<TileMaterializer> {

    public MaterializerNetworkElement(DimPos pos) {
        super(pos);
    }

    @Nullable
    @Override
    public TileMaterializer getNetworkEventListener() {
        return getTile();
    }

    @Override
    protected Class<TileMaterializer> getTileClass() {
        return TileMaterializer.class;
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
        return GeneralConfig.materializerBaseConsumption;
    }
}
