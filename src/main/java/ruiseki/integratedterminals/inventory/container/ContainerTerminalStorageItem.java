package ruiseki.integratedterminals.inventory.container;

import java.io.IOException;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.part.PartTypeConnectorOmniDirectional;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabCommon;
import ruiseki.integratedterminals.api.terminalstorage.location.ITerminalStorageLocation;
import ruiseki.integratedterminals.core.terminalstorage.location.TerminalStorageLocations;
import ruiseki.integratedterminals.item.ItemTerminalStoragePortable;
import ruiseki.integratedterminals.item.ItemTerminalStoragePortableConfig;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.helper.InventoryHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.network.ExtendedBuffer;

/**
 * @author rubensworks
 */
public class ContainerTerminalStorageItem extends ContainerTerminalStorageBase<Integer> {

    // Based on ItemInventoryContainer

    private final int itemIndex;

    public ContainerTerminalStorageItem(InventoryPlayer playerInventory, ExtendedBuffer packetBuffer)
        throws IOException {
        this(
            playerInventory,
            packetBuffer.readInt(),
            packetBuffer.readBoolean() ? Optional.of(InitTabData.readFromPacketBuffer(packetBuffer)) : Optional.empty(),
            TerminalStorageState.readFromPacketBuffer(packetBuffer));
        getGuiState().setDirtyMarkListener(this::sendGuiStateToServer);
    }

    public ContainerTerminalStorageItem(InventoryPlayer playerInventory, int location,
        Optional<InitTabData> initTabData, TerminalStorageState terminalStorageState) {
        this(
            ContainerTerminalStorageItemConfig._instance.getInstance(),
            playerInventory,
            location,
            initTabData,
            terminalStorageState);
    }

    public ContainerTerminalStorageItem(@Nullable ContainerType<?> type, InventoryPlayer playerInventory,
        int itemLocation, Optional<InitTabData> initTabData, TerminalStorageState terminalStorageState) {
        super(
            type,
            playerInventory,
            initTabData,
            terminalStorageState,
            getNetworkFromItem(InventoryHelpers.getItemFromIndex(playerInventory.player, itemLocation)),
            getVariableInventoryFromItem(InventoryHelpers.getItemFromIndex(playerInventory.player, itemLocation)));
        this.itemIndex = itemLocation;
    }

    public static Optional<INetwork> getNetworkFromItem(ItemStack itemStack) {
        if (MinecraftHelpers.isClientSide()) {
            return Optional.empty();
        }
        int groupId = ItemTerminalStoragePortable.getGroupId(itemStack);
        if (groupId < 0) {
            return Optional.empty();
        }
        for (PartPos pos : PartTypeConnectorOmniDirectional.LOADED_GROUPS.getPositions(groupId)) {
            LazyOptional<INetwork> network = NetworkHelpers.getNetwork(pos);
            if (network.isPresent()) {
                return network.map(a -> a);
            }
        }
        return Optional.empty();
    }

    public static Optional<ITerminalStorageTabCommon.IVariableInventory> getVariableInventoryFromItem(
        ItemStack itemStack) {
        return Optional.ofNullable(ItemTerminalStoragePortable.getVariableInventory(itemStack));
    }

    public ItemStack getItemStack(EntityPlayer player) {
        return InventoryHelpers.getItemFromIndex(player, itemIndex);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        ItemStack item = getItemStack(player);
        return item != null && item.getItem() == ItemTerminalStoragePortableConfig._instance.getInstance();
    }

    @Override
    public ITerminalStorageLocation<Integer> getLocation() {
        return TerminalStorageLocations.ITEM;
    }

    @Override
    public Integer getLocationInstance() {
        return itemIndex;
    }

    @Override
    public void onVariableContentsUpdated(INetwork network, IVariable<?> variable) {
        // We don't have a real part, so don't emit anything here
    }

    @Override
    protected Slot createNewSlot(IInventory inventory, int index, int x, int y) {
        return new Slot(inventory, index, x, y) {

            @Override
            public boolean canTakeStack(EntityPlayer playerIn) {
                return super.canTakeStack(playerIn) && itemIndex != index;
            }
        };
    }
}
