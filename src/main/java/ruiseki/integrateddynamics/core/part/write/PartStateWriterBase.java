package ruiseki.integrateddynamics.core.part.write;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.Maps;

import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.api.part.aspect.IAspectWrite;
import ruiseki.integrateddynamics.api.part.write.IPartStateWriter;
import ruiseki.integrateddynamics.api.part.write.IPartTypeWriter;
import ruiseki.integrateddynamics.core.part.PartStateActiveVariableBase;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.okcore.helper.CollectionHelpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.persist.nbt.NBTClassType;

/**
 * A default implementation of the {@link IPartTypeWriter}.
 *
 * @author rubensworks
 */
public class PartStateWriterBase<P extends IPartTypeWriter> extends PartStateActiveVariableBase<P>
    implements IPartStateWriter<P> {

    private IAspectWrite activeAspect = null;
    private Map<String, List<LangHelpers.UnlocalizedString>> errorMessages = Maps.newHashMap();
    private boolean firstTick = true;

    public PartStateWriterBase(int inventorySize) {
        super(inventorySize);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        if (this.activeAspect != null) tag.setString("activeAspectName", this.activeAspect.getUnlocalizedName());
        NBTClassType.getType(Map.class, this.errorMessages)
            .writePersistedField("errorMessages", this.errorMessages, tag);
        super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        IAspect aspect = Aspects.REGISTRY.getAspect(tag.getString("activeAspectName"));
        if (aspect instanceof IAspectWrite) {
            this.activeAspect = (IAspectWrite) aspect;
        }
        this.errorMessages = (Map<String, List<LangHelpers.UnlocalizedString>>) NBTClassType
            .getType(Map.class, this.errorMessages)
            .readPersistedField("errorMessages", tag);
        super.readFromNBT(tag);
    }

    @Override
    protected void validate(INetwork network, IPartNetwork partNetwork) {
        // Note that this is only called server-side, so these errors are sent via NBT to the client(s).
        if (getActiveAspect() != null) {
            this.currentVariableFacade.validate(
                network,
                partNetwork,
                new PartStateWriterBase.Validator(this, getActiveAspect()),
                getActiveAspect().getValueType());
        }
    }

    @Override
    protected void onCorruptedState() {
        super.onCorruptedState();
        this.activeAspect = null;
    }

    @Override
    public boolean hasVariable() {
        return getActiveAspect() != null && getErrors(getActiveAspect()).isEmpty() && super.hasVariable();
    }

    @Override
    public void triggerAspectInfoUpdate(P partType, PartTarget target, IAspectWrite newAspect,
        boolean isNetworkInitializing) {
        if (!isNetworkInitializing) {
            // We skip network content updates during network init,
            // as it will be called once for all parts right after network init.
            // This is to avoid re-updating variable contents many times during network init, which can get expensive.
            onVariableContentsUpdated(partType, target);
        }
        IAspectWrite activeAspect = getActiveAspect();
        if (activeAspect != null && activeAspect != newAspect) {
            activeAspect.onDeactivate(partType, target, this);
        }
        if (newAspect != null && activeAspect != newAspect) {
            newAspect.onActivate(partType, target, this);
        }
        this.activeAspect = newAspect;
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
        return activeAspect;
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
