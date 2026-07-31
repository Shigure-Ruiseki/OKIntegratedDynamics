package ruiseki.integrateddynamics.core.evaluate.variable;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.core.helper.L10NValues;
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

    public ValueTypeBase(String typeName, int color, String colorFormat) {
        this.typeName = typeName;
        this.color = color;
        this.colorFormat = colorFormat;
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
        return getUnlocalizedPrefix() + ".name";
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
    public boolean correspondsTo(IValueType valueType) {
        return this == valueType;
    }

    @SideOnly(Side.CLIENT)
    protected void registerModelResourceLocation() {
        ValueTypes.REGISTRY.registerValueTypeIconPath(
            this,
            getModId() + ":valuetypes" + getTypeNamespace().replace('.', '/') + getTypeName().replace('.', '/'));
    }

    @Override
    public void loadTooltip(List<String> lines, boolean appendOptionalInfo) {
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
    public V materialize(V value) {
        return value;
    }

    @Override
    public String toString() {
        return LangHelpers.localize(getUnlocalizedName());
    }

    protected String getModId() {
        return Reference.MOD_ID;
    }

}
