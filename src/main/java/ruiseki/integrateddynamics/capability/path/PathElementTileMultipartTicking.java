package ruiseki.integrateddynamics.capability.path;

import java.util.Set;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integrateddynamics.api.block.cable.ICable;
import ruiseki.integrateddynamics.api.path.IPathElement;
import ruiseki.integrateddynamics.api.path.ISidedPathElement;
import ruiseki.integrateddynamics.core.tileentity.TileMultipartTicking;

/**
 * Implementation of {@link IPathElement} for {@link TileMultipartTicking}.
 *
 * @author rubensworks
 */
public class PathElementTileMultipartTicking extends PathElementTile<TileMultipartTicking> {

    public PathElementTileMultipartTicking(TileMultipartTicking tile, ICable cable) {
        super(tile, cable);
    }

    @Override
    public Set<ISidedPathElement> getReachableElements() {
        // Add the reachable path elements from the parts that provide one.
        Set<ISidedPathElement> pathElements = super.getReachableElements();
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            if (getTile().getPartContainer()
                .getCapability(PathElementConfig.CAPABILITY, side)
                .isPresent()) {
                pathElements.addAll(
                    getTile().getPartContainer()
                        .getCapability(PathElementConfig.CAPABILITY, side)
                        .getOrNull()
                        .getReachableElements());
            }
        }
        return pathElements;
    }
}
