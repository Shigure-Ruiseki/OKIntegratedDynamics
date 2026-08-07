package ruiseki.integrateddynamics.capability.path;

import java.util.Set;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Sets;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.block.cable.ICable;
import ruiseki.integrateddynamics.api.path.IPathElement;
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
    public Set<IPathElement> getReachableElements() {
        Set<IPathElement> elements = Sets.newHashSet();
        World world = getPosition().getWorld();
        BlockPos pos = getPosition().getBlockPos();
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            if (getCable().isConnected(side)) {
                BlockPos posOffset = pos.offset(side);
                IPathElement pathElement = CapabilityHelpers
                    .getCapability(world, posOffset, PathElementConfig.CAPABILITY)
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
                    elements.add(pathElement);
                }
            }
        }
        return elements;
    }
}
