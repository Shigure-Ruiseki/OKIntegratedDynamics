package ruiseki.integratedcrafting.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import ruiseki.integratedcrafting.client.gui.GuiPartInterfaceCrafting;
import ruiseki.integratedcrafting.part.PartTypeInterfaceCrafting;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.client.gui.ExtendedGuiHandler;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.inventory.container.ContainerMultipart;
import ruiseki.integrateddynamics.core.inventory.container.slot.SlotVariable;
import ruiseki.integrateddynamics.item.ItemVariable;
import ruiseki.integrateddynamics.item.ItemVariableConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.inventory.IGuiContainerProvider;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.inventory.container.InventoryContainer;
import ruiseki.okcore.inventory.container.button.IButtonActionServer;

/**
 * Container for the crafting interface.
 *
 * @author rubensworks
 */
public class ContainerPartInterfaceCrafting
    extends ContainerMultipart<PartTypeInterfaceCrafting, PartTypeInterfaceCrafting.State> {

    /**
     * Make a new instance.
     *
     * @param target        The target.
     * @param player        The player.
     * @param partContainer The part container.
     * @param partType      The part type.
     */
    public ContainerPartInterfaceCrafting(EntityPlayer player, PartTarget target, IPartContainer partContainer,
        IPartType partType) {
        super(player, target, partContainer, (PartTypeInterfaceCrafting) partType);

        SimpleInventory inventory = getPartState().getInventoryVariables();
        addInventory(inventory, 0, 8, 22, 1, inventory.getSizeInventory());
        addPlayerInventory(player.inventory, 8, 59);

        getPartState().setLastPlayer(player);
    }

    @Override
    protected Slot createNewSlot(IInventory inventory, int index, int x, int y) {
        if (inventory instanceof SimpleInventory) {
            return new SlotVariable(inventory, index, x, y) {

                @Override
                public boolean isItemValid(ItemStack itemStack) {
                    IVariableFacade variableFacade = ((ItemVariable) ItemVariableConfig._instance.getInstance())
                        .getVariableFacade(itemStack);
                    return variableFacade != null
                        && ValueHelpers.correspondsTo(variableFacade.getOutputType(), ValueTypes.OBJECT_RECIPE)
                        && super.isItemValid(itemStack);
                }
            };
        }
        return super.createNewSlot(inventory, index, x, y);
    }

    @Override
    protected int getSizeInventory() {
        return getPartState().getInventoryVariables()
            .getSizeInventory();
    }

    @Override
    public void onDirty() {

    }
}
