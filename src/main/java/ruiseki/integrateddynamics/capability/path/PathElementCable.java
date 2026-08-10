package ruiseki.integrateddynamics.capability.path;

import java.util.Set;

import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Sets;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.block.cable.ICable;
import ruiseki.integrateddynamics.api.path.IPathElement;
import ruiseki.integrateddynamics.api.path.ISidedPathElement;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.CapabilityHelpers;

/**
 * Implementation of {@link IPathElement} for cables.
 *
 * @author rubensworks
 */
public abstract class PathElementCable extends PathElementDefault {

    protected abstract ICable getCable();

    @Override
    public Set<ISidedPathElement> getReachableElements() {
        Set<ISidedPathElement> elements = Sets.newHashSet();
        BlockPos pos = getPosition().getBlockPos();
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            if (getCable().isConnected(side)) {
                BlockPos posOffset = pos.offset(side);
                ForgeDirection pathElementSide = side.getOpposite();
                IPathElement pathElement = CapabilityHelpers
                    .getCapability(getPosition().getWorld(), posOffset, PathElementConfig.CAPABILITY, pathElementSide)
                    .getOrNull();
                if (pathElement == null) {
                    IntegratedDynamics.clog(
                        Level.ERROR,
                        String.format(
                            "The position at %s was incorrectly marked "
                                + "as reachable as path element by %s at %s side %s.",
                            posOffset,
                            getCable(),
                            pos,
                            side));
                } else {
                    elements.add(SidedPathElement.of(pathElement, pathElementSide));
                }
            }
        }
        return elements;
    }
}
