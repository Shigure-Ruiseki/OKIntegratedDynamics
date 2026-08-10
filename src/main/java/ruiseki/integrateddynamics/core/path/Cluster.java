package ruiseki.integrateddynamics.core.path;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Sets;

import cpw.mods.fml.common.FMLCommonHandler;
import lombok.Data;
import lombok.experimental.Delegate;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.path.IPathElement;
import ruiseki.integrateddynamics.api.path.ISidedPathElement;
import ruiseki.integrateddynamics.capability.path.PathElementConfig;
import ruiseki.integrateddynamics.capability.path.SidedPathElement;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.persist.nbt.INBTSerializable;

/**
 * A cluster for a collection of path elements.
 *
 * @author rubensworks
 */
@Data
public class Cluster implements Collection<ISidedPathElement>, INBTSerializable {

    @Delegate
    private final Set<ISidedPathElement> elements;

    /**
     * This constructor should not be called, except for the process of constructing networks from NBT.
     */
    public Cluster() {
        this.elements = Sets.newTreeSet();
    }

    public Cluster(TreeSet<ISidedPathElement> elements) {
        this.elements = elements;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList list = new NBTTagList();

        for (ISidedPathElement e : elements) {
            NBTTagCompound elementTag = new NBTTagCompound();
            elementTag.setInteger(
                "dimension",
                e.getPathElement()
                    .getPosition()
                    .getDimensionId());
            elementTag.setLong(
                "pos",
                e.getPathElement()
                    .getPosition()
                    .getBlockPos()
                    .toLong());
            if (e.getSide() != null) {
                elementTag.setInteger(
                    "side",
                    e.getSide()
                        .ordinal());
            }
            list.appendTag(elementTag);
        }

        tag.setTag("list", list);
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        NBTTagList list = tag.getTagList("list", MinecraftHelpers.NBTTag_Types.NBTTagCompound.ordinal());

        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound elementTag = list.getCompoundTagAt(i);
            int dimensionId = elementTag.getInteger("dimension");
            BlockPos pos = BlockPos.fromLong(elementTag.getLong("pos"));
            ForgeDirection side = null;
            if (elementTag.hasKey("side", Constants.NBT.TAG_INT)) {
                side = ForgeDirection.VALID_DIRECTIONS[elementTag.getInteger("side")];
            }

            if (!net.minecraftforge.common.DimensionManager.isDimensionRegistered(dimensionId)) {
                IntegratedDynamics.clog(
                    Level.WARN,
                    String.format(
                        "Skipped loading part from a network at the " + "invalid dimension id %s.",
                        dimensionId));
            } else {
                World world = FMLCommonHandler.instance()
                    .getMinecraftServerInstance()
                    .worldServerForDimension(dimensionId);
                IPathElement pathElement = CapabilityHelpers
                    .getCapability(world, pos, PathElementConfig.CAPABILITY, side)
                    .getOrNull();
                if (pathElement == null) {
                    IntegratedDynamics.clog(
                        Level.WARN,
                        String.format(
                            "Skipped loading part from a network at "
                                + "position %s in world %s because it has no valid path element.",
                            pos,
                            dimensionId));
                } else {
                    elements.add(SidedPathElement.of(pathElement, side));
                }
            }
        }
    }
}
