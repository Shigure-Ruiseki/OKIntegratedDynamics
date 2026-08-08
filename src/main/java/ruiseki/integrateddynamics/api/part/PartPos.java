package ruiseki.integrateddynamics.api.part;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;

import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;

/**
 * Object holder to refer to a block side and position.
 *
 * @author rubensworks
 */
public class PartPos {

    private final DimPos pos;
    private final ForgeDirection side;

    public static PartPos of(World world, BlockPos pos, ForgeDirection side) {
        return of(DimPos.of(world, pos), side);
    }

    public static PartPos of(DimPos pos, ForgeDirection side) {
        return new PartPos(pos, side);
    }

    private PartPos(DimPos pos, ForgeDirection side) {
        this.pos = pos;
        this.side = side;
    }

    public DimPos getPos() {
        return pos;
    }

    public ForgeDirection getSide() {
        return side;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof PartPos)) return false;

        PartPos partPos = (PartPos) o;

        if (!pos.equals(partPos.pos)) return false;
        return side == partPos.side;

    }

    @Override
    public int hashCode() {
        int result = pos.hashCode();
        if (side != null) {
            result = 31 * result + side.hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        return "PartPos{" + "pos=" + pos + ", side=" + side + '}';
    }

    /**
     * Get part data from the given position.
     *
     * @param pos The part position.
     * @return A pair of part type and part state or null if not found.
     */
    public static Pair<IPartType, IPartState> getPartData(PartPos pos) {
        IPartContainer partContainer = PartHelpers.getPartContainer(pos.getPos());
        if (partContainer != null) {
            IPartType partType = partContainer.getPart(pos.getSide());
            IPartState partState = partContainer.getPartState(pos.getSide());
            if (partType != null && partState != null) {
                return Pair.of(partType, partState);
            }
        }
        return null;
    }

}
