package ruiseki.integrateddynamics.core.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectProperties;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectPropertyTypeInstance;
import ruiseki.integrateddynamics.core.client.gui.ExtendedGuiHandler;
import ruiseki.integrateddynamics.core.client.gui.container.GuiAspectSettings;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.network.event.VariableContentsUpdatedEvent;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.ValueNotifierHelpers;
import ruiseki.okcore.inventory.IGuiContainerProvider;
import ruiseki.okcore.inventory.container.ExtendedInventoryContainer;
import ruiseki.okcore.inventory.container.InventoryContainer;
import ruiseki.okcore.inventory.container.button.IButtonActionServer;

/**
 * Container for aspect settings.
 *
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class ContainerAspectSettings extends ExtendedInventoryContainer {

    public static final int BUTTON_SETTINGS = 1;
    private static final int PAGE_SIZE = 3;

    private final PartTarget target;
    private final IPartContainer partContainer;
    private final IPartType partType;
    private final World world;
    private final BlockPos pos;
    private final IAspect aspect;

    private final BiMap<Integer, IAspectPropertyTypeInstance> propertyIds = HashBiMap.create();

    /**
     * Make a new instance.
     *
     * @param target        The target.
     * @param player        The player.
     * @param partContainer The part container.
     * @param partType      The part type.
     * @param aspect        The aspect.
     */
    public ContainerAspectSettings(final EntityPlayer player, PartTarget target, IPartContainer partContainer,
        IPartType partType, IAspect aspect) {
        super(player.inventory, (IGuiContainerProvider) partType);
        this.target = target;
        this.partContainer = partContainer;
        this.partType = partType;
        this.world = player.getEntityWorld();
        if (target != null && target.getCenter() != null) {
            this.pos = target.getCenter()
                .getPos()
                .getBlockPos();
        } else {
            this.pos = new BlockPos(
                (int) Math.floor(player.posX),
                (int) Math.floor(player.posY),
                (int) Math.floor(player.posZ));
        }
        this.aspect = aspect;

        addPlayerInventory(player.inventory, 8, 131);

        for (IAspectPropertyTypeInstance property : ((IAspect<?, ?>) aspect).getPropertyTypes()) {
            propertyIds.put(getNextValueId(), property);
        }

        putButtonAction(GuiAspectSettings.BUTTON_EXIT, new IButtonActionServer<InventoryContainer>() {

            @Override
            public void onAction(int buttonId, InventoryContainer container) {
                if (!world.isRemote) {
                    IntegratedDynamics._instance.getGuiHandler()
                        .setTemporaryData(
                            ExtendedGuiHandler.PART,
                            getTarget().getCenter()
                                .getSide());
                    BlockPos pos = getTarget().getCenter()
                        .getPos()
                        .getBlockPos();
                    player.openGui(
                        IntegratedDynamics._instance.getModId(),
                        ((IGuiContainerProvider) getPartType()).getGuiID(),
                        world,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ());
                }
            }
        });
    }

    @Override
    protected void initializeValues() {
        super.initializeValues();
        IAspectProperties properties = aspect.getProperties(getPartType(), getTarget(), getPartState());
        for (IAspectPropertyTypeInstance property : ((IAspect<?, ?>) aspect).getPropertyTypes()) {
            setValue(property, properties.getValue(property));
        }
    }

    public void setValue(IAspectPropertyTypeInstance property, IValue value) {
        ValueNotifierHelpers.setValue(
            this,
            propertyIds.inverse()
                .get(property),
            ValueHelpers.serializeRaw(value));
    }

    @SuppressWarnings("unchecked")
    public IPartState getPartState() {
        return partContainer.getPartState(
            getTarget().getCenter()
                .getSide());
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    protected int getSizeInventory() {
        return 0;
    }

    public <T extends IValueType<V>, V extends IValue> V getPropertyValue(IAspectPropertyTypeInstance<T, V> property) {
        if (propertyIds.containsValue(property)) {
            String value = ValueNotifierHelpers.getValueString(
                this,
                propertyIds.inverse()
                    .get(property));
            if (value != null) {
                return ValueHelpers.deserializeRaw(property.getType(), value);
            }
        }
        return null;
    }

    @Override
    public void onUpdate(int valueId, NBTTagCompound value) {
        super.onUpdate(valueId, value);
        if (!world.isRemote) {
            IAspectPropertyTypeInstance property = propertyIds.get(valueId);
            if (property != null) {
                IAspectProperties aspectProperties = getAspect()
                    .getProperties(getPartType(), getTarget(), getPartState());
                aspectProperties = aspectProperties.clone();
                IValue trueValue = ValueHelpers
                    .deserializeRaw(property.getType(), value.getString(ValueNotifierHelpers.KEY));
                aspectProperties.setValue(property, trueValue);
                getAspect().setProperties(getPartType(), getTarget(), getPartState(), aspectProperties);

                // Changing the properties might cause some erroring variables to become valid again, so trigger an
                // update.
                INetwork network = NetworkHelpers.getNetwork(
                    getTarget().getCenter()
                        .getPos()
                        .getWorld(),
                    getTarget().getCenter()
                        .getPos()
                        .getBlockPos(),
                    getTarget().getCenter()
                        .getSide());
                if (network != null) {
                    network.getEventBus()
                        .post(new VariableContentsUpdatedEvent(network));
                }
            }
        }
    }
}
