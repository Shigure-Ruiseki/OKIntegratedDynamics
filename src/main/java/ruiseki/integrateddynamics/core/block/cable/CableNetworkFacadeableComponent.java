package ruiseki.integrateddynamics.core.block.cable;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import ruiseki.integrateddynamics.api.block.cable.ICableFacadeable;
import ruiseki.integrateddynamics.api.block.cable.ICableNetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.path.ICablePathElement;
import ruiseki.integrateddynamics.api.tileentity.ITileCableFacadeable;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.TileHelpers;

/**
 * A component for {@link ICableFacadeable}.
 * 
 * @author rubensworks
 */
public class CableNetworkFacadeableComponent<C extends Block & ICableNetwork<IPartNetwork, ICablePathElement>>
    extends CableNetworkComponent<C> implements ICableFacadeable<ICablePathElement> {

    public CableNetworkFacadeableComponent(C cable) {
        super(cable);
    }

    @Override
    public boolean hasFacade(IBlockAccess world, BlockPos pos) {
        ITileCableFacadeable tile = TileHelpers.getSafeTile(world, pos, ITileCableFacadeable.class);
        if (tile != null) {
            return tile.hasFacade();
        }
        return false;
    }

    @Override
    public BlockState getFacade(World world, BlockPos pos) {
        ITileCableFacadeable tile = TileHelpers.getSafeTile(world, pos, ITileCableFacadeable.class);
        if (tile != null) {
            return tile.getFacade();
        }
        return null;
    }

    @Override
    public void setFacade(World world, BlockPos pos, @Nullable BlockState blockState) {
        ITileCableFacadeable tile = TileHelpers.getSafeTile(world, pos, ITileCableFacadeable.class);
        if (tile != null) {
            tile.setFacade(blockState);
        }
    }

}
