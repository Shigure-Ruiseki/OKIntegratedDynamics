package ruiseki.integratedterminals.inventory.container;

import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

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
import ruiseki.okcore.helper.InventoryHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.inventory.IGuiContainerProvider;

/**
 * @author rubensworks
 */
public class ContainerTerminalStorageItem extends ContainerTerminalStorageBase<Integer> {

    // Based on ItemInventoryContainer

    private final int itemIndex;

    public ContainerTerminalStorageItem(EntityPlayer player, int itemIndex, InitTabData initTabData) {
        super(
            player,
            ((IGuiContainerProvider) ItemTerminalStoragePortableConfig._instance.getInstance()),
            initTabData,
            Optional.ofNullable(getNetworkFromItem(InventoryHelpers.getItemFromIndex(player, itemIndex))),
            getVariableInventoryFromItem(InventoryHelpers.getItemFromIndex(player, itemIndex)));
        this.itemIndex = itemIndex;
    }

    public ContainerTerminalStorageItem(EntityPlayer player, int itemIndex) {
        this(player, itemIndex, null);
    }

    public static INetwork getNetworkFromItem(ItemStack itemStack) {
        if (MinecraftHelpers.isClientSide()) {
            return null;
        }
        int groupId = ItemTerminalStoragePortable.getGroupId(itemStack);
        if (groupId < 0) {
            return null;
        }
        for (PartPos pos : PartTypeConnectorOmniDirectional.LOADED_GROUPS.getPositions(groupId)) {
            INetwork network = NetworkHelpers.getNetwork(pos);
            if (network != null) {
                return network;
            }
        }
        return null;
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
