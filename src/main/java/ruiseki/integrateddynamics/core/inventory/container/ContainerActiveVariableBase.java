package ruiseki.integrateddynamics.core.inventory.container;

import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

import org.apache.commons.lang3.tuple.Pair;

import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.tileentity.TileActiveVariableBase;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.ValueNotifierHelpers;
import ruiseki.okcore.inventory.container.TileInventoryContainer;

/**
 * Base container for part entities that can hold variables.
 *
 * @author rubensworks
 */
public class ContainerActiveVariableBase<T extends TileActiveVariableBase<?>> extends TileInventoryContainer<T> {

    private final int readValueId;
    private final int readColorId;
    private final int readErrorsId;

    public ContainerActiveVariableBase(ContainerType<?> guiType, InventoryPlayer playerInventory, IInventory inventory,
        T tile) {
        super(guiType, playerInventory, inventory, tile);
        this.readValueId = getNextValueId();
        this.readColorId = getNextValueId();
        this.readErrorsId = getNextValueId();
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        this.getTile()
            .ifPresent(tile -> {
                NetworkHelpers.getPartNetwork(tile.getNetwork())
                    .ifPresent(partNetwork -> {
                        IVariable variable = tile.getVariable(partNetwork);
                        Pair<String, Integer> readValue = ValueHelpers.getSafeReadableValue(variable);
                        ValueNotifierHelpers.setValue(this, readValueId, readValue.getLeft());
                        ValueNotifierHelpers.setValue(this, readColorId, readValue.getRight());
                        ValueNotifierHelpers.setValueUnlocalizedStringList(
                            this,
                            readErrorsId,
                            tile.getEvaluator()
                                .getErrors());
                    });
            });
    }

    public String getReadValue() {
        return ValueNotifierHelpers.getValueString(this, readValueId);
    }

    public int getReadValueColor() {
        return ValueNotifierHelpers.getValueInt(this, readColorId);
    }

    public List<LangHelpers.UnlocalizedString> getReadErrors() {
        return ValueNotifierHelpers.getValueUnlocalizedStringList(this, readErrorsId);
    }
}
