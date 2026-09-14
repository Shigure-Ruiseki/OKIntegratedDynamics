package ruiseki.integratedrest.inventory.container;

import java.util.Objects;
import java.util.Optional;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.inventory.container.ContainerActiveVariableBase;
import ruiseki.integrateddynamics.core.inventory.container.slot.SlotVariable;
import ruiseki.integratedrest.tileentity.TileHttp;
import ruiseki.okcore.helper.ValueNotifierHelpers;
import ruiseki.okcore.inventory.slot.SlotRemoveOnly;

public class ContainerHttp extends ContainerActiveVariableBase<TileHttp> {

    private final int valueTypeId;

    /**
     * Make a new instance.
     *
     * @param inventory The player inventory.
     * @param tile      The part.
     */
    public ContainerHttp(InventoryPlayer inventory, TileHttp tile) {
        super(inventory, tile);
        addSlotToContainer(new SlotVariable(tile, TileHttp.SLOT_WRITE_IN, 56, 63));
        addSlotToContainer(new SlotRemoveOnly(tile, TileHttp.SLOT_WRITE_OUT, 104, 63));
        addPlayerInventory(inventory, offsetX + 9, offsetY + 92);

        valueTypeId = getNextValueId();
        tile.setLastPlayer(inventory.player);
    }

    @Override
    protected void initializeValues() {
        ValueNotifierHelpers.setValue(
            this,
            getValueTypeId(),
            getTile().getValueType()
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
                getValueType().ifPresent(vt -> getTile().setValueType(vt));
            }
        }
    }
}
