package ruiseki.integrateddynamics.core.tileentity;

import net.minecraft.nbt.NBTTagCompound;

import lombok.Getter;
import lombok.experimental.Delegate;
import ruiseki.integrateddynamics.api.block.cable.ICable;
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
import ruiseki.okcore.tileentity.TileEntityOK;

/**
 * A part entity whose block can connect with cables.
 *
 * @author rubensworks
 */
public class TileCableConnectable extends TileEntityOK implements TileEntityOK.ITickingTile {

    @Delegate
    protected final ITickingTile tickingTileComponent = new TickingTileComponent(this);

    @NBTPersist
    private EnumFacingMap<Boolean> connected = EnumFacingMap.newMap();

    @Getter
    private final ICable cable;
    private final INetworkCarrier networkCarrier;

    public TileCableConnectable() {
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
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        connected.clear();
    }

    @Override
    protected void updateTileEntity() {
        super.updateTileEntity();
        if (connected.isEmpty()) {
            cable.updateConnections();
        }
        if (getWorldObj() != null && !getWorldObj().isRemote) {
            NetworkHelpers.revalidateNetworkElements(getWorldObj(), getPos());
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        if (getWorldObj() != null && !getWorldObj().isRemote) {
            NetworkHelpers.invalidateNetworkElements(getWorldObj(), getPos(), this);
        }
    }
}
