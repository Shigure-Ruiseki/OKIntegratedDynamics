package ruiseki.integratedterminals.api.terminalstorage;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

import ruiseki.integratedterminals.part.PartTypeTerminalStorage;

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
        PartTypeTerminalStorage.State partState) {
        return Collections.emptyList();
    }

    public default void onUpdate(Container container, EntityPlayer player, PartTypeTerminalStorage.State partState) {

    }

}
