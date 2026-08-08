package ruiseki.integrateddynamics.core.network.diagnostics;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import lombok.Data;
import ruiseki.okcore.datastructure.BlockPos;

/**
 * @author rubensworks
 */
@Data
public class RawPartData implements IRawData {

    private final int dimension;
    private final BlockPos pos;
    private final ForgeDirection side;
    private final String name;
    private final long last20TicksDurationNs;

    @Override
    public String toString() {
        return String.format("%s: %s,%s,%s,%s (%s)", name, pos.getX(), pos.getY(), pos.getZ(), side, dimension);
    }

    public NBTTagCompound toNbt() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("dimension", dimension);
        tag.setLong("pos", pos.toLong());
        tag.setInteger("side", side.ordinal());
        tag.setString("name", name);
        tag.setLong("last20TicksDurationNs", last20TicksDurationNs);
        return tag;
    }

    public static RawPartData fromNbt(NBTTagCompound tag) {
        return new RawPartData(
            tag.getInteger("dimension"),
            BlockPos.fromLong(tag.getLong("pos")),
            ForgeDirection.values()[tag.getInteger("side")],
            tag.getString("name"),
            tag.getLong("last20TicksDurationNs"));
    }

}
