package ruiseki.integratedrest.inventory.container;

import java.util.Objects;
import java.util.Optional;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.inventory.container.ContainerActiveVariableBase;
import ruiseki.integrateddynamics.core.inventory.container.slot.SlotVariable;
import ruiseki.integratedrest.tileentity.TileHttp;
import ruiseki.okcore.helper.ValueNotifierHelpers;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.inventory.slot.SlotRemoveOnly;

public class ContainerHttp extends ContainerActiveVariableBase<TileHttp> {

    private final int valueTypeId;

    public ContainerHttp(InventoryPlayer playerInventory) {
        this(playerInventory, new SimpleInventory(TileHttp.INVENTORY_SIZE), null);
    }

    public ContainerHttp(InventoryPlayer playerInventory, IInventory inventory, TileHttp tileSupplier) {
        super(ContainerHttpConfig._instance.getInstance(), playerInventory, inventory, tileSupplier);
        addSlotToContainer(new SlotVariable(inventory, TileHttp.SLOT_WRITE_IN, 56, 63));
        addSlotToContainer(new SlotRemoveOnly(inventory, TileHttp.SLOT_WRITE_OUT, 104, 63));
        addPlayerInventory(playerInventory, offsetX + 9, offsetY + 92);

        valueTypeId = getNextValueId();
        tile.setLastPlayer(playerInventory.player);
    }

    @Override
    protected void initializeValues() {
        ValueNotifierHelpers.setValue(
            this,
            getValueTypeId(),
            getTile().get()
                .getValueType()
                .getUniqueName()
                .toString());
    }

    public int getValueTypeId() {
        return valueTypeId;
    }

    public Optional<IValueType> getValueType() {
        String id = ValueNotifierHelpers.getValueString(this, getValueTypeId());
        return id == null ? Optional.empty()
            : Optional.of(Objects.requireNonNull(ValueTypes.REGISTRY.getValueType(new ResourceLocation(id)), id));
    }

    @Override
    public void onUpdate(int valueId, NBTTagCompound value) {
        super.onUpdate(valueId, value);
        if (getTile() != null) {
            if (valueId == getValueTypeId()) {
                getValueType().ifPresent(
                    vt -> getTile().get()
                        .setValueType(vt));
            }
        }
    }
}
