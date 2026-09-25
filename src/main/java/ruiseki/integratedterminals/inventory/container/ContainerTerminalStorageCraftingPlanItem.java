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
import ruiseki.okcore.helper.InventoryHelpers;
import ruiseki.okcore.network.ExtendedBuffer;

/**
 * @author rubensworks
 */
public class ContainerTerminalStorageCraftingPlanItem extends ContainerTerminalStorageCraftingPlanBase<Integer> {

    // Based on ItemInventoryContainer

    private final int itemIndex;

    public ContainerTerminalStorageCraftingPlanItem(InventoryPlayer playerInventory, ExtendedBuffer packetBuffer)
        throws IOException {
        this(playerInventory, packetBuffer.readInt(), CraftingOptionGuiData.readFromPacketBuffer(packetBuffer));
    }

    public ContainerTerminalStorageCraftingPlanItem(InventoryPlayer playerInventory, int itemLocation,
        CraftingOptionGuiData craftingOptionGuiData) {
        this(
            ContainerTerminalStorageCraftingPlanItemConfig._instance.getInstance(),
            playerInventory,
            itemLocation,
            craftingOptionGuiData);
    }

    public ContainerTerminalStorageCraftingPlanItem(@Nullable ContainerType<?> type, InventoryPlayer playerInventory,
        int itemLocation, CraftingOptionGuiData craftingOptionGuiData) {
        super(type, playerInventory, craftingOptionGuiData);
        this.itemIndex = itemLocation;
    }

    public ItemStack getItemStack(EntityPlayer player) {
        return InventoryHelpers.getItemFromIndex(player, itemIndex);
    }

    @Override
    public Optional<INetwork> getNetwork() {
        return ContainerTerminalStorageItem.getNetworkFromItem(getItemStack(player));
    }
}
