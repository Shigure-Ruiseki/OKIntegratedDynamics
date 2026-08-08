package ruiseki.integrateddynamics.core.part.aspect;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.item.IAspectVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.api.part.aspect.IAspectRead;
import ruiseki.integrateddynamics.api.part.aspect.IAspectRegistry;
import ruiseki.integrateddynamics.api.part.aspect.IAspectWrite;
import ruiseki.integrateddynamics.core.item.AspectVariableFacade;
import ruiseki.okcore.helper.MinecraftHelpers;

/**
 * Registry for {@link IAspect}.
 *
 * @author rubensworks
 */
public final class AspectRegistry implements IAspectRegistry {

    private static AspectRegistry INSTANCE = new AspectRegistry();
    private static final IAspectVariableFacade INVALID_FACADE = new AspectVariableFacade(false, -1, null);

    private final Map<IPartType, Set<IAspect>> partAspects = new IdentityHashMap<>();
    private final Map<IPartType, Set<IAspectRead>> partReadAspects = new IdentityHashMap<>();
    private final Map<IPartType, Set<IAspectWrite>> partWriteAspects = new IdentityHashMap<>();
    private final Map<IPartType, List<IAspectRead>> partReadAspectsListTransform = new IdentityHashMap<>();
    private final Map<IPartType, List<IAspectWrite>> partWriteAspectsListTransform = new IdentityHashMap<>();
    private final Map<String, IAspect> unlocalizedAspects = Maps.newHashMap();
    private final Map<String, IAspectRead> unlocalizedReadAspects = Maps.newHashMap();
    private final Map<String, IAspectWrite> unlocalizedWriteAspects = Maps.newHashMap();

    @SideOnly(Side.CLIENT)
    private Map<IAspect, String> aspectIconPaths;

    private AspectRegistry() {
        IntegratedDynamics._instance.getRegistryManager()
            .getRegistry(IVariableFacadeHandlerRegistry.class)
            .registerHandler(this);
        if (MinecraftHelpers.isClientSide()) {
            aspectIconPaths = new IdentityHashMap<>();
        }
    }

    /**
     * @return The unique instance.
     */
    public static AspectRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public IAspect register(IPartType partType, IAspect aspect) {
        registerSubAspectType(partType, aspect, partAspects, unlocalizedAspects);
        if (aspect instanceof IAspectRead) {
            registerSubAspectType(partType, (IAspectRead) aspect, partReadAspects, unlocalizedReadAspects);
            partReadAspectsListTransform.put(partType, Lists.newArrayList(partReadAspects.get(partType)));
        }
        if (aspect instanceof IAspectWrite) {
            registerSubAspectType(partType, (IAspectWrite) aspect, partWriteAspects, unlocalizedWriteAspects);
            partWriteAspectsListTransform.put(partType, Lists.newArrayList(partWriteAspects.get(partType)));
        }
        return aspect;
    }

    protected <T extends IAspect> void registerSubAspectType(IPartType partType, T aspect,
        Map<IPartType, Set<T>> partAspects, Map<String, T> unlocalizedAspects) {
        Set<T> aspects = partAspects.get(partType);
        if (aspects == null) {
            aspects = Sets.newLinkedHashSet();
            partAspects.put(partType, aspects);
        }
        aspects.add(aspect);
        unlocalizedAspects.put(aspect.getUnlocalizedName(), aspect);
    }

    @Override
    public void register(IPartType partType, Collection<IAspect> aspects) {
        for (IAspect aspect : aspects) {
            register(partType, aspect);
        }
    }

    @Override
    public Set<IAspect> getAspects(IPartType partType) {
        Set<IAspect> aspects = partAspects.get(partType);
        if (aspects == null) {
            return Collections.unmodifiableSet(Collections.<IAspect>emptySet());
        }
        return Collections.unmodifiableSet(aspects);
    }

    @Override
    public List<IAspectRead> getReadAspects(IPartType partType) {
        return Collections.unmodifiableList(partReadAspectsListTransform.get(partType));
    }

    @Override
    public List<IAspectWrite> getWriteAspects(IPartType partType) {
        return Collections.unmodifiableList(partWriteAspectsListTransform.get(partType));
    }

    @Override
    public Set<IAspect> getAspects() {
        return ImmutableSet.copyOf(unlocalizedAspects.values());
    }

    @Override
    public Set<IAspectRead> getReadAspects() {
        return ImmutableSet.copyOf(unlocalizedReadAspects.values());
    }

    @Override
    public Set<IAspectWrite> getWriteAspects() {
        return ImmutableSet.copyOf(unlocalizedWriteAspects.values());
    }

    @Override
    public IAspect getAspect(String unlocalizedName) {
        return unlocalizedAspects.get(unlocalizedName);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerAspectIconPath(IAspect aspect, String iconPath) {
        aspectIconPaths.put(aspect, iconPath);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getAspectIconPath(IAspect aspect) {
        return aspectIconPaths.get(aspect);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Collection<String> getAspectIconPaths() {
        return Collections.unmodifiableCollection(aspectIconPaths.values());
    }

    @Override
    public String getTypeId() {
        return "aspect";
    }

    @Override
    public IAspectVariableFacade getVariableFacade(int id, NBTTagCompound tag) {
        if (!tag.hasKey("partId", MinecraftHelpers.NBTTag_Types.NBTTagInt.ordinal())
            || !tag.hasKey("aspectName", MinecraftHelpers.NBTTag_Types.NBTTagString.ordinal())) {
            return INVALID_FACADE;
        }
        int partId = tag.getInteger("partId");
        IAspect aspect = getAspect(tag.getString("aspectName"));
        if (aspect == null) {
            return INVALID_FACADE;
        }
        return new AspectVariableFacade(id, partId, aspect);
    }

    @Override
    public void setVariableFacade(NBTTagCompound tag, IAspectVariableFacade variableFacade) {
        tag.setInteger("partId", variableFacade.getPartId());
        tag.setString(
            "aspectName",
            variableFacade.getAspect()
                .getUnlocalizedName());
    }
}
