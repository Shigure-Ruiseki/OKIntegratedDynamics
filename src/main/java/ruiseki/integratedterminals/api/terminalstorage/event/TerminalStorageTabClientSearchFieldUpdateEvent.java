package ruiseki.integratedterminals.api.terminalstorage.event;

import cpw.mods.fml.common.eventhandler.Event;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabClient;

/**
 * An event that is emitted on the Forge event bus when
 * the search field of an {@link ITerminalStorageTabClient} is updated.
 * is called.
 * 
 * @author rubensworks
 */
public class TerminalStorageTabClientSearchFieldUpdateEvent extends Event {

    private final ITerminalStorageTabClient<?> clientTab;

    private String searchString;

    public TerminalStorageTabClientSearchFieldUpdateEvent(ITerminalStorageTabClient<?> clientTab, String searchString) {
        this.clientTab = clientTab;

        this.searchString = searchString;
    }

    public ITerminalStorageTabClient<?> getClientTab() {
        return clientTab;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
}
