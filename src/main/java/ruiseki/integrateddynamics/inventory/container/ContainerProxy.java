package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

import ruiseki.integrateddynamics.core.inventory.container.ContainerActiveVariableBase;
import ruiseki.integrateddynamics.core.inventory.container.slot.SlotVariable;
import ruiseki.integrateddynamics.tileentity.TileProxy;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.inventory.slot.SlotRemoveOnly;

/**
 * Container for the proxy.
 *
 * @author rubensworks
 */
public class ContainerProxy extends ContainerActiveVariableBase<TileProxy> {

    public ContainerProxy(InventoryPlayer playerInventory) {
        this(playerInventory, new SimpleInventory(TileProxy.INVENTORY_SIZE), null);
    }

    public ContainerProxy(InventoryPlayer playerInventory, IInventory inventory, TileProxy tile) {
        super(ContainerProxyConfig._instance.getInstance(), playerInventory, inventory, tile);
        addSlotToContainer(new SlotVariable(inventory, TileProxy.SLOT_READ, 81, 25));
        addSlotToContainer(new SlotVariable(inventory, TileProxy.SLOT_WRITE_IN, 56, 78));
        addSlotToContainer(new SlotRemoveOnly(inventory, TileProxy.SLOT_WRITE_OUT, 104, 78));
        addPlayerInventory(playerInventory, offsetX + 9, offsetY + 107);
        getTile().ifPresent(t -> t.setLastPlayer(playerInventory.player));
    }

}
