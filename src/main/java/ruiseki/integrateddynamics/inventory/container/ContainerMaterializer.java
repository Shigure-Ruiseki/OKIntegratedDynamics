package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

import ruiseki.integrateddynamics.core.inventory.container.ContainerActiveVariableBase;
import ruiseki.integrateddynamics.core.inventory.container.slot.SlotVariable;
import ruiseki.integrateddynamics.tileentity.TileMaterializer;
import ruiseki.integrateddynamics.tileentity.TileProxy;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.inventory.slot.SlotRemoveOnly;

/**
 * Container for the materializer.
 *
 * @author rubensworks
 */
public class ContainerMaterializer extends ContainerActiveVariableBase<TileMaterializer> {

    public ContainerMaterializer(InventoryPlayer playerInventory) {
        this(playerInventory, new SimpleInventory(TileMaterializer.INVENTORY_SIZE), null);
    }

    public ContainerMaterializer(InventoryPlayer inventoryPlayer, IInventory inventory, TileMaterializer tile) {
        super(ContainerMaterializerConfig._instance.getInstance(), inventoryPlayer, inventory, tile);
        addSlotToContainer(new SlotVariable(tile, TileProxy.SLOT_READ, 81, 25));
        addSlotToContainer(new SlotVariable(tile, TileProxy.SLOT_WRITE_IN, 56, 78));
        addSlotToContainer(new SlotRemoveOnly(tile, TileProxy.SLOT_WRITE_OUT, 104, 78));
        addPlayerInventory(inventoryPlayer, offsetX + 9, offsetY + 107);
        getTile().ifPresent(t -> t.setLastPlayer(inventoryPlayer.player));
    }

}
