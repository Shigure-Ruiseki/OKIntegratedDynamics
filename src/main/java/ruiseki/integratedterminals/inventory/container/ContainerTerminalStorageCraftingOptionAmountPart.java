package ruiseki.integratedterminals.inventory.container;

import java.io.IOException;
import java.util.Optional;

import net.minecraft.entity.player.InventoryPlayer;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.integratedterminals.part.PartTypeTerminalStorage;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.network.ExtendedBuffer;

/**
 * @author rubensworks
 */
public class ContainerTerminalStorageCraftingOptionAmountPart
    extends ContainerTerminalStorageCraftingOptionAmountBase<PartPos> {

    private final Optional<PartTarget> target;
    private final Optional<IPartContainer> partContainer;
    private final PartTypeTerminalStorage partType;

    public ContainerTerminalStorageCraftingOptionAmountPart(InventoryPlayer playerInventory,
        ExtendedBuffer packetBuffer) throws IOException {
        this(
            playerInventory,
            Optional.empty(),
            Optional.empty(),
            PartHelpers.readPart(packetBuffer),
            CraftingOptionGuiData.readFromPacketBuffer(packetBuffer));
    }

    public ContainerTerminalStorageCraftingOptionAmountPart(InventoryPlayer playerInventory,
        Optional<PartTarget> target, Optional<IPartContainer> partContainer, PartTypeTerminalStorage partType,
        CraftingOptionGuiData craftingOptionGuiData) {
        this(
            ContainerTerminalStorageCraftingOptionAmountPartConfig._instance.getInstance(),
            playerInventory,
            target,
            partContainer,
            partType,
            craftingOptionGuiData);
    }

    public ContainerTerminalStorageCraftingOptionAmountPart(@Nullable ContainerType<?> type,
        InventoryPlayer playerInventory, Optional<PartTarget> target, Optional<IPartContainer> partContainer,
        PartTypeTerminalStorage partType, CraftingOptionGuiData craftingOptionGuiData) {
        super(type, playerInventory, craftingOptionGuiData);
        this.target = target;
        this.partType = partType;
        this.partContainer = partContainer;
    }
}
