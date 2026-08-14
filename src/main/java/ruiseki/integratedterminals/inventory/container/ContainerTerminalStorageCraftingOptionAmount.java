package ruiseki.integratedterminals.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.integratedterminals.proxy.guiprovider.GuiProviders;
import ruiseki.okcore.inventory.container.ExtendedInventoryContainer;

/**
 * A container for setting the amount for a given crafting option.
 * 
 * @author rubensworks
 */
public class ContainerTerminalStorageCraftingOptionAmount extends ExtendedInventoryContainer {

    private final World world;
    private final PartTarget target;
    private final IPartContainer partContainer;
    private final IPartType partType;
    private final CraftingOptionGuiData craftingOptionGuiData;

    /**
     * Make a new instance.
     * 
     * @param target                The target.
     * @param player                The player.
     * @param partContainer         The part container.
     * @param partType              The part type.
     * @param craftingOptionGuiData The job data.
     */
    public ContainerTerminalStorageCraftingOptionAmount(final EntityPlayer player, PartTarget target,
        IPartContainer partContainer, IPartType partType, CraftingOptionGuiData craftingOptionGuiData) {
        super(player.inventory, GuiProviders.GUI_TERMINAL_STORAGE_CRAFTNG_OPTION_AMOUNT);

        addPlayerInventory(player.inventory, 9, 80);

        this.world = target.getCenter()
            .getPos()
            .getWorld();
        this.target = target;
        this.partContainer = partContainer;
        this.partType = partType;
        this.craftingOptionGuiData = craftingOptionGuiData;
    }

    @Override
    protected int getSizeInventory() {
        return 0;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

}
