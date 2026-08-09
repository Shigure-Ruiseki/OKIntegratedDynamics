package ruiseki.integrateddynamics.core.block;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Locale;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.gtnewhorizon.gtnhlib.blockstate.core.InvalidPropertyTextException;

import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.IProperty;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
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

    /**
     * Make a new blockState instance.
     *
     * @param eConfig Config for this blockState.
     */
    public IgnoredBlockStatus(ExtendedConfig<BlockConfig> eConfig) {
        super(eConfig);
    }

    public static class PropertyStatus implements IProperty<Status> {

        private final String name;
        private final ImmutableSet<Status> allowedValues;

        protected PropertyStatus(String name, Collection<Status> values) {
            this.name = name;
            this.allowedValues = ImmutableSet.copyOf(values);
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public Type getType() {
            return Status.class;
        }

        @Override
        public Status getDefaultValue() {
            return Status.INACTIVE;
        }

        public Collection<Status> getAllowedValues() {
            return this.allowedValues;
        }

        @Override
        public Status parse(String text) throws InvalidPropertyTextException {
            try {
                Status status = Status.valueOf(text.toUpperCase(Locale.ENGLISH));
                if (allowedValues.contains(status)) {
                    return status;
                }
            } catch (IllegalArgumentException ignored) {}
            throw new InvalidPropertyTextException("Invalid status value: " + text);
        }

        @Override
        public String stringify(Status value) {
            return value.name()
                .toLowerCase(Locale.ENGLISH);
        }

        /**
         * Create a new PropertyStatus with all Enum constants of the given class.
         * 
         * @param name  The property name.
         * @param clazz The property class.
         * @return The property
         */
        public static PropertyStatus create(String name, Class<Status> clazz) {
            return create(name, clazz, Predicates.<Status>alwaysTrue());
        }

        /**
         * Create a new PropertyStatus with filtered Enum constants of the given class.
         * 
         * @param name   The property name.
         * @param clazz  The property class.
         * @param filter The filter for checking property values.
         * @return The property
         */
        public static PropertyStatus create(String name, Class<Status> clazz, Predicate<Status> filter) {
            return create(name, clazz, Collections2.filter(Lists.newArrayList(clazz.getEnumConstants()), filter));
        }

        /**
         * Create a new PropertyStatus with specific allowed property values.
         * 
         * @param name   The property name.
         * @param clazz  The property class.
         * @param values The possible property values.
         * @return The property
         */
        public static PropertyStatus create(String name, Class<Status> clazz, Collection<Status> values) {
            return new PropertyStatus(name, values);
        }
    }

    public enum Status {
        ACTIVE,
        INACTIVE,
        ERROR
    }
}
