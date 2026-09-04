package ruiseki.integratedterminals.api.terminalstorage.event;

import java.util.List;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

import org.apache.commons.lang3.tuple.Pair;

import cpw.mods.fml.common.eventhandler.Event;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabCommon;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;

/**
 * An event that is emitted on the Forge event bus after
 * {@link ITerminalStorageTabCommon#loadSlots(Container, int, EntityPlayer, Optional)}
 * is called.
 *
 * @author rubensworks
 */
public class TerminalStorageTabCommonLoadSlotsEvent extends Event {

    private final ITerminalStorageTabCommon commonTab;
    private final ContainerTerminalStorageBase container;

    private List<Pair<Slot, ITerminalStorageTabCommon.ISlotPositionCallback>> slots;

    public TerminalStorageTabCommonLoadSlotsEvent(ITerminalStorageTabCommon commonTab,
        ContainerTerminalStorageBase container,
        List<Pair<Slot, ITerminalStorageTabCommon.ISlotPositionCallback>> slots) {
        this.commonTab = commonTab;
        this.container = container;

        this.slots = slots;
    }

    public ITerminalStorageTabCommon getCommonTab() {
        return commonTab;
    }

    public ContainerTerminalStorageBase getContainer() {
        return container;
    }

    public List<Pair<Slot, ITerminalStorageTabCommon.ISlotPositionCallback>> getSlots() {
        return slots;
    }

    public void setSlots(List<Pair<Slot, ITerminalStorageTabCommon.ISlotPositionCallback>> slots) {
        this.slots = slots;
    }
}
