package ruiseki.integratednbt.network;

import ruiseki.integrateddynamics.api.network.IEventListenableNetworkElement;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetwork;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.network.TileNetworkElement;
import ruiseki.integratednbt.GeneralConfig;
import ruiseki.integratednbt.tileentity.TileNbtExtractor;
import ruiseki.okcore.datastructure.DimPos;

/**
 * @author rubensworks
 */
public class NbtExtractorNetworkElement extends TileNetworkElement<TileNbtExtractor>
    implements IEventListenableNetworkElement<TileNbtExtractor> {

    public NbtExtractorNetworkElement(DimPos pos) {
        super(pos);
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
        return GeneralConfig.nbtExtractorBaseConsumption;
    }

    @Override
    public TileNbtExtractor getNetworkEventListener() {
        return getTile();
    }

    @Override
    protected Class<TileNbtExtractor> getTileClass() {
        return TileNbtExtractor.class;
    }

    @Override
    public boolean onNetworkAddition(INetwork network) {
        TileNbtExtractor blockEntity = getTile();
        if (blockEntity == null || !blockEntity.hasWorldObj()) {
            return false;
        }
        return NetworkHelpers.getPartNetwork(network)
            .map(
                partNetwork -> partNetwork
                    .addVariableContainer(DimPos.of(blockEntity.getWorldObj(), blockEntity.getPos())))
            .orElse(false);
    }

    @Override
    public void onNetworkRemoval(INetwork network) {
        TileNbtExtractor blockEntity = getTile();
        if (blockEntity == null || !blockEntity.hasWorldObj()) {
            return;
        }
        NetworkHelpers.getPartNetwork(network)
            .ifPresent(
                partNetwork -> partNetwork
                    .removeVariableContainer(DimPos.of(blockEntity.getWorldObj(), blockEntity.getPos())));
    }
}
