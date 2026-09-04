package ruiseki.integrateddynamics.network;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetwork;
import ruiseki.integrateddynamics.api.network.IPositionedNetworkElement;
import ruiseki.integrateddynamics.core.network.NetworkElementBase;
import ruiseki.integrateddynamics.tileentity.TileCoalGenerator;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.InventoryHelpers;
import ruiseki.okcore.helper.TileHelpers;
import ruiseki.okcore.item.capability.CapabilityItemHandler;

/**
 * Network element for coal generators.
 *
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class CoalGeneratorNetworkElement extends NetworkElementBase implements IPositionedNetworkElement {

    private final DimPos pos;

    protected TileCoalGenerator getTile() {
        return TileHelpers.getSafeTile(getPos().getWorld(), getPos().getBlockPos(), TileCoalGenerator.class);
    }

    @Override
    public void addDrops(List<ItemStack> itemStacks, boolean dropMainElement, boolean saveState) {
        CapabilityHelpers.getCapability(getTile(), CapabilityItemHandler.ITEM_HANDLER, ForgeDirection.UNKNOWN)
            .ifPresent(handler -> {
                if (!getTile().getWorldObj().isRemote) return;
                InventoryHelpers.dropItems(getPos().getWorld(), handler, getPos().getBlockPos());
                InventoryHelpers.clearInventory(handler);
            });
    }

    @Override
    public void setPriorityAndChannel(INetwork network, int priority, int channel) {

    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public int getChannel() {
        return IPositionedAddonsNetwork.DEFAULT_CHANNEL;
    }

    @Override
    public boolean canRevalidate(INetwork network) {
        return canRevalidatePositioned(network, pos);
    }

    @Override
    public void revalidate(INetwork network) {
        super.revalidate(network);
        revalidatePositioned(network, pos);
    }

    @Override
    public int compareTo(INetworkElement o) {
        if (o instanceof CoalGeneratorNetworkElement) {
            return getPos().compareTo(((CoalGeneratorNetworkElement) o).getPos());
        }
        return this.getClass()
            .getCanonicalName()
            .compareTo(
                o.getClass()
                    .getCanonicalName());
    }

    @Override
    public DimPos getPosition() {
        return this.pos;
    }
}
