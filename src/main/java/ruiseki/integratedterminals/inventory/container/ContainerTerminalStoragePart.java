package ruiseki.integratedterminals.inventory.container;

import java.io.IOException;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.common.MinecraftForge;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.core.inventory.container.ContainerMultipart;
import ruiseki.integrateddynamics.core.part.event.PartVariableDrivenVariableContentsUpdatedEvent;
import ruiseki.integratedterminals.api.terminalstorage.location.ITerminalStorageLocation;
import ruiseki.integratedterminals.core.terminalstorage.location.TerminalStorageLocations;
import ruiseki.integratedterminals.part.PartTypeTerminalStorage;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.network.ExtendedBuffer;

/**
 * @author rubensworks
 */
public class ContainerTerminalStoragePart extends ContainerTerminalStorageBase<PartPos> {

    private final PartTarget target;
    private final Optional<IPartContainer> partContainer;
    private final PartTypeTerminalStorage partType;

    public ContainerTerminalStoragePart(InventoryPlayer playerInventory, ExtendedBuffer packetBuffer)
        throws IOException {
        this(
            playerInventory,
            PartHelpers.readPartTarget(packetBuffer),
            PartHelpers.readPart(packetBuffer),
            packetBuffer.readBoolean() ? Optional.of(InitTabData.readFromPacketBuffer(packetBuffer)) : Optional.empty(),
            TerminalStorageState.readFromPacketBuffer(packetBuffer));
        getGuiState().setDirtyMarkListener(this::sendGuiStateToServer);
    }

    public ContainerTerminalStoragePart(InventoryPlayer playerInventory, PartTarget target,
        PartTypeTerminalStorage partType, Optional<ContainerTerminalStorageBase.InitTabData> initTabData,
        TerminalStorageState terminalStorageState) {
        this(
            ContainerTerminalStoragePartConfig._instance.getInstance(),
            playerInventory,
            target,
            Optional.of(
                PartHelpers.getPartContainer(
                    target.getCenter()
                        .getPos(),
                    target.getCenter()
                        .getSide())
                    .orElseThrow(() -> new IllegalStateException("Could not find part container"))),
            partType,
            initTabData,
            terminalStorageState);
    }

    public ContainerTerminalStoragePart(@Nullable ContainerType<?> type, InventoryPlayer playerInventory,
        PartTarget target, Optional<IPartContainer> partContainer, PartTypeTerminalStorage partType,
        Optional<ContainerTerminalStorageBase.InitTabData> initTabData, TerminalStorageState terminalStorageState) {
        super(
            type,
            playerInventory,
            initTabData,
            terminalStorageState,
            NetworkHelpers.getNetwork(target.getCenter())
                .map(a -> a),
            partContainer.map(
                p -> (PartTypeTerminalStorage.State) p.getPartState(
                    target.getCenter()
                        .getSide())));
        this.target = target;
        this.partType = partType;
        this.partContainer = partContainer;

        putButtonAction(ContainerMultipart.BUTTON_SETTINGS, (s, containerExtended) -> {
            if (!getWorld().isRemote) {
                PartHelpers.openContainerPart((EntityPlayerMP) player, target.getCenter(), partType);
            }
        });
    }

    public PartTypeTerminalStorage getPartType() {
        return partType;
    }

    public PartTarget getPartTarget() {
        return target;
    }

    public Optional<PartTypeTerminalStorage.State> getPartState() {
        return partContainer.map(
            p -> (PartTypeTerminalStorage.State) p.getPartState(
                getPartTarget().getCenter()
                    .getSide()));
    }

    public Optional<IPartContainer> getPartContainer() {
        return partContainer;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return PartHelpers.canInteractWith(getPartTarget(), player, this.partContainer.get());
    }

    @Override
    public ITerminalStorageLocation<PartPos> getLocation() {
        return TerminalStorageLocations.PART;
    }

    @Override
    public PartPos getLocationInstance() {
        return getPartTarget() != null ? getPartTarget().getCenter() : null;
    }

    @Override
    public void onVariableContentsUpdated(INetwork network, IVariable<?> variable) {
        try {
            IPartNetwork partNetwork = NetworkHelpers.getPartNetworkChecked(network);
            MinecraftForge.EVENT_BUS.post(
                new PartVariableDrivenVariableContentsUpdatedEvent<>(
                    network,
                    partNetwork,
                    getPartTarget(),
                    getPartType(),
                    getPartState().get(),
                    player,
                    variable,
                    variable != null ? variable.getValue() : null));
        } catch (EvaluationException e) {
            // Ignore error
        }
    }
}
