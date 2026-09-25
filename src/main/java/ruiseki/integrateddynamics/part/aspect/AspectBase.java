package ruiseki.integrateddynamics.part.aspect;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectProperties;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectPropertyTypeInstance;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.core.inventory.container.ContainerAspectSettings;
import ruiseki.integrateddynamics.core.part.PartTypeBase;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.inventory.IGuiConstructor;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.inventory.container.ContainerExtended;

/**
 * Base class for aspects.
 *
 * @author rubensworks
 */
public abstract class AspectBase<V extends IValue, T extends IValueType<V>> implements IAspect<V, T> {

    private final IAspectProperties defaultProperties;

    private final String modId;
    private String unlocalizedName = null;

    public AspectBase(String modId, IAspectProperties defaultProperties) {
        this.modId = modId;
        this.defaultProperties = defaultProperties == null ? createDefaultProperties() : defaultProperties;
    }

    @Override
    public ResourceLocation getUniqueName() {
        return new ResourceLocation(getModId(), getUnlocalizedName());
    }

    @Override
    public String getUnlocalizedName() {
        return unlocalizedName != null ? unlocalizedName : (unlocalizedName = getUnlocalizedPrefix() + ".name");
    }

    protected String getUnlocalizedPrefix() {
        return "aspect.aspects." + getModId() + "." + getUnlocalizedType();
    }

    protected abstract String getUnlocalizedType();

    @Override
    public void loadTooltip(List<String> lines, boolean appendOptionalInfo) {
        String aspectName = LangHelpers.localize(getUnlocalizedName());
        String valueTypeName = LangHelpers.localize(getValueType().getUnlocalizedName());
        lines.add(LangHelpers.localize(L10NValues.ASPECT_TOOLTIP_ASPECTNAME, aspectName));
        lines.add(
            LangHelpers.localize(
                L10NValues.ASPECT_TOOLTIP_VALUETYPENAME,
                getValueType().getDisplayColorFormat() + valueTypeName));
        if (appendOptionalInfo) {
            LangHelpers.addOptionalInfo(lines, getUnlocalizedPrefix());
        }
    }

    @Override
    public <P extends IPartType<P, S>, S extends IPartState<P>> boolean hasProperties() {
        return getDefaultProperties() != null;
    }

    @Override
    public <P extends IPartType<P, S>, S extends IPartState<P>> IAspectProperties getProperties(P partType,
        PartTarget target, S state) {
        IAspectProperties properties = state.getAspectProperties(this);
        if (properties == null) {
            properties = getDefaultProperties().clone();
            setProperties(partType, target, state, properties);
        }
        return properties;
    }

    @Override
    public <P extends IPartType<P, S>, S extends IPartState<P>> void setProperties(P partType, PartTarget target,
        S state, IAspectProperties properties) {
        state.setAspectProperties(this, properties);
    }

    @Override
    public final IAspectProperties getDefaultProperties() {
        return defaultProperties;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Collection<IAspectPropertyTypeInstance> getPropertyTypes() {
        return hasProperties() ? getDefaultProperties().getTypes() : Collections.emptyList();
    }

    @Override
    public IGuiConstructor getPropertiesContainerProvider(PartPos pos) {
        return new IGuiConstructor() {

            @Override
            public @Nullable ContainerExtended createContainer(int windowId, InventoryPlayer playerInventory,
                EntityPlayer player) {
                Triple<IPartContainer, PartTypeBase, PartTarget> data = PartHelpers
                    .getContainerPartConstructionData(pos);
                return new ContainerAspectSettings(
                    playerInventory,
                    new SimpleInventory(0),
                    Optional.of(data.getRight()),
                    Optional.of(data.getLeft()),
                    Optional.of(data.getMiddle()),
                    AspectBase.this);
            }
        };
    }

    /**
     * Creates the default properties for this aspect, only called once.
     * 
     * @return The default properties.
     */
    @Deprecated
    protected IAspectProperties createDefaultProperties() {
        return null;
    }

    protected String getModId() {
        return this.modId;
    }
}
