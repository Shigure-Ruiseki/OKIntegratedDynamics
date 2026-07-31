package ruiseki.integrateddynamics.core.part.aspect.build;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.IAspectRead;
import ruiseki.integrateddynamics.api.part.aspect.IAspectWrite;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectProperties;
import ruiseki.integrateddynamics.api.part.write.IPartStateWriter;
import ruiseki.integrateddynamics.api.part.write.IPartTypeWriter;
import ruiseki.integrateddynamics.core.helper.Helpers;
import ruiseki.integrateddynamics.part.aspect.read.AspectReadBase;
import ruiseki.integrateddynamics.part.aspect.write.AspectWriteBase;

/**
 * Immutable builder for aspects.
 *
 * @param <V> The value type.
 * @param <T> The value type type.
 * @param <O> The current output type for value handling.
 * @author rubensworks
 */
public class AspectBuilder<V extends IValue, T extends IValueType<V>, O> {

    private final boolean read;
    private final T valueType;
    private final List<String> kinds;
    private final IAspectProperties defaultAspectProperties;
    private final List<IAspectValuePropagator> valuePropagators;
    private final List<IAspectWriteActivator> writeActivators;
    private final List<IAspectWriteDeactivator> writeDeactivators;
    private final String customIconPath; // Trường lưu custom icon path

    private AspectBuilder(boolean read, T valueType, List<String> kinds, IAspectProperties defaultAspectProperties,
        List<IAspectValuePropagator> valuePropagators, List<IAspectWriteActivator> writeActivators,
        List<IAspectWriteDeactivator> writeDeactivators, String customIconPath) {
        this.read = read;
        this.valueType = valueType;
        this.kinds = kinds;
        this.defaultAspectProperties = defaultAspectProperties;
        this.valuePropagators = valuePropagators;
        this.writeActivators = writeActivators;
        this.writeDeactivators = writeDeactivators;
        this.customIconPath = customIconPath;
    }

    /**
     * Set a custom icon/texture path for this aspect.
     *
     * @param customIconPath Relative path inside textures/items/aspects/ (e.g. "read/block")
     * @return The new builder instance.
     */
    public AspectBuilder<V, T, O> handleTexture(String customIconPath) {
        return new AspectBuilder<>(
            this.read,
            this.valueType,
            Helpers.joinList(this.kinds, null),
            this.defaultAspectProperties,
            Helpers.joinList(this.valuePropagators, null),
            Helpers.joinList(this.writeActivators, null),
            Helpers.joinList(this.writeDeactivators, null),
            customIconPath);
    }

    /**
     * Add the given value propagator.
     */
    public <O2> AspectBuilder<V, T, O2> handle(IAspectValuePropagator<O, O2> valuePropagator) {
        return handle(valuePropagator, null);
    }

    /**
     * Add the given value propagator.
     */
    public <O2> AspectBuilder<V, T, O2> handle(IAspectValuePropagator<O, O2> valuePropagator, String kind) {
        return new AspectBuilder<>(
            this.read,
            this.valueType,
            Helpers.joinList(this.kinds, kind),
            this.defaultAspectProperties,
            Helpers.joinList(this.valuePropagators, valuePropagator),
            Helpers.joinList(writeActivators, null),
            Helpers.joinList(writeDeactivators, null),
            this.customIconPath);
    }

    /**
     * Add the given kind.
     */
    public AspectBuilder<V, T, O> appendKind(String kind) {
        return new AspectBuilder<>(
            this.read,
            this.valueType,
            Helpers.joinList(this.kinds, kind),
            this.defaultAspectProperties,
            Helpers.joinList(this.valuePropagators, null),
            Helpers.joinList(writeActivators, null),
            Helpers.joinList(writeDeactivators, null),
            this.customIconPath);
    }

    /**
     * Set the given default aspect properties.
     */
    public AspectBuilder<V, T, O> withProperties(IAspectProperties aspectProperties) {
        return new AspectBuilder<>(
            this.read,
            this.valueType,
            Helpers.joinList(this.kinds, null),
            aspectProperties,
            Helpers.joinList(this.valuePropagators, null),
            Helpers.joinList(writeActivators, null),
            Helpers.joinList(writeDeactivators, null),
            this.customIconPath);
    }

    /**
     * Add the given aspect activator.
     */
    public AspectBuilder<V, T, O> appendActivator(IAspectWriteActivator activator) {
        if (this.read) {
            throw new RuntimeException("Activators are only applicable for writers.");
        }
        return new AspectBuilder<>(
            this.read,
            this.valueType,
            Helpers.joinList(this.kinds, null),
            this.defaultAspectProperties,
            Helpers.joinList(this.valuePropagators, null),
            Helpers.joinList(writeActivators, activator),
            Helpers.joinList(writeDeactivators, null),
            this.customIconPath);
    }

    /**
     * Add the given aspect deactivator.
     */
    public AspectBuilder<V, T, O> appendDeactivator(IAspectWriteDeactivator deactivator) {
        if (this.read) {
            throw new RuntimeException("Deactivators are only applicable for writers.");
        }
        return new AspectBuilder<>(
            this.read,
            this.valueType,
            Helpers.joinList(this.kinds, null),
            this.defaultAspectProperties,
            Helpers.joinList(this.valuePropagators, null),
            Helpers.joinList(writeActivators, null),
            Helpers.joinList(writeDeactivators, deactivator),
            this.customIconPath);
    }

    /**
     * @return The built read aspect.
     */
    @SuppressWarnings("unchecked")
    public IAspectRead<V, T> buildRead() {
        if (!this.read) {
            throw new RuntimeException("Tried to build a reader from a writer builder");
        }
        return new BuiltReader<V, T>((AspectBuilder<V, T, V>) this);
    }

    /**
     * @return The built write aspect.
     */
    @SuppressWarnings("unchecked")
    public IAspectWrite<V, T> buildWrite() {
        if (this.read) {
            throw new RuntimeException("Tried to build a writer from a reader builder");
        }
        return new BuiltWriter<V, T>((AspectBuilder<V, T, V>) this);
    }

    /**
     * Create a new read builder for the given value type.
     */
    public static <V extends IValue, T extends IValueType<V>> AspectBuilder<V, T, Pair<PartTarget, IAspectProperties>> forReadType(
        T valueType) {
        return new AspectBuilder<>(
            true,
            valueType,
            Lists.newArrayList(valueType.getTypeName()),
            null,
            Collections.<IAspectValuePropagator>emptyList(),
            Collections.<IAspectWriteActivator>emptyList(),
            Collections.<IAspectWriteDeactivator>emptyList(),
            null);
    }

    /**
     * Create a new write builder for the given value type.
     */
    public static <V extends IValue, T extends IValueType<V>> AspectBuilder<V, T, Triple<PartTarget, IAspectProperties, IVariable<V>>> forWriteType(
        T valueType) {
        return new AspectBuilder<>(
            false,
            valueType,
            Lists.newArrayList(valueType.getTypeName()),
            null,
            Collections.<IAspectValuePropagator>emptyList(),
            Collections.<IAspectWriteActivator>emptyList(),
            Collections.<IAspectWriteDeactivator>emptyList(),
            null);
    }

    private static class BuiltReader<V extends IValue, T extends IValueType<V>> extends AspectReadBase<V, T> {

        private final T valueType;
        private final List<IAspectValuePropagator> valuePropagators;

        public BuiltReader(AspectBuilder<V, T, V> aspectBuilder) {
            super(
                deriveUnlocalizedType(aspectBuilder),
                aspectBuilder.defaultAspectProperties,
                deriveCustomIconPath(aspectBuilder));
            this.valueType = aspectBuilder.valueType;
            this.valuePropagators = aspectBuilder.valuePropagators;
        }

        protected static <V extends IValue, T extends IValueType<V>> String deriveCustomIconPath(
            AspectBuilder<V, T, V> aspectBuilder) {
            if (aspectBuilder.customIconPath != null) {
                return aspectBuilder.customIconPath;
            }
            return "read" + deriveUnlocalizedType(aspectBuilder).replaceAll("\\.", "/");
        }

        protected static <V extends IValue, T extends IValueType<V>> String deriveUnlocalizedType(
            AspectBuilder<V, T, V> aspectBuilder) {
            StringBuilder sb = new StringBuilder();
            for (String kind : aspectBuilder.kinds) {
                sb.append(".");
                sb.append(kind);
            }
            return sb.toString();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected V getValue(PartTarget target, IAspectProperties properties) {
            Object output = Pair.of(target, properties);
            for (IAspectValuePropagator valuePropagator : valuePropagators) {
                try {
                    output = valuePropagator.getOutput(output);
                } catch (EvaluationException e) {
                    e.printStackTrace();
                    throw new RuntimeException(
                        "Caught unexpected exception in read aspect, this is probably a programming error.");
                }
            }
            return (V) output;
        }

        @Override
        public T getValueType() {
            return valueType;
        }
    }

    private static class BuiltWriter<V extends IValue, T extends IValueType<V>> extends AspectWriteBase<V, T> {

        private final T valueType;
        private final List<IAspectValuePropagator> valuePropagators;
        private final List<IAspectWriteActivator> writeActivators;
        private final List<IAspectWriteDeactivator> writeDeactivators;

        public BuiltWriter(AspectBuilder<V, T, V> aspectBuilder) {
            super(
                deriveUnlocalizedType(aspectBuilder),
                aspectBuilder.defaultAspectProperties,
                deriveCustomIconPath(aspectBuilder));
            this.valueType = aspectBuilder.valueType;
            this.valuePropagators = aspectBuilder.valuePropagators;
            this.writeActivators = aspectBuilder.writeActivators;
            this.writeDeactivators = aspectBuilder.writeDeactivators;
        }

        protected static <V extends IValue, T extends IValueType<V>> String deriveCustomIconPath(
            AspectBuilder<V, T, V> aspectBuilder) {
            if (aspectBuilder.customIconPath != null) {
                return aspectBuilder.customIconPath;
            }
            return "write" + deriveUnlocalizedType(aspectBuilder).replaceAll("\\.", "/");
        }

        protected static <V extends IValue, T extends IValueType<V>> String deriveUnlocalizedType(
            AspectBuilder<V, T, V> aspectBuilder) {
            StringBuilder sb = new StringBuilder();
            for (String kind : aspectBuilder.kinds) {
                sb.append(".");
                sb.append(kind);
            }
            return sb.toString();
        }

        @Override
        public T getValueType() {
            return valueType;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public <P extends IPartTypeWriter<P, S>, S extends IPartStateWriter<P>> void write(P partType,
            PartTarget target, S state, IVariable<V> variable) throws EvaluationException {
            IAspectProperties properties = hasProperties() ? getProperties(partType, target, state) : null;
            Object output = Triple.of(target, properties, variable);
            for (IAspectValuePropagator valuePropagator : valuePropagators) {
                output = valuePropagator.getOutput(output);
            }
        }

        @Override
        public <P extends IPartTypeWriter<P, S>, S extends IPartStateWriter<P>> void onActivate(P partType,
            PartTarget target, S state) {
            super.onActivate(partType, target, state);
            for (IAspectWriteActivator writeActivator : this.writeActivators) {
                writeActivator.onActivate(partType, target, state);
            }
        }

        @Override
        public <P extends IPartTypeWriter<P, S>, S extends IPartStateWriter<P>> void onDeactivate(P partType,
            PartTarget target, S state) {
            super.onDeactivate(partType, target, state);
            for (IAspectWriteDeactivator writeDeactivator : this.writeDeactivators) {
                writeDeactivator.onDeactivate(partType, target, state);
            }
        }
    }
}
