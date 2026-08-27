package ruiseki.integratedterminals.core.terminalstorage.slot;

import net.minecraft.client.gui.inventory.GuiContainer;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integratedterminals.api.ingredient.IIngredientComponentTerminalStorageHandler;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageSlot;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabClient;
import ruiseki.integratedterminals.core.client.gui.GuiTerminalStorage;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentClient;

/**
 * An ingredient slot.
 *
 * @param <T> The instance type.
 * @param <M> The matching condition parameter.
 * @author rubensworks
 */
public class TerminalStorageSlotIngredient<T, M> implements ITerminalStorageSlot {

    private final IIngredientComponentTerminalStorageHandler<T, M> ingredientComponentViewHandler;
    private final T instance;

    public TerminalStorageSlotIngredient(
        IIngredientComponentTerminalStorageHandler<T, M> ingredientComponentViewHandler, T instance) {
        this.ingredientComponentViewHandler = ingredientComponentViewHandler;
        this.instance = instance;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawGuiContainerLayer(GuiContainer gui, GuiTerminalStorage.DrawLayer layer, float partialTick, int x,
        int y, int mouseX, int mouseY, ITerminalStorageTabClient tab, int channel, @Nullable String label) {
        long maxQuantity = ((TerminalStorageTabIngredientComponentClient) tab).getMaxQuantity(channel);
        ingredientComponentViewHandler
            .drawInstance(instance, maxQuantity, label, gui, layer, partialTick, x, y, mouseX, mouseY, null);
    }

    public IIngredientComponentTerminalStorageHandler<T, M> getIngredientComponentViewHandler() {
        return ingredientComponentViewHandler;
    }

    public T getInstance() {
        return instance;
    }
}
