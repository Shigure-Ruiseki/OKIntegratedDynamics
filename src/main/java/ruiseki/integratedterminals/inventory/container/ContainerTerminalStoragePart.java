package ruiseki.integratedterminals.inventory.container;

import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.core.part.event.PartVariableDrivenVariableContentsUpdatedEvent;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabCommon;
import ruiseki.integratedterminals.api.terminalstorage.location.ITerminalStorageLocation;
import ruiseki.integratedterminals.core.terminalstorage.location.TerminalStorageLocations;
import ruiseki.integratedterminals.part.PartTypeTerminalStorage;

/**
 * @author rubensworks
 */
public class ContainerTerminalStoragePart extends ContainerTerminalStorageBase<PartPos> {

    private final PartTarget target;
    private final IPartContainer partContainer;
    private final PartTypeTerminalStorage partType;

    public ContainerTerminalStoragePart(EntityPlayer player, PartTarget target, IPartContainer partContainer,
        IPartType partType, InitTabData initTabData) {
        super(
            player,
            (PartTypeTerminalStorage) partType,
            initTabData,
            NetworkHelpers.getNetwork(target.getCenter())
                .map(a -> a),
            Optional
                .ofNullable(
                    partContainer != null && target.getCenter() != null
                        ? (ITerminalStorageTabCommon.IVariableInventory) partContainer.getPartState(
                            target.getCenter()
                                .getSide())
                        : null));
        this.target = target;
        this.partType = (PartTypeTerminalStorage) partType;
        this.partContainer = partContainer;
    }

    public ContainerTerminalStoragePart(EntityPlayer player, PartTarget target, IPartContainer partContainer,
        IPartType partType) {
        this(player, target, partContainer, partType, (ContainerTerminalStorageBase.InitTabData) null);
    }

    public PartTypeTerminalStorage getPartType() {
        return partType;
    }

    public PartTarget getPartTarget() {
        return target;
    }

    public PartTypeTerminalStorage.State getPartState() {
        if (partContainer == null || target == null || target.getCenter() == null) return null;
        return (PartTypeTerminalStorage.State) partContainer.getPartState(
            getPartTarget().getCenter()
                .getSide());
    }

    public IPartContainer getPartContainer() {
        return partContainer;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return PartHelpers.canInteractWith(getPartTarget(), player, this.partContainer);
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
            IPartNetwork partNetwork = NetworkHelpers.getPartNetwork(network)
                .getOrNull();
            MinecraftForge.EVENT_BUS.post(
                new PartVariableDrivenVariableContentsUpdatedEvent<>(
                    network,
                    partNetwork,
                    getPartTarget(),
                    getPartType(),
                    getPartState(),
                    player,
                    variable,
                    variable != null ? variable.getValue() : null));
        } catch (EvaluationException e) {
            // Ignore error
        }
    }
}
