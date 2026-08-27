package ruiseki.integratedterminals.inventory.container;

import net.minecraft.entity.player.EntityPlayer;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.integratedterminals.part.PartTypeTerminalStorage;

/**
 * @author rubensworks
 */
public class ContainerTerminalStorageCraftingPlanPart extends ContainerTerminalStorageCraftingPlanBase<PartPos> {

    // Based on ContainerMultipart

    private final PartTarget target;
    private final IPartContainer partContainer;
    private final PartTypeTerminalStorage partType;

    public ContainerTerminalStorageCraftingPlanPart(EntityPlayer player, PartTarget target,
        IPartContainer partContainer, PartTypeTerminalStorage partType, CraftingOptionGuiData craftingOptionGuiData) {
        super(player, partType, craftingOptionGuiData);
        this.target = target;
        this.partType = partType;
        this.partContainer = partContainer;
    }

    public PartTarget getTarget() {
        return target;
    }

    @Override
    public INetwork getNetwork() {
        return NetworkHelpers.getNetwork(getTarget().getCenter());
    }
}
