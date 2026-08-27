package ruiseki.integratedterminals.api.terminalstorage.event;

import java.util.List;

import cpw.mods.fml.common.eventhandler.Event;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalButton;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabClient;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;

/**
 * An event that is emitted on the Forge event bus when
 * a {@link ITerminalStorageTabClient} is constructed and populates its buttons.
 * is called.
 *
 * @author rubensworks
 */
public class TerminalStorageTabClientLoadButtonsEvent extends Event {

    private final ContainerTerminalStorageBase container;
    private final ITerminalStorageTabClient<?> clientTab;

    private List<ITerminalButton<?, ?, ?>> buttons;

    public TerminalStorageTabClientLoadButtonsEvent(ContainerTerminalStorageBase container,
        ITerminalStorageTabClient<?> clientTab, List<ITerminalButton<?, ?, ?>> buttons) {
        this.container = container;
        this.clientTab = clientTab;

        this.buttons = buttons;
    }

    public ContainerTerminalStorageBase getContainer() {
        return container;
    }

    public ITerminalStorageTabClient<?> getClientTab() {
        return clientTab;
    }

    public List<ITerminalButton<?, ?, ?>> getButtons() {
        return buttons;
    }

    public void setButtons(List<ITerminalButton<?, ?, ?>> buttons) {
        this.buttons = buttons;
    }
}
