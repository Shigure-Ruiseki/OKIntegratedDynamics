package ruiseki.integrateddynamics.api.part.aspect;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.item.IAspectVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandler;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.okcore.init.IRegistry;

/**
 * Registry for {@link IAspect}.
 *
 * @author rubensworks
 */
public interface IAspectRegistry extends IRegistry, IVariableFacadeHandler<IAspectVariableFacade> {

    /**
     * Register a new aspect for a given part type.
     *
     * @param partType The part type.
     * @param aspect   The aspect.
     * @return The registered element.
     */
    public IAspect register(IPartType partType, IAspect aspect);

    /**
     * Register a set of aspects for a given part type.
     *
     * @param partType The part type.
     * @param aspects  The aspects.
     */
    public void register(IPartType partType, Collection<IAspect> aspects);

    /**
     * Get the registered aspects for a given part type.
     *
     * @param partType The part type.
     * @return The aspects.
     */
    public Set<IAspect> getAspects(IPartType partType);

    /**
     * Get the registered read aspects for a given part type.
     *
     * @param partType The part type.
     * @return The read aspects.
     */
    public List<IAspectRead> getReadAspects(IPartType partType);

    /**
     * Get the registered write aspects for a given part type.
     *
     * @param partType The part type.
     * @return The write aspects.
     */
    public List<IAspectWrite> getWriteAspects(IPartType partType);

    /**
     * Get all registered aspects.
     *
     * @return The aspects.
     */
    public Set<IAspect> getAspects();

    /**
     * Get all registered read aspects.
     *
     * @return The read aspects.
     */
    public Set<IAspectRead> getReadAspects();

    /**
     * Get all registered write aspects.
     *
     * @return The write aspects.
     */
    public Set<IAspectWrite> getWriteAspects();

    /**
     * Get an aspect by unlocalized name.
     *
     * @param unlocalizedName The unlocalized name of the aspect.
     * @return The matching aspect.
     */
    public IAspect getAspect(String unlocalizedName);

    /**
     * Register an icon path for the given aspect.
     *
     * @param aspect   The aspect.
     * @param iconPath The icon path (e.g., "integrateddynamics:aspects/read_boolean").
     */
    @SideOnly(Side.CLIENT)
    public void registerAspectIconPath(IAspect aspect, String iconPath);

    /**
     * Get the icon path of the given aspect.
     *
     * @param aspect The aspect.
     * @return The icon path string.
     */
    @SideOnly(Side.CLIENT)
    public String getAspectIconPath(IAspect aspect);

    /**
     * Get all registered icon paths for the aspects.
     *
     * @return All icon paths.
     */
    @SideOnly(Side.CLIENT)
    public Collection<String> getAspectIconPaths();

}
