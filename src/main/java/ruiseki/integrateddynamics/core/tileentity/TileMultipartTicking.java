package ruiseki.integrateddynamics.core.tileentity;

import java.util.Objects;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import ruiseki.integrateddynamics.api.block.cable.ICable;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.tileentity.ITileCableNetwork;
import ruiseki.integrateddynamics.capability.DynamicLightConfig;
import ruiseki.integrateddynamics.capability.DynamicLightTileMultipartTicking;
import ruiseki.integrateddynamics.capability.DynamicRedstoneConfig;
import ruiseki.integrateddynamics.capability.DynamicRedstoneTileMultipartTicking;
import ruiseki.integrateddynamics.capability.FacadeableConfig;
import ruiseki.integrateddynamics.capability.FacadeableTileMultipartTicking;
import ruiseki.integrateddynamics.capability.NetworkElementProviderConfig;
import ruiseki.integrateddynamics.capability.NetworkElementProviderPartContainer;
import ruiseki.integrateddynamics.capability.PartContainerConfig;
import ruiseki.integrateddynamics.capability.PartContainerTileMultipartTicking;
import ruiseki.integrateddynamics.core.block.cable.CableNetworkComponent;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.resolver.BasicCapabilityResolver;
import ruiseki.okcore.capabilities.resolver.SidedCapabilityResolver;
import ruiseki.okcore.datastructure.EnumFacingMap;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.persist.nbt.NBTPersist;
import ruiseki.okcore.tileentity.TileEntityOK;

/**
 * A ticking tile entity which is made up of different parts.
 *
 * @author Ruben Taelman
 */
public class TileMultipartTicking extends TileEntityOK
    implements TileEntityOK.ITickingTile, ITileCableNetwork, PartHelpers.IPartStateHolderCallback {

    private final EnumFacingMap<PartHelpers.PartStateHolder<?, ?>> partData = EnumFacingMap.newMap();
    @Delegate
    protected final ITickingTile tickingTileComponent = new TickingTileComponent(this);

    @NBTPersist
    private boolean realCable = true;
    @NBTPersist
    private EnumFacingMap<Boolean> connected = EnumFacingMap.newMap();
    @NBTPersist
    private EnumFacingMap<Boolean> forceDisconnected = EnumFacingMap.newMap();
    @Getter
    @NBTPersist
    private EnumFacingMap<Integer> redstoneLevels = EnumFacingMap.newMap();
    @Getter
    @NBTPersist
    private EnumFacingMap<Boolean> redstoneInputs = EnumFacingMap.newMap();
    @Getter
    @NBTPersist
    private EnumFacingMap<Integer> lightLevels = EnumFacingMap.newMap();
    private EnumFacingMap<Integer> previousLightLevels;
    @Getter
    @Setter
    @NBTPersist
    private String facadeBlockName = null;
    @Getter
    @Setter
    @NBTPersist
    private int facadeMeta = 0;

    @Getter
    @Setter
    private IPartNetwork network;

    @Getter
    private final PartContainerTileMultipartTicking partContainer;

    public TileMultipartTicking() {
        partContainer = new PartContainerTileMultipartTicking(this);
        this.capabilityCache.addCapabilityResolver(
            BasicCapabilityResolver.create(PartContainerConfig.CAPABILITY, () -> this.partContainer));
        this.capabilityCache.addCapabilityResolver(
            BasicCapabilityResolver.create(
                NetworkElementProviderConfig.CAPABILITY,
                () -> new NetworkElementProviderPartContainer(partContainer)));
        this.capabilityCache.addCapabilityResolver(
            BasicCapabilityResolver
                .create(FacadeableConfig.CAPABILITY, () -> new FacadeableTileMultipartTicking(this)));
        this.capabilityCache.addCapabilityResolver(
            SidedCapabilityResolver
                .create(DynamicLightConfig.CAPABILITY, side -> new DynamicLightTileMultipartTicking(this, side)));
        this.capabilityCache.addCapabilityResolver(
            SidedCapabilityResolver
                .create(DynamicRedstoneConfig.CAPABILITY, side -> new DynamicRedstoneTileMultipartTicking(this, side)));
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("partContainer", partContainer.serializeNBT());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        EnumFacingMap<Boolean> lastConnected = connected;
        String lastFacadeBlockName = facadeBlockName;
        int lastFacadeMeta = facadeMeta;
        boolean lastRealCable = realCable;
        PartHelpers.readPartsFromNBT(getNetwork(), getPos(), tag, this.partData, getWorldObj());
        if (tag.hasKey("parts", MinecraftHelpers.NBTTag_Types.NBTTagList.ordinal())
            && !tag.hasKey("partContainer", MinecraftHelpers.NBTTag_Types.NBTTagCompound.ordinal())) {
            // Backwards compatibility with old part saving.
            // TODO: remove in next major MC update.
            PartHelpers.readPartsFromNBT(getNetwork(), getPos(), tag, partContainer.getPartData(), getWorldObj());
        } else {
            partContainer.deserializeNBT(tag.getCompoundTag("partContainer"));
        }
        super.readFromNBT(tag);
        if (getWorldObj() != null && (lastConnected == null || connected == null
            || !lastConnected.equals(connected)
            || !Objects.equals(lastFacadeBlockName, facadeBlockName)
            || lastFacadeMeta != facadeMeta
            || lastRealCable != realCable)) {
            getWorldObj().markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    /**
     * Indicate that this cable is not a real cable if false and should not allow any connections.
     * Parts can be added to it though.
     *
     * @param realCable If this cable is real and should accept connections.
     */
    public void setRealCable(boolean realCable) {
        this.realCable = realCable;
        sendUpdate();
    }

    /**
     * @return If this cable is real.
     */
    public boolean isRealCable() {
        return this.realCable;
    }

    @Override
    public void onUpdateReceived() {
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        if (!lightLevels.equals(previousLightLevels)) {
            previousLightLevels = lightLevels;
            this.worldObj.func_147451_t(this.xCoord, this.yCoord, this.zCoord);
        }

    }

    public boolean isForceDisconnected(ForgeDirection side) {
        if (!isRealCable() || partContainer.hasPart(side)) return true;
        if (!forceDisconnected.containsKey(side)) return false;
        return forceDisconnected.get(side);
    }

    @Override
    protected void updateTileEntity() {
        super.updateTileEntity();
        // If the connection data were reset, update the cable connections
        if (connected.isEmpty()) {
            updateConnections();
        }

        partContainer.update();
    }

    public void updateRedstoneInfo(ForgeDirection side) {
        sendUpdate();

        int x = getPos().getX();
        int y = getPos().getY();
        int z = getPos().getZ();

        ForgeDirection opposite = side.getOpposite();
        int offsetX = x + opposite.offsetX;
        int offsetY = y + opposite.offsetY;
        int offsetZ = z + opposite.offsetZ;

        getWorldObj().notifyBlocksOfNeighborChange(x, y, z, getBlock());

        getWorldObj().notifyBlocksOfNeighborChange(offsetX, offsetY, offsetZ, getBlock());
    }

    public void updateLightInfo() {
        sendUpdate();
    }

    @Override
    public void resetCurrentNetwork() {
        if (network != null) setNetwork(null);
    }

    @Override
    public boolean canConnect(ICable connector, ForgeDirection side) {
        return !isForceDisconnected(side);
    }

    @Override
    public void updateConnections() {
        World world = getWorldObj();
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            boolean cableConnected = CableNetworkComponent.canSideConnect(world, getPos(), side, (ICable) getBlock());
            connected.put(side, cableConnected);

            // Remove any already existing force-disconnects for this side.
            if (!cableConnected) {
                forceDisconnected.put(side, false);
            }
        }
        markDirty();
        sendUpdate();
    }

    @Override
    public boolean isConnected(ForgeDirection side) {
        return connected.containsKey(side) && connected.get(side);
    }

    @Override
    public void disconnect(ForgeDirection side) {
        forceDisconnected.put(side, true);
    }

    @Override
    public void reconnect(ForgeDirection side) {
        forceDisconnected.remove(side);
    }

    @Override
    public void onSet(PartHelpers.PartStateHolder<?, ?> partStateHolder) {

    }

    /**
     * @return The raw force disconnection data.
     */
    public EnumFacingMap<Boolean> getForceDisconnected() {
        return this.forceDisconnected;
    }

    public void setForceDisconnected(EnumFacingMap<Boolean> forceDisconnected) {
        this.forceDisconnected.clear();
        this.forceDisconnected.putAll(forceDisconnected);
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return true;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability,
        @Nullable ForgeDirection facing) {
        return super.getCapability(capability, facing);
    }
}
