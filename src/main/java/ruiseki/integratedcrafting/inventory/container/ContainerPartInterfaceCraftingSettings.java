package ruiseki.integratedcrafting.inventory.container;

import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Maps;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integratedcrafting.core.part.PartTypeInterfaceCraftingBase;
import ruiseki.integratedcrafting.part.PartTypeInterfaceCrafting;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.client.gui.ExtendedGuiHandler;
import ruiseki.integrateddynamics.core.client.gui.container.GuiMultipart;
import ruiseki.integrateddynamics.core.inventory.container.ContainerPartSettings;
import ruiseki.integrateddynamics.core.part.PartTypeConfigurable;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.ValueNotifierHelpers;
import ruiseki.okcore.inventory.IGuiContainerProvider;
import ruiseki.okcore.inventory.container.InventoryContainer;
import ruiseki.okcore.inventory.container.button.IButtonActionServer;

/**
 * @author rubensworks
 */
public class ContainerPartInterfaceCraftingSettings extends ContainerPartSettings {

    private final int lastChannelInterfaceCraftingValueId;
    private final Map<IngredientComponent<?, ?>, Integer> targetSideOverrideValueIds;
    private final int lastDisableCraftingCheckValueId;
    private final int lastBlockingModeValueId;

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
        lastBlockingModeValueId = getNextValueId();

        // Expose the offsets gui from within the settings gui,
        // as some crafting interfaces (such as the attuned one) show the settings gui as their main gui.

        putButtonAction(GuiMultipart.BUTTON_OFFSETS, new IButtonActionServer<InventoryContainer>() {

            @Override
            public void onAction(int buttonId, InventoryContainer container) {
                if (!player.worldObj.isRemote) {
                    IGuiContainerProvider gui = ((PartTypeConfigurable<?, ?>) getPartType()).getOffsetsGuiProvider();
                    IntegratedDynamics._instance.getGuiHandler()
                        .setTemporaryData(
                            ExtendedGuiHandler.PART,
                            getTarget().getCenter()
                                .getSide()); // Pass the side as extra data to the gui
                    BlockPos cPos = getTarget().getCenter()
                        .getPos()
                        .getBlockPos();
                    ContainerPartInterfaceCraftingSettings.this.player.openGui(
                        gui.getModGui(),
                        gui.getGuiID(),
                        player.worldObj,
                        cPos.getX(),
                        cPos.getY(),
                        cPos.getZ());
                }
            }
        });

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
        if (partState instanceof PartTypeInterfaceCrafting.State stateNormal) {
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
        if (partState instanceof PartTypeInterfaceCrafting.State stateNormal) {
            stateNormal.setDisableCraftingCheck(getLastDisableCraftingCheckValue());
        }
        if (partState.getCraftingJobHandler()
            .setBlockingJobsMode(getLastBlockingModeValue())) {
            partState.sendUpdate();
            partState.onDirty();
        }
    }
}
