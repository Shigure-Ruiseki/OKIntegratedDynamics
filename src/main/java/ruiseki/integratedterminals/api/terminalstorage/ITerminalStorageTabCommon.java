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

        public void loadNamedInventory(String name, IInventory inventory);

        public void saveNamedInventory(String name, IInventory inventory);

        @Nullable
        public List<ItemStack> getNamedInventory(String name);

        public void setNamedInventory(String name, List<ItemStack> inventory);
    }

}
