package ruiseki.integrateddynamics.core.tileentity;

import lombok.Getter;
import lombok.experimental.Delegate;
import ruiseki.integrateddynamics.api.block.cable.ICable;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkCarrier;
import ruiseki.integrateddynamics.capability.cable.CableConfig;
import ruiseki.integrateddynamics.capability.cable.CableTile;
import ruiseki.integrateddynamics.capability.network.NetworkCarrierConfig;
import ruiseki.integrateddynamics.capability.network.NetworkCarrierDefault;
import ruiseki.integrateddynamics.capability.path.PathElementConfig;
import ruiseki.integrateddynamics.capability.path.PathElementTile;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.okcore.capabilities.resolver.BasicCapabilityResolver;
import ruiseki.okcore.datastructure.EnumFacingMap;
import ruiseki.okcore.persist.nbt.NBTPersist;
import ruiseki.okcore.tileentity.InventoryTileEntity;
import ruiseki.okcore.tileentity.TileEntityOK;

/**
 * A tile entity with inventory whose block can connect with cables.
 *
 * @author rubensworks
 */
public class TileCableConnectableInventory extends InventoryTileEntity implements TileEntityOK.ITickingTile {

    @Delegate
    protected final ITickingTile tickingTileComponent = new TickingTileComponent(this);

    @NBTPersist
    private EnumFacingMap<Boolean> connected = EnumFacingMap.newMap();

    @Getter
    private final ICable cable;
    private final INetworkCarrier networkCarrier;

    public TileCableConnectableInventory(int inventorySize, String inventoryName, int stackSize) {
        super(inventorySize, inventoryName, stackSize);
        cable = new CableTile<>(this) {

            @Override
            protected boolean isForceDisconnectable() {
                return false;
            }

            @Override
            protected EnumFacingMap<Boolean> getForceDisconnected() {
                return null;
            }

            @Override
            protected EnumFacingMap<Boolean> getConnected() {
                return tile.connected;
            }
        };
        this.capabilityCache.addCapabilityResolver(BasicCapabilityResolver.create(CableConfig.CAPABILITY, () -> cable));
        networkCarrier = new NetworkCarrierDefault();
        this.capabilityCache.addCapabilityResolver(
            BasicCapabilityResolver.create(NetworkCarrierConfig.CAPABILITY, () -> networkCarrier));
        this.capabilityCache.addCapabilityResolver(
            BasicCapabilityResolver.create(PathElementConfig.CAPABILITY, () -> new PathElementTile<>(this, cable)));
    }

    @Override
    protected void updateTileEntity() {
        super.updateTileEntity();
        super.updateTileEntity();
        if (connected.isEmpty()) {
            cable.updateConnections();
        }
        if (getWorldObj() != null && !getWorldObj().isRemote) {
            NetworkHelpers.revalidateNetworkElements(getWorldObj(), getPos());
        }
    }

    /**
     * Called after the network has been fully initialized
     */
    public void afterNetworkReAlive() {

    }

    public INetwork getNetwork() {
        return this.networkCarrier.getNetwork();
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        if (getWorldObj() != null && !getWorldObj().isRemote) {
            NetworkHelpers.invalidateNetworkElements(getWorldObj(), getPos());
        }
    }
}
