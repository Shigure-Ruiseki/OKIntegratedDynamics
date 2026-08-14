package ruiseki.integratedterminals.api.terminalstorage.event;

import java.util.List;

import net.minecraft.inventory.Slot;

import cpw.mods.fml.common.eventhandler.Event;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabCommon;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorage;

/**
 * An event that is emitted on the Forge event bus after
 * {@link ITerminalStorageTabCommon#loadSlots(Container, int, EntityPlayer, PartTypeTerminalStorage.State)}
 * is called.
 * 
 * @author rubensworks
 */
public class TerminalStorageTabCommonLoadSlotsEvent extends Event {

    private final ITerminalStorageTabCommon commonTab;
    private final ContainerTerminalStorage container;

    private List<Slot> slots;

    public TerminalStorageTabCommonLoadSlotsEvent(ITerminalStorageTabCommon commonTab,
        ContainerTerminalStorage container, List<Slot> slots) {
        this.commonTab = commonTab;
        this.container = container;

        this.slots = slots;
    }

    public ITerminalStorageTabCommon getCommonTab() {
        return commonTab;
    }

    public ContainerTerminalStorage getContainer() {
        return container;
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public void setSlots(List<Slot> slots) {
        this.slots = slots;
    }
}
