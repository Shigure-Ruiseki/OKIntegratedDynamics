package ruiseki.integratedcrafting.inventory.container;

import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Maps;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integratedcrafting.part.PartTypeInterfaceCrafting;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.inventory.container.ContainerPartSettings;
import ruiseki.okcore.helper.ValueNotifierHelpers;

/**
 * @author rubensworks
 */
public class ContainerPartInterfaceCraftingSettings extends ContainerPartSettings {

    private final int lastChannelInterfaceCraftingValueId;
    private final Map<IngredientComponent<?, ?>, Integer> targetSideOverrideValueIds;
    private final int lastDisableCraftingCheckValueId;

    public ContainerPartInterfaceCraftingSettings(EntityPlayer player, PartTarget target, IPartContainer partContainer,
        IPartType partType) {
        super(player, target, partContainer, partType);
        lastChannelInterfaceCraftingValueId = getNextValueId();
        targetSideOverrideValueIds = Maps.newIdentityHashMap();

        for (ResourceLocation key : IngredientComponent.REGISTRY.getKeys()) {
            IngredientComponent<?, ?> ingredientComponent = IngredientComponent.REGISTRY.getValue(key);
            targetSideOverrideValueIds.put(ingredientComponent, getNextValueId());
        }
        lastDisableCraftingCheckValueId = getNextValueId();
    }

    @Override
    protected int getPlayerInventoryOffsetY() {
        return 174;
    }

    @Override
    protected void initializeValues() {
        super.initializeValues();
        ValueNotifierHelpers.setValue(
            this,
            lastChannelInterfaceCraftingValueId,
            ((PartTypeInterfaceCrafting.State) getPartState()).getChannelCrafting());

        // ĐÃ SỬA: Thay getValuesCollection() thành values()
        for (IngredientComponent<?, ?> ingredientComponent : IngredientComponent.REGISTRY.getValuesCollection()) {
            ValueNotifierHelpers.setValue(
                this,
                getTargetSideOverrideValueId(ingredientComponent),
                ((PartTypeInterfaceCrafting.State) getPartState())
                    .getIngredientComponentTargetSideOverride(ingredientComponent)
                    .ordinal());
        }
        ValueNotifierHelpers.setValue(
            this,
            lastDisableCraftingCheckValueId,
            ((PartTypeInterfaceCrafting.State) getPartState()).isDisableCraftingCheck());
    }

    public int getLastChannelInterfaceCraftingValueId() {
        return lastChannelInterfaceCraftingValueId;
    }

    public int getLastChannelInterfaceValue() {
        return ValueNotifierHelpers.getValueInt(this, lastChannelInterfaceCraftingValueId);
    }

    public int getTargetSideOverrideValueId(IngredientComponent<?, ?> ingredientComponent) {
        return targetSideOverrideValueIds.get(ingredientComponent);
    }

    @Nullable
    public ForgeDirection getTargetSideOverrideValue(IngredientComponent<?, ?> ingredientComponent) {
        int i = ValueNotifierHelpers.getValueInt(this, getTargetSideOverrideValueId(ingredientComponent));
        if (i < 0) {
            return getTarget().getTarget()
                .getSide();
        }
        return ForgeDirection.VALID_DIRECTIONS[i];
    }

    public int getLastDisableCraftingCheckValueId() {
        return lastDisableCraftingCheckValueId;
    }

    public boolean getLastDisableCraftingCheckValue() {
        return ValueNotifierHelpers.getValueBoolean(this, lastDisableCraftingCheckValueId);
    }

    public void setLastDisableCraftingCheckValue(boolean value) {
        ValueNotifierHelpers.setValue(this, lastDisableCraftingCheckValueId, value);
    }

    @Override
    protected void updatePartSettings() {
        super.updatePartSettings();
        ((PartTypeInterfaceCrafting.State) getPartState()).setChannelCrafting(getLastChannelInterfaceValue());

        for (IngredientComponent<?, ?> ingredientComponent : IngredientComponent.REGISTRY.getValuesCollection()) {
            ((PartTypeInterfaceCrafting.State) getPartState()).setIngredientComponentTargetSideOverride(
                ingredientComponent,
                getTargetSideOverrideValue(ingredientComponent));
        }
        ((PartTypeInterfaceCrafting.State) getPartState()).setDisableCraftingCheck(getLastDisableCraftingCheckValue());
    }
}
