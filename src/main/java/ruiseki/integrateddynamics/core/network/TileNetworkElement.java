package ruiseki.integrateddynamics.core.network;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.core.tileentity.TileCableConnectableInventory;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.InventoryHelpers;
import ruiseki.okcore.helper.TileHelpers;
import ruiseki.okcore.item.capability.CapabilityItemHandler;

/**
 * Network element for part entities.
 *
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = false)
@Data
public abstract class TileNetworkElement<T extends TileCableConnectableInventory> extends ConsumingNetworkElementBase {

    private final DimPos pos;

    protected abstract Class<T> getTileClass();

    protected T getTile() {
        return TileHelpers.getSafeTile(getPos(), getTileClass());
    }

    @Override
    public void addDrops(List<ItemStack> itemStacks, boolean dropMainElement) {
        T tile = getTile();
        TileEntity entity = (TileEntity) (Object) tile;
        World world = getPos().getWorld();
        BlockPos pos = getPos().getBlockPos();
        CapabilityHelpers.getCapability(entity, CapabilityItemHandler.ITEM_HANDLER, ForgeDirection.UNKNOWN)
            .ifPresent(handler -> {
                InventoryHelpers.dropItems(world, handler, pos);
                InventoryHelpers.clearInventory(handler);
            });
    }

    @Override
    public int compareTo(INetworkElement o) {
        if (o instanceof TileNetworkElement) {
            return getPos().compareTo(((TileNetworkElement) o).getPos());
        }
        return this.getClass()
            .getCanonicalName()
            .compareTo(
                o.getClass()
                    .getCanonicalName());
    }

    @Override
    public void afterNetworkReAlive(INetwork network) {
        super.afterNetworkReAlive(network);
        getTile().afterNetworkReAlive();
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
}
