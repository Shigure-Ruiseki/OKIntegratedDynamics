package ruiseki.integratedcrafting.inventory.container;

import java.util.Map;
import java.util.Optional;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Maps;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integratedcrafting.core.part.PartTypeInterfaceCraftingBase;
import ruiseki.integratedcrafting.core.part.PartTypeInterfaceCraftingVariableBase;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.core.inventory.container.ContainerPartSettings;
import ruiseki.okcore.helper.ValueNotifierHelpers;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.network.ExtendedBuffer;

/**
 * @author rubensworks
 */
public class ContainerPartInterfaceCraftingSettings extends ContainerPartSettings {

    private final int lastChannelInterfaceCraftingValueId;
    private final Map<IngredientComponent<?, ?>, Integer> targetSideOverrideValueIds;
    private final int lastDisableCraftingCheckValueId;
    private final int lastBlockingModeValueId;

    public ContainerPartInterfaceCraftingSettings(InventoryPlayer playerInventory, ExtendedBuffer packetBuffer) {
        this(
            playerInventory,
            new SimpleInventory(0),
            PartHelpers.readPartTarget(packetBuffer),
            Optional.empty(),
            PartHelpers.readPart(packetBuffer));
    }

    public ContainerPartInterfaceCraftingSettings(InventoryPlayer playerInventory, IInventory inventory,
        PartTarget target, Optional<IPartContainer> partContainer, IPartType partType) {
        super(
            ContainerPartInterfaceCraftingSettingsConfig._instance.getInstance(),
            playerInventory,
            inventory,
            target,
            partContainer,
            partType);
        lastChannelInterfaceCraftingValueId = getNextValueId();
        targetSideOverrideValueIds = Maps.newIdentityHashMap();

        for (ResourceLocation key : IngredientComponent.REGISTRY.getKeys()) {
            IngredientComponent<?, ?> ingredientComponent = IngredientComponent.REGISTRY.getValue(key);
            targetSideOverrideValueIds.put(ingredientComponent, getNextValueId());
        }
        lastDisableCraftingCheckValueId = getNextValueId();
        lastBlockingModeValueId = getNextValueId();
    }

    @Override
    protected int getPlayerInventoryOffsetY() {
        return 174;
    }

    @Override
    protected void initializeValues() {
        super.initializeValues();
        PartTypeInterfaceCraftingBase.State<?, ?> partState = (PartTypeInterfaceCraftingBase.State<?, ?>) getPartState();
        ValueNotifierHelpers.setValue(this, lastChannelInterfaceCraftingValueId, partState.getChannelCrafting());
        for (IngredientComponent<?, ?> ingredientComponent : IngredientComponent.REGISTRY.getValuesCollection()) {
            ValueNotifierHelpers.setValue(
                this,
                getTargetSideOverrideValueId(ingredientComponent),
                partState.getIngredientComponentTargetSideOverride(ingredientComponent)
                    .ordinal());
        }
        if (partState instanceof PartTypeInterfaceCraftingVariableBase.State<?, ?>stateNormal) {
            ValueNotifierHelpers.setValue(this, lastDisableCraftingCheckValueId, stateNormal.isDisableCraftingCheck());
        }
        ValueNotifierHelpers.setValue(
            this,
            lastBlockingModeValueId,
            partState.getCraftingJobHandler()
                .isBlockingJobsMode());
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

    public int getLastBlockingModeValueId() {
        return lastBlockingModeValueId;
    }

    public boolean getLastDisableCraftingCheckValue() {
        return ValueNotifierHelpers.getValueBoolean(this, lastDisableCraftingCheckValueId);
    }

    public boolean getLastBlockingModeValue() {
        return ValueNotifierHelpers.getValueBoolean(this, lastBlockingModeValueId);
    }

    public void setLastDisableCraftingCheckValue(boolean value) {
        ValueNotifierHelpers.setValue(this, lastDisableCraftingCheckValueId, value);
    }

    public void setLastBlockingModeValue(boolean value) {
        ValueNotifierHelpers.setValue(this, lastBlockingModeValueId, value);
    }

    @Override
    protected void updatePartSettings() {
        super.updatePartSettings();
        PartTypeInterfaceCraftingBase.State<?, ?> partState = (PartTypeInterfaceCraftingBase.State<?, ?>) getPartState();
        partState.setChannelCrafting(getLastChannelInterfaceValue());
        for (IngredientComponent<?, ?> ingredientComponent : IngredientComponent.REGISTRY.getValuesCollection()) {
            partState.setIngredientComponentTargetSideOverride(
                ingredientComponent,
                getTargetSideOverrideValue(ingredientComponent));
        }
        if (partState instanceof PartTypeInterfaceCraftingVariableBase.State<?, ?>stateNormal) {
            stateNormal.setDisableCraftingCheck(getLastDisableCraftingCheckValue());
        }
        if (partState.getCraftingJobHandler()
            .setBlockingJobsMode(getLastBlockingModeValue())) {
            partState.sendUpdate();
            partState.onDirty();
        }
    }
}
