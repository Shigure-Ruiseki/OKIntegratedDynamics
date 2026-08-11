package ruiseki.integrateddynamics.core.part.event;

import net.minecraft.entity.player.EntityPlayer;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;

/**
 * An event that is posted in the Forge event bus when the variable contents in a variable-driven part is updated.
 *
 * @author rubensworks
 */
public class PartVariableDrivenVariableContentsUpdatedEvent<P extends IPartType<P, S>, S extends IPartState<P>>
    extends PartEvent<P, S> {

    @Nullable
    private final EntityPlayer entityPlayer;
    @Nullable
    private final IVariable variable;
    @Nullable
    private final IValue value;

    public PartVariableDrivenVariableContentsUpdatedEvent(INetwork network, IPartNetwork partNetwork, PartTarget target,
        P partType, S partState, @Nullable EntityPlayer entityPlayer, IVariable variable, IValue value) {
        super(network, partNetwork, target, partType, partState);
        this.entityPlayer = entityPlayer;
        this.value = value;
        this.variable = variable;
    }

    @Nullable
    public EntityPlayer getEntityPlayer() {
        return entityPlayer;
    }

    @Nullable
    public IValue getValue() {
        return value;
    }

    @Nullable
    public IVariable getVariable() {
        return variable;
    }
}
