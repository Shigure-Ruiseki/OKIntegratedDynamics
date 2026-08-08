package ruiseki.integrateddynamics.core.part.write;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;

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
import ruiseki.integrateddynamics.api.part.aspect.IAspectWrite;
import ruiseki.integrateddynamics.api.part.write.IPartStateWriter;
import ruiseki.integrateddynamics.api.part.write.IPartTypeWriter;
import ruiseki.integrateddynamics.client.gui.GuiPartWriter;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.network.event.VariableContentsUpdatedEvent;
import ruiseki.integrateddynamics.core.part.PartTypeAspects;
import ruiseki.integrateddynamics.inventory.container.ContainerPartWriter;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.okcore.helper.LangHelpers;

/**
 * An abstract {@link IPartTypeWriter}.
 *
 * @author rubensworks
 */
public abstract class PartTypeWriteBase<P extends IPartTypeWriter<P, S>, S extends IPartStateWriter<P>>
    extends PartTypeAspects<P, S> implements IPartTypeWriter<P, S> {

    public PartTypeWriteBase(String name) {
        super(name, new PartRenderPosition(0.3125F, 0.3125F, 0.25F, 0.25F));
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
        return actions;
    }

    @Override
    public Class<? super P> getPartTypeClass() {
        return IPartTypeWriter.class;
    }

    @Override
    public void addDrops(PartTarget target, S state, List<ItemStack> itemStacks, boolean dropMainElement) {
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
        super.addDrops(target, state, itemStacks, dropMainElement);
    }

    @Override
    public void beforeNetworkKill(IPartNetwork network, PartTarget target, S state) {
        super.beforeNetworkKill(network, target, state);
        state.triggerAspectInfoUpdate((P) this, target, null);
    }

    @Override
    public void afterNetworkAlive(IPartNetwork network, PartTarget target, S state) {
        super.afterNetworkAlive(network, target, state);
        updateActivation(target, state);
    }

    @Override
    public List<IAspectWrite> getWriteAspects() {
        return Aspects.REGISTRY.getWriteAspects(this);
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
    public void updateActivation(PartTarget target, S partState) {
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
    }

    protected void onVariableContentsUpdated(IPartNetwork network, PartTarget target, S state) {
        state.onVariableContentsUpdated((P) this, target);
    }

    protected Status getStatus(IPartStateWriter state) {
        Status status = Status.INACTIVE;
        if (state != null && !state.getInventory()
            .isEmpty()) {
            if (state.hasVariable() && state.isEnabled()) {
                status = Status.ACTIVE;
            } else {
                status = Status.ERROR;
            }
        }
        return status;
    }

    @Override
    public String getBlockModelPath(IPartContainer partContainer, ForgeDirection side) {
        String status = "_inactive";
        if (partContainer != null) {
            IPartStateWriter state = (IPartStateWriter) partContainer.getPartState(side);
            if (state != null) {
                IAspectWrite aspectWrite = state.getActiveAspect();
                if (aspectWrite != null) {
                    if (state.hasVariable() && state.isEnabled()) {
                        status = "_active";
                    } else {
                        status = "_error";
                    }
                }
            }
        }
        return super.getBlockModelPath(partContainer, side) + status;
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
