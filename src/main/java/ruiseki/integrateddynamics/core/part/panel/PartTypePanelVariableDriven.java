package ruiseki.integrateddynamics.core.part.panel;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.Level;

import lombok.Getter;
import lombok.Setter;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.network.event.INetworkEvent;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.client.gui.GuiPartDisplay;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.helper.WrenchHelpers;
import ruiseki.integrateddynamics.core.network.event.VariableContentsUpdatedEvent;
import ruiseki.integrateddynamics.core.part.PartStateActiveVariableBase;
import ruiseki.integrateddynamics.inventory.container.ContainerPartDisplay;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;

/**
 * A panel part that is driven by a contained variable.
 *
 * @author rubensworks
 */
public abstract class PartTypePanelVariableDriven<P extends PartTypePanelVariableDriven<P, S>, S extends PartTypePanelVariableDriven.State<P, S>>
    extends PartTypePanel<P, S> {

    public PartTypePanelVariableDriven(String name) {
        super(name);
    }

    @Override
    protected Map<Class<? extends INetworkEvent<IPartNetwork>>, IEventAction> constructNetworkEventActions() {
        Map<Class<? extends INetworkEvent<IPartNetwork>>, IEventAction> actions = super.constructNetworkEventActions();
        actions.put(VariableContentsUpdatedEvent.class, new IEventAction<P, S, VariableContentsUpdatedEvent>() {

            @Override
            public void onAction(IPartNetwork network, PartTarget target, S state, VariableContentsUpdatedEvent event) {
                onVariableContentsUpdated(event.getNetwork(), target, state);
            }
        });
        return actions;
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
        state.onVariableContentsUpdated((P) this, target);
        super.addDrops(target, state, itemStacks, dropMainElement);
    }

    @Override
    public void beforeNetworkKill(IPartNetwork network, PartTarget target, S state) {
        super.beforeNetworkKill(network, target, state);
        state.onVariableContentsUpdated((P) this, target);
    }

    @Override
    public void afterNetworkAlive(IPartNetwork network, PartTarget target, S state) {
        super.afterNetworkAlive(network, target, state);
        state.onVariableContentsUpdated((P) this, target);
    }

    @Override
    public boolean isUpdate(S state) {
        return true;
    }

    @Override
    public void update(IPartNetwork network, PartTarget target, S state) {
        super.update(network, target, state);
        IValue lastValue = state.getDisplayValue();
        IValue newValue = null;
        if (state.hasVariable()) {
            try {
                IVariable variable = state.getVariable(network);
                if (variable != null) {
                    newValue = variable.getValue();
                }
            } catch (EvaluationException e) {
                state.addGlobalError(new LangHelpers.UnlocalizedString(e.getLocalizedMessage()));
            }
        }
        if (!ValueHelpers.areValuesEqual(lastValue, newValue)) {
            onValueChanged(network, target, state, lastValue, newValue);
            state.sendUpdate();
        }
    }

    protected void onValueChanged(IPartNetwork network, PartTarget target, S state, IValue lastValue, IValue newValue) {
        state.setDisplayValue(
            newValue != null ? newValue.getType()
                .materialize(newValue) : null);
    }

    @Override
    protected boolean hasGui() {
        return true;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerPartDisplay.class;
    }

    @Override
    public Class<? extends GuiScreen> getGui() {
        return GuiPartDisplay.class;
    }

    protected Status getStatus(PartTypePanelVariableDriven.State state) {
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
            IPartState stateBase = partContainer.getPartState(side);
            if (stateBase instanceof PartTypePanelVariableDriven.State) {
                PartTypePanelVariableDriven.State state = (PartTypePanelVariableDriven.State) stateBase;
                if (state.hasVariable() && state.isEnabled()) {
                    status = "_active";
                } else if (!state.getInventory()
                    .isEmpty()) {
                        status = "_error";
                    }
            }
        }
        return super.getBlockModelPath(partContainer, side) + status;
    }

    @Override
    public String getItemModelPath() {
        return super.getItemModelPath() + "_inactive";
    }

    protected void onVariableContentsUpdated(IPartNetwork network, PartTarget target, S state) {
        state.onVariableContentsUpdated((P) this, target);
    }

    @Override
    public boolean onPartActivated(World world, BlockPos pos, final S partState, EntityPlayer player,
        ItemStack heldItem, ForgeDirection side, float hitX, float hitY, float hitZ) {
        if (WrenchHelpers.isWrench(player, heldItem, world, pos, side)) {
            WrenchHelpers.wrench(player, heldItem, world, pos, side, new WrenchHelpers.IWrenchAction<Void>() {

                @Override
                public void onWrench(EntityPlayer player, BlockPos pos, Void parameter) {
                    partState.setFacingRotation(
                        partState.getFacingRotation()
                            .getRotation(ForgeDirection.UP));
                }
            });
            return true;
        }
        return super.onPartActivated(world, pos, partState, player, heldItem, side, hitX, hitY, hitZ);
    }

    @Override
    public void loadTooltip(S state, List<String> lines) {
        if (!state.getInventory()
            .isEmpty()) {
            if (state.hasVariable() && state.isEnabled()) {
                IValue value = state.getDisplayValue();
                if (value != null) {
                    IValueType valueType = value.getType();
                    lines.add(
                        LangHelpers.localize(
                            L10NValues.PART_TOOLTIP_DISPLAY_ACTIVEVALUE,
                            valueType.getDisplayColorFormat() + valueType.toCompactString(value),
                            LangHelpers.localize(valueType.getUnlocalizedName())));
                }
            } else {
                lines.add(EnumChatFormatting.RED + LangHelpers.localize(L10NValues.PART_TOOLTIP_ERRORS));
                for (LangHelpers.UnlocalizedString error : state.getGlobalErrors()) {
                    lines.add(EnumChatFormatting.RED + error.localize());
                }
            }
        } else {
            lines.add(LangHelpers.localize(L10NValues.PART_TOOLTIP_INACTIVE));
        }
        super.loadTooltip(state, lines);
    }

    @Override
    public boolean shouldTriggerBlockRenderUpdate(@Nullable S oldPartState, @Nullable S newPartState) {
        return super.shouldTriggerBlockRenderUpdate(oldPartState, newPartState)
            || getStatus(oldPartState) != getStatus(newPartState);
    }

    public static abstract class State<P extends PartTypePanelVariableDriven<P, S>, S extends PartTypePanelVariableDriven.State<P, S>>
        extends PartStateActiveVariableBase<P> {

        @Getter
        @Setter
        private IValue displayValue;
        @Getter
        @Setter
        private ForgeDirection facingRotation = ForgeDirection.NORTH;

        public State() {
            super(1);
        }

        @Override
        public void writeToNBT(NBTTagCompound tag) {
            super.writeToNBT(tag);
            IValue value = getDisplayValue();
            if (value != null) {
                tag.setString(
                    "displayValueType",
                    value.getType()
                        .getUnlocalizedName());
                tag.setString(
                    "displayValue",
                    value.getType()
                        .serialize(value));
            }
            tag.setInteger("facingRotation", facingRotation.ordinal());
        }

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            super.readFromNBT(tag);
            if (tag.hasKey("displayValueType", MinecraftHelpers.NBTTag_Types.NBTTagString.ordinal())
                && tag.hasKey("displayValue", MinecraftHelpers.NBTTag_Types.NBTTagString.ordinal())) {
                IValueType valueType = ValueTypes.REGISTRY.getValueType(tag.getString("displayValueType"));
                if (valueType != null) {
                    String serializedValue = tag.getString("displayValue");
                    LangHelpers.UnlocalizedString deserializationError = valueType.canDeserialize(serializedValue);
                    if (deserializationError == null) {
                        setDisplayValue(valueType.deserialize(serializedValue));
                    } else {
                        IntegratedDynamics.clog(Level.ERROR, deserializationError.localize());
                    }
                } else {
                    IntegratedDynamics.clog(
                        Level.ERROR,
                        String.format(
                            "Tried to deserialize the value \"%s\" for type \"%s\" which could not be found.",
                            tag.getString("displayValueType"),
                            tag.getString("value")));
                }
            } else {
                setDisplayValue(null);
            }
            facingRotation = ForgeDirection.values()[Math.max(2, tag.getInteger("facingRotation"))];
        }
    }
}
