package ruiseki.integrateddynamics.part.aspect.read;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.IAspectRead;
import ruiseki.integrateddynamics.api.part.aspect.IAspectVariable;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectProperties;
import ruiseki.integrateddynamics.api.part.read.IPartStateReader;
import ruiseki.integrateddynamics.api.part.read.IPartTypeReader;
import ruiseki.integrateddynamics.core.part.aspect.LazyAspectVariable;
import ruiseki.integrateddynamics.part.aspect.AspectBase;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.init.ModBase;

/**
 * Base class for read aspects.
 *
 * @author rubensworks
 */
public abstract class AspectReadBase<V extends IValue, T extends IValueType<V>> extends AspectBase<V, T>
    implements IAspectRead<V, T> {

    private final String unlocalizedTypeSuffix;
    private final String customIconPath;

    public AspectReadBase(ModBase mod, ModBase modGui, String unlocalizedTypeSuffix,
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
    public <P extends IPartType<P, S>, S extends IPartState<P>> void update(IPartNetwork network, P partType,
        PartTarget target, S state) {
        IAspectVariable variable = ((IPartTypeReader) partType).getVariable(target, (IPartStateReader) state, this);
        if (variable.requiresUpdate()) {
            variable.update();
        }
    }

    protected String getUnlocalizedType() {
        return "read" + this.unlocalizedTypeSuffix;
    }

    @SideOnly(Side.CLIENT)
    protected void registerModelResourceLocation() {
        Aspects.REGISTRY.registerAspectIconPath(
            this,
            getModId() + ":aspects/"
                + (customIconPath.isEmpty() ? getUnlocalizedType().replaceAll("\\.", "/") : customIconPath));
    }

    /**
     * This is only called lazy.
     *
     * @param target     The target to get the value for.
     * @param properties The optional properties for this aspect.
     * @return The value that will be inserted into a variable so it can be used elsewhere.
     * @throws EvaluationException If evaluation has gone wrong.
     */
    protected abstract V getValue(PartTarget target, IAspectProperties properties) throws EvaluationException;

    @Override
    public IAspectVariable<V> createNewVariable(final PartTarget target) {
        return new LazyAspectVariable<V>(getValueType(), target, this) {

            @Override
            public V getValueLazy() throws EvaluationException {
                return AspectReadBase.this.getValue(target, getAspectProperties());
            }
        };
    }

}
