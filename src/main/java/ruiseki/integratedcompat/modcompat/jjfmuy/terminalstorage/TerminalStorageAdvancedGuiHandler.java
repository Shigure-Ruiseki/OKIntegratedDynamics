package ruiseki.integratedcompat.modcompat.jjfmuy.terminalstorage;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabClient;
import ruiseki.integratedterminals.client.gui.container.GuiTerminalStorage;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentClient;
import ruiseki.jfmuy.api.gui.IAdvancedGuiHandler;

/**
 * This handler allows JEI to recognise the terminal storage slot contents.
 * 
 * @author rubensworks
 */
public class TerminalStorageAdvancedGuiHandler implements IAdvancedGuiHandler<GuiTerminalStorage> {

    @Override
    public Class<GuiTerminalStorage> getGuiContainerClass() {
        return GuiTerminalStorage.class;
    }

    @Nullable
    @Override
    public Object getIngredientUnderMouse(GuiTerminalStorage guiContainer, int mouseX, int mouseY) {
        int slotIndex = guiContainer.getStorageSlotIndexAtPosition(mouseX, mouseY);
        if (slotIndex >= 0) {
            Optional<ITerminalStorageTabClient<?>> tabOptional = guiContainer.getSelectedClientTab();
            if (tabOptional.isPresent()) {
                ITerminalStorageTabClient<?> tab = tabOptional.get();
                if (tab instanceof TerminalStorageTabIngredientComponentClient<?, ?>) {
                    return ((TerminalStorageTabIngredientComponentClient) tab)
                        .getSlotInstance(
                            guiContainer.getContainer()
                                .getSelectedChannel(),
                            slotIndex)
                        .orElse(null);
                }
            }
        }
        return null;
    }
}
