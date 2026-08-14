package ruiseki.integratedcrafting.core.crafting.processoverride;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

import ruiseki.commoncapabilities.api.ingredient.IMixedIngredients;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.okcore.helper.ItemStackHelpers;

/**
 * A crafting grid itemstack holder (Minecraft 1.7.10 fix for slot indexing).
 *
 * @author rubensworks
 */
public class CraftingGrid extends InventoryCrafting {

    public CraftingGrid(IMixedIngredients ingredients, int rows, int columns) {
        super(new Container() {

            @Override
            public boolean canInteractWith(EntityPlayer playerIn) {
                return false;
            }
        }, rows, columns);

        if (!ingredients.getComponents().contains(IngredientComponent.ITEMSTACK)) {
            throw new IllegalArgumentException(
                "Can only craft with items, missing ITEMSTACK component in: " + ingredients.getComponents());
        }

        List<ItemStack> itemStacks = ingredients.getInstances(IngredientComponent.ITEMSTACK);
        int maxSlots = rows * columns;

        if (itemStacks != null) {
            if (itemStacks.size() > maxSlots) {
                throw new IllegalArgumentException(
                    "Can only craft in a grid with " + maxSlots + " items, while got " + itemStacks.size());
            }

            for (int slotIndex = 0; slotIndex < itemStacks.size(); slotIndex++) {
                ItemStack stack = itemStacks.get(slotIndex);

                if (stack != null && stack.getItem() != null) {
                    ItemStack copyStack = stack.copy();
                    copyStack.stackSize = 1;
                    setInventorySlotContents(slotIndex, copyStack);
                } else {
                    setInventorySlotContents(slotIndex, null);
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CraftingGrid)) {
            return false;
        }
        CraftingGrid other = (CraftingGrid) obj;
        if (this.getSizeInventory() != other.getSizeInventory()) {
            return false;
        }
        for (int i = 0; i < getSizeInventory(); i++) {
            if (!ItemStack.areItemStacksEqual(this.getStackInSlot(i), other.getStackInSlot(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 11 + getSizeInventory();
        for (int i = 0; i < getSizeInventory(); i++) {
            hash = hash << 1;
            hash |= ItemStackHelpers.getItemStackHashCode(getStackInSlot(i));
        }
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.getSizeInventory(); i++) {
            sb.append(this.getStackInSlot(i));
            sb.append(",");
        }
        return sb.toString();
    }
}
