package ruiseki.integrateddynamics.core.network;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.core.tileentity.TileCableConnectableInventory;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.InventoryHelpers;
import ruiseki.okcore.helper.TileHelpers;
import ruiseki.okcore.item.capability.CapabilityItemHandler;

/**
 * Network element for tile entities.
 *
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = false)
@Data
public abstract class TileNetworkElement<T extends TileCableConnectableInventory>
    extends ConsumingNetworkElementBase<IPartNetwork> {

    private final DimPos pos;

    protected abstract Class<T> getTileClass();

    protected T getTile() {
        return TileHelpers.getSafeTile(getPos().getWorld(), getPos().getBlockPos(), getTileClass());
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
        return Integer.compare(hashCode(), o.hashCode());
    }

    @Override
    public void afterNetworkReAlive(IPartNetwork network) {
        super.afterNetworkReAlive(network);
        getTile().afterNetworkReAlive();
    }
}
