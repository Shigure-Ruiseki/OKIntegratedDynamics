package ruiseki.integrateddynamics.core.inventory.container;

import net.minecraft.entity.player.InventoryPlayer;

import org.apache.commons.lang3.tuple.Pair;

import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.tileentity.TileActiveVariableBase;
import ruiseki.okcore.helper.ValueNotifierHelpers;
import ruiseki.okcore.inventory.container.TileInventoryContainerConfigurable;

/**
 * Base container for part entities that can hold variables.
 *
 * @author rubensworks
 */
public class ContainerActiveVariableBase<T extends TileActiveVariableBase<?>>
    extends TileInventoryContainerConfigurable<T> {

    private final int readValueId;
    private final int readColorId;

    /**
     * Make a new instance.
     *
     * @param inventory The player inventory.
     * @param tile      The part.
     */
    public ContainerActiveVariableBase(InventoryPlayer inventory, T tile) {
        super(inventory, tile);
        this.readValueId = getNextValueId();
        this.readColorId = getNextValueId();
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        NetworkHelpers.getPartNetwork(tile.getNetwork())
            .ifPresent(partNetwork -> {
                IVariable variable = tile.getVariable(partNetwork);
                Pair<String, Integer> readValue = ValueHelpers.getSafeReadableValue(variable);
                ValueNotifierHelpers.setValue(this, readValueId, readValue.getLeft());
                ValueNotifierHelpers.setValue(this, readColorId, readValue.getRight());
            });
    }

    public String getReadValue() {
        return ValueNotifierHelpers.getValueString(this, readValueId);
    }

    public int getReadValueColor() {
        return ValueNotifierHelpers.getValueInt(this, readColorId);
    }

}
