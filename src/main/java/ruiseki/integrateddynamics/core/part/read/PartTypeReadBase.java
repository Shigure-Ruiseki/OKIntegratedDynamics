package ruiseki.integrateddynamics.core.part.read;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import com.google.common.collect.Sets;

import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartRenderPosition;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.AspectUpdateType;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.api.part.aspect.IAspectRead;
import ruiseki.integrateddynamics.api.part.aspect.IAspectVariable;
import ruiseki.integrateddynamics.api.part.read.IPartStateReader;
import ruiseki.integrateddynamics.api.part.read.IPartTypeReader;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.core.part.PartTypeAspects;
import ruiseki.integrateddynamics.core.part.PartTypeBase;
import ruiseki.integrateddynamics.inventory.container.ContainerPartReader;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.inventory.IGuiConstructor;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.inventory.container.ContainerExtended;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.network.PacketCodec;

/**
 * An abstract {@link IPartTypeReader}.
 *
 * @author rubensworks
 */
public abstract class PartTypeReadBase<P extends IPartTypeReader<P, S>, S extends IPartStateReader<P>>
    extends PartTypeAspects<P, S> implements IPartTypeReader<P, S> {

    private List<IAspectRead> aspectsRead = null;
    private EnumMap<AspectUpdateType, Set<IAspectRead>> updateAspects = null;

    public PartTypeReadBase(String name) {
        this(name, new PartRenderPosition(0.1875F, 0.3125F, 0.625F, 0.625F));
    }

    public PartTypeReadBase(String name, PartRenderPosition partRenderPosition) {
        super(name, partRenderPosition);
    }

    protected Set<IAspectRead> getUpdateAspects(AspectUpdateType updateType) {
        if (updateAspects == null) {
            updateAspects = new EnumMap<>(AspectUpdateType.class);
            for (AspectUpdateType aspectUpdateType : AspectUpdateType.values()) {
                updateAspects.put(aspectUpdateType, Sets.newLinkedHashSet());
            }
            for (IAspect aspect : getAspects()) {
                if (aspect instanceof IAspectRead) {
                    IAspectRead aspectRead = (IAspectRead) aspect;
                    updateAspects.get(aspectRead.getUpdateType())
                        .add(aspectRead);
                }
            }
        }

        return updateAspects.get(updateType);
    }

    @Override
    public boolean isSolid(S state) {
        return true;
    }

    @Override
    public Class<? super P> getPartTypeClass() {
        return IPartTypeReader.class;
    }

    @Override
    public void update(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        super.update(network, partNetwork, target, state);
        for (IAspect aspect : getUpdateAspects(AspectUpdateType.NETWORK_TICK)) {
            aspect.update(network, partNetwork, this, target, state);
        }

        // Special case: if we have an offset, also update block-update-based aspects, because we can't rely on just
        // block updates.
        if (!this.getTargetOffset(state)
            .equals(new Vector3i(0, 0, 0))) {
            for (IAspect aspect : getUpdateAspects(AspectUpdateType.BLOCK_UPDATE)) {
                aspect.update(network, partNetwork, this, target, state);
            }
        }
    }

    @Override
    public void onBlockNeighborChange(INetwork network, IPartNetwork partNetwork, PartTarget target, S state,
        IBlockAccess world, Block neighbourBlock, BlockPos neighbourBlockPos) {
        super.onBlockNeighborChange(network, partNetwork, target, state, world, neighbourBlock, neighbourBlockPos);
        for (IAspect aspect : getUpdateAspects(AspectUpdateType.BLOCK_UPDATE)) {
            aspect.update(network, partNetwork, this, target, state);
        }
    }

    @Override
    public List<IAspectRead> getReadAspects() {
        if (aspectsRead == null) {
            aspectsRead = Aspects.REGISTRY.getReadAspects(this);
        }
        return aspectsRead;
    }

    @Override
    public <V extends IValue, T extends IValueType<V>> IAspectVariable<V> getVariable(PartTarget target, S partState,
        IAspectRead<V, T> aspect) {
        IAspectVariable<V> variable = partState.getVariable(aspect);
        if (variable == null) {
            if (!getAspects().contains(aspect)) {
                throw new IllegalArgumentException(
                    String.format(
                        "Tried to get the variable for the aspect %s that did not exist within the " + "part type %s.",
                        aspect.getUniqueName(),
                        this));
            }
            variable = aspect.createNewVariable(() -> getTarget(target.getCenter(), partState));
            partState.setVariable(aspect, variable);
        }
        return variable;
    }

    @Override
    public boolean setTargetOffset(S state, PartPos center, Vector3i offset) {
        Vector3i lastOffset = getTargetOffset(state);
        boolean ret = super.setTargetOffset(state, center, offset);
        if (!lastOffset.equals(offset)) {
            state.resetVariables();
        }
        return ret;
    }

    @Override
    public void setTargetSideOverride(S state, @Nullable ForgeDirection side) {
        ForgeDirection lastSide = getTargetSideOverride(state);
        super.setTargetSideOverride(state, side);
        if (lastSide != side) {
            state.resetVariables();
        }
    }

    @Override
    public Optional<IGuiConstructor> getContainerProvider(PartPos pos) {
        return Optional.of(new IGuiConstructor() {

            @Override
            public @Nullable ContainerExtended createContainer(int windowId, InventoryPlayer playerInventory,
                EntityPlayer player) {
                Triple<IPartContainer, PartTypeBase, PartTarget> data = PartHelpers
                    .getContainerPartConstructionData(pos);
                return new ContainerPartReader<>(
                    playerInventory,
                    new SimpleInventory(0),
                    data.getRight(),
                    Optional.of(data.getLeft()),
                    (PartTypeReadBase) data.getMiddle());
            }
        });
    }

    @Override
    public void writeExtraGuiData(ExtendedBuffer packetBuffer, PartPos pos, EntityPlayerMP player) {
        // Write part position
        try {
            PacketCodec.getAction(PartPos.class)
                .encode(pos, packetBuffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        super.writeExtraGuiData(packetBuffer, pos, player);
    }

}
