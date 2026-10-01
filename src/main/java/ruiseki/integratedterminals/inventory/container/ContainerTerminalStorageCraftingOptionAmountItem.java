package ruiseki.integratedterminals.inventory.container;

import java.io.IOException;

import net.minecraft.entity.player.InventoryPlayer;

import org.jetbrains.annotations.Nullable;

import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.inventory.ItemLocation;
import ruiseki.okcore.network.ExtendedBuffer;

/**
 * @author rubensworks
 */
public class ContainerTerminalStorageCraftingOptionAmountItem
    extends ContainerTerminalStorageCraftingOptionAmountBase<Integer> {

    // Based on ItemInventoryContainer

    private final ItemLocation itemLocation;

    public ContainerTerminalStorageCraftingOptionAmountItem(int id, InventoryPlayer playerInventory,
        ExtendedBuffer packetBuffer) throws IOException {
        this(
            id,
            playerInventory,
            ItemLocation.readFromPacketBuffer(packetBuffer),
            CraftingOptionGuiData.readFromPacketBuffer(packetBuffer));
    }

    public ContainerTerminalStorageCraftingOptionAmountItem(int id, InventoryPlayer playerInventory,
        ItemLocation location, CraftingOptionGuiData craftingOptionGuiData) {
        this(
            ContainerTerminalStorageCraftingOptionAmountItemConfig._instance.getInstance(),
            id,
            playerInventory,
            location,
            craftingOptionGuiData);
    }

    public ContainerTerminalStorageCraftingOptionAmountItem(@Nullable ContainerType<?> type, int id,
        InventoryPlayer playerInventory, ItemLocation itemLocation, CraftingOptionGuiData craftingOptionGuiData) {
        super(type, id, playerInventory, craftingOptionGuiData);
        this.itemLocation = itemLocation;
    }

}
