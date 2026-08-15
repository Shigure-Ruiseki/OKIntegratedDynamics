package ruiseki.integrateddynamics.core.inventory.container.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.item.ItemVariable;
import ruiseki.okcore.inventory.slot.SlotSingleItem;

/**
 * Slot for a variable item.
 *
 * @author rubensworks
 */
public class SlotVariable extends SlotSingleItem {

    public static ResourceLocation VARIABLE_EMPTY = new ResourceLocation(
        Reference.MOD_ID,
        "textures/slot/variable_empty.png");

    /**
     * Make a new instance.
     *
     * @param inventory The inventory this slot will be in.
     * @param index     The index of this slot.
     * @param x         X coordinate.
     * @param y         Y coordinate.
     */
    public SlotVariable(IInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y, ItemVariable.getInstance());
        setBackgroundTexture(SlotVariable.VARIABLE_EMPTY);
    }
}
