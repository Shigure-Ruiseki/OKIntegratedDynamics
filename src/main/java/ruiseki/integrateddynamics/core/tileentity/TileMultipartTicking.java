package ruiseki.integrateddynamics.core.tileentity;

import java.util.Map;
import java.util.Objects;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import ruiseki.integrateddynamics.api.block.cable.ICableFakeable;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkCarrier;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.capability.cable.CableConfig;
import ruiseki.integrateddynamics.capability.cable.CableFakeableConfig;
import ruiseki.integrateddynamics.capability.cable.CableFakeableMultipartTicking;
import ruiseki.integrateddynamics.capability.cable.CableTileMultipartTicking;
import ruiseki.integrateddynamics.capability.dynamiclight.DynamicLightConfig;
import ruiseki.integrateddynamics.capability.dynamiclight.DynamicLightTileMultipartTicking;
import ruiseki.integrateddynamics.capability.dynamicredstone.DynamicRedstoneConfig;
import ruiseki.integrateddynamics.capability.dynamicredstone.DynamicRedstoneTileMultipartTicking;
import ruiseki.integrateddynamics.capability.facadeable.FacadeableConfig;
import ruiseki.integrateddynamics.capability.facadeable.FacadeableTileMultipartTicking;
import ruiseki.integrateddynamics.capability.network.NetworkCarrierConfig;
import ruiseki.integrateddynamics.capability.network.NetworkCarrierDefault;
import ruiseki.integrateddynamics.capability.networkelementprovider.NetworkElementProviderConfig;
import ruiseki.integrateddynamics.capability.networkelementprovider.NetworkElementProviderPartContainer;
import ruiseki.integrateddynamics.capability.partcontainer.PartContainerConfig;
import ruiseki.integrateddynamics.capability.partcontainer.PartContainerTileMultipartTicking;
import ruiseki.integrateddynamics.capability.path.PathElementConfig;
import ruiseki.integrateddynamics.capability.path.PathElementTileMultipartTicking;
import ruiseki.integrateddynamics.core.helper.CableHelpers;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.resolver.BasicCapabilityResolver;
import ruiseki.okcore.capabilities.resolver.SidedCapabilityResolver;
import ruiseki.okcore.datastructure.DimPos;
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
    implements TileEntityOK.ITickingTile, PartHelpers.IPartStateHolderCallback {

    private final EnumFacingMap<PartHelpers.PartStateHolder<?, ?>> partData = EnumFacingMap.newMap();
    @Delegate
    protected final ITickingTile tickingTileComponent = new TickingTileComponent(this);

    @Getter
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
    private EnumFacingMap<Boolean> redstoneStrong = EnumFacingMap.newMap();
    @Getter
    @NBTPersist
    private EnumFacingMap<Integer> lastRedstonePulses = EnumFacingMap.newMap();
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
    private final PartContainerTileMultipartTicking partContainer;
    @Getter
    private final CableTileMultipartTicking cable;
    @Getter
    private final INetworkCarrier networkCarrier;
    @Getter
    private final ICableFakeable cableFakeable;

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
        cable = new CableTileMultipartTicking(this);
        this.capabilityCache.addCapabilityResolver(BasicCapabilityResolver.create(CableConfig.CAPABILITY, () -> cable));
        networkCarrier = new NetworkCarrierDefault();
        this.capabilityCache.addCapabilityResolver(
            BasicCapabilityResolver.create(NetworkCarrierConfig.CAPABILITY, () -> networkCarrier));
        cableFakeable = new CableFakeableMultipartTicking(this);
        this.capabilityCache
            .addCapabilityResolver(BasicCapabilityResolver.create(CableFakeableConfig.CAPABILITY, () -> cableFakeable));
        this.capabilityCache.addCapabilityResolver(
            BasicCapabilityResolver
                .create(PathElementConfig.CAPABILITY, () -> new PathElementTileMultipartTicking(this, cable)));
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
        tag.setBoolean("realCable", cableFakeable.isRealCable());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        EnumFacingMap<Boolean> lastConnected = EnumFacingMap.newMap(connected);
        String lastFacadeBlockName = facadeBlockName;
        int lastFacadeMeta = facadeMeta;
        boolean lastRealCable = cableFakeable.isRealCable();
        PartHelpers.readPartsFromNBT(getNetwork(), getPos(), tag, this.partData, getWorldObj());
        if (tag.hasKey("parts", MinecraftHelpers.NBTTag_Types.NBTTagList.ordinal())
            && !tag.hasKey("partContainer", MinecraftHelpers.NBTTag_Types.NBTTagCompound.ordinal())) {
            // Backwards compatibility with old part saving.
            // TODO: remove in next major MC update.
            PartHelpers.readPartsFromNBT(getNetwork(), getPos(), tag, partContainer.getPartData(), getWorldObj());
        } else {
            partContainer.deserializeNBT(tag.getCompoundTag("partContainer"));
        }
        boolean wasLightTransparent = getWorldObj() != null
            && CableHelpers.isLightTransparent(getWorldObj(), getPos(), null);

        super.readFromNBT(tag);
        cableFakeable.setRealCable(tag.getBoolean("realCable"));
        boolean isLightTransparent = getWorldObj() != null
            && CableHelpers.isLightTransparent(getWorldObj(), getPos(), null);
        if (getWorldObj() != null && (lastConnected == null || connected == null
            || !lastConnected.equals(connected)
            || !Objects.equals(lastFacadeBlockName, facadeBlockName)
            || lastFacadeMeta != facadeMeta
            || lastRealCable != cableFakeable.isRealCable()
            || wasLightTransparent != isLightTransparent)) {
            getWorldObj().markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public void onUpdateReceived() {
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        if (!lightLevels.equals(previousLightLevels)) {
            previousLightLevels = lightLevels;
            this.worldObj.func_147451_t(this.xCoord, this.yCoord, this.zCoord);
        }

    }

    @Override
    protected void updateTileEntity() {
        super.updateTileEntity();
        if (connected.isEmpty()) {
            cable.updateConnections();
        }
        partContainer.update();

        // Revalidate network if that hasn't happened yet
        if (getNetwork() == null && getWorldObj() != null && !getWorldObj().isRemote) {
            NetworkHelpers.revalidateNetworkElements(getWorldObj(), getPos());
        }
    }

    public INetwork getNetwork() {
        return networkCarrier.getNetwork();
    }

    public void updateRedstoneInfo(ForgeDirection side, boolean strongPower) {
        this.markDirty();
        int targetX = xCoord + side.offsetX;
        int targetY = yCoord + side.offsetY;
        int targetZ = zCoord + side.offsetZ;
        if (this.worldObj != null && this.worldObj.blockExists(targetX, targetY, targetZ)) {
            this.worldObj.notifyBlockOfNeighborChange(targetX, targetY, targetZ, getBlockType());
            if (strongPower) {
                // When we are emitting a strong power, also update all neighbours of the target
                this.worldObj.notifyBlockOfNeighborChange(targetX, targetY, targetZ, getBlockType());
            }
        }
    }

    public void updateLightInfo() {
        sendUpdate();
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

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        invalidateParts();
    }

    protected void invalidateParts() {
        if (getWorldObj() != null && !getWorldObj().isRemote) {
            INetwork network = getNetwork();
            if (network != null) {
                for (Map.Entry<ForgeDirection, PartHelpers.PartStateHolder<?, ?>> entry : partContainer.getPartData()
                    .entrySet()) {
                    INetworkElement element = entry.getValue()
                        .getPart()
                        .createNetworkElement(getPartContainer(), DimPos.of(getWorldObj(), getPos()), entry.getKey());
                    element.invalidate(network);
                }
            }
        }
    }
}
