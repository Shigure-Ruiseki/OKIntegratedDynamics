package ruiseki.integrateddynamics.part.aspect.write;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.IAspectWrite;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectProperties;
import ruiseki.integrateddynamics.api.part.write.IPartStateWriter;
import ruiseki.integrateddynamics.api.part.write.IPartTypeWriter;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.part.aspect.AspectBase;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.init.ModBase;

/**
 * Base class for write aspects.
 *
 * @author rubensworks
 */
public abstract class AspectWriteBase<V extends IValue, T extends IValueType<V>> extends AspectBase<V, T>
    implements IAspectWrite<V, T> {

    protected final String unlocalizedTypeSuffix;
    private final String customIconPath;

    public AspectWriteBase(ModBase mod, ModBase modGui, String unlocalizedTypeSuffix,
        IAspectProperties defaultProperties, String customIconPath) {
        super(mod, modGui, defaultProperties);
        this.unlocalizedTypeSuffix = unlocalizedTypeSuffix;
        this.customIconPath = customIconPath;
        if (MinecraftHelpers.isClientSide()) {
            registerModelResourceLocation();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <P extends IPartType<P, S>, S extends IPartState<P>> void update(INetwork network, IPartNetwork partNetwork,
        P partType, PartTarget target, S state) {
        IPartTypeWriter partTypeWriter = (IPartTypeWriter) partType;
        IPartStateWriter writerState = (IPartStateWriter) state;
        IVariable variable = partTypeWriter.getActiveVariable(network, partNetwork, target, writerState);
        if (variable != null && writerState.getErrors(this)
            .isEmpty() && ValueHelpers.correspondsTo(getValueType(), variable.getType())) {
            if (writerState.isDeactivated() || writerState.checkAndResetFirstTick()) {
                onActivate(partTypeWriter, target, writerState);
            }
            try {
                write(partTypeWriter, target, writerState, variable);
            } catch (EvaluationException e) {
                writerState.addError(this, new LangHelpers.UnlocalizedString(e.getLocalizedMessage()));
                writerState.setDeactivated(true);
            }
        } else if (!writerState.isDeactivated()) {
            onDeactivate(partTypeWriter, target, writerState);
        }
    }

    @Override
    public <P extends IPartTypeWriter<P, S>, S extends IPartStateWriter<P>> void onActivate(P partType,
        PartTarget target, S state) {
        state.setDeactivated(false);
    }

    @Override
    public <P extends IPartTypeWriter<P, S>, S extends IPartStateWriter<P>> void onDeactivate(P partType,
        PartTarget target, S state) {
        state.setDeactivated(true);
    }

    protected String getUnlocalizedType() {
        return "write" + unlocalizedTypeSuffix;
    }

    @SideOnly(Side.CLIENT)
    protected void registerModelResourceLocation() {
        Aspects.REGISTRY.registerAspectIconPath(
            this,
            getModId() + ":aspects/"
                + (customIconPath.isEmpty() ? getUnlocalizedType().replaceAll("\\.", "/") : customIconPath));
    }

}
