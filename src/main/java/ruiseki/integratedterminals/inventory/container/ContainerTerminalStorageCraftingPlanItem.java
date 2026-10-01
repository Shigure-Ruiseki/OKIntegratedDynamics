package ruiseki.integratedterminals.inventory.container;

import java.io.IOException;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.inventory.ItemLocation;
import ruiseki.okcore.network.ExtendedBuffer;

/**
 * @author rubensworks
 */
public class ContainerTerminalStorageCraftingPlanItem extends ContainerTerminalStorageCraftingPlanBase<ItemLocation> {

    // Based on ItemInventoryContainer

    private final ItemLocation itemLocation;

    public ContainerTerminalStorageCraftingPlanItem(int id, InventoryPlayer playerInventory,
        ExtendedBuffer packetBuffer) throws IOException {
        this(
            id,
            playerInventory,
            ItemLocation.readFromPacketBuffer(packetBuffer),
            CraftingOptionGuiData.readFromPacketBuffer(packetBuffer));
    }

    public ContainerTerminalStorageCraftingPlanItem(int id, InventoryPlayer playerInventory, ItemLocation itemLocation,
        CraftingOptionGuiData craftingOptionGuiData) {
        this(
            ContainerTerminalStorageCraftingPlanItemConfig._instance.getInstance(),
            id,
            playerInventory,
            itemLocation,
            craftingOptionGuiData);
    }

    public ContainerTerminalStorageCraftingPlanItem(@Nullable ContainerType<?> type, int id,
        InventoryPlayer playerInventory, ItemLocation itemLocation, CraftingOptionGuiData craftingOptionGuiData) {
        super(type, id, playerInventory, craftingOptionGuiData);
        this.itemLocation = itemLocation;
    }

    public ItemStack getItemStack(EntityPlayer player) {
        return itemLocation.getItemStack(player);
    }

    @Override
    public Optional<INetwork> getNetwork() {
        return ContainerTerminalStorageItem.getNetworkFromItem(getItemStack(player));
    }
}
