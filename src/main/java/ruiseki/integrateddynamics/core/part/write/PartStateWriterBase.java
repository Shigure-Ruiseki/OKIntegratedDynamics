package ruiseki.integrateddynamics.core.part.write;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.api.part.aspect.IAspectWrite;
import ruiseki.integrateddynamics.api.part.write.IPartStateWriter;
import ruiseki.integrateddynamics.api.part.write.IPartTypeWriter;
import ruiseki.integrateddynamics.core.part.PartStateActiveVariableBase;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.okcore.helper.CollectionHelpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.persist.nbt.NBTPersist;

/**
 * A default implementation of the {@link IPartTypeWriter} with auto-persistence
 * of fields annotated with {@link NBTPersist}.
 * 
 * @author rubensworks
 */
public class PartStateWriterBase<P extends IPartTypeWriter> extends PartStateActiveVariableBase<P>
    implements IPartStateWriter<P> {

    @NBTPersist
    private String activeAspectName = null;
    @NBTPersist
    private Map<String, List<LangHelpers.UnlocalizedString>> errorMessages = Maps.newHashMap();
    private boolean firstTick = true;

    public PartStateWriterBase(int inventorySize) {
        super(inventorySize);
    }

    @Override
    protected void validate(IPartNetwork network) {
        // Note that this is only called server-side, so these errors are sent via NBT to the client(s).
        if (getActiveAspect() != null) {
            this.currentVariableFacade.validate(
                network,
                new PartStateWriterBase.Validator(this, getActiveAspect()),
                getActiveAspect().getValueType());
        }
    }

    @Override
    protected void onCorruptedState() {
        super.onCorruptedState();
        this.activeAspectName = null;
    }

    @Override
    public boolean hasVariable() {
        return getActiveAspect() != null && getErrors(getActiveAspect()).isEmpty() && super.hasVariable();
    }

    @Override
    public void triggerAspectInfoUpdate(P partType, PartTarget target, IAspectWrite newAspect) {
        onVariableContentsUpdated(partType, target);
        IAspectWrite activeAspect = getActiveAspect();
        if (activeAspect != null && activeAspect != newAspect) {
            activeAspect.onDeactivate(partType, target, this);
        }
        if (newAspect != null && activeAspect != newAspect) {
            newAspect.onActivate(partType, target, this);
        }
        this.activeAspectName = newAspect == null ? null : newAspect.getUnlocalizedName();
    }

    @Override
    public void onVariableContentsUpdated(P partType, PartTarget target) {
        // Resets the errors for this aspect
        super.onVariableContentsUpdated(partType, target);
        IAspectWrite activeAspect = getActiveAspect();
        if (activeAspect != null) {
            addError(activeAspect, null);
        }
    }

    @Override
    public IAspectWrite getActiveAspect() {
        if (this.activeAspectName == null) {
            return null;
        }
        IAspect aspect = Aspects.REGISTRY.getAspect(this.activeAspectName);
        if (!(aspect instanceof IAspectWrite)) {
            return null;
        }
        return (IAspectWrite) aspect;
    }

    @Override
    public List<LangHelpers.UnlocalizedString> getErrors(IAspectWrite aspect) {
        List<LangHelpers.UnlocalizedString> errors = errorMessages.get(aspect.getUnlocalizedName());
        if (errors == null) {
            return Collections.emptyList();
        }
        return errors;
    }

    @Override
    public void addError(IAspectWrite aspect, LangHelpers.UnlocalizedString error) {
        if (error == null) {
            errorMessages.remove(aspect.getUnlocalizedName());
        } else {
            CollectionHelpers.addToMapList(errorMessages, aspect.getUnlocalizedName(), error);
        }
        onDirty();
        sendUpdate(); // We want this error messages to be sent to the client(s).
    }

    @Override
    public Class<? extends IPartState> getPartStateClass() {
        return IPartStateWriter.class;
    }

    @Override
    public boolean checkAndResetFirstTick() {
        if (firstTick) {
            firstTick = false;
            return true;
        }
        return false;
    }

    public static class Validator implements IVariableFacade.IValidator {

        private final IPartStateWriter state;
        private final IAspectWrite aspect;

        /**
         * Make a new instance
         * 
         * @param state  The part state.
         * @param aspect The aspect to set the error for.
         */
        public Validator(IPartStateWriter state, IAspectWrite aspect) {
            this.state = state;
            this.aspect = aspect;
        }

        @Override
        public void addError(LangHelpers.UnlocalizedString error) {
            this.state.addError(aspect, error);
        }

    }

}
