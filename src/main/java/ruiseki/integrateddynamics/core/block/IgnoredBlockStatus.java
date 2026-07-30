package ruiseki.integrateddynamics.core.block;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.block.BlockInvisibleLight;
import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.IProperty;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;

/**
 * A block that is not visible to the player.
 * Just used for providing models, until a better way for doing this comes around.
 *
 * @author rubensworks
 */
public class IgnoredBlockStatus extends IgnoredBlock {

    @BlockProperty
    public static final PropertyStatus STATUS = PropertyStatus.create("status", Status.class);

    private static BlockInvisibleLight _instance = null;

    public static BlockInvisibleLight getInstance() {
        return _instance;
    }

    /**
     * Make a new blockState instance.
     *
     * @param eConfig Config for this blockState.
     */
    public IgnoredBlockStatus(ExtendedConfig eConfig) {
        super(eConfig);
    }

    public static class PropertyStatus implements IProperty<IgnoredBlockStatus.Status> {

        private final String name;
        private final Class<Status> valueClass;
        private final ImmutableSet<Status> allowedValues;

        @SuppressWarnings("unchecked")
        protected PropertyStatus(String name, Class<Status> valueClass, Collection<Status> values) {
            this.name = name;
            this.valueClass = valueClass;
            this.allowedValues = ImmutableSet.copyOf(values);
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public Type getType() {
            return this.valueClass;
        }

        @Override
        public Status getDefaultValue() {
            return Status.INACTIVE;
        }

        public Collection<Status> getAllowedValues() {
            return this.allowedValues;
        }

        public String getValueName(Status value) {
            return value.name()
                .toLowerCase();
        }

        /**
         * Create a new PropertyStatus with all Enum constants of the given class.
         *
         * @param name  The property name.
         * @param clazz The property class.
         * @return The property
         */
        @SuppressWarnings("rawtypes")
        public static PropertyStatus create(String name, Class clazz) {
            return create(name, clazz, Predicates.alwaysTrue());
        }

        /**
         * Create a new PropertyStatus with all Enum constants of the given class.
         *
         * @param name   The property name.
         * @param clazz  The property class.
         * @param filter The filter for checking property values.
         * @return The property
         */
        @SuppressWarnings({ "unchecked", "rawtypes" })
        public static PropertyStatus create(String name, Class clazz, Predicate filter) {
            List constants = Lists.newArrayList(clazz.getEnumConstants());
            return create(name, clazz, Collections2.filter(constants, filter));
        }

        /**
         * Create a new PropertyStatus with all Enum constants of the given class.
         *
         * @param name   The property name.
         * @param clazz  The property class.
         * @param values The possible property values.
         * @return The property
         */
        @SuppressWarnings({ "unchecked", "rawtypes" })
        public static PropertyStatus create(String name, Class clazz, Collection values) {
            return new PropertyStatus(name, clazz, values);
        }
    }

    public enum Status {

        ACTIVE,
        INACTIVE,
        ERROR

    }

}
