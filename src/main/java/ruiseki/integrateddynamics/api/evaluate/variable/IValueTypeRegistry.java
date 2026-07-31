package ruiseki.integrateddynamics.api.evaluate.variable;

import java.util.Collection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.item.IValueTypeVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandler;
import ruiseki.okcore.init.IRegistry;

/**
 * Registry for {@link IValueType}
 *
 * @author rubensworks
 */
public interface IValueTypeRegistry extends IRegistry, IVariableFacadeHandler<IValueTypeVariableFacade> {

    /**
     * Register a new value type.
     *
     * @param valueType The part type.
     * @param <V>       The value type.
     * @param <T>       The value type type.
     * @return The registered value type.
     */
    public <V extends IValue, T extends IValueType<V>> T register(T valueType);

    /**
     * Register a new value category.
     * This registration can be overwritten, so only the last registered category is remembered.
     *
     * @param category The category.
     * @param <V>      The value type.
     * @param <T>      The value type type.
     * @return The registered category.
     */
    public <V extends IValue, T extends IValueTypeCategory<V>> T registerCategory(T category);

    /**
     * Get the value type by name.
     *
     * @param name The unique name.
     * @return The value type or null if not found.
     */
    public IValueType getValueType(String name);

    /**
     * Register an icon path for the given value type.
     *
     * @param <V>       The value type.
     * @param <T>       The value type type.
     * @param valueType The value type.
     * @param iconPath  The icon path (e.g., "integrateddynamics:valuetype/boolean").
     */
    @SideOnly(Side.CLIENT)
    public <V extends IValue, T extends IValueType<V>> void registerValueTypeIconPath(T valueType, String iconPath);

    /**
     * Get the icon path of the given value type.
     *
     * @param <V>       The value type.
     * @param <T>       The value type type.
     * @param valueType The value type.
     * @return The icon path string.
     */
    @SideOnly(Side.CLIENT)
    public <V extends IValue, T extends IValueType<V>> String getValueTypeIconPath(T valueType);

    /**
     * Get all registered icon paths for the value types.
     *
     * @return All icon paths.
     */
    @SideOnly(Side.CLIENT)
    public Collection<String> getValueTypeIconPaths();

    /**
     * @return All registered value types.
     */
    public Collection<IValueType> getValueTypes();

}
