package ruiseki.integratedterminals.api.terminalstorage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.datastructure.NonNullList;

/**
 * A common-side terminal storage tab for loading slots.
 *
 * @author rubensworks
 */
public interface ITerminalStorageTabCommon {

    /**
     * @return The unique tab name, as inherited from {@link ITerminalStorageTab#getName()}.
     */
    public ResourceLocation getName();

    public default List<Slot> loadSlots(Container container, int startIndex, EntityPlayer player,
        Optional<IVariableInventory> variableInventory) {
        return Collections.emptyList();
    }

    public default void onUpdate(Container container, EntityPlayer player,
        Optional<IVariableInventory> variableInventory) {

    }

    public static interface IVariableInventory {

        public default void loadNamedInventory(String name, IInventory inventory) {
            List<ItemStack> tabItems = this.getNamedInventory(name);
            if (tabItems != null) {
                for (int i = 0; i < tabItems.size(); i++) {
                    inventory.setInventorySlotContents(i, tabItems.get(i));
                }
            }
        }

        public default void saveNamedInventory(String name, IInventory inventory) {
            NonNullList<ItemStack> latestItems = NonNullList.create();
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                latestItems.add(inventory.getStackInSlot(i));
            }
            this.setNamedInventory(name, latestItems);
        }

        @Nullable
        public List<ItemStack> getNamedInventory(String name);

        public void setNamedInventory(String name, List<ItemStack> inventory);
    }

}
