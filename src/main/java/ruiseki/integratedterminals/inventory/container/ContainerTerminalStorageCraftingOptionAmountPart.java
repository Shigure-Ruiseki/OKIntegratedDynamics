package ruiseki.integratedterminals.inventory.container;

import net.minecraft.entity.player.EntityPlayer;

import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.integratedterminals.part.PartTypeTerminalStorage;

/**
 * @author rubensworks
 */
public class ContainerTerminalStorageCraftingOptionAmountPart
    extends ContainerTerminalStorageCraftingOptionAmountBase<PartPos> {

    private final PartTarget target;
    private final IPartContainer partContainer;
    private final PartTypeTerminalStorage partType;

    public ContainerTerminalStorageCraftingOptionAmountPart(EntityPlayer player, PartTarget target,
        IPartContainer partContainer, IPartType partType, CraftingOptionGuiData craftingOptionGuiData) {
        super(player, craftingOptionGuiData);
        this.target = target;
        this.partContainer = partContainer;
        this.partType = (PartTypeTerminalStorage) partType;
    }
}
