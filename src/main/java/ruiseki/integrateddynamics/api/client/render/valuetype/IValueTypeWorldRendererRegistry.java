package ruiseki.integrateddynamics.api.client.render.valuetype;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.okcore.init.IRegistry;

/**
 * Registry for {@link IValueTypeWorldRenderer}.
 *
 * @author rubensworks
 */
@SideOnly(Side.CLIENT)
public interface IValueTypeWorldRendererRegistry extends IRegistry {

    /**
     * Set the renderer for the given value type.
     *
     * @param valueType The value type
     * @param renderer  The renderer.
     * @param <R>       The renderer type.
     * @return The registered renderer.
     */
    public <R extends IValueTypeWorldRenderer> R register(IValueType<?> valueType, R renderer);

    /**
     * Get the renderer for the value type.
     *
     * @param valueType The value type
     * @return The registered renderer of null.
     */
    public @Nullable IValueTypeWorldRenderer getRenderer(IValueType<?> valueType);

}
