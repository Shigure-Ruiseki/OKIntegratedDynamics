package ruiseki.integrateddynamics.core.inventory.container;

import java.util.Objects;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectProperties;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectPropertyTypeInstance;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.core.network.event.VariableContentsUpdatedEvent;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.okcore.helper.ValueNotifierHelpers;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.inventory.container.InventoryContainer;
import ruiseki.okcore.network.ExtendedBuffer;

/**
 * Container for aspect settings.
 *
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class ContainerAspectSettings extends InventoryContainer {

    public static final String BUTTON_EXIT = "button_exit";
    private static final int PAGE_SIZE = 3;

    private final Optional<PartTarget> target;
    private final Optional<IPartContainer> partContainer;
    private final Optional<IPartType> partType;
    private final World world;
    private final IAspect<?, ?> aspect;

    private final BiMap<Integer, IAspectPropertyTypeInstance> propertyIds = HashBiMap.create();

    public ContainerAspectSettings(InventoryPlayer playerInventory, ExtendedBuffer packetBuffer) {
        this(
            playerInventory,
            new SimpleInventory(0),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            readAspect(packetBuffer));
    }

    protected static IAspect<?, ?> readAspect(ExtendedBuffer packetBuffer) {
        String name = packetBuffer.readString();
        return Objects.requireNonNull(
            AspectRegistry.getInstance()
                .getAspect(new ResourceLocation(name)),
            String.format("Could not find an aspect by name %s", name));
    }

    public ContainerAspectSettings(InventoryPlayer playerInventory, IInventory inventory, Optional<PartTarget> target,
        Optional<IPartContainer> partContainer, Optional<IPartType> partType, IAspect<?, ?> aspect) {
        super(ContainerAspectSettingsConfig._instance.getInstance(), playerInventory, inventory);
        this.target = target;
        this.partContainer = partContainer;
        this.partType = partType;
        this.world = player.getEntityWorld();
        this.aspect = aspect;

        addPlayerInventory(player.inventory, 8, 131);

        for (IAspectPropertyTypeInstance property : aspect.getPropertyTypes()) {
            propertyIds.put(getNextValueId(), property);
        }

        putButtonAction(ContainerAspectSettings.BUTTON_EXIT, (s, containerExtended) -> {
            if (!world.isRemote) {
                PartHelpers.openContainerPart(
                    (EntityPlayerMP) playerInventory.player,
                    getTarget().get()
                        .getCenter(),
                    getPartType().get());
            }
        });
    }

    @Override
    protected void initializeValues() {
        super.initializeValues();
        IAspectProperties properties = aspect
            .getProperties(getPartType().get(), getTarget().get(), getPartState().get());
        for (IAspectPropertyTypeInstance property : aspect.getPropertyTypes()) {
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

    public Optional<IPartState> getPartState() {
        return partContainer.map(
            p -> p.getPartState(
                getTarget().get()
                    .getCenter()
                    .getSide()));
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
            NBTBase value = ValueNotifierHelpers.getValueNbt(
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
                IPartType partType = getPartType().get();
                PartTarget target = getTarget().get();
                IPartState partState = getPartState().get();

                IAspectProperties aspectProperties = aspect.getProperties(partType, target, partState);
                aspectProperties = aspectProperties.clone();
                IValue trueValue = ValueHelpers
                    .deserializeRaw(property.getType(), value.getTag(ValueNotifierHelpers.KEY));
                aspectProperties.setValue(property, trueValue);
                aspect.setProperties(partType, target, partState, aspectProperties);

                // Changing the properties might cause some erroring variables to become valid again, so trigger an
                // update.
                NetworkHelpers.getNetwork(target.getCenter())
                    .ifPresent(
                        network -> network.getEventBus()
                            .post(new VariableContentsUpdatedEvent(network)));
            }
        }
    }
}
