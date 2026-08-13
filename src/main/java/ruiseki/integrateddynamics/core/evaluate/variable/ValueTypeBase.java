package ruiseki.integrateddynamics.core.evaluate.variable;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeLPElementBase;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;

/**
 * Base implementation of a value type.
 *
 * @author rubensworks
 */
public abstract class ValueTypeBase<V extends IValue> implements IValueType<V> {

    private final String typeName;
    private final int color;
    private final String colorFormat;
    private final Class<V> valueClass;

    private String unlocalizedName = null;

    public ValueTypeBase(String typeName, int color, String colorFormat, @Nullable Class<V> valueClass) {
        this.typeName = typeName;
        this.color = color;
        this.colorFormat = colorFormat;
        this.valueClass = valueClass;
        if (MinecraftHelpers.isModdedEnvironment() && MinecraftHelpers.isClientSide()) {
            registerModelResourceLocation();
        }
    }

    @Override
    public boolean isCategory() {
        return false;
    }

    @Override
    public boolean isObject() {
        return false;
    }

    protected String getUnlocalizedPrefix() {
        return "valuetype.valuetypes." + getModId() + getTypeNamespace() + getTypeName();
    }

    protected String getTypeNamespace() {
        return ".";
    }

    @Override
    public String getUnlocalizedName() {
        return unlocalizedName != null ? unlocalizedName : (unlocalizedName = getUnlocalizedPrefix() + ".name");
    }

    @Override
    public String getTypeName() {
        return this.typeName;
    }

    @Override
    public int getDisplayColor() {
        return this.color;
    }

    @Override
    public String getDisplayColorFormat() {
        return this.colorFormat;
    }

    @Override
    public boolean correspondsTo(IValueType<?> valueType) {
        return this == valueType;
    }

    @SideOnly(Side.CLIENT)
    protected void registerModelResourceLocation() {
        ValueTypes.REGISTRY.registerValueTypeModel(
            this,
            new ResourceLocation(
                getModId() + ":valuetype" + getTypeNamespace().replace('.', '/') + getTypeName().replace('.', '/')));
    }

    @Override
    public void loadTooltip(List<String> lines, boolean appendOptionalInfo, @Nullable V value) {
        String typeName = LangHelpers.localize(getUnlocalizedName());
        lines.add(LangHelpers.localize(L10NValues.VALUETYPE_TOOLTIP_TYPENAME, getDisplayColorFormat() + typeName));
        if (appendOptionalInfo) {
            LangHelpers.addOptionalInfo(lines, getUnlocalizedPrefix());
        }
    }

    @Override
    public LangHelpers.UnlocalizedString canDeserialize(String value) {
        try {
            deserialize(value);
            return null;
        } catch (IllegalArgumentException e) {
            return new LangHelpers.UnlocalizedString(L10NValues.VALUETYPE_ERROR_INVALIDINPUT, value);
        }
    }

    @Override
    public V materialize(V value) throws EvaluationException {
        return value;
    }

    @Override
    public String toString() {
        return LangHelpers.localize(getUnlocalizedName());
    }

    @Override
    public ValueTypeLPElementBase createLogicProgrammerElement() {
        return null;
    }

    protected String getModId() {
        return Reference.MOD_ID;
    }

    @Override
    public V cast(IValue value) throws EvaluationException {
        try {
            return this.valueClass.cast(value);
        } catch (ClassCastException e) {
            throw new EvaluationException(
                String.format(
                    "Attempted to cast %s to %s, for value \"%s\"",
                    LangHelpers.localize(
                        value.getType()
                            .getUnlocalizedName()),
                    LangHelpers.localize(this.getUnlocalizedName()),
                    value.getType()
                        .toCompactString(value)));
        }
    }
}
