package ruiseki.integratedterminals.inventory.container;

import java.io.IOException;
import java.util.Optional;

import net.minecraft.entity.player.InventoryPlayer;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.integratedterminals.part.PartTypeTerminalStorage;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.network.ExtendedBuffer;

/**
 * @author rubensworks
 */
public class ContainerTerminalStorageCraftingPlanPart extends ContainerTerminalStorageCraftingPlanBase<PartPos> {

    // Based on ContainerMultipart

    private final Optional<PartTarget> target;
    private final Optional<IPartContainer> partContainer;
    private final PartTypeTerminalStorage partType;

    public ContainerTerminalStorageCraftingPlanPart(InventoryPlayer playerInventory, ExtendedBuffer packetBuffer)
        throws IOException {
        this(
            playerInventory,
            Optional.empty(),
            Optional.empty(),
            PartHelpers.readPart(packetBuffer),
            CraftingOptionGuiData.readFromPacketBuffer(packetBuffer));
    }

    public ContainerTerminalStorageCraftingPlanPart(InventoryPlayer playerInventory, Optional<PartTarget> target,
        Optional<IPartContainer> partContainer, PartTypeTerminalStorage partType,
        CraftingOptionGuiData craftingOptionGuiData) {
        this(
            ContainerTerminalStorageCraftingPlanPartConfig._instance.getInstance(),
            playerInventory,
            target,
            partContainer,
            partType,
            craftingOptionGuiData);
    }

    public ContainerTerminalStorageCraftingPlanPart(@Nullable ContainerType<?> type, InventoryPlayer playerInventory,
        Optional<PartTarget> target, Optional<IPartContainer> partContainer, PartTypeTerminalStorage partType,
        CraftingOptionGuiData craftingOptionGuiData) {
        super(type, playerInventory, craftingOptionGuiData);
        this.target = target;
        this.partType = partType;
        this.partContainer = partContainer;
    }

    public Optional<PartTarget> getTarget() {
        return target;
    }

    @Override
    public Optional<INetwork> getNetwork() {
        return NetworkHelpers.getNetwork(
            getTarget().get()
                .getCenter())
            .map(a -> a);
    }
}
