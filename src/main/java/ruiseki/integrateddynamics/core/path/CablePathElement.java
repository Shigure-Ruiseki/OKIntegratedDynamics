package ruiseki.integrateddynamics.core.path;

import java.util.Set;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Sets;

import lombok.Data;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.block.cable.ICable;
import ruiseki.integrateddynamics.api.path.ICablePathElement;
import ruiseki.integrateddynamics.core.helper.CableHelpers;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;

/**
 * A path element in the form of a cable.
 *
 * @author rubensworks
 */
@Data
public class CablePathElement implements ICablePathElement {

    private final ICable cable;
    private final DimPos position;

    @Override
    public Set<ICablePathElement> getReachableElements() {
        Set<ICablePathElement> elements = Sets.newHashSet();
        World world = getPosition().getWorld();
        BlockPos pos = getPosition().getBlockPos();
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            if (cable.isConnected(world, pos, side)) {
                BlockPos posOffset = pos.offset(side);
                ICable cable = CableHelpers.getInterface(world, posOffset, ICable.class);
                if (cable == null) {
                    IntegratedDynamics.clog(
                        Level.ERROR,
                        String.format(
                            "The position at %s was incorrectly marked " + "as reachable as cable by %s.",
                            pos,
                            getCable()));
                } else {
                    elements.add(((ICable<ICablePathElement>) cable).createPathElement(world, posOffset));
                }
            }
        }
        return elements;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CablePathElement && compareTo((CablePathElement) o) == 0;
    }

    @Override
    public int compareTo(ICablePathElement o) {
        return position.compareTo(o.getPosition());
    }

    @Override
    public int hashCode() {
        return position.hashCode();
    }
}
