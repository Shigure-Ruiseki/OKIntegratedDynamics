package ruiseki.integrateddynamics.api.part;

import java.io.IOException;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.network.PacketCodec;

/**
 * Object holder to refer to a block side and position.
 *
 * @author rubensworks
 */
public class PartPos implements Comparable<PartPos> {

    static {

        PacketCodec.addCodedAction(PartPos.class, new PacketCodec.ICodecAction() {

            @Override
            public void encode(Object o, ExtendedBuffer extendedBuffer) throws IOException {
                PartPos pos = (PartPos) o;
                PacketCodec.getAction(DimPos.class)
                    .encode(pos.getPos(), extendedBuffer);
                PacketCodec.getAction(ForgeDirection.class)
                    .encode(pos.getSide(), extendedBuffer);
            }

            @Override
            public Object decode(ExtendedBuffer extendedBuffer) {
                DimPos pos = (DimPos) PacketCodec.getAction(DimPos.class)
                    .decode(extendedBuffer);
                ForgeDirection side = (ForgeDirection) PacketCodec.getAction(ForgeDirection.class)
                    .decode(extendedBuffer);
                return PartPos.of(pos, side);
            }
        });
    }

    private final DimPos pos;
    private final ForgeDirection side;

    public static PartPos of(World world, BlockPos pos, @Nullable ForgeDirection side) {
        return of(DimPos.of(world, pos), side);
    }

    public static PartPos of(DimPos pos, @Nullable ForgeDirection side) {
        return new PartPos(pos, side);
    }

    private PartPos(DimPos pos, @Nullable ForgeDirection side) {
        this.pos = pos;
        this.side = side;
    }

    public DimPos getPos() {
        return pos;
    }

    @Nullable
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
        return 31 * pos.hashCode() + (side != null ? side.hashCode() : 0);
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
        IPartContainer partContainer = PartHelpers.getPartContainer(pos.getPos(), pos.getSide())
            .getOrNull();
        if (partContainer != null) {
            IPartType partType = partContainer.getPart(pos.getSide());
            IPartState partState = partContainer.getPartState(pos.getSide());
            if (partType != null && partState != null) {
                return Pair.of(partType, partState);
            }
        }
        return null;
    }

    @Override
    public int compareTo(PartPos o) {
        int pos = this.getPos()
            .compareTo(o.getPos());
        if (pos == 0) {
            ForgeDirection thisSide = this.getSide();
            ForgeDirection thatSide = o.getSide();
            return thisSide == null ? (thatSide == null ? 0 : -1)
                : (thatSide == null ? 1 : thisSide.compareTo(thatSide));
        }
        return pos;
    }
}
