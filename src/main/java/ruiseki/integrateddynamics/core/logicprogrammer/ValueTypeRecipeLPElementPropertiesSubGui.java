package ruiseki.integrateddynamics.core.logicprogrammer;

import net.minecraft.item.ItemStack;

import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammerBase;
import ruiseki.integrateddynamics.core.ingredient.ItemMatchProperties;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;

/**
 * @author rubensworks
 */
public class ValueTypeRecipeLPElementPropertiesSubGui
    extends ValueTypeRecipeAdapterLPElementPropertiesSubGui<ValueTypeRecipeLPElement> {

    public ValueTypeRecipeLPElementPropertiesSubGui(ValueTypeRecipeLPElement element, int baseX, int baseY,
        int maxWidth, int maxHeight, GuiLogicProgrammerBase gui, ContainerLogicProgrammerBase container, int slotId) {
        super(element, baseX, baseY, maxWidth, maxHeight, gui, container, slotId);
    }

    @Override
    protected void returnToMainGui() {
        element.lastGui.setRecipeSubGui();
    }

    @Override
    public ItemStack getSlotContents() {
        return container.inventorySlots.get(slotId + ValueTypeRecipeLPElement.SLOT_OFFSET)
            .getStack();
    }

    @Override
    public ItemMatchProperties getSlotProperties() {
        return getElement().getInputStacks()
            .get(slotId);
    }

    @Override
    public void saveGuiToState() {
        super.saveGuiToState();
        element.sendSlotPropertiesToServer(slotId, getSlotProperties());
    }
}
