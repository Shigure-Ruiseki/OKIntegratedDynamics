package ruiseki.integratedterminals.inventory.container;

import java.io.IOException;

import net.minecraft.entity.player.InventoryPlayer;

import org.jetbrains.annotations.Nullable;

import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.network.ExtendedBuffer;

/**
 * @author rubensworks
 */
public class ContainerTerminalStorageCraftingOptionAmountItem
    extends ContainerTerminalStorageCraftingOptionAmountBase<Integer> {

    // Based on ItemInventoryContainer

    private final int location;

    public ContainerTerminalStorageCraftingOptionAmountItem(InventoryPlayer playerInventory,
        ExtendedBuffer packetBuffer) throws IOException {
        this(playerInventory, packetBuffer.readInt(), CraftingOptionGuiData.readFromPacketBuffer(packetBuffer));
    }

    public ContainerTerminalStorageCraftingOptionAmountItem(InventoryPlayer playerInventory, int location,
        CraftingOptionGuiData craftingOptionGuiData) {
        this(
            ContainerTerminalStorageCraftingOptionAmountItemConfig._instance.getInstance(),
            playerInventory,
            location,
            craftingOptionGuiData);
    }

    public ContainerTerminalStorageCraftingOptionAmountItem(@Nullable ContainerType<?> type,
        InventoryPlayer playerInventory, int location, CraftingOptionGuiData craftingOptionGuiData) {
        super(type, playerInventory, craftingOptionGuiData);
        this.location = location;
    }

}
