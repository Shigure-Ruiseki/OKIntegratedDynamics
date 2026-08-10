package ruiseki.integrateddynamics.core.part.write;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.network.event.INetworkEvent;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.PartRenderPosition;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.api.part.aspect.IAspectWrite;
import ruiseki.integrateddynamics.api.part.write.IPartStateWriter;
import ruiseki.integrateddynamics.api.part.write.IPartTypeWriter;
import ruiseki.integrateddynamics.client.gui.GuiPartWriter;
import ruiseki.integrateddynamics.core.block.IgnoredBlock;
import ruiseki.integrateddynamics.core.block.IgnoredBlockStatus;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.network.event.NetworkElementAddEvent;
import ruiseki.integrateddynamics.core.network.event.VariableContentsUpdatedEvent;
import ruiseki.integrateddynamics.core.part.PartTypeAspects;
import ruiseki.integrateddynamics.core.part.event.PartWriterAspectEvent;
import ruiseki.integrateddynamics.inventory.container.ContainerPartWriter;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.helper.BlockStateHelpers;
import ruiseki.okcore.helper.LangHelpers;

/**
 * An abstract {@link IPartTypeWriter}.
 *
 * @author rubensworks
 */
public abstract class PartTypeWriteBase<P extends IPartTypeWriter<P, S>, S extends IPartStateWriter<P>>
    extends PartTypeAspects<P, S> implements IPartTypeWriter<P, S> {

    private List<IAspectWrite> aspectsWrite = null;

    public PartTypeWriteBase(String name) {
        this(name, new PartRenderPosition(0.3125F, 0.3125F, 0.625F, 0.625F, 0.25F, 0.25F));
    }

    public PartTypeWriteBase(String name, PartRenderPosition partRenderPosition) {
        super(name, partRenderPosition);
    }

    @Override
    protected Map<Class<? extends INetworkEvent>, IEventAction> constructNetworkEventActions() {
        Map<Class<? extends INetworkEvent>, IEventAction> actions = super.constructNetworkEventActions();
        actions.put(VariableContentsUpdatedEvent.class, new IEventAction<P, S, VariableContentsUpdatedEvent>() {

            @Override
            public void onAction(INetwork network, PartTarget target, S state, VariableContentsUpdatedEvent event) {
                IPartNetwork partNetwork = NetworkHelpers.getPartNetwork(network);
                onVariableContentsUpdated(partNetwork, target, state);
            }
        });
        actions.put(NetworkElementAddEvent.Post.class, new IEventAction<P, S, NetworkElementAddEvent.Post>() {

            @Override
            public void onAction(INetwork network, PartTarget target, S state, NetworkElementAddEvent.Post event) {
                IPartNetwork partNetwork = NetworkHelpers.getPartNetwork(network);
                onVariableContentsUpdated(partNetwork, target, state);
            }
        });
        return actions;
    }

    @Override
    protected Block createBlock(BlockConfig blockConfig) {
        return new IgnoredBlockStatus(blockConfig);
    }

    @Override
    public Class<? super P> getPartTypeClass() {
        return IPartTypeWriter.class;
    }

    @Override
    public void update(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        super.update(network, partNetwork, target, state);
        IAspect aspect = getActiveAspect(target, state);
        if (aspect != null) {
            aspect.update(partNetwork, this, target, state);
        }
    }

    @Override
    public void addDrops(PartTarget target, S state, List<ItemStack> itemStacks, boolean dropMainElement,
        boolean saveState) {
        for (int i = 0; i < state.getInventory()
            .getSizeInventory(); i++) {
            ItemStack itemStack = state.getInventory()
                .getStackInSlot(i);
            if (itemStack != null) {
                itemStacks.add(itemStack);
            }
        }
        state.getInventory()
            .clear();
        state.triggerAspectInfoUpdate((P) this, target, null);
        super.addDrops(target, state, itemStacks, dropMainElement, saveState);
    }

    @Override
    public void beforeNetworkKill(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        super.beforeNetworkKill(network, partNetwork, target, state);
        state.triggerAspectInfoUpdate((P) this, target, null);
    }

    @Override
    public void afterNetworkAlive(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        super.afterNetworkAlive(network, partNetwork, target, state);
        updateActivation(target, state, null);
    }

    @Override
    public List<IAspectWrite> getWriteAspects() {
        if (aspectsWrite == null) {
            aspectsWrite = Aspects.REGISTRY.getWriteAspects(this);
        }
        return aspectsWrite;
    }

    @Override
    public boolean hasActiveVariable(IPartNetwork network, PartTarget target, S partState) {
        return partState.hasVariable();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V extends IValue> IVariable<V> getActiveVariable(IPartNetwork network, PartTarget target, S partState) {
        return partState.getVariable(network);
    }

    @Override
    public IAspectWrite getActiveAspect(PartTarget target, S partState) {
        return partState.getActiveAspect();
    }

    @Override
    public void updateActivation(PartTarget target, S partState, @Nullable EntityPlayer player) {
        // Check inside the inventory for a variable item and determine everything with that.
        int activeIndex = -1;
        for (int i = 0; i < partState.getInventory()
            .getSizeInventory(); i++) {
            if (partState.getInventory()
                .getStackInSlot(i) != null) {
                activeIndex = i;
                break;
            }
        }
        IAspectWrite aspect = activeIndex == -1 ? null : getWriteAspects().get(activeIndex);
        partState.triggerAspectInfoUpdate((P) this, target, aspect);

        if (aspect != null) {
            INetwork network = NetworkHelpers.getNetwork(
                target.getCenter()
                    .getPos()
                    .getWorld(),
                target.getCenter()
                    .getPos()
                    .getBlockPos());
            IPartNetwork partNetwork = NetworkHelpers.getPartNetwork(network);
            MinecraftForge.EVENT_BUS.post(
                new PartWriterAspectEvent<>(
                    network,
                    partNetwork,
                    target,
                    (P) this,
                    partState,
                    player,
                    aspect,
                    partState.getInventory()
                        .getStackInSlot(activeIndex)));
        }
    }

    protected void onVariableContentsUpdated(IPartNetwork network, PartTarget target, S state) {
        state.onVariableContentsUpdated((P) this, target);
    }

    protected IgnoredBlockStatus.Status getStatus(IPartStateWriter state) {
        IgnoredBlockStatus.Status status = IgnoredBlockStatus.Status.INACTIVE;
        if (state != null && !state.getInventory()
            .isEmpty()) {
            if (state.hasVariable() && state.isEnabled()) {
                status = IgnoredBlockStatus.Status.ACTIVE;
            } else {
                status = IgnoredBlockStatus.Status.ERROR;
            }
        }
        return status;
    }

    @Override
    public BlockState getBlockState(IPartContainer partContainer, ForgeDirection side) {
        BlockState state = BlockStateHelpers.getState(getBlock(), 0);
        IgnoredBlockStatus.Status status = getStatus(
            partContainer != null ? (IPartStateWriter) partContainer.getPartState(side) : null);
        state.setPropertyValue(IgnoredBlock.FACING, side);
        state.setPropertyValue(IgnoredBlockStatus.STATUS, status);
        return state;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerPartWriter.class;
    }

    @Override
    public void loadTooltip(S state, List<String> lines) {
        super.loadTooltip(state, lines);
        IAspectWrite aspectWrite = state.getActiveAspect();
        if (aspectWrite != null) {
            if (state.hasVariable() && state.isEnabled()) {
                lines.add(
                    LangHelpers.localize(
                        L10NValues.PART_TOOLTIP_WRITER_ACTIVEASPECT,
                        LangHelpers.localize(aspectWrite.getUnlocalizedName()),
                        aspectWrite.getValueType()
                            .getDisplayColorFormat()
                            + LangHelpers.localize(
                                aspectWrite.getValueType()
                                    .getUnlocalizedName())
                            + EnumChatFormatting.RESET));
            } else {
                lines.add(EnumChatFormatting.RED + LangHelpers.localize(L10NValues.PART_TOOLTIP_ERRORS));
                for (LangHelpers.UnlocalizedString error : state.getErrors(aspectWrite)) {
                    lines.add(EnumChatFormatting.RED + error.localize());
                }
            }
        } else {
            lines.add(LangHelpers.localize(L10NValues.PART_TOOLTIP_INACTIVE));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Class<? extends GuiScreen> getGui() {
        return GuiPartWriter.class;
    }

    @Override
    public boolean shouldTriggerBlockRenderUpdate(@Nullable S oldPartState, @Nullable S newPartState) {
        return super.shouldTriggerBlockRenderUpdate(oldPartState, newPartState)
            || getStatus(oldPartState) != getStatus(newPartState);
    }
}
